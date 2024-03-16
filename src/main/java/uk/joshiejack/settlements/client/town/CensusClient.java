package uk.joshiejack.settlements.client.town;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.settlements.world.entity.npc.DynamicNPC;
import uk.joshiejack.settlements.world.level.town.people.AbstractCensus;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class CensusClient extends AbstractCensus {
    private final Set<DynamicNPC> customNPCs = Sets.newHashSet();

    public boolean hasResident(ResourceLocation npc) {
        return residents.contains(npc);
    }

    public void setInvitableList(Set<ResourceLocation> invitableList) {
        this.invitableList.clear();
        this.invitableList.addAll(invitableList);
    }

    public void setCustomNPCs(Collection<DynamicNPC> custom) {
        customNPCs.clear();
        customNPCs.addAll(custom);
    }

    public Collection<DynamicNPC> getCustomNPCs() {
        return customNPCs;
    }

    @Override
    public Collection<ResourceLocation> getCustomNPCKeys() {
        return customNPCs.stream().map(DynamicNPC::id).collect(Collectors.toList());
    }
}
