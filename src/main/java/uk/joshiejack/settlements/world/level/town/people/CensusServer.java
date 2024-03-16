package uk.joshiejack.settlements.world.level.town.people;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.Pair;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.settlements.AdventureDataLoader;
import uk.joshiejack.settlements.entity.EntityNPC;
import uk.joshiejack.settlements.entity.ai.action.Action;
import uk.joshiejack.settlements.network.town.people.PacketSyncCustomNPCs;
import uk.joshiejack.settlements.network.town.people.PacketSyncResidents;
import uk.joshiejack.settlements.npcs.NPC;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.Action;
import uk.joshiejack.settlements.world.entity.npc.DynamicNPC;
import uk.joshiejack.settlements.world.entity.npc.NPC;
import uk.joshiejack.settlements.world.entity.npc.NPCInfo;
import uk.joshiejack.settlements.world.level.TownSavedData;
import uk.joshiejack.settlements.world.level.town.TownServer;
import uk.joshiejack.settlements.world.town.TownServer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CensusServer extends AbstractCensus implements INBTSerializable<CompoundTag> {
    private final Set<ResourceLocation> inviteList = Sets.newHashSet(); //SUB_SET of Residents SERVER ONLY
    private final Multimap<ResourceLocation, Action> memorableActions = HashMultimap.create(); //SERVER ONLY
    private final Map<ResourceLocation, DynamicNPC> customNPCs = Maps.newHashMap();
    private final Spawner spawner;
    private final TownServer town;

    public CensusServer(TownServer town) {
        this.town = town;
        this.spawner = new Spawner(town);
    }

    public Spawner getSpawner() {
        return spawner;
    }

    public Collection<ResourceLocation> getCustomNPCKeys() {
        return customNPCs.keySet();
    }

    public Collection<DynamicNPC> getCustomNPCs() {
        return customNPCs.values();
    }

    public void createCustomNPCFromData(Level world, ResourceLocation uniqueID, DynamicNPC npc) {
        customNPCs.put(uniqueID, npc);
        onNPCsChanged(world);
        TownSavedData.get((ServerLevel) world).setDirty();
        PenguinNetwork.sendToEveryone(new PacketSyncCustomNPCs(world.dimension(), town.getID(), customNPCs.values()));
    }

    public void invite(ResourceLocation npc) {
        inviteList.add(npc);
    }

    public void onNewDay(Level world) {
        if (!inviteList.isEmpty()) {
            for (ResourceLocation uniqueID : inviteList) {
                NPC theNPC = customNPCs.containsKey(uniqueID) ? NPC.getNPCFromRegistry(customNPCs.get(uniqueID).getLeft()) : NPC.getNPCFromRegistry(uniqueID);
                CompoundTag theData = customNPCs.containsKey(uniqueID) ? customNPCs.get(uniqueID).getRight() : null;
                NPCMob npcEntity = spawner.getNPC(world, theNPC, uniqueID, theData, town.getCentre());
                if (npcEntity != null) {
                    npcEntity.getPhysicalAI().addToHead((LinkedList<Action>) memorableActions.get(uniqueID).stream().filter(a -> a.getAIType() == Action.AIType.PHYSICAL).collect(Collectors.toCollection(LinkedList::new)));
                    npcEntity.getMentalAI().addToHead((LinkedList<Action>) memorableActions.get(uniqueID).stream().filter(a -> a.getAIType() == Action.AIType.MENTAL).collect(Collectors.toCollection(LinkedList::new)));
                    npcEntity.getLookAI().addToHead((LinkedList<Action>) memorableActions.get(uniqueID).stream().filter(a -> a.getAIType() == Action.AIType.LOOK).collect(Collectors.toCollection(LinkedList::new)));
                    memorableActions.removeAll(uniqueID); //Clear
                }
            }

            inviteList.clear();
        }

        onNPCsChanged(world); //Resync
    }

    public void onNPCDeath(NPCMob npc) {
        npc.getPhysicalAI().all().stream().filter(Action::isMemorable)
                .forEach(action -> memorableActions.get(npc.getNPC().id()).add(action));
        npc.getMentalAI().all().stream().filter(Action::isMemorable)
                .forEach(action -> memorableActions.get(npc.getNPC().id()).add(action));
    }

    public void onNPCsChanged(Level world) {
        Set<ResourceLocation> original = Sets.newHashSet(invitableList);
        invitableList.clear();
        invitableList.addAll(residents);
        invitableList.addAll(customNPCs.keySet());
        spawner.getNearbyNPCs(world).forEach(e -> invitableList.remove(e.getNPC().id()));
        invitableList.removeAll(inviteList); //If they are already invited remove them
        if (!original.equals(invitableList)) {
            PenguinNetwork.sendToTeam(new PacketSyncResidents(town.getID(), invitableList), world, town.getCharter().getTeamID());
        }
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        residents.clear(); //Updo
        ListTag inviteTagList = tag.getList("InviteList", 8);
        for (int i = 0; i < inviteTagList.size(); i++) {
            inviteList.add(new ResourceLocation(inviteTagList.getString(i)));
        }
        //Action memory
        memorableActions.clear(); //Empty
        ListTag memorableList = tag.getList("MemorableList", 10);
        for (int i = 0; i < memorableList.size(); i++) {
            CompoundTag memorableNBT = memorableList.getCompound(i);
            ResourceLocation npc = new ResourceLocation(memorableNBT.getString("NPC"));
            ListTag actionList = memorableNBT.getList("Actions", 10);
            for (int j = 0; j < actionList.size(); j++) {
                CompoundTag actionNBT = actionList.getCompound(j);
                Action action = Action.createOfType(actionNBT.getString("Type"), actionNBT.getCompound("Data"));
                memorableActions.get(npc).add(action);
            }
        }

        ListTag memorableDataList = tag.getList("CustomNPCs", 10);
        for (int i = 0; i < memorableDataList.size(); i++) {
            CompoundTag data = memorableDataList.getCompound(i);
            DynamicNPC npc = DynamicNPC.fromTag(data.getCompound("Data"));
            customNPCs.put(npc.id(), npc);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag inviteTagList = new ListTag();
        inviteList.forEach(npc -> inviteTagList.add(StringTag.valueOf(npc.toString())));
        tag.put("InviteList", inviteTagList);
        //Action memory
        ListTag memorableList = new ListTag();
        memorableActions.keySet().forEach((npc) -> {
            CompoundTag memorableNBT = new CompoundTag();
            memorableNBT.putString("NPC", npc.toString());
            ListTag actionList = new ListTag();
            for (Action action : memorableActions.get(npc)) {
                CompoundTag actionNBT = new CompoundTag();
                actionNBT.putString("Type", action.getType());
                actionNBT.put("Data", action.serializeNBT());
                actionList.add(actionNBT);
            }

            memorableNBT.put("Actions", actionList);
            memorableList.add(memorableNBT);
        });

        tag.put("MemorableList", memorableList);

        //Save the custom data
        ListTag memorableDataList = new ListTag();
        customNPCs.keySet().forEach(npc -> {
            CompoundTag data = new CompoundTag();
            data.put("NPC", customNPCs.get(npc).toTag());
            memorableDataList.add(data);
        });

        tag.put("CustomNPCs", memorableDataList);
        return tag;
    }
}
