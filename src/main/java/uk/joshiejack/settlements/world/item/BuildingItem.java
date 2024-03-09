package uk.joshiejack.settlements.world.item;

import net.minecraft.world.item.Item;
import uk.joshiejack.penguinlib.world.item.PenguinRegistryItem;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.building.Building;

public class BuildingItem extends PenguinRegistryItem<Building> {
    public BuildingItem(Item.Properties properties) {
        super(Settlements.Registries.BUILDINGS,"Building", properties);
    }
//
//    @Override
//    protected Building getNullEntry() {
//        return Building.NULL;
//    }
//
//    @Override
//    @Nonnull
//    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, EntityPlayer player, @Nonnull EnumHand hand) {
//        ItemStack stack = player.getHeldItem(hand);
//        Building building = getObjectFromStack(stack);
//        if (building != null) {
//            if (!world.isRemote) {
//                Template template = building.getTemplate();
//                BuildingPlacement placement = BuildingPlacement.onActivated(building, player);
//                if (placement != null && placement.getMode() == BuildingPlacement.BuildingPlacementMode.PLACED) {
//                    Town<?> town = TownFinder.getFinder(world).findOrCreate(player, placement.getPosition());
//                    TownBuilding townBuilding = placement.toTownBuilding().setBuilt();
//                    town.getLandRegistry().addBuilding(world, townBuilding);
//                    PenguinNetwork.sendToEveryone(new PacketAddBuilding(world.provider.getDimension(), town.getID(), townBuilding));
//                    template.placeBlocks(world, placement.getPosition(), placement.getRotation()); //Place the blocks AFTER creating the town
//                    stack.splitStack(1);
//                }
//
//                return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
//            }
//        }
//
//        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
//    }
}
