package uk.joshiejack.settlements.world.entity.ai.action.chat;

import net.minecraft.world.InteractionResult;
import uk.joshiejack.settlements.world.entity.NPCMob;

public class LookAction extends uk.joshiejack.settlements.world.entity.ai.action.LookAction {
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
