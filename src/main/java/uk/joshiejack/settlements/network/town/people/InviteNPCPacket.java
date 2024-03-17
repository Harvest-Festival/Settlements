package uk.joshiejack.settlements.network.town.people;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;
import uk.joshiejack.settlements.world.level.TownSavedData;
import uk.joshiejack.settlements.world.level.town.TownFinder;
import uk.joshiejack.settlements.world.level.town.TownServer;

@Packet(PacketFlow.SERVERBOUND)
public record InviteNPCPacket(ResourceLocation npc) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("invite_npc");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
    public InviteNPCPacket(FriendlyByteBuf buf) {
        this(buf.readResourceLocation());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(npc);
    }

    @Override
    public void handleServer(ServerPlayer player) {
        TownServer town = TownFinder.find(player.serverLevel(), player.blockPosition()); //Don't create any towns here
        if (PenguinTeams.get(player.serverLevel()).getTeamMembers(town.getCharter().getTeamID()).contains(player.getUUID())) {
            if (town.getCensus().isInvitable(npc)) {
                town.getCensus().invite(npc); //Now we need to resend the list
                town.getCensus().onNPCsChanged(player.serverLevel()); //Invites changed
                TownSavedData.get(player.serverLevel()).setDirty(); //Save the invites
            }
        }
    }
}
