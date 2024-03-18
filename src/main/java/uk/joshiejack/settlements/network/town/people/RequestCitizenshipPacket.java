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
import uk.joshiejack.settlements.world.level.town.Town;
import uk.joshiejack.settlements.world.level.town.people.Citizenship;

@Packet(PacketFlow.SERVERBOUND)
public record RequestCitizenshipPacket(ResourceKey<Level> dimension, int townID) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("request_citizenship");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
    public RequestCitizenshipPacket(FriendlyByteBuf buf) {
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
        TownSavedData data = TownSavedData.get(level);
        Town<?> town = data.getTownByID(townID);
        if (town.getGovernment().getCitizenship() == Citizenship.APPLICATION) {
            town.getGovernment().addApplication(player.getUUID());
            PenguinNetwork.sendToTeam(player.serverLevel(), town.getCharter().getTeamID(), new SyncApplicationsPacket(dimension, townID, town.getGovernment().getApplications()));
            data.setDirty();
        }
    }
}
