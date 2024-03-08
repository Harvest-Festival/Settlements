package uk.joshiejack.settlements.network.npc;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.settlements.world.entity.EntityNPC;
import uk.joshiejack.settlements.world.entity.ai.action.chat.ActionAsk;

@Packet(PacketFlow.CLIENTBOUND)
public class PacketAsk extends PacketButtonLoad<ActionAsk> {
    public static final ResourceLocation ID = PenguinLib.prefix("ask_question");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public PacketAsk(FriendlyByteBuf buf) {
        super(buf);
    }

    public PacketAsk(Player player, EntityNPC npc, ActionAsk action) {
        super(player, npc, action);
    }

    @Override
    public void handle(Player player) {
        super.handle(player);
        Entity entity = player.level().getEntity(npcID);
        if (entity instanceof EntityNPC) {
            //TODO: Minecraft.getInstance().displayGuiScreen(new GuiNPCAsk((EntityNPC)entity, action.registryName, action.isQuest, action.question, action.answers, action.formatting));
        }
    }
}
