package uk.joshiejack.settlements.world.level.town.people;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.settlements.world.building.Building;
import uk.joshiejack.settlements.world.level.town.land.TownBuilding;

import java.util.Collection;
import java.util.Set;

public abstract class AbstractCensus {
    protected final Set<ResourceLocation> residents = Sets.newHashSet();
    protected final Set<ResourceLocation> invitableList = Sets.newHashSet(); //SUB_SET of Residents BOTH SIDES

    public int population() {
        return residents.size();
    }

    public boolean isInvitable(ResourceLocation npc) {
        return invitableList.contains(npc);
    }

    public void onBuildingsChanged(Multimap<Building, TownBuilding> buildings) {
        residents.clear();
        for (Building building : buildings.keySet()) {
            building.template().getResidents().forEach(npc -> residents.add(npc.id()));
        }
    }

    public abstract Collection<ResourceLocation> getCustomNPCKeys();
}
