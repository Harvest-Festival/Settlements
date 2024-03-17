package uk.joshiejack.settlements.world.level.town.people;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.level.town.Town;
import uk.joshiejack.settlements.world.level.town.TownFinder;
import uk.joshiejack.settlements.world.level.town.land.Interaction;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = Settlements.MODID)
public class Protection {
    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof Player && event.getEntity() instanceof Player) {
            Town<?> town = TownFinder.find(event.getEntity().level(), event.getEntity().blockPosition());
            if (town.getGovernment().hasLaw(Ordinance.NO_KILL)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
        Town<?> town = TownFinder.find(event.getEntity().level(), event.getEntity().blockPosition());
        if (town.getGovernment().hasLaw(Ordinance.BAN_ITEM_USAGE) && event.getItemStack().getItem().getFoodProperties(event.getItemStack(), event.getEntity()) != null) {
            event.setCanceled(true);
        }
    }

    private static boolean isProtected(@Nullable Player player, Level world, BlockPos pos, Interaction interaction) {
        Town<?> town = TownFinder.find(world, pos);
        if (town.getGovernment().hasLaw(Ordinance.EXTERNAL_PROTECTION) && (player == null || !PenguinTeams.getTeamForPlayer(player).getID().equals(town.getCharter().getTeamID()))) {
            return true;
        }

        return town.getGovernment().hasLaw(Ordinance.INTERNAL_PROTECTION) && town.getLandRegistry().getFootprints(world, interaction).contains(pos);
    }
}
