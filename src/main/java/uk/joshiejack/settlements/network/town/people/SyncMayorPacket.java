package uk.joshiejack.settlements.network.town.people;

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
import uk.joshiejack.settlements.world.level.town.Town;

@Packet(PacketFlow.CLIENTBOUND)
public record SyncMayorPacket(ResourceKey<Level> dimension, int townID, Component mayor) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("sync_mayor");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public SyncMayorPacket(FriendlyByteBuf buf) {
        this(buf.readResourceKey(Registries.DIMENSION), buf.readInt(), buf.readComponent());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
        buf.writeInt(townID);
        buf.writeComponent(mayor);
    }

    @Override
    public void handleClient() {
        Town<?> town = WorldMap.getTownByID(dimension, townID);
        if (town != null) {
            town.getCharter().setMayorString(mayor);
        }
    }
}
