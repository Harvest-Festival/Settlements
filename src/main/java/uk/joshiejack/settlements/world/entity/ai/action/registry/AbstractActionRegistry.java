package uk.joshiejack.settlements.world.entity.ai.action.registry;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.MentalAction;

public abstract class AbstractActionRegistry<O extends ReloadableRegistry.PenguinRegistry<O>> extends MentalAction {
    protected ResourceLocation resource;
    protected final ReloadableRegistry<O> registry;


    public AbstractActionRegistry(ReloadableRegistry<O> registry) {
        this.registry = registry;
    }

    @Override
    public InteractionResult execute(NPCMob npc) {
        if (player != null) {
            O p = registry.get(getResource());
            if (p != null) {
                performAction(npc.level(), p);
            }
        }

        return InteractionResult.SUCCESS;
    }

    public ResourceLocation getResource() {
        return registryName;
    }

    public abstract void performAction(Level world, O object);

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Resource", getResource().toString());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        resource = new ResourceLocation(tag.getString("Resource"));
    }
}
