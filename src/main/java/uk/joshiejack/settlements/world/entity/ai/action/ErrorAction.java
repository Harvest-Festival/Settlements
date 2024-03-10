package uk.joshiejack.settlements.world.entity.ai.action;

import net.minecraft.world.InteractionResult;
import uk.joshiejack.settlements.world.entity.NPCMob;

//TODO@PenguinLoader("error")
public class ErrorAction extends ActionMental {
    public static final Action INSTANCE = new ErrorAction();

    @Override
    public InteractionResult execute(NPCMob npc) {
        return InteractionResult.FAIL;
    }
}
