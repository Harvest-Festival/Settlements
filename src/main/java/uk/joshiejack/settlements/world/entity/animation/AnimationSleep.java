//package uk.joshiejack.settlements.world.entity.animation;
//
//import net.minecraft.core.Direction;
//import net.minecraft.nbt.CompoundTag;
//import uk.joshiejack.settlements.entity.EntityNPC;
//import uk.joshiejack.penguinlib.util.PenguinLoader;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.util.EnumFacing;
//import uk.joshiejack.settlements.world.entity.EntityNPC;
//
////@PenguinLoader("sleep") //TODO:
//public class AnimationSleep extends Animation {
//    private Direction facing;
//
//    public float getFacingInDegrees() {
//        return facing == Direction.SOUTH ? 90F : facing == Direction.EAST? 180F: facing == Direction.NORTH ? 270F: 0F;
//    }
//
//    @Override
//    public boolean renderLiving(EntityNPC npc, double x, double y, double z) {
//        GlStateManager.translate((float)x + (double) npc.renderOffsetX, y, (float) z + (double) npc.renderOffsetZ);
//        return true;
//    }
//
//    @Override
//    public boolean applyRotation(EntityNPC npc) {
//        GlStateManager.rotate(getFacingInDegrees(), 0.0F, 1.0F, 0.0F);
//        GlStateManager.rotate(90F, 0.0F, 0.0F, 1.0F);
//        GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
//        return true;
//    }
//
//    @Override
//    public Animation withData(Object... args) {
//        facing = (Direction) args[0];
//        return this;
//    }
//
//
//    @Override
//    public void play(EntityNPC npc) {
//        npc.renderOffsetX = -1.8F * (float) facing.getFrontOffsetX();
//        npc.renderOffsetZ = -1.8F * (float) facing.getFrontOffsetZ();
//    }
//
//
//    @Override
//    public CompoundTag serializeNBT() {
//        CompoundTag tag = new CompoundTag();
//        tag.setByte("Facing", (byte) facing.getIndex());
//        return tag;
//    }
//
//    @Override
//    public void deserializeNBT(NBTTagCompound tag) {
//        this.facing = EnumFacing.getFront(tag.getByte("Facing"));
//    }
//}
