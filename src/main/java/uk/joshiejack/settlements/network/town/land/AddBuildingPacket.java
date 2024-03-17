package uk.joshiejack.settlements.network.town.land;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.client.WorldMap;
import uk.joshiejack.settlements.world.level.town.land.TownBuilding;

@Packet(PacketFlow.CLIENTBOUND)
public record AddBuildingPacket(ResourceKey<Level> dimension, int townID, TownBuilding building) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("add_building");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public AddBuildingPacket(FriendlyByteBuf buf) {
        this(buf.readResourceKey(Registries.DIMENSION), buf.readInt(),
                new TownBuilding(PenguinNetwork.readRegistry(Settlements.Registries.BUILDINGS, buf), buf.readBlockPos(), buf.readEnum(Rotation.class), buf.readBoolean()));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
        buf.writeInt(townID);
        PenguinNetwork.writeRegistry(building.getBuilding(), buf);
        buf.writeBlockPos(building.getPosition());
        buf.writeEnum(building.getRotation());
        buf.writeBoolean(building.isBuilt());
    }

    @Override
    public void handle(Player player) {
        WorldMap.getTownByID(dimension, townID).getLandRegistry().addBuilding(player.level(), building);
    }
}
