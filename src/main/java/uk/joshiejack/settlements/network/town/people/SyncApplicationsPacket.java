package uk.joshiejack.settlements.network.town.people;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.client.gui.book.Book;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.settlements.client.WorldMap;
import uk.joshiejack.settlements.world.level.town.people.Government;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Packet(PacketFlow.CLIENTBOUND)
public record SyncApplicationsPacket(ResourceKey<Level> dimension, int townID, Set<UUID> applications) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("sync_applications");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public SyncApplicationsPacket(FriendlyByteBuf buf) {
        this(buf.readResourceKey(Registries.DIMENSION), buf.readInt(), buf.readCollection(HashSet::new, FriendlyByteBuf::readUUID));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
        buf.writeInt(townID);
        buf.writeCollection(applications, FriendlyByteBuf::writeUUID);
    }

    @Override
    public void handleClient() {
        Government government = WorldMap.getTownByID(dimension, townID).getGovernment();
        government.getApplications().clear();
        government.getApplications().addAll(applications);
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof Book book) {
            book.markChanged();
        }
    }
}
