package uk.joshiejack.settlements.world.entity.ai.action.tasks;

//@PenguinLoader("chop_tree")
//TODO:
/*
public class ActionChopTree extends ActionPhysical {
    private int timeTaken;
    private BlockPos tree;
    public ActionChopTree() {}

    @Override
    public InteractionResult execute(EntityNPC npc) {
        if (npc.level().getDayTime() %20 == 0) {
            //If we haven't found a tree, look for one nearby
            if (tree == null) {
                for (int i = 0; i < 256; i++) {
                    Vec3 target = RandomPositionGenerator.findRandomTarget(npc, 12, 8);
                    if (target != null && TreeTasks.findTree(npc.world, new BlockPos(target))
                            && !npc.world.getBlockState(new BlockPos(target).down()).getBlock().isWood(npc.level(), new BlockPos(target).down())) {
                        tree = new BlockPos(target);
                        npc.getNavigator().tryMoveToXYZ(target.x, target.y, target.z, 0.5F);
                        break;
                    }
                }
            } else {
                npc.getNavigator().tryMoveToXYZ(tree.getX(), tree.getY(), tree.getZ(), 0.5F);
                int y = tree.getY();
                while (npc.world.getBlockState(new BlockPos(tree.getX(), y, tree.getZ())).getBlock().isWood(npc.world, new BlockPos(tree.getX(), y, tree.getZ()))) {
                    y--;
                }

                if (tree.getDistance((int) npc.posX, y + 1, (int)npc.posZ) < 2) {
                    npc.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_AXE));
                    npc.swingArm(InteractionHand.MAIN_HAND);
                    NeoForge.EVENT_BUS.register(new TreeTasks.ChopTree(tree,
                            FakePlayerHelper.getFakePlayerWithPosition((ServerLevel) npc.world, tree), npc.getMainHandItem()));
                    tree = null;
                    return InteractionResult.SUCCESS;
                }

                if (timeTaken > 500) { //time_unit > half_hour
                    tree = null; //Reset if taking too long
                }

                timeTaken++;
            }
        }

        return EnumActionResult.PASS;
    }

    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {}
}
*/