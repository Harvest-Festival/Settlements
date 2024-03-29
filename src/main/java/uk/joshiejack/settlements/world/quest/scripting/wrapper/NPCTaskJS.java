package uk.joshiejack.settlements.world.quest.scripting.wrapper;

//public class NPCTaskJS extends AbstractJS<NPCTaskJS.TaskList> {
//    private final EntityNPCJS activeNPC;
//    private PlayerJS activePlayer;
//    private ResourceLocation activeQuest;
//
//    public NPCTaskJS(LinkedList<Action> mentalList, LinkedList<Action> physicalList, EntityNPCJS npc) {
//        super(new TaskList(mentalList, physicalList));
//        this.activeNPC = npc;
//    }
//
//    public NPCTaskJS start(ResourceLocation quest, PlayerJS player) {
//        this.activeQuest = quest;
//        this.activePlayer = player;
//        return this;
//    }
//
//    public NPCTaskJS add(String type, Object... params) {
//        TaskList object = penguinScriptingObject;
//        if (object != null && activePlayer != null && activeQuest != null) {
//            ResourceLocation registryName = new ResourceLocation(activeQuest.getResourceDomain().equals("quest") ? activeQuest.getResourcePath() : activeQuest.toString());
//            boolean isQuest = activeQuest.getResourceDomain().contains("quest");
//            Action a = Action.createOfType(type)
//                    .withPlayer(activePlayer.penguinScriptingObject)
//                    .withScript(registryName, isQuest).withData(params);
//            if (a.isPhysical()) activeNPC.penguinScriptingObject.getPhysicalAI().addToEnd(a);
//            else activeNPC.penguinScriptingObject.getMentalAI().addToEnd(a);
//        }
//
//        return this;
//    }
//
//    public static class TaskList {
//        final LinkedList<Action> physicalList;
//        final LinkedList<Action> mentalList;
//
//        TaskList(LinkedList<Action> mentalList, LinkedList<Action> physicalList) {
//            this.mentalList = mentalList;
//            this.physicalList = physicalList;
//        }
//
//        void clear() {
//            physicalList.clear();
//            mentalList.clear();
//        }
//
//        void add(Action action) {
//            if (action.isPhysical()) {
//                physicalList.add(action);
//            } else mentalList.add(action);
//        }
//    }
//}
