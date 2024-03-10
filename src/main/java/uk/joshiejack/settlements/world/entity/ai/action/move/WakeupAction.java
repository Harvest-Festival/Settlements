package uk.joshiejack.settlements.world.entity.ai.action.move;

import net.minecraft.world.InteractionResult;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.ActionPhysical;

//TODO: @PenguinLoader("wakeup")
public class WakeupAction extends ActionPhysical {
    @Override
    public InteractionResult execute(NPCMob npc) {
        npc.setAnimation("wakeup");
        return InteractionResult.SUCCESS;
    }
}
