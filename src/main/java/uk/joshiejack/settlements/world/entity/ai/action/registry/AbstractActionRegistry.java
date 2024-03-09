package uk.joshiejack.settlements.world.entity.ai.action.registry;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.ActionMental;

public abstract class AbstractActionRegistry<O extends ReloadableRegistry.PenguinRegistry<O>> extends ActionMental {
    protected ResourceLocation resource;
    protected final ReloadableRegistry<O> registry;

    public AbstractActionRegistry(ReloadableRegistry<O> registry) {
        this.registry = registry;
    }

    @Override
    public AbstractActionRegistry<?> withData(Object... params) {
        this.resource = new ResourceLocation((String)params[0]);
        return this;
    }

    @Override
    public InteractionResult execute(NPCMob npc) {
        if (player != null) {
            O p = registry.get(resource);
            if (p != null) {
                performAction(npc.level(), p);
            }
        }

        return InteractionResult.SUCCESS;
    }

    public abstract void performAction(Level world, O object);

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Resource", resource.toString());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        resource = new ResourceLocation(tag.getString("Resource"));
    }
}
