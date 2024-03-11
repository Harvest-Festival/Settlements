package uk.joshiejack.settlements.scripting.wrapper;

import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import uk.joshiejack.penguinlib.scripting.wrapper.AbstractJS;
import uk.joshiejack.penguinlib.scripting.wrapper.PlayerJS;
import uk.joshiejack.settlements.world.entity.ai.action.Action;
import uk.joshiejack.settlements.world.entity.ai.action.move.AttackAction;
import uk.joshiejack.settlements.world.entity.ai.action.move.MoveAction;

import java.util.LinkedList;

public class NPCTaskJS extends AbstractJS<NPCTaskJS.TaskList> {
    private final EntityNPCJS activeNPC;
    private PlayerJS activePlayer;
    private ResourceLocation activeQuest;

    public NPCTaskJS(LinkedList<Action> mentalList, LinkedList<Action> physicalList, EntityNPCJS npc) {
        super(new TaskList(mentalList, physicalList));
        this.activeNPC = npc;
    }

    public NPCTaskJS start(ResourceLocation quest, PlayerJS player) {
        this.activeQuest = quest;
        this.activePlayer = player;
        return this;
    }

    public NPCTaskJS attackPlayer(double speed) {
        return queueAction(new AttackAction(speed));
    }

    public NPCTaskJS moveTo(int x, int y, int z, double speed) {
        return queueAction(new MoveAction(new BlockPos(x, y, z), speed));
    }

    @HideFromJS
    public NPCTaskJS queueAction(Action action) {
        if (penguinScriptingObject == null || activePlayer == null || activeQuest == null) return this;
        ResourceLocation registryName = new ResourceLocation(activeQuest.getNamespace().equals("quest") ? activeQuest.getPath() : activeQuest.toString());
        boolean isQuest = activeQuest.getNamespace().contains("quest");
        action.withPlayer((ServerPlayer) activePlayer.get()).withScript(registryName, isQuest);
        if (action.isPhysical()) activeNPC.get().getPhysicalAI().addToEnd(action);
        else activeNPC.get().getMentalAI().addToEnd(action);
        return this;
    }

//    public NPCTaskJS add(String type, Object... params) {
//        if (penguinScriptingObject != null && activePlayer != null && activeQuest != null) {
//            ResourceLocation registryName = new ResourceLocation(activeQuest.getNamespace().equals("quest") ? activeQuest.getPath() : activeQuest.toString());
//            boolean isQuest = activeQuest.getNamespace().contains("quest");
//            Action a = Action.createOfType(type)
//                    .withPlayer((ServerPlayer) activePlayer.get())
//                    .withScript(registryName, isQuest).withData(params);
//            if (a.isPhysical()) activeNPC.get().getPhysicalAI().addToEnd(a);
//            else activeNPC.get().getMentalAI().addToEnd(a);
//        }
//
//        return this;
//    }

    public static class TaskList {
        final LinkedList<Action> physicalList;
        final LinkedList<Action> mentalList;

        TaskList(LinkedList<Action> mentalList, LinkedList<Action> physicalList) {
            this.mentalList = mentalList;
            this.physicalList = physicalList;
        }

        void clear() {
            physicalList.clear();
            mentalList.clear();
        }

        void add(Action action) {
            if (action.isPhysical()) {
                physicalList.add(action);
            } else mentalList.add(action);
        }
    }
}