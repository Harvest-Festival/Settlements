package uk.joshiejack.settlements.network.town.people;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.settlements.client.WorldMap;
import uk.joshiejack.settlements.client.town.TownClient;
import uk.joshiejack.settlements.world.entity.npc.DynamicNPC;

import java.util.Collection;
import java.util.Objects;

@Packet(PacketFlow.CLIENTBOUND)
public record SyncCustomNPCsPacket(ResourceKey<Level> dimension, int town, Collection<DynamicNPC> custom) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("sync_custom_npcs");
    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public SyncCustomNPCsPacket(FriendlyByteBuf buf) {
        this(buf.readResourceKey(Registries.DIMENSION), buf.readInt(),
                buf.readList((buffer) -> DynamicNPC.fromTag(Objects.requireNonNull(buffer.readNbt()))));

    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
        buf.writeInt(town);
        buf.writeInt(custom.size());
        buf.writeCollection(custom, (buffer, npc) -> {
            buffer.writeNbt(npc.toTag());
        });
    }

    @Override
    public void handle(Player player) {
        TownClient town = WorldMap.getTownByID(dimension, town());
        town.getCensus().setCustomNPCs(custom);
    }
}
