package uk.joshiejack.settlements.world.entity.ai.action.move;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.ActionPhysical;

//@PenguinLoader("wait")
public class ActionWait extends ActionPhysical {
    private int ticks;
    private int targetTicks;

    @Override
    public ActionWait withData(Object... data) {
        this.targetTicks = (Integer) data[0] * 20;
        return this;
    }

    @Override
    public InteractionResult execute(NPCMob npc) {
        if (ticks < targetTicks) {
            //TODO: Find clear path npc.getNavigator().clearPath();
            ticks++;
            return InteractionResult.PASS;
        } else return InteractionResult.SUCCESS;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Target", targetTicks);
        tag.putInt("Ticks", ticks);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.targetTicks = tag.getInt("Target");
        this.ticks = tag.getInt("Ticks");
    }
}
