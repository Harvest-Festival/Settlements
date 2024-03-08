package uk.joshiejack.settlements.world.entity.npc.status;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;

public class StatusTracker implements INBTSerializable<CompoundTag> {
    private final Map<ResourceLocation, Object2IntMap<String>> status = Maps.newHashMap();
    private final Map<ResourceLocation, Object2IntMap<String>> status_timers = Maps.newHashMap();
    private WeakReference<Player> player;
    private final UUID uuid;

    public StatusTracker(UUID uuid) {
        this.uuid = uuid;
    }

    private Object2IntMap<String> getDataForNPC(Map<ResourceLocation, Object2IntMap<String>> map, ResourceLocation npc) {
        if (!map.containsKey(npc)) {
            map.put(npc, new Object2IntOpenHashMap<>());
        }

        return map.get(npc);
    }

    @Nullable
    private Player getPlayer(Level world) {
        if (player == null || player.get() == null) {
            player = new WeakReference<>(world.getPlayerByUUID(uuid));
        }

        return player.get();
    }

    public void newDay() {
        status.keySet().forEach(npc -> {
            Object2IntMap<String> status = this.status.get(npc);
            if (status != null) {
                status.keySet().forEach(s -> {
                    if (Status.resets(s)) {
                        int value = status.getInt(s);
                        Object2IntMap<String> status_timer = getDataForNPC(this.status_timers, npc);
                        if (value > 0) status.mergeInt(s, 1, Integer::sum);;
                        int timer = status_timer.getInt(s);
                        if (timer >= Status.getReset(s)) {
                            status_timer.put(s, 0); // Reset the timer
                            status.put(s, 0); //Reset the value
                        }
                    }
                });
            }
        });

        //TODO PenguinNetwork.sendToEveryone(new PacketSyncNPCStatuses(status));
    }

    public boolean has(ResourceLocation npc, String status) {
        return this.status.get(npc).containsKey(status);
    }

    public int total(String status) {
        int count = 0;
        for (ResourceLocation k: this.status.keySet()) {
            count += this.status.get(k).getInt(status);
        }

        return count;
    }

    public int get(ResourceLocation npc, String status) {
        return this.status.containsKey(npc) ? this.getDataForNPC(this.status, npc).getInt(status) : 0;
    }

    public void set(Level world, ResourceLocation npc, String status, int value) {
        if (value == 0){
            this.getDataForNPC(this.status, npc).remove(status); //Don't bother storing if the value is 0
        } else this.getDataForNPC(this.status, npc).put(status, value);
        updateClient(world, npc, status); //Sync the value to the client
    }

    public void adjust(Level world, ResourceLocation npc, String status, int value) {
        value = this.getDataForNPC(this.status, npc).merge(status, value, Integer::sum);
        if (value == 0) this.getDataForNPC(this.status, npc).remove(status);
        updateClient(world, npc, status); //Sync the value to the client
    }

    private static ListTag writeNPCtoObjectIntMap(Map<ResourceLocation, Object2IntMap<String>> save) {
        ListTag list = new ListTag();
        for (Map.Entry<ResourceLocation, Object2IntMap<String>> e: save.entrySet()) {
            CompoundTag data = new CompoundTag();
            data.putString("NPC", e.getKey().toString());
            ListTag map = new ListTag();
            for (String status: e.getValue().keySet()) {
                CompoundTag statusTag = new CompoundTag();
                statusTag.putString("Status", status);
                statusTag.putInt("Value", e.getValue().getInt(status));
                map.add(statusTag);
            }

            data.put("Map", map);
            list.add(data);
        }

        return list;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("Statuses", writeNPCtoObjectIntMap(status));
        tag.put("StatusTimers", writeNPCtoObjectIntMap(status_timers));
        return tag;
    }

    private static void readNPCToObjectIntMap(ListTag list, Map<ResourceLocation, Object2IntMap<String>> load) {
        load.clear();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag data = list.getCompound(i);
            ResourceLocation npc = new ResourceLocation(data.getString("NPC"));
            load.put(npc, new Object2IntOpenHashMap<>()); //Add for this npc as they have data...
            ListTag map = data.getList("Map", 10);
            for (int j = 0; j < map.size(); j++) {
                CompoundTag statusTag = map.getCompound(j);
                String status =statusTag.getString("Status");
                int value = statusTag.getInt("Value");
                load.get(npc).put(status, value);
            }
        }
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        readNPCToObjectIntMap(nbt.getList("Statuses", 10), status);
        readNPCToObjectIntMap(nbt.getList("StatusTimers", 10), status_timers);
    }

    private void updateClient(Level world, ResourceLocation npc, String status) {
        Player player = getPlayer(world);
        if (player != null) {
           //TODO PenguinNetwork.sendToClient(new PacketNPCStatusUpdate(npc, status, this.status.get(npc).get(status)), player);
        }
    }

    public void sync(Level world) {
        Player player = getPlayer(world);
        if (player != null) {
            //TODO PenguinNetwork.sendToClient(new PacketSyncNPCStatuses(status), player);
        }
    }
}
