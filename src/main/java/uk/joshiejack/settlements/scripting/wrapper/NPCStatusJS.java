package uk.joshiejack.settlements.scripting.wrapper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import uk.joshiejack.penguinlib.scripting.wrapper.AbstractJS;
import uk.joshiejack.penguinlib.scripting.wrapper.LevelJS;
import uk.joshiejack.penguinlib.scripting.wrapper.PlayerJS;
import uk.joshiejack.settlements.world.entity.npc.status.StatusTracker;
import uk.joshiejack.settlements.world.level.StatusSavedData;

public class NPCStatusJS extends AbstractJS<ResourceLocation> {
    public NPCStatusJS(ResourceLocation npc) {
        super(npc);
    }

    public boolean is(LevelJS<?> worldJS, String status, int value) {
        StatusSavedData data = StatusSavedData.get((ServerLevel) worldJS.get());
        for (StatusTracker tracker: data.getStatusTrackers()) {
            if (tracker.get(penguinScriptingObject, status) == value) return true;
        }

        return false;
    }

    public int get(PlayerJS wrapper, String status) {
        return StatusSavedData.get((ServerLevel) wrapper.get().level()).getStatusTracker(wrapper.get()).get(penguinScriptingObject, status);
    }

    public void set(PlayerJS wrapper, String status, int value) {
        StatusSavedData data = StatusSavedData.get((ServerLevel) wrapper.get().level());
        data.getStatusTracker(wrapper.get()).set(wrapper.level().get(), penguinScriptingObject, status, value);
        data.setDirty();
    }

    public void adjust(PlayerJS wrapper, String status, int value) {
        StatusSavedData data = StatusSavedData.get((ServerLevel) wrapper.get().level());
        data.getStatusTracker(wrapper.get())
                .adjust(wrapper.level().get(), penguinScriptingObject, status, value);
        data.setDirty();
    }

    public void adjustWithRange(PlayerJS wrapper, String status, int value, int min, int max) {
        ResourceLocation object = penguinScriptingObject;
        StatusSavedData data = StatusSavedData.get((ServerLevel) wrapper.get().level());
        StatusTracker tracker = data.getStatusTracker(wrapper.get());
        tracker.adjust(wrapper.level().get(), object, status, value);
        tracker.set(wrapper.level().get(), object, status, Math.min(max, Math.max(min, tracker.get(object, status))));
        data.setDirty();
    }
}
