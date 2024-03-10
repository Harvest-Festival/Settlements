package uk.joshiejack.settlements.world.entity.ai;

import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.goal.Goal;
import net.neoforged.neoforge.common.util.INBTSerializable;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.Action;

import java.util.Deque;
import java.util.EnumSet;
import java.util.LinkedList;

public class EntityAIActionQueue extends Goal implements INBTSerializable<ListTag> {
    private final Deque<Action> queue = new LinkedList<>();
    private final NPCMob npc;
    private Action current;

    public EntityAIActionQueue(NPCMob npc, Flag flag) {
        this.npc = npc;
        this.setFlags(EnumSet.of(flag));
        //this.setMutexBits(mutex);
    }

    public Deque<Action> all() {
        return queue;
    }

    public Action getCurrent() {
        return current;
    }

    public void addToEnd(Action action) {
        queue.addLast(action);
    }

    public void addToEnd(LinkedList<Action> object) {
        object.forEach(queue::addLast); //Add to the end?
    }

    public void addToHead(Action action) {
        queue.addFirst(action);
    }

    public void addToHead(LinkedList<Action> object) {
        Lists.reverse(object).forEach(queue::addFirst); //Reverse the list to add it in the correct order at the head of the queue
        current = null; //Clear out the head of the queue
    }

    @Override
    public boolean canUse() {
        return current != null || !queue.isEmpty();
    }

    @Override
    public void tick() {
        if (current == null) current = queue.peek();
        if (current != null) {
            InteractionResult result = current.execute(npc); //Execute the task
            if (result == InteractionResult.FAIL) queue.clear();
            else if (result == InteractionResult.SUCCESS) {
                queue.remove(); //Remove the head of the queue
                current = queue.peek(); //Grab the new head of the queue
            }
        }
    }

    @Override
    public void start() {
        //TODO: Clear Path? npc.getNavigator().clearPath();
    }

    @Override
    public ListTag serializeNBT() {
        ListTag list = new ListTag();
        queue.forEach(action -> {
            CompoundTag tag = new CompoundTag();
            tag.putString("Type", action.getType());
            CompoundTag data = action.serializeNBT();
            if (data != null)
                tag.put("Data", data);
            list.add(tag);
        });

        return list;
    }

    @Override
    public void deserializeNBT(ListTag list) {
        CompoundTag EMPTY = new CompoundTag();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            Action action = tag.contains("Data") ? Action.createOfType(tag.getString("Type"), tag.getCompound("Data")) :
                    Action.createOfType(tag.getString("Type"), EMPTY);
            queue.add(action); //This might help ;)
        }
    }
}
