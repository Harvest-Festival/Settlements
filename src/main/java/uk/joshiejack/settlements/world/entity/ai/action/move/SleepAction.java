package uk.joshiejack.settlements.world.entity.ai.action.move;

import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.PhysicalAction;

import java.util.Set;

public class SleepAction extends PhysicalAction {
    private final Set<BlockPos> searched = Sets.newHashSet();
    private Direction facing;

    public SleepAction() {}
    public SleepAction(Direction facing) {
        this.facing = facing;
    }

    @Override
    public InteractionResult execute(NPCMob npc) {
        // Find the closest bed for this npc
        // Teleport them there and make them sleep in it


        npc.setAnimation("sleep", facing);
        return InteractionResult.SUCCESS;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Facing", facing.getSerializedName());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        facing = Direction.byName(tag.getString("Facing"));
    }
}
