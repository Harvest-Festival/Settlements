package uk.joshiejack.settlements.world.item;

import net.minecraft.world.item.Item;

public class DestroyBuildingItem extends Item {
    public DestroyBuildingItem(Item.Properties properties) {
        super(properties);
    }

//    @Override
//    @Nonnull
//    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
//        ItemStack stack = player.getHeldItem(hand);
//        if (!world.isRemote) {
//            BuildingPlacement placement = BuildingPlacement.onActivated(Building.NULL, player);
//            if (placement != null && placement.getMode() == BuildingPlacement.BuildingPlacementMode.PLACED) {
//                TownServer town = TownFinder.getFinder(world).findOrCreate(player, placement.getPosition());
//                EntityNPC npc = town.getCensus().getSpawner().byOccupation(world, "builder", placement.getPosition());
//                if (npc != null) {
//                    npc.getPhysicalAI().addToEnd(new ActionDestroy(placement.toTownBuilding()));
//                    npc.getPhysicalAI().addToEnd(new ActionGiftItem().withPlayer(player).withData(AdventureItems.BLUEPRINT.getStackFromResource(placement.toTownBuilding().getBuilding().getRegistryName())));
//                }
//
//
//                stack.splitStack(1);
//            }
//
//            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
//        }
//
//        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
//    }
}
