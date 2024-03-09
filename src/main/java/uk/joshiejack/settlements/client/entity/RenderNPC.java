package uk.joshiejack.settlements.client.entity;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.entity.EntityNPC;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class RenderNPC extends HumanoidMobRenderer<EntityNPC, PlayerModel<EntityNPC>> {
    public static final ResourceLocation MISSING = new ResourceLocation(Settlements.MODID, "textures/entity/missing.png");
    private static final Cache<ResourceLocation, Boolean> HAS_SKIN = CacheBuilder.newBuilder().build();
    private static final TextureManager manager = Minecraft.getInstance().getTextureManager();
    protected final PlayerModel<EntityNPC> alex;

    public RenderNPC(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
        this.alex = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
        this.addLayer(new AdaptableArmor(this, new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM_INNER_ARMOR)),
                new HumanoidArmorModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM_OUTER_ARMOR)), context.getModelManager()));
        this.addLayer(new ArrowLayer<>(context, this));
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean textureExists(ResourceLocation resource) {
        try {
            return HAS_SKIN.get(resource, () -> {
                AbstractTexture itextureobject = Minecraft.getInstance().getTextureManager().getTexture(resource);
                if (itextureobject == null) {
                    itextureobject = new SimpleTexture(resource);
                }

                return manager.loadTexture(resource, itextureobject) != null && itextureobject != MissingTextureAtlasSprite.getTexture();
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ResourceLocation getSkinFromUsernameOrUUID(@Nullable UUID uuid, @Nullable String playerSkin) {
        GameProfile profile = SkinCache.getOrResolveGameProfile(uuid);
        PlayerInfo info = new PlayerInfo(profile, false);
        return info.getSkin().texture();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@Nonnull EntityNPC npc) {
        return textureExists(npc.getInfo().getSkin()) ? npc.getInfo().getSkin() : MISSING;
    }

    @Override
    public void render(EntityNPC npc, float pEntityYaw, float pPartialTicks, PoseStack pose, MultiBufferSource pBuffer, int pPackedLight) {
        model = npc.getInfo().getNPCClass().smallArms() ? alex : model;
        pose.pushPose();
        pose.scale(0.9375F, 0.9375F, 0.9375F);
        float f1 = npc.getInfo().getNPCClass().height();
        float f3 = 1.0F;
        pose.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
        if (npc.getAnimation() == null || (npc.isAlive() && !npc.getAnimation().render(npc, pEntityYaw, pPartialTicks, pose, pBuffer, pPackedLight))) {
            super.render(npc, pEntityYaw, pPartialTicks, pose, pBuffer, pPackedLight);
        }

        pose.popPose();
    }

    @Override
    protected void setupRotations(EntityNPC npc, @NotNull PoseStack pose, float ageInTicks, float rotationYaw, float partialTicks) {
        if (npc.getAnimation() == null || (npc.isAlive() && !npc.getAnimation().applyRotation(npc))) {
            super.setupRotations(npc, pose, ageInTicks, rotationYaw, partialTicks);
        }
    }
    @Override
    protected boolean shouldShowName(@Nonnull EntityNPC npc) {
        return false;
    }


    @Override
    public Vec3 getRenderOffset(EntityNPC npc, float p_117786_) {
        return npc.isCrouching() ? new Vec3(0.0, -0.125, 0.0) : super.getRenderOffset(npc, p_117786_);
    }

    public static class AdaptableArmor extends HumanoidArmorLayer<EntityNPC, PlayerModel<EntityNPC>, HumanoidArmorModel<EntityNPC>> {
        protected final HumanoidArmorModel<EntityNPC> alexInner;
        protected final HumanoidArmorModel<EntityNPC> alexOuter;
        public AdaptableArmor(RenderNPC parent,
                              HumanoidArmorModel<EntityNPC> inner, HumanoidArmorModel<EntityNPC> outer,
                              HumanoidArmorModel<EntityNPC> alexInner, HumanoidArmorModel<EntityNPC> alexOuter,
                              ModelManager manager) {
            super(parent, inner, outer, manager);
            this.alexInner = alexInner;
            this.alexOuter = alexOuter;
        }

        @Override
        public void render(@NotNull PoseStack stack, @NotNull MultiBufferSource buffer, int num1, @NotNull EntityNPC npc, float f1, float f2, float f3, float f4, float f5, float f6) {
            innerModel = npc.getInfo().getNPCClass().smallArms() ? alexInner : innerModel;
            outerModel = npc.getInfo().getNPCClass().smallArms() ? alexOuter : outerModel;
        }
    }
}
