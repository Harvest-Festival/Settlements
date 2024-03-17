package uk.joshiejack.settlements.world.level.town;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import uk.joshiejack.penguinlib.event.TeamChangedOwnerEvent;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.network.town.people.SyncMayorPacket;
import uk.joshiejack.settlements.world.level.TownSavedData;

@Mod.EventBusSubscriber(modid = Settlements.MODID)
public class UpdateMayor {
    @SubscribeEvent
    public static void onTeamChangedOwner(TeamChangedOwnerEvent event) {
        for (ServerLevel world: ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
            TownSavedData.get(world).getTowns(world).stream().filter(town ->
                    town.getCharter().getTeamID().equals(event.getTeamUUID())).findFirst().ifPresent(t -> {
                        t.getCharter().setMayor(event.getNewOwner());
                        PenguinNetwork.sendToEveryone(new SyncMayorPacket(world.dimension(), t.getID(), t.getCharter().getMayor()));
            });
        }
    }
}