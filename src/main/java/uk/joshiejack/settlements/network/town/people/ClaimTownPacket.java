package uk.joshiejack.settlements.network.town.people;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.penguinlib.world.team.PenguinTeam;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;
import uk.joshiejack.settlements.world.level.TownSavedData;
import uk.joshiejack.settlements.world.level.town.Town;

@Packet(PacketFlow.SERVERBOUND)
public record ClaimTownPacket(ResourceKey<Level> dimension, int townID) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("claim_town");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
    public ClaimTownPacket(FriendlyByteBuf buf) {
        this(buf.readResourceKey(Registries.DIMENSION), buf.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
        buf.writeInt(townID);
    }

    @Override
    public void handleServer(ServerPlayer player) {
        Town<?> town = TownSavedData.get(player.serverLevel()).getTownByID(dimension, townID);
        PenguinTeam owner = PenguinTeams.getTeamFromID(player.serverLevel(), town.getCharter().getTeamID());
        //If the team has no owner
        if (owner.getOwner() == null) {
            PenguinTeams.get(player.serverLevel()).changeTeam(player.serverLevel(), player.getUUID(), owner.getID());
        }
    }
}
