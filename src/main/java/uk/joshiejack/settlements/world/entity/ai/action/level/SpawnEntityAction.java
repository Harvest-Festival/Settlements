package uk.joshiejack.settlements.world.entity.ai.action.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.MentalAction;

//TODO: @PenguinLoader("spawn_entity")
public class SpawnEntityAction extends MentalAction {
    private ResourceLocation entityName;
    private BlockPos pos;

    public SpawnEntityAction() {}
    public SpawnEntityAction(ResourceLocation entityName, BlockPos pos) {
        this.entityName = entityName;
        this.pos = pos;
    }

    @Override
    public InteractionResult execute(NPCMob npc) {
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
