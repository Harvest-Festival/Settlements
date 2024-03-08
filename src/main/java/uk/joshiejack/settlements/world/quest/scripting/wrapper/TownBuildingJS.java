package uk.joshiejack.settlements.world.quest.scripting.wrapper;

//public class TownBuildingJS extends AbstractJS<TownBuilding> {
//    public TownBuildingJS(TownBuilding location) {
//        super(location);
//    }
//
//    public boolean is(String building) {
//        return penguinScriptingObject.getBuilding().getRegistryName().toString().equals(building);
//    }
//
//    public List<PositionJS> waypointsByPrefix(String name) {
//        List<BlockPos> list = penguinScriptingObject.getWaypointsByPrefix(name);
//        List<PositionJS> positions = Lists.newArrayList();
//        list.forEach(p -> positions.add(WrapperRegistry.wrap(p)));
//        return positions;
//    }
//
//    public Rotation rotation() {
//        return penguinScriptingObject.getRotation();
//    }
//
//    public PositionJS pos() {
//        return WrapperRegistry.wrap(penguinScriptingObject.getPosition());
//    }
//}
