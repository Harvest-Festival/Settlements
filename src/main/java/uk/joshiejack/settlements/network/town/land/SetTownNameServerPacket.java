package uk.joshiejack.settlements.network.town.land;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.penguinlib.world.team.PenguinTeam;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;
import uk.joshiejack.settlements.world.level.TownSavedData;
import uk.joshiejack.settlements.world.level.town.TownServer;

import java.util.UUID;

@Packet(PacketFlow.SERVERBOUND)
public record SetTownNameServerPacket(ResourceKey<Level> dimension, int townID, String name) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("set_town_name_server");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public SetTownNameServerPacket(FriendlyByteBuf buf) {
        this(buf.readResourceKey(Registries.DIMENSION), buf.readInt(), buf.readUtf());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
        buf.writeInt(townID);
        buf.writeUtf(name);
    }

    @Override
    public void handleServer(ServerPlayer player) {
        UUID playerUUID = player.getUUID();
        PenguinTeam team = PenguinTeams.getTeamFromID(player.serverLevel(), playerUUID);
        TownSavedData data = TownSavedData.get(player.serverLevel());
        TownServer town = data.getTownByID(dimension, townID);
        if (playerUUID.equals(team.getOwner())) { //Only the owner can kick
            town.getCharter().setName(Component.literal(name));
            TownSavedData.get(player.serverLevel()).setDirty();
            PenguinNetwork.sendToEveryone(new SetTownNameServerPacket(dimension, townID, name));
        }
    }
}
