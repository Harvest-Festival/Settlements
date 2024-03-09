package uk.joshiejack.settlements.client.entity;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.npc.NPC;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class RenderNPC extends HumanoidMobRenderer<NPCMob, PlayerModel<NPCMob>> {
    private static final Cache<ResourceLocation, Boolean> HAS_SKIN = CacheBuilder.newBuilder().build();
    private static final TextureManager manager = Minecraft.getInstance().getTextureManager();
    protected final PlayerModel<NPCMob> alex;

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
    public @NotNull ResourceLocation getTextureLocation(@Nonnull NPCMob npc) {
        return textureExists(npc.getInfo().getSkin()) ? npc.getInfo().getSkin() : NPC.MISSING_TEXTURE;
    }

    @Override
    protected void scale(NPCMob npc, PoseStack pose, float pPartialTicks) {
        float f1 = npc.getInfo().getNPCClass().height();
        float f3 = 0.9375F;
        pose.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
    }

    @Override
    public void render(NPCMob npc, float pEntityYaw, float pPartialTicks, PoseStack pose, MultiBufferSource pBuffer, int pPackedLight) {
        model = npc.getInfo().getNPCClass().smallArms() ? alex : model;
        setModelProperties(npc);
        if (npc.getAnimation() == null || (npc.isAlive() && !npc.getAnimation().render(npc, pEntityYaw, pPartialTicks, pose, pBuffer, pPackedLight))) {
            super.render(npc, pEntityYaw, pPartialTicks, pose, pBuffer, pPackedLight);
        }
    }

    private void setModelProperties(NPCMob p_117819_) {
        PlayerModel<NPCMob> playermodel = this.getModel();
        if (p_117819_.isSpectator()) {
            playermodel.setAllVisible(false);
            playermodel.head.visible = true;
            playermodel.hat.visible = true;
        } else {
            playermodel.setAllVisible(true);
            playermodel.hat.visible = p_117819_.isModelPartShown(PlayerModelPart.HAT);
            playermodel.jacket.visible = p_117819_.isModelPartShown(PlayerModelPart.JACKET);
            playermodel.leftPants.visible = p_117819_.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            playermodel.rightPants.visible = p_117819_.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            playermodel.leftSleeve.visible = p_117819_.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            playermodel.rightSleeve.visible = p_117819_.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            playermodel.crouching = p_117819_.isCrouching();
            HumanoidModel.ArmPose humanoidmodel$armpose = getArmPose(p_117819_, InteractionHand.MAIN_HAND);
            HumanoidModel.ArmPose humanoidmodel$armpose1 = getArmPose(p_117819_, InteractionHand.OFF_HAND);
            if (humanoidmodel$armpose.isTwoHanded()) {
                humanoidmodel$armpose1 = p_117819_.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
            }

            if (p_117819_.getMainArm() == HumanoidArm.RIGHT) {
                playermodel.rightArmPose = humanoidmodel$armpose;
                playermodel.leftArmPose = humanoidmodel$armpose1;
            } else {
                playermodel.rightArmPose = humanoidmodel$armpose1;
                playermodel.leftArmPose = humanoidmodel$armpose;
            }
        }
    }

    private static HumanoidModel.ArmPose getArmPose(NPCMob p_117795_, InteractionHand p_117796_) {
        ItemStack itemstack = p_117795_.getItemInHand(p_117796_);
        if (itemstack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        } else {
            if (p_117795_.getUsedItemHand() == p_117796_ && p_117795_.getUseItemRemainingTicks() > 0) {
                UseAnim useanim = itemstack.getUseAnimation();
                if (useanim == UseAnim.BLOCK) {
                    return HumanoidModel.ArmPose.BLOCK;
                }

                if (useanim == UseAnim.BOW) {
                    return HumanoidModel.ArmPose.BOW_AND_ARROW;
                }

                if (useanim == UseAnim.SPEAR) {
                    return HumanoidModel.ArmPose.THROW_SPEAR;
                }

                if (useanim == UseAnim.CROSSBOW && p_117796_ == p_117795_.getUsedItemHand()) {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }

                if (useanim == UseAnim.SPYGLASS) {
                    return HumanoidModel.ArmPose.SPYGLASS;
                }

                if (useanim == UseAnim.TOOT_HORN) {
                    return HumanoidModel.ArmPose.TOOT_HORN;
                }

                if (useanim == UseAnim.BRUSH) {
                    return HumanoidModel.ArmPose.BRUSH;
                }
            } else if (!p_117795_.swinging && itemstack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack)) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }

            HumanoidModel.ArmPose forgeArmPose = net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.of(itemstack).getArmPose(p_117795_, p_117796_, itemstack);
            if (forgeArmPose != null) return forgeArmPose;
            return HumanoidModel.ArmPose.ITEM;
        }
    }

    @Override
    protected void setupRotations(NPCMob npc, @NotNull PoseStack pose, float ageInTicks, float rotationYaw, float partialTicks) {
        if (npc.getAnimation() == null || (npc.isAlive() && !npc.getAnimation().applyRotation(npc))) {
            super.setupRotations(npc, pose, ageInTicks, rotationYaw, partialTicks);
        }
    }
    @Override
    protected boolean shouldShowName(@Nonnull NPCMob npc) {
        return false;
    }


    @Override
    public @NotNull Vec3 getRenderOffset(NPCMob npc, float p_117786_) {
        return npc.isCrouching() ? new Vec3(0.0, -0.125, 0.0) : super.getRenderOffset(npc, p_117786_);
    }

    public static class AdaptableArmor extends HumanoidArmorLayer<NPCMob, PlayerModel<NPCMob>, HumanoidArmorModel<NPCMob>> {
        protected final HumanoidArmorModel<NPCMob> alexInner;
        protected final HumanoidArmorModel<NPCMob> alexOuter;
        public AdaptableArmor(RenderNPC parent,
                              HumanoidArmorModel<NPCMob> inner, HumanoidArmorModel<NPCMob> outer,
                              HumanoidArmorModel<NPCMob> alexInner, HumanoidArmorModel<NPCMob> alexOuter,
                              ModelManager manager) {
            super(parent, inner, outer, manager);
            this.alexInner = alexInner;
            this.alexOuter = alexOuter;
        }

        @Override
        public void render(@NotNull PoseStack stack, @NotNull MultiBufferSource buffer, int num1, @NotNull NPCMob npc, float f1, float f2, float f3, float f4, float f5, float f6) {
            innerModel = npc.getInfo().getNPCClass().smallArms() ? alexInner : innerModel;
            outerModel = npc.getInfo().getNPCClass().smallArms() ? alexOuter : outerModel;
            super.render(stack, buffer, num1, npc, f1, f2, f3, f4, f5, f6);
        }
    }
}
