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
import uk.joshiejack.penguinlib.world.team.PenguinTeam;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;
import uk.joshiejack.settlements.world.level.TownSavedData;
import uk.joshiejack.settlements.world.level.town.TownServer;

@Packet(PacketFlow.CLIENTBOUND)
public record ToggleCitizenshipServerPacket(ResourceKey<Level> dimension, int townID) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("toggle_citizenship_server");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public ToggleCitizenshipServerPacket(FriendlyByteBuf buf) {
        this(buf.readResourceKey(Registries.DIMENSION), buf.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
        buf.writeInt(townID);
    }

    @Override
    public void handleServer(ServerPlayer player) {
        ServerLevel level = player.serverLevel().getServer().getLevel(dimension);
        if (level == null) return; //If the level is null, then we can't do anything
        TownServer town = TownSavedData.get(level).getTownByID(townID);
        PenguinTeam team = PenguinTeams.getTeamForPlayer(player);
        if (town.getCharter().getTeamID().equals(team.getID())) {
            town.getGovernment().toggleCitizenship();
            TownSavedData.get(player.serverLevel()).setDirty();
            PenguinNetwork.sendToEveryone(new ToggleCitizenshipClientPacket(dimension, townID));
        }
    }
}
