package uk.joshiejack.settlements.world.entity.ai.action;

public abstract class ActionMental extends Action {
    @Override
    public boolean isPhysical() {
        return false;
    }
}
