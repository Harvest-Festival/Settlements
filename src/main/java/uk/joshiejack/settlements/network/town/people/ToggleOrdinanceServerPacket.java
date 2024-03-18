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
import uk.joshiejack.settlements.world.level.town.people.Ordinance;

@Packet(PacketFlow.SERVERBOUND)
public record ToggleOrdinanceServerPacket(ResourceKey<Level> dimension, int townID, Ordinance ordinance, boolean enact) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("toggle_ordinance_server");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public ToggleOrdinanceServerPacket(FriendlyByteBuf buf) {
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
    public void handleServer(ServerPlayer player) {
        ServerLevel level = player.serverLevel().getServer().getLevel(dimension);
        if (level == null) return; //If the level is null, then we can't do anything
        TownSavedData data = TownSavedData.get(level);
        Town<?> town = data.getTownByID(townID);
        PenguinTeam team = PenguinTeams.getTeamForPlayer(player);
        if (town.getCharter().getTeamID().equals(team.getID())) {
            boolean set = !town.getGovernment().hasLaw(ordinance);
            town.getGovernment().setLaw(ordinance, set); //Set the law
            data.setDirty();
            //Sync back to the client
            PenguinNetwork.sendToEveryone(new ToggleOrdinanceClientPacket(dimension, townID, ordinance, enact));
        }
    }
}
