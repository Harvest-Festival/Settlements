package uk.joshiejack.settlements.world.level;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.util.helper.TagHelper;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;
import uk.joshiejack.settlements.network.town.SyncTownsPacket;
import uk.joshiejack.settlements.network.town.land.CreateTownPacket;
import uk.joshiejack.settlements.world.level.town.Town;
import uk.joshiejack.settlements.world.level.town.TownFinder;
import uk.joshiejack.settlements.world.level.town.TownServer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;


public class TownSavedData extends SavedData {
    public static final String DATA_NAME = "settlements_towns";
    private final Int2ObjectMap<TownServer> towns = new Int2ObjectOpenHashMap<>();

    public static TownSavedData get(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(new SavedData.Factory<>(TownSavedData::new, TownSavedData::load), DATA_NAME);
    }

    public Collection<TownServer> getTowns() {
        return towns.values();
    }

    private Int2ObjectMap<TownServer> getTownMap() {
        return towns;
    }

    private int getUnusedID() {
        Int2ObjectMap<TownServer> map = getTownMap();
        for (int i = 1; i <= Short.MAX_VALUE; i++) {
            if (!map.containsKey(i)) return i;
        }

        return 0; //Null town, should never happen in theory
    }

    public Town<?> createTown(Level world, Player player) {
        TownServer town = new TownServer(getUnusedID(), player.blockPosition()); //Give the town the same uuid as the team
        UUID townUUID = UUID.randomUUID(); //Randomly generated
        town.getCharter().setFoundingInformation(Component.literal(player.getDisplayName().getString() + "Ville"), player.getName(), world.getDayTime(), townUUID);
        PenguinTeams.get((ServerLevel) world).changeTeam((ServerLevel) world, player.getUUID(), townUUID); //Player joins this new team
        towns.put(town.getID(), town);
        TownSavedData.get((ServerLevel) world).setDirty(); //Save me bitch!
        TownFinder.getFinder(world).clearCache(); //Clear the cache for the town finder
        PenguinNetwork.sendToEveryone(new CreateTownPacket(world.dimension(), town));
        return town;
    }

    @Nonnull
    public TownServer getTownByID(int townID) {
        return towns.containsKey(townID) ? towns.get(townID) : TownServer.NULL;
    }

    public static TownSavedData load(@Nonnull CompoundTag compound) {
        TownSavedData data = new TownSavedData();
        TagHelper.readMap(compound.getList("Towns", 10), data.towns,
                (tag1) -> tag1.getInt("ID"),
                (tag2, id) -> {
                    TownServer townServer = new TownServer(id, BlockPos.of(tag2.getLong("Centre")));
                    townServer.deserializeNBT(tag2.getCompound("Town"));
                    return townServer;
                });
        return data;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compound) {
        compound.put("Towns", TagHelper.writeMap(towns,
                (tag1, id) -> tag1.putInt("ID", (int) id),
                (tag2, town) -> {
                    tag2.putLong("Centre", town.getCentre().asLong());
                    tag2.put("Town", town.serializeNBT());
                }));
        return compound;
    }

    public void sync(ServerPlayer player) {
        PenguinNetwork.sendToClient(player, new SyncTownsPacket(player.level().dimension(), new ArrayList<>(towns.values())));
    }
}
