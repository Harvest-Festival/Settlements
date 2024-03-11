package uk.joshiejack.settlements.world.entity.ai.action.move;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.PhysicalAction;

public class MoveAction extends PhysicalAction {
    private BlockPos target;
    private double speed;

    public MoveAction() {}
    public MoveAction(BlockPos target, double speed) {
        this.target = target;
        this.speed = speed;
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
