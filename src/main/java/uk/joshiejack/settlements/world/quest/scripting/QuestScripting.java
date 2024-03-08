package uk.joshiejack.settlements.world.quest.scripting;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.penguinlib.event.ScriptingEvents;
import uk.joshiejack.penguinlib.scripting.wrapper.LevelJS;
import uk.joshiejack.penguinlib.scripting.wrapper.PlayerJS;
import uk.joshiejack.penguinlib.scripting.wrapper.TeamJS;
import uk.joshiejack.settlements.world.level.QuestSavedData;
import uk.joshiejack.settlements.world.quest.Quest;
import uk.joshiejack.settlements.world.quest.data.QuestTracker;
import uk.joshiejack.settlements.world.quest.scripting.wrapper.QuestJS;
import uk.joshiejack.settlements.world.quest.scripting.wrapper.QuestTrackerJS;

@Mod.EventBusSubscriber(modid = Settlements.MODID)
public class QuestScripting {
    @SubscribeEvent
    public static void onCollectWrappers(ScriptingEvents.CollectWrapper event) {
//        event.registerExtensible(EntityNPCJS.class, EntityNPC.class).setDynamic().setSided();
//        event.register(NPCStatusJS.class, ResourceLocation.class).disable();
//        event.register(NPCTaskJS.class, NPCTaskJS.TaskList.class);
        event.register(QuestTrackerJS.class, QuestTracker.class);
        event.register(QuestJS.class, Quest.class);
//        event.register(TownBuildingJS.class, TownBuilding.class);
//        event.register(TownClientJS.class, TownClient.class);
//        event.register(TownServerJS.class, TownServer.class);
    }

    @SubscribeEvent
    public static void onCollectScriptingMethods(ScriptingEvents.CollectMethod event) {
        event.add("display");
        event.add("onRightClickedNPC");
        event.add("onNPCSpawned");
    }

    @SubscribeEvent
    public static void onCollectScriptingFunctions(ScriptingEvents.CollectGlobalVarsAndFunctions event) {
        event.registerVar("settlements", Impl.INSTANCE);
    }

    public static class Impl {
        public static Impl INSTANCE = new Impl();

//        public void createChild(PlayerJS playerJS) {
//            CommandNPCGenerator.createEntityWithClassAndName(playerJS.penguinScriptingObject, "child", null);
//        }

//        public ItemStackJS building(String building) {
//            return WrapperRegistry.wrap(AdventureItems.BUILDING.getStackFromResource(new ResourceLocation(building)));
//        }
//
//        public ItemStackJS blueprint(String blueprint) {
//            return WrapperRegistry.wrap(AdventureItems.BLUEPRINT.getStackFromResource(new ResourceLocation(blueprint)));
//        }
//
//        public ItemStackJS getNPCIcon(String npc) {
//            return WrapperRegistry.wrap(AdventureItems.NPC_SPAWNER.getStackFromResource(new ResourceLocation(npc)));
//        }
//
//        public EntityNPCJS getNearbyNPC(PlayerJS player) {
//            return getNearbyNPC(player, null);
//        }
//
//        public EntityNPCJS getNearbyNPC(PlayerJS player, String name) {
//            return getNPC((WorldServerJS) player.world(), name, player.pos().x(), player.pos().y(), player.pos().z(), 64);
//        }
//
//        public EntityNPCJS getOrSpawnNPCAt(PlayerJS playerW, PositionJS posW, String npc) {
//            EntityNPCJS entity = getNearbyNPC(playerW, npc);
//            if (entity == null)
//                entity = ((TownServerJS) TownScripting.find((WorldServerJS) playerW.world(), posW)).spawn_at(playerW.world(), posW, npc);
//            return entity; //Return the entity
//        }
//
//        public EntityNPCJS getNPC(WorldServerJS world, @Nullable String name, int x, int y, int z, int distance) {
//            ResourceLocation npc = name == null ? NPC.NULL_NPC.getRegistryName() : new ResourceLocation(name);
//            List<EntityNPC> entity = world.penguinScriptingObject.getEntitiesWithinAABB(EntityNPC.class, new AxisAlignedBB(x - 0.5F, y - 0.5F, z - 0.5F, x + 0.5F, y + 0.5F, z + 0.5F).expand(distance, distance, distance));
//            for (EntityNPC e : entity) {
//                if (npc.equals(NPC.NULL_NPC.getRegistryName()) || e.getInfo().getRegistryName().equals(npc))
//                    return WrapperRegistry.wrap(e);
//            }
//
//            return null;
//        }
//
//        private static NPCStatusJS[] statuses;
//
//        public NPCStatusJS[] statuses(WorldJS<?> world) {
//            if (statuses == null) {
//                List<NPCStatusJS> list = Lists.newArrayList();
//                NPC.all().forEach(n -> list.add(WrapperRegistry.wrap(n.getRegistryName())));
//                TownFinder.all((World) world.penguinScriptingObject)
//                        .forEach(t -> t.getCensus().getCustomNPCKeys().forEach(k -> list.add(WrapperRegistry.wrap(k))));
//                statuses = list.toArray(new NPCStatusJS[0]);
//            }
//
//            return statuses;
//        }
//
//        public NPCStatusJS status(String name) {
//            return WrapperRegistry.wrap(new ResourceLocation(name));
//        }

        public QuestTrackerJS quests(LevelJS<?> worldJS) {
            return new QuestTrackerJS(QuestSavedData.get((ServerLevel) worldJS.penguinScriptingObject).getServerTracker());
        }

        public QuestTrackerJS quests(PlayerJS player) {
            return new QuestTrackerJS(QuestSavedData.get((ServerLevel) player.penguinScriptingObject.level()).getPlayerTracker(player.penguinScriptingObject));
        }

        public QuestTrackerJS quests(LevelJS<?> worldJS, TeamJS team) {
            return new QuestTrackerJS(QuestSavedData.get((ServerLevel) worldJS.penguinScriptingObject).getTeamTracker(team.penguinScriptingObject.getID()));
        }
    }
}
