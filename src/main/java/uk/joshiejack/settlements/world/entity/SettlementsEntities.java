package uk.joshiejack.settlements.world.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import uk.joshiejack.settlements.Settlements;

@Mod.EventBusSubscriber(modid = Settlements.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SettlementsEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Settlements.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<NPCMob>> NPC = createEntity("npc", NPCMob::new, 0.6F, 1.95F);

    private static <E extends Entity> DeferredHolder<EntityType<?>, EntityType<E>> createEntity(String id, EntityType.EntityFactory<E> factory, float width, float height) {
        return createEntity(new ResourceLocation(Settlements.MODID, id), factory, width, height);
    }

    private static <E extends Entity> DeferredHolder<EntityType<?>, EntityType<E>> createEntity(ResourceLocation id, EntityType.EntityFactory<E> factory, float width, float height) {
        EntityType.Builder<E> builder = EntityType.Builder.of(factory, MobCategory.MISC)
                .sized(width, height)
                .clientTrackingRange(10);
        return ENTITIES.register(id.getPath(), () -> builder.build(id.toString()));
    }

    @SubscribeEvent
    public static void addEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(NPC.get(), NPCMob.createAttributes().build());
    }
}
