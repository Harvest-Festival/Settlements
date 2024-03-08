package uk.joshiejack.settlements.world.entity.ai.action.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import uk.joshiejack.penguinlib.scripting.wrapper.PositionJS;
import uk.joshiejack.settlements.world.entity.EntityNPC;
import uk.joshiejack.settlements.world.entity.ai.action.ActionMental;

//TODO: @PenguinLoader("spawn_entity")
public class ActionSpawnEntity extends ActionMental {
    private ResourceLocation entityName;
    private BlockPos pos;

    @Override
    public ActionSpawnEntity withData(Object... params) {
        entityName = new ResourceLocation((String) params[0]);
        if (params.length == 2) {
            pos = ((PositionJS)params[0]).get();
        } else pos = new BlockPos((int)params[1], (int)params[2], (int)params[3]);
        return this;
    }

    @Override
    public InteractionResult execute(EntityNPC npc) {
        Entity entity = BuiltInRegistries.ENTITY_TYPE.get(entityName).create(player.level());
        if (entity != null) {
            entity.setPos(pos.getX(), pos.getY(), pos.getZ());
            npc.level().addFreshEntity(entity);
            return InteractionResult.SUCCESS;
        } else return InteractionResult.FAIL;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Entity", entityName.toString());
        tag.putLong("Position", pos.asLong());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        entityName = new ResourceLocation(tag.getString("Entity"));
        pos = BlockPos.of(tag.getLong("Position"));
    }
}
