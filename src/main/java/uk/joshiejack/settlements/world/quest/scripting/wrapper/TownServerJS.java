package uk.joshiejack.settlements.world.quest.scripting.wrapper;

//public class TownServerJS extends AbstractTownJS<TownServer> {
//    public TownServerJS(TownServer town) {
//        super(town);
//    }
//
//    @SuppressWarnings("unchecked")
//    @Nullable
//    public EntityNPCJS spawn_at(WorldJS<?> worldWrapper, PositionJS positionWrapper, String search_string) {
//        TownServer town = penguinScriptingObject;
//        World world = worldWrapper.penguinScriptingObject;
//        BlockPos pos = positionWrapper.penguinScriptingObject;
//        NPC npc = NPC.getNPCFromRegistry(new ResourceLocation(search_string));
//        return WrapperRegistry.wrap(search_string.contains(":") ? town.getCensus().getSpawner().getNPC(world,
//                npc, npc.getRegistryName(), null, pos) : town.getCensus().getSpawner().byOccupation(world, search_string, pos));
//    }
//
//    @SuppressWarnings("unchecked")
//    @Nullable
//    public EntityNPCJS spawn(WorldJS<?> worldWrapper, String search_string) {
//        Town<?> town = penguinScriptingObject;
//        return spawn_at(worldWrapper, WrapperRegistry.wrap(town.getCentre()), search_string);
//    }
//}
