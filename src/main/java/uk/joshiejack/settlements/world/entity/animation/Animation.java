package uk.joshiejack.settlements.world.entity.animation;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import uk.joshiejack.settlements.world.entity.NPCMob;

public abstract class Animation implements INBTSerializable<CompoundTag> {
    private static final BiMap<String, Class<? extends Animation>> ANIMATIONS = HashBiMap.create();
    public static void register(String type, Class<? extends Animation> clazz) {
        ANIMATIONS.put(type, clazz);
    }

    public abstract void play(NPCMob entityNPC);

    public static Animation create(String animation) throws IllegalAccessException, InstantiationException {
        return ANIMATIONS.get(animation).newInstance();
    }

    public String getID() {
        return ANIMATIONS.inverse().get(this.getClass());
    }

    public Animation withData(Object... args) {
        return this;
    }

    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {}

    public boolean renderLiving(NPCMob npc, double x, double y, double z) {
        return false;
    }

    public boolean applyRotation(NPCMob npc) {
        return false;
    }

    public boolean render(NPCMob npc, float pEntityYaw, float pPartialTicks, PoseStack pose, MultiBufferSource pBuffer, int pPackedLight) {
        return false;
    }
}
