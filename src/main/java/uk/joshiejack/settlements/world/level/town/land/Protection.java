package uk.joshiejack.settlements.world.level.town.land;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;
import uk.joshiejack.penguinlib.world.teams.PenguinTeams;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.util.TownFinder;
import uk.joshiejack.settlements.world.level.town.Town;
import uk.joshiejack.settlements.world.level.town.TownFinder;
import uk.joshiejack.settlements.world.level.town.people.Ordinance;
import uk.joshiejack.settlements.world.town.Town;
import uk.joshiejack.settlements.world.town.people.Ordinance;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Settlements.MODID)
public class Protection {
    public static final TagKey<Block> RIGHT_CLICK_PREVENTION = BlockTags.create(Settlements.prefix("right_click_prevention"));

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (isProtected(event.getEntity(), event.getEntity().level(), event.getPosition(), Interaction.BREAK, Ordinance.EXTERNAL_PROTECTION)) {
            event.setNewSpeed(0F);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDetonate(ExplosionEvent.Detonate event) {
        if (!event.getAffectedBlocks().isEmpty()) {
            BlockPos origin = new BlockPos(Objects.requireNonNull(event.getExplosion().getDirectSourceEntity()).blockPosition());
            Town<?> town = TownFinder.find(event.getLevel(), origin);
            if (town.getGovernment().hasLaw(Ordinance.INTERNAL_PROTECTION)) {
                List<BlockPos> footprints = town.getLandRegistry().getFootprints(event.getLevel(), Interaction.FOOTPRINT);
                event.getAffectedBlocks().removeAll(footprints);
                event.getAffectedEntities().removeIf(e -> footprints.contains(e.blockPosition()));
            }

            if (town.getGovernment().hasLaw(Ordinance.EXTERNAL_PROTECTION)) {
                event.getAffectedBlocks().removeIf(pos -> TownFinder.find(event.getLevel(), pos) == town);
                event.getAffectedEntities().removeIf(entity -> TownFinder.find(event.getLevel(), entity.getPosition()) == town);
            }
        }
    }

//    @SubscribeEvent(priority = EventPriority.LOWEST) //TOD: Fix this
//    public static void onHarvestBlock(BlockEvent.HarvestDropsEvent event) {
//        if (isProtected(event.getHarvester(), event.getWorld(), event.getPos(), Interaction.BREAK, Ordinance.EXTERNAL_PROTECTION)) {
//            event.getDrops().clear();
//        }
//    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBreakBlock(BlockEvent.BreakEvent event) {
        if (isProtected(event.getPlayer(), event.getPlayer().level(), event.getPos(), Interaction.BREAK, Ordinance.EXTERNAL_PROTECTION)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof ItemFrame) {
            if (isProtected(event.getEntity(), event.getLevel(), event.getPos(), Interaction.ENTITY_INTERACT, Ordinance.BAN_COMMUNICATION)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onActivate(PlayerInteractEvent.RightClickBlock event) {
        if (isProtected(event.getEntity(), event.getLevel(), event.getPos(), Interaction.RIGHT_CLICK, Ordinance.BAN_INTERACTION)) {
            event.setUseBlock(Event.Result.DENY);
        }
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player && isProtected(player, player.level(), event.getPos(), Interaction.RIGHT_CLICK, Ordinance.BAN_CONSTRUCTION)) {
            event.setCanceled(true);
        }
    }

    private static boolean isProtected(@Nullable Player player, Level world, BlockPos pos, Interaction interaction, Ordinance law) {
        Town<?> town = TownFinder.find(world, pos);
        if (town.getGovernment().hasLaw(law) && (player == null || !PenguinTeams.getTeamForPlayer(player).getID().equals(town.getCharter().getTeamID()))) {
            return true;
        }

        return town.getGovernment().hasLaw(Ordinance.INTERNAL_PROTECTION) && town.getLandRegistry().getFootprints(world, interaction).contains(pos);
    }
}
