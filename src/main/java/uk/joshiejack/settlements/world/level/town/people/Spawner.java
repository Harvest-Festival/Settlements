package uk.joshiejack.settlements.world.level.town.people;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import uk.joshiejack.penguinlib.util.helpers.minecraft.EntityHelper;
import uk.joshiejack.settlements.AdventureConfig;
import uk.joshiejack.settlements.entity.EntityNPC;
import uk.joshiejack.settlements.npcs.HomeOverrides;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.SettlementsEntities;
import uk.joshiejack.settlements.world.entity.npc.NPC;
import uk.joshiejack.settlements.world.entity.npc.NPCInfo;
import uk.joshiejack.settlements.world.level.town.TownServer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Spawner {
    private final TownServer town;

    public Spawner(TownServer town) {
        this.town = town;
    }

    private List<NPCInfo> all() {
        List<NPCInfo> list = Lists.newArrayList(NPC.all());
        list.addAll(town.getCensus().getCustomNPCs());
        return list;
    }

    public NPCMob byOccupation(Level world, String occupation, BlockPos origin) {
        return getNearbyNPCs(world).stream().filter(e -> e.getNPC().getOccupation().equals(occupation)).findFirst()
                .orElseGet(() -> create(world, all().stream().filter(n -> n.getOccupation().equals(occupation)).findFirst().orElse(NPC.UNKNOWN), origin));
    }

    public NPCMob getNPC(Level world, NPCInfo npc, BlockPos origin) {
        return getNearbyNPCs(world).stream().filter(e -> e.getNPC() == npc && e.getNPC().id().equals(npc.id())).findFirst().orElseGet(() -> create(world, npc, origin));
    }

    private NPCMob create(Level world, NPCInfo npc, BlockPos origin) {
        NPCMob newNPC = new NPCMob(SettlementsEntities.NPC.get(), world, npc);
        newNPC.getTown();//Set the town by calling the getter
        BlockPos home = town.getLandRegistry().getWaypoint(HomeOverrides.get(search)).getKey();
        BlockPos target = town.equals(TownServer.NULL) || !origin.equals(town.getCentre()) ? origin : home;
        newNPC.setLocationAndAngles(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 1F, 1F);
        world.addFreshEntity(newNPC);
        return newNPC;
    }

    public List<NPCMob> getNearbyNPCs(Level world) {
        //Remove duplicates
        Set<ResourceLocation> exists = Sets.newHashSet();
        List<NPCMob> nearby = EntityHelper.getEntities(EntityNPC.class, world, town.getCentre(), AdventureConfig.townDistance, 128D).stream()
                .filter(e -> !e.isDead && e.getTown() == town.getID()).collect(Collectors.toList());
        List<NPCMob> remaining = Lists.newArrayList();
        for (NPCMob e : nearby) {
            if (!exists.contains(e.getNPC().id())) {
                exists.add(e.getNPC().id());
                remaining.add(e);
            } else {
                e.kill();
            }
        }

        return remaining;
    }
}
