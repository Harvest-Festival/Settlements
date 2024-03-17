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

@Packet(PacketFlow.CLIENTBOUND)
public record ToggleCitizenshipClientPacket(ResourceKey<Level> dimension, int townID) implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("toggle_citizenship_client");

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public ToggleCitizenshipClientPacket(FriendlyByteBuf buf) {
        this(buf.readResourceKey(Registries.DIMENSION), buf.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceKey(dimension);
        buf.writeInt(townID);
    }

    @Override
    public void handleClient() {
        WorldMap.getTownByID(dimension, townID).getGovernment().toggleCitizenship();
    }
}


//package uk.joshiejack.settlements.network.town.people;
//
//import net.minecraft.entity.player.EntityPlayer;
//import uk.joshiejack.penguinlib.network.PenguinNetwork;
//import uk.joshiejack.penguinlib.util.PenguinLoader;
//import uk.joshiejack.penguinlib.world.teams.PenguinTeam;
//import uk.joshiejack.penguinlib.world.teams.PenguinTeams;
//import uk.joshiejack.settlements.AdventureDataLoader;
//import uk.joshiejack.settlements.network.town.PacketAbstractTownSync;
//import uk.joshiejack.settlements.world.town.Town;
//
//@PenguinLoader
//public class PacketToggleCitizenship extends PacketAbstractTownSync {
//    public PacketToggleCitizenship(){}
//    public PacketToggleCitizenship(int dimension, int town) {
//        super(dimension, town);
//    }
//
//    @Override
//    protected void handlePacket(EntityPlayer player, Town<?> town) {
//        if (!player.world.isRemote) {
//            PenguinTeam team = PenguinTeams.getTeamForPlayer(player);
//            if (town.getCharter().getTeamID().equals(team.getID())) {
//                town.getGovernment().toggleCitizenship();
//                AdventureDataLoader.get(player.world).markDirty();
//                PenguinNetwork.sendToEveryone(new PacketToggleCitizenship(dimension, id));
//            }
//        } else town.getGovernment().toggleCitizenship();
//    }
//}
