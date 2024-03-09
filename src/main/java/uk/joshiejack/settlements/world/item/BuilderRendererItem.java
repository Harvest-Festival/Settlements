package uk.joshiejack.settlements.world.item;

import net.minecraft.world.item.Item;
import uk.joshiejack.penguinlib.world.item.PenguinRegistryItem;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.building.Building;

public class BuilderRendererItem extends PenguinRegistryItem<Building> {
    public BuilderRendererItem(Item.Properties properties) {
        super(Settlements.Registries.BUILDINGS,"Building", properties);
    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    public void registerModels() {
//        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Objects.requireNonNull(getRegistryName()), "inventory"));
//        this.setTileEntityItemStackRenderer(new BuildingItemRenderer());
//    }
//
//    @Override
//    protected Building getNullEntry() {
//        return Building.NULL;
//    }
}
