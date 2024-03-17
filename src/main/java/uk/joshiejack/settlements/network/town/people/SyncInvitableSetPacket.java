package uk.joshiejack.settlements.network.town.people;

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

import java.util.HashSet;
import java.util.Set;

@Packet(PacketFlow.CLIENTBOUND)
public record SyncInvitableSetPacket(ResourceKey<Level> dimension, int townID, Set<ResourceLocation> invitableList) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("sync_invitable_list");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public SyncInvitableSetPacket(FriendlyByteBuf buf) {
        this(buf.readResourceKey(Registries.DIMENSION), buf.readInt(), buf.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
        buf.writeInt(townID);
        buf.writeCollection(invitableList, FriendlyByteBuf::writeResourceLocation);
    }

    @Override
    public void handleClient() {
        WorldMap.getTownByID(dimension, townID).getCensus().setInvitableList(invitableList);
    }
}
