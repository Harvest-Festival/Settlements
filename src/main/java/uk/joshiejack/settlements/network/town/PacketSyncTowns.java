package uk.joshiejack.settlements.network.town;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.settlements.client.WorldMap;
import uk.joshiejack.settlements.client.town.TownClient;
import uk.joshiejack.settlements.network.town.people.PacketRequestCustomNPCS;
import uk.joshiejack.settlements.world.level.town.Town;

import java.util.Collection;
import java.util.Objects;

@Packet(PacketFlow.CLIENTBOUND)
public record PacketSyncTowns(ResourceKey<Level> dimension, Collection<Town<?>> towns) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("sync_towns");
    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public PacketSyncTowns(FriendlyByteBuf buf) {
        this(buf.readResourceKey(Registries.DIMENSION), Lists.newArrayList());
        int count = buf.readInt();
        for (int i = 0; i < count; i++) {
            TownClient town = new TownClient(buf.readInt(), BlockPos.of(buf.readLong()));
            town.deserializeNBT(Objects.requireNonNull(buf.readNbt()));
            towns.add(town);
        }
    }
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
        buf.writeInt(towns.size());
        towns.forEach(t-> {
                buf.writeInt(t.getID());
                buf.writeLong(t.getCentre().asLong());
                buf.writeNbt(t.getTagForSync());
        });
    }

    @Override
    public void handle(Player player) {
        WorldMap.setTowns(dimension, towns);
        //Request the Custom NPCS from the server
        PenguinNetwork.sendToServer(new PacketRequestCustomNPCS(dimension));
    }
}
