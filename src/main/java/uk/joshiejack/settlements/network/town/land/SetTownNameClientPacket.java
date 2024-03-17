package uk.joshiejack.settlements.network.town.land;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.settlements.client.WorldMap;

@Packet(PacketFlow.CLIENTBOUND)
public record SetTownNameClientPacket(ResourceKey<Level> dimension, int townID, String name) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("set_town_name_client");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public SetTownNameClientPacket(FriendlyByteBuf buf) {
        this(buf.readResourceKey(Registries.DIMENSION), buf.readInt(), buf.readUtf());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
        buf.writeInt(townID);
        buf.writeUtf(name);
    }

    @Override
    public void handleClient() {
        WorldMap.getTownByID(dimension, townID).getCharter().setName(Component.literal(name));
    }
}
