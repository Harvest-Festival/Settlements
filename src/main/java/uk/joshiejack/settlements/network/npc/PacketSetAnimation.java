package uk.joshiejack.settlements.network.npc;

import net.minecraft.nbt.CompoundTag;
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

@Packet(PacketFlow.CLIENTBOUND)
public record PacketSetAnimation(int npcID, String animation, CompoundTag tag) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("set_animation");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public PacketSetAnimation(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readUtf(), buf.readNbt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(npcID);
        buf.writeUtf(animation);
        buf.writeNbt(tag);
    }

    @Override
    public void handle(Player player) {
        Entity entity = player.level().getEntity(npcID);
        if (entity instanceof EntityNPC) {
            ((EntityNPC)entity).setAnimation(animation, tag);
        }
    }
}
