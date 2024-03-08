package uk.joshiejack.settlements.world.entity.ai.action.move;

import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import uk.joshiejack.settlements.world.entity.EntityNPC;
import uk.joshiejack.settlements.world.entity.ai.action.ActionPhysical;

import java.util.Set;

//@PenguinLoader("sleep")
public class ActionSleep extends ActionPhysical {
    private final Set<BlockPos> searched = Sets.newHashSet();
    private Direction facing;

    @Override
    public ActionSleep withData(Object... params) {
        this.facing = (Direction) params[0];
        return this;
    }

    @Override
    public InteractionResult execute(EntityNPC npc) {
        // Find the closest bed for this npc
        // Teleport them there and make them sleep in it


        npc.setAnimation("sleep", facing);
        return InteractionResult.SUCCESS;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        //TODO:tag.putByte("Facing", (byte) facing.getIndex());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        //TODO: this.facing = EnumFacing.getFront(tag.getByte("Facing")); //TODFO WRONG
    }
}
