package uk.joshiejack.settlements.world.entity.ai.action.move;

import net.minecraft.world.InteractionResult;
import uk.joshiejack.settlements.world.entity.EntityNPC;
import uk.joshiejack.settlements.world.entity.ai.action.ActionPhysical;

//TODO: @PenguinLoader("wakeup")
public class ActionWakeup extends ActionPhysical {
    @Override
    public InteractionResult execute(EntityNPC npc) {
        npc.setAnimation("wakeup");
        return InteractionResult.SUCCESS;
    }
}
