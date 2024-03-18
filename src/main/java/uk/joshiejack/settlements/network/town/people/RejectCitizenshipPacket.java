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
import uk.joshiejack.settlements.world.level.town.Town;

import java.util.UUID;

@Packet(PacketFlow.SERVERBOUND)
public record RejectCitizenshipPacket(ResourceKey<Level> dimension, int townID, UUID member) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("reject_citizenship");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
    public RejectCitizenshipPacket(FriendlyByteBuf buf) {
        this(buf.readResourceKey(Registries.DIMENSION), buf.readInt(), buf.readUUID());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
        buf.writeInt(townID);
        buf.writeUUID(member);
    }

    @Override
    public void handleServer(ServerPlayer player) {
        ServerLevel level = player.serverLevel().getServer().getLevel(dimension);
        if (level == null) return; //If the level is null, then we can't do anything
        Town<?> town = TownSavedData.get(level).getTownByID(townID);
        PenguinTeam team = PenguinTeams.getTeamFromID(player.serverLevel(), member);
        if (team.getID().equals(town.getCharter().getTeamID())) { //Only the owner can kick
            town.getGovernment().getApplications().remove(member);
            PenguinNetwork.sendToTeam(player.serverLevel(), town.getCharter().getTeamID(), new SyncApplicationsPacket(dimension, townID, town.getGovernment().getApplications()));
        }
    }
}