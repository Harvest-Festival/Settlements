package uk.joshiejack.settlements.world.entity.ai.action.chat;

import net.minecraft.world.InteractionResult;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.ActionMental;

//TODO@PenguinLoader("look")
public class ActionLook extends ActionMental {
    private int lookTimer;

    @Override
    public InteractionResult execute(NPCMob npc) {
        if (player != null) {
            npc.getLookControl().setLookAt(player.getX(), player.getY() + (double) player.getEyeHeight(), player.getZ(), (float) npc.getHeadRotSpeed(), (float) npc.getMaxHeadXRot());
        }

        lookTimer++;

        return lookTimer > 40? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
}
