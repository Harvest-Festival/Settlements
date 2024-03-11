package uk.joshiejack.settlements.world.entity.ai.action;

public abstract class LookAction extends Action {
    @Override
    public AIType getAIType() {
        return AIType.LOOK;
    }
}
