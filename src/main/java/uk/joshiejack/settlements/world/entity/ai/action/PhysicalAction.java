package uk.joshiejack.settlements.world.entity.ai.action;

public abstract class PhysicalAction extends Action {
    @Override
    public AIType getAIType() {
        return AIType.PHYSICAL;
    }
}
