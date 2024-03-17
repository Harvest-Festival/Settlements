package uk.joshiejack.settlements.world.level;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.storage.WorldSavedData;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.scripting.Scripting;
import uk.joshiejack.penguinlib.util.PenguinGroup;
import uk.joshiejack.penguinlib.util.helpers.minecraft.PlayerHelper;
import uk.joshiejack.penguinlib.util.helpers.minecraft.TimeHelper;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;
import uk.joshiejack.penguinlib.world.teams.PenguinTeams;
import uk.joshiejack.settlements.network.block.PacketSyncDailies;
import uk.joshiejack.settlements.network.town.SyncTownsPacket;
import uk.joshiejack.settlements.network.town.land.CreateTownPacket;
import uk.joshiejack.settlements.npcs.status.StatusTracker;
import uk.joshiejack.settlements.quest.Quest;
import uk.joshiejack.settlements.quest.data.QuestData;
import uk.joshiejack.settlements.quest.data.QuestTracker;
import uk.joshiejack.settlements.quest.settings.Information;
import uk.joshiejack.settlements.util.QuestHelper;
import uk.joshiejack.settlements.util.TownFinder;
import uk.joshiejack.settlements.world.level.town.Town;
import uk.joshiejack.settlements.world.level.town.TownFinder;
import uk.joshiejack.settlements.world.level.town.TownServer;
import uk.joshiejack.settlements.world.town.Town;
import uk.joshiejack.settlements.world.town.TownServer;

import javax.annotation.Nonnull;
import java.util.*;


public class TownSavedData extends SavedData {
    public static final String DATA_NAME = "settlements_towns";
    private final Object2ObjectMap<ResourceKey<Level>, Int2ObjectMap<TownServer>> towns = new Object2ObjectOpenHashMap<>();
    private int day;

    public static TownSavedData get(ServerLevel world) {
        return world.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(TownSavedData::new, TownSavedData::load), DATA_NAME);
    }


    public Collection<TownServer> getTowns(Level world) {
        return getTownMap(world.dimension()).values();
    }

    private Int2ObjectMap<TownServer> getTownMap(ResourceKey<Level> dim) {
        Int2ObjectMap<TownServer> map = towns.get(dim);
        if (map == null) {
            map = new Int2ObjectOpenHashMap<>();
            towns.put(dim, map);
        }

        return map;
    }

    private int getUnusedID(ResourceKey<Level> dimension) {
        Int2ObjectMap<TownServer> map = getTownMap(dimension);
        for (int i = 1; i <= Short.MAX_VALUE; i++) {
            if (!map.containsKey(i)) return i;
        }

        return 0; //Null town, should never happen in theory
    }

    public Town<?> createTown(Level world, Player player) {
        TownServer town = new TownServer(getUnusedID(world.dimension()), player.blockPosition()); //Give the town the same uuid as the team
        UUID townUUID = UUID.randomUUID(); //Randomly generated
        town.getCharter().setFoundingInformation(Component.literal(player.getDisplayName().getString() + "Ville"), player.getName(), world.getDayTime(), townUUID);
        PenguinTeams.get((ServerLevel) world).changeTeam((ServerLevel) world, player.getUUID(), townUUID); //Player joins this new team
        getTownMap(world.dimension()).put(town.getID(), town);
        TownSavedData.get((ServerLevel) world).setDirty(); //Save me bitch!
        TownFinder.getFinder(world).clearCache(); //Clear the cache for the town finder
        PenguinNetwork.sendToEveryone(new CreateTownPacket(world.provider.getDimension(), town));
        return town;
    }

    @Nonnull
    public TownServer getTownByID(ResourceKey<Level> dimension, int townID) {
        Int2ObjectMap<TownServer> towns = getTownMap(dimension);
        return towns.containsKey(townID) ? towns.get(townID) : TownServer.NULL;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
        this.markDirty();
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        try {
            day = compound.getInteger("Day");
            QuestHelper.readUUIDtoRelationshipMap(compound.getTagList("Statuses", 10), status);
            NBTTagList list = compound.getTagList("Dimensions", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                QuestHelper.readIDToTownMap(tag.getTagList("Towns", 10), getTownMap(tag.getInteger("ID")));
            }

            //Quests
            NBTTagCompound nbt = compound.getCompoundTag("Quests");
            QuestHelper.readUUIDtoTrackerMap(PenguinGroup.PLAYER, nbt.getTagList("Player", 10), playerQuests);
            QuestHelper.readUUIDtoTrackerMap(PenguinGroup.TEAM, nbt.getTagList("Team", 10), teamQuests);
            globalQuests.deserializeNBT(nbt.getCompoundTag("Global"));

            //Reload in the triggers, so yey!
            playerQuests.values().forEach((t) -> t.setupOrRefresh(day));
            teamQuests.values().forEach((t) -> t.setupOrRefresh(day));
            globalQuests.setupOrRefresh(day);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compound) {
        ListTag list = new ListTag();
        towns.forEach((i, map) -> {
            CompoundTag tag = new CompoundTag();
            tag.setInteger("ID", i);
            tag.put("Towns", QuestHelper.writeIDToTownMap(map));
            list.add(tag);
        });

        compound.put("Dimensions", list);
        return compound;
    }

    public void sync(Player player) {
        towns.forEach((i, map) -> {
            PenguinNetwork.sendToClient(new SyncTownsPacket(i, getTownMap(i).values()), player);
        });
    }
}
