package uk.joshiejack.settlements.world.entity.ai.action.move;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.PhysicalAction;

//TODO: @PenguinLoader("teleport")
public class TeleportAction extends PhysicalAction {
    private BlockPos target;

    public TeleportAction() {}
    public TeleportAction(BlockPos target) {
        this.target = target;
    }

    @Override
    public InteractionResult execute(NPCMob npc) {
        npc.setPos(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
        return InteractionResult.SUCCESS;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("Target", target.asLong());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.target = BlockPos.of(tag.getLong("Target"));
    }
}
