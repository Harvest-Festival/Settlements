package uk.joshiejack.settlements.world.entity.ai.action;

import net.minecraft.world.entity.ai.goal.Goal;

public class EmptyGoal extends Goal {
    public static final EmptyGoal INSTANCE = new EmptyGoal();

    @Override
    public boolean canUse() {
        return false;
    }
}
