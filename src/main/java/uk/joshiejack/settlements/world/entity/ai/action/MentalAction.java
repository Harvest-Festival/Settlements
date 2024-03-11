package uk.joshiejack.settlements.world.entity.ai.action;

public abstract class MentalAction extends Action {
    @Override
    public Action.AIType getAIType() {
        return AIType.MENTAL;
    }
}
