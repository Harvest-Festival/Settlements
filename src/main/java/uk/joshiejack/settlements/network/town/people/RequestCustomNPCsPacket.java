package uk.joshiejack.settlements.network.town.people;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.settlements.world.level.TownSavedData;
import uk.joshiejack.settlements.world.level.town.TownServer;

import java.util.Objects;

@Packet(PacketFlow.SERVERBOUND)
public record RequestCustomNPCsPacket(ResourceKey<Level> dimension) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("request_custom_npcs");
    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public RequestCustomNPCsPacket(FriendlyByteBuf buf) {
        this(buf.readResourceKey(Registries.DIMENSION));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
    }

    @Override
    public void handleServer(ServerPlayer player) {
        ServerLevel level = Objects.requireNonNull(player.getServer()).getLevel(dimension);
        if (level != null) {
            for (TownServer town : TownSavedData.get(level).getTowns()) {
                PenguinNetwork.sendToClient(player, new SyncCustomNPCsPacket(dimension, town.getID(), town.getCensus().getCustomNPCs()));
            }
        }
    }
}
