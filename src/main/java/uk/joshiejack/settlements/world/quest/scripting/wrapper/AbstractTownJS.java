package uk.joshiejack.settlements.world.quest.scripting.wrapper;

//public abstract class AbstractTownJS<T extends Town<?>> extends AbstractJS<T> {
//    public AbstractTownJS(T town) {
//        super(town);
//    }
//
//    public int population() {
//        return penguinScriptingObject.getCensus().population();
//    }
//
//    public int id() {
//        return penguinScriptingObject.getID();
//    }
//
//    public boolean hasBuilding(String building) {
//        Building theBuilding = Building.REGISTRY.get(new ResourceLocation(building));
//        return theBuilding != Building.NULL && penguinScriptingObject.getLandRegistry().getBuildingCount(theBuilding) > 0;
//    }
//
//    public TownBuildingJS getClosestBuilding(WorldJS<?> worldWrapper, PositionJS pos) {
//        return WrapperRegistry.wrap(penguinScriptingObject.getLandRegistry().getClosestBuilding(worldWrapper.penguinScriptingObject, pos.penguinScriptingObject));
//    }
//
//    public void destroy(WorldJS<?> worldWrapper, TownBuildingJS buildingW, String demolish) {
//        Building buildingToBuild = Building.REGISTRY.get(new ResourceLocation(demolish));
//        if (buildingToBuild != Building.NULL) {
//            TownBuilding building = buildingW.penguinScriptingObject;
//            buildingToBuild.getTemplate().removeBlocks(worldWrapper.penguinScriptingObject, building.getPosition(), building.getRotation(), Blocks.AIR.getDefaultState(), false);
//        }
//    }
//
//    public void build(WorldJS<?> worldWrapper, TownBuildingJS buildingW, String upgrade) {
//        Building buildingToBuild = Building.REGISTRY.get(new ResourceLocation(upgrade));
//        if (buildingToBuild != Building.NULL) {
//            TownBuilding building = buildingW.penguinScriptingObject;
//            buildingToBuild.getTemplate().placeBlocks(worldWrapper.penguinScriptingObject, building.getPosition(), building.getRotation());
//        }
//    }
//}
