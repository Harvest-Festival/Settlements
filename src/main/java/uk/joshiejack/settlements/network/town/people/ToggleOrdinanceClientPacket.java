package uk.joshiejack.settlements.network.town.people;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.settlements.client.WorldMap;
import uk.joshiejack.settlements.world.level.town.people.Ordinance;

@Packet(PacketFlow.CLIENTBOUND)
public record ToggleOrdinanceClientPacket(ResourceKey<Level> dimension, int townID, Ordinance ordinance, boolean enact) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("toggle_ordinance_client");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public ToggleOrdinanceClientPacket(FriendlyByteBuf buf) {
        this(buf.readResourceKey(Registries.DIMENSION), buf.readInt(), buf.readEnum(Ordinance.class), buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
        buf.writeInt(townID);
        buf.writeEnum(ordinance);
        buf.writeBoolean(enact);
    }

    @Override
    public void handleClient() {
        WorldMap.getTownByID(dimension, townID).getGovernment().setLaw(ordinance, enact);
    }
}
