package uk.joshiejack.settlements.world.entity.ai.action.move;

import net.minecraft.world.InteractionResult;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.PhysicalAction;

//TODO: @PenguinLoader("wakeup")
public class WakeupAction extends PhysicalAction {
    @Override
    public InteractionResult execute(NPCMob npc) {
        npc.setAnimation("wakeup");
        return InteractionResult.SUCCESS;
    }
}
