package uk.joshiejack.settlements.world.entity.ai.action;

import net.minecraft.world.InteractionResult;
import uk.joshiejack.settlements.world.entity.NPCMob;

//TODO@PenguinLoader("error")
public class ActionError extends ActionMental {
    public static final Action INSTANCE = new ActionError();

    @Override
    public InteractionResult execute(NPCMob npc) {
        return InteractionResult.FAIL;
    }
}
