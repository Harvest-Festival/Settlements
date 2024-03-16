package uk.joshiejack.settlements.network.town.people;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import uk.joshiejack.penguinlib.util.PenguinLoader;
import uk.joshiejack.penguinlib.util.helpers.minecraft.PlayerHelper;
import uk.joshiejack.penguinlib.world.teams.PenguinTeam;
import uk.joshiejack.penguinlib.world.teams.PenguinTeams;
import uk.joshiejack.settlements.network.town.PacketAbstractTownSync;
import uk.joshiejack.settlements.world.town.Town;

@PenguinLoader(side = Side.SERVER)
public class PacketClaimTown extends PacketAbstractTownSync {
    public PacketClaimTown() { }
    public PacketClaimTown(int dimension, int id) {
        super(dimension, id);
    }

    @Override
    public void handlePacket(EntityPlayer player, Town<?> town) {
        PenguinTeam owner = PenguinTeams.getTeamFromID(player.world, town.getCharter().getTeamID());
        //If the team has no owner
        if (owner.getOwner() == null) {
            PenguinTeams.get(player.world).changeTeam(player.world, PlayerHelper.getUUIDForPlayer(player), owner.getID());
        }
    }
}
