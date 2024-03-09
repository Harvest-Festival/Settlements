package uk.joshiejack.settlements.network.npc;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.chat.ActionGreet;

@SuppressWarnings("unused")
@Packet(PacketFlow.CLIENTBOUND)
public class PacketGreet extends PacketButtonLoad<ActionGreet> {
    public static final ResourceLocation ID = PenguinLib.prefix("greet");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public PacketGreet(FriendlyByteBuf buf) {
        super(buf);
    }
    public PacketGreet(Player player, NPCMob npc, ActionGreet action) {
        super(player, npc, action);
    }

    @Override
    public void handle(Player player) {
        super.handle(player);
        Entity entity = player.level().getEntity(npcID);
        if (entity instanceof NPCMob npc) {
            //TODOD Minecraft.getInstance().displayGuiScreen(new GuiNPCChat(npc, new Chatter(npc.getInfo().getGreeting(player.world.rand)), player.getDisplayNameString(), entity.getName()));
        }
    }
}
