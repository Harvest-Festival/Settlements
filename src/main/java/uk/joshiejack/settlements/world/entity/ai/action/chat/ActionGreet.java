package uk.joshiejack.settlements.world.entity.ai.action.chat;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.settlements.network.npc.PacketGreet;
import uk.joshiejack.settlements.world.entity.EntityNPC;
import uk.joshiejack.settlements.world.entity.ai.action.ActionChat;
import uk.joshiejack.settlements.world.entity.ai.action.ActionMental;

//TODO: Register @PenguinLoader("greet")
public class ActionGreet extends ActionMental implements ActionChat {
    private boolean read;
    private boolean displayed;

    @Override
    public void onGuiClosed(Player player, EntityNPC npc, Object... parameters) {
        read = true;
    }

    @Override
    public InteractionResult execute(EntityNPC npc) {
        if (!displayed && player != null) {
            displayed = true; //Marked it as displayed
            npc.addTalking(player); //Add the talking
            PenguinNetwork.sendToClient(player, new PacketGreet(player, npc, this));
        }

        return player == null || npc.IsNotTalkingTo(player) || read ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
}
