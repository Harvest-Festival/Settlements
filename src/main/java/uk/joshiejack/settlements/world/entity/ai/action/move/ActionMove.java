package uk.joshiejack.settlements.world.entity.ai.action.move;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import uk.joshiejack.penguinlib.scripting.wrapper.WrapperRegistry;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.ActionPhysical;

//TODO @PenguinLoader("move")
public class ActionMove extends ActionPhysical {
    private BlockPos target;
    private double speed;

    @Override
    public ActionMove withData(Object... params) {
        this.target = WrapperRegistry.unwrap(params[0]);
        this.speed = (double) params[1];
        return this;
    }

    @Override
    public InteractionResult execute(NPCMob npc) {
        npc.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, speed);
        if (npc.distanceToSqr(target.getX() + 0.5, target.getY(), target.getZ() + 0.5) < 1) {
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("Target", target.asLong());
        tag.putDouble("Speed", speed);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.target = BlockPos.of(tag.getLong("Target"));
        this.speed = tag.getDouble("Speed");
    }
}
