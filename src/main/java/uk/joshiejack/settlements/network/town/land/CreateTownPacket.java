package uk.joshiejack.settlements.network.town.land;

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
import uk.joshiejack.settlements.client.town.TownClient;
import uk.joshiejack.settlements.world.level.town.Town;

import java.util.Objects;

@Packet(PacketFlow.CLIENTBOUND)
public record CreateTownPacket(ResourceKey<Level> dimension, Town<?> town) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("create_town");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public CreateTownPacket(FriendlyByteBuf buf) {
        this(buf.readResourceKey(Registries.DIMENSION), new TownClient(buf.readInt(), buf.readBlockPos()));
        town.deserializeNBT(Objects.requireNonNull(buf.readNbt()));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
        buf.writeInt(town.getID());
        buf.writeBlockPos(town.getCentre());
        buf.writeNbt(town.getTagForSync());
    }

    @Override
    public void handleClient() {
        WorldMap.addTown(dimension, (TownClient) town);
    }
}
