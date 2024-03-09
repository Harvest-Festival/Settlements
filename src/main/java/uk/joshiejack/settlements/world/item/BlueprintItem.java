package uk.joshiejack.settlements.world.item;

import net.minecraft.world.item.Item;
import uk.joshiejack.penguinlib.world.item.PenguinRegistryItem;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.building.Building;

public class BlueprintItem extends PenguinRegistryItem<Building> {
    public BlueprintItem(Item.Properties properties) {
        super(Settlements.Registries.BUILDINGS,"Blueprint", properties);
    }
    /*
    @Override
    @Nonnull
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Building building = fromStack(stack);
        if (building != null) {
            if (!level.isClientSide) {
                BuildingPlacement placement = BuildingPlacement.onActivated(building, player);
                if (placement != null && placement.getMode() == BuildingPlacement.BuildingPlacementMode.PLACED) {
                    TownServer town = TownFinder.getFinder(world).findOrCreate(player, placement.getPosition());
                    EntityNPC npc = town.getCensus().getSpawner().byOccupation(world, "builder", placement.getOriginalPosition()); //Spawn the builder where you click
                    if (npc != null) {
                        TownBuilding townBuilding = placement.toTownBuilding();
                        town.getLandRegistry().addBuilding(world, townBuilding);
                        PenguinNetwork.sendToEveryone(new PacketAddBuilding(world.provider.getDimension(), town.getID(), townBuilding));
                        npc.getPhysicalAI().addToEnd(new ActionBuild(building, placement.getPosition(), placement.getRotation()));
                    }

                    stack.splitStack(1);
                }

                return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
            }
        }

        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
    } */
}
