package uk.joshiejack.settlements.world.entity.ai.action.chat;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import uk.joshiejack.settlements.event.NPCEvent;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.ActionChat;
import uk.joshiejack.settlements.world.entity.ai.action.MentalAction;

public class NextAction extends MentalAction implements ActionChat {
    @Override
    public InteractionResult execute(NPCMob npc) {
        NPCEvent.NPCRightClickedEvent event = new NPCEvent.NPCRightClickedEvent(npc, player, player.getUsedItemHand());
        return player == null ||
                !NeoForge.EVENT_BUS.post(event).isCanceled()
                ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @Override
    public void onGuiClosed(Player player, NPCMob npc, Object... parameters) {}
}
