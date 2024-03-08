package uk.joshiejack.settlements.world.entity.ai.action;

import net.minecraft.world.InteractionResult;
import uk.joshiejack.settlements.world.entity.EntityNPC;

//TODO@PenguinLoader("error")
public class ActionError extends ActionMental {
    public static final Action INSTANCE = new ActionError();

    @Override
    public InteractionResult execute(EntityNPC npc) {
        return InteractionResult.FAIL;
    }
}