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
import uk.joshiejack.settlements.world.entity.ai.action.chat.ActionAsk;

@Packet(PacketFlow.SERVERBOUND)
public record PacketAnswer(int npcID, ResourceLocation registryName, boolean isQuest, int option) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("answer_question");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public PacketAnswer(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readResourceLocation(), buf.readBoolean(), buf.readByte());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(npcID);
        buf.writeResourceLocation(registryName);
        buf.writeBoolean(isQuest);
        buf.writeByte(option);
    }

    @Override
    public void handle(Player player) {
        Entity entity = player.level().getEntity(npcID);
        if (entity instanceof EntityNPC) {
            Action action = ((EntityNPC) entity).getMentalAI().getCurrent();
            (((EntityNPC)entity)).removeTalking(player);
            if (action instanceof ActionAsk) {
                ((ActionAsk)action).onGuiClosed(player, (EntityNPC) entity, option);
            }
        }
    }
}
