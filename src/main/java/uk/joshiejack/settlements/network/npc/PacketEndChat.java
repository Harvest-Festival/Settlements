package uk.joshiejack.settlements.network.npc;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.settlements.world.entity.EntityNPC;
import uk.joshiejack.settlements.world.entity.ai.action.Action;
import uk.joshiejack.settlements.world.entity.ai.action.ActionChat;

@Packet(PacketFlow.SERVERBOUND)
public record PacketEndChat(int npcID) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("end_chat");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public PacketEndChat(FriendlyByteBuf buf) {
        this(buf.readInt());
    }
    public PacketEndChat(int npcID) {
        this.npcID = npcID;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(npcID);
    }

    @Override
    public void handle(Player player) {
        Entity entity = player.level().getEntity(npcID);
        if (entity instanceof EntityNPC npc) {
            Action action = npc.getMentalAI().getCurrent();
            if (action instanceof ActionChat chat) {
                npc.removeTalking(player);
                chat.onGuiClosed(player, (EntityNPC) entity);
            }
        }
    }
}
