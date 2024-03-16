package uk.joshiejack.settlements.network.town.people;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.World;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.network.PenguinPacket;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.PenguinLoader;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.settlements.AdventureDataLoader;
import uk.joshiejack.settlements.world.level.TownSavedData;
import uk.joshiejack.settlements.world.level.town.TownServer;
import uk.joshiejack.settlements.world.town.TownServer;

import java.util.Objects;

@Packet(PacketFlow.SERVERBOUND)
public record PacketRequestCustomNPCS(ResourceKey<Level> dimension) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("request_custom_npcs");
    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public PacketRequestCustomNPCS(FriendlyByteBuf buf) {
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
            for (TownServer town : TownSavedData.get(level).getTowns(level)) {
                PenguinNetwork.sendToClient(new PacketSyncCustomNPCs(dimension, town.getID(), town.getCensus().getCustomNPCs()), player);
            }
        }
    }
}
