package uk.joshiejack.settlements.world.level;

import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.settlements.world.entity.npc.status.StatusTracker;
import uk.joshiejack.settlements.world.quest.QuestHelper;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;


public class StatusSavedData extends SavedData {
    public static final String DATA_NAME = "settlements_status";
    private final Map<UUID, StatusTracker> status = Maps.newHashMap();

    public static StatusSavedData get(ServerLevel world) {
        return world.getServer().overworld().getDataStorage().computeIfAbsent(new Factory<>(StatusSavedData::new, StatusSavedData::load), DATA_NAME);
    }

    public Collection<StatusTracker> getStatusTrackers() {
        return status.values();
    }

    public StatusTracker getStatusTracker(Player player) {
        UUID playerID = player.getUUID();
        if (!status.containsKey(playerID)) {
            status.put(playerID, new StatusTracker(playerID)); //Create a new map!
            setDirty(); //Save the new value
        }

        return status.get(playerID);
    }

    private static StatusSavedData load(CompoundTag compound) {
        StatusSavedData data = new StatusSavedData();
        QuestHelper.readUUIDToStatusTrackerMap(compound.getList("Statuses", 10), data.status);
        return data;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compound) {
        compound.put("Statuses", QuestHelper.writeUUIDToTrackerMap(status));
        return compound;
    }


    public void sync(Player player) {
        getStatusTracker(player).sync(player.level());
    }
}
