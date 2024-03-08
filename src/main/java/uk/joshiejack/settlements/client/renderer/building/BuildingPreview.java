package uk.joshiejack.settlements.client.renderer.building;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.joshiejack.penguinlib.util.helper.EntityHelper;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.building.Building;
import uk.joshiejack.settlements.world.building.BuildingPlacement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(modid = Settlements.MODID, value = Dist.CLIENT)
public class BuildingPreview {
    private static final List<BuildingPlacement> SHARED_CLIENT_PREVIEWS = new ArrayList<>();
    private static BuildingPlacement PLAYER_PREVIEW = null;
    public static final LoadingCache<BuildingPlacement.PlacementKey, BuildingRenderer> RENDERING_CACHE = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES)
            .<BuildingPlacement.PlacementKey, BuildingRenderer>removalListener(entry -> entry.getValue().close()).build(new CacheLoader<>() {
        @Override
        public @NotNull BuildingRenderer load(@NotNull BuildingPlacement.PlacementKey key) {
            return new BuildingRenderer(new BuildingBlockGetter(key.building().getTemplate(), key.rotation()));
        }
    });

    public static void addPreview(BuildingPlacement placement) {
        SHARED_CLIENT_PREVIEWS.add(placement);
    }

    public static void removePreview(BuildingPlacement placement) {
        SHARED_CLIENT_PREVIEWS.remove(placement);
    }

    @Nullable
    public static BuildingPlacement.PlacementKey getKeyFromPlayer(Player player) {
        for (InteractionHand hand : InteractionHand.values()) {
            Building building = Building.getBuildingFromItem(player.getItemInHand(hand));
            if (building != null) {
                Rotation rotation = EntityHelper.getRotationFromEntity(player);
                return new BuildingPlacement.PlacementKey(rotation, building);
            }
        }

        return null;
    }

    @Nullable
    private static BuildingPlacement previewFromPlayer(@NotNull BuildingPlacement.PlacementKey key, BlockPos lookingAt) {
        if (PLAYER_PREVIEW == null || !PLAYER_PREVIEW.getPlacementKey().equals(key)) {
            PLAYER_PREVIEW = new BuildingPlacement(key, lookingAt);
        }

        return PLAYER_PREVIEW.setPos(lookingAt);
    }

    @Nullable
    public static BuildingPlacement getPlacementFromPlayer(Player player) {
        BlockPos lookingAt = EntityHelper.lookingAt(player, 24D);
        if (lookingAt == null) return null; //Return early if we're not looking at anything
        BuildingPlacement.PlacementKey key = getKeyFromPlayer(player);
        return key == null ? null : previewFromPlayer(key, lookingAt);
    }

    private static boolean hasRenderers(Player player) {
        return !SHARED_CLIENT_PREVIEWS.isEmpty() || getPlacementFromPlayer(player) != null;
    }

    @SubscribeEvent
    public static void renderBuildingPreviews(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        Player player = Minecraft.getInstance().player;
        if (!hasRenderers(player)) return; //Return early if we don't have any renderers to render

        PoseStack matrixStack = event.getPoseStack();
        Minecraft mc = Minecraft.getInstance();
        Vec3 view = mc.gameRenderer.getMainCamera().getPosition();
        matrixStack.pushPose();
        matrixStack.translate(-view.x(), -view.y(), -view.z());
        //Render the player local preview
        BuildingPlacement playerPlacement = getPlacementFromPlayer(player);
        if (playerPlacement != null)
            RENDERING_CACHE.getUnchecked(playerPlacement.getPlacementKey()).draw(playerPlacement.getPos(), event.getFrustum(), event.getPoseStack());
        //Renders all of the previews that have been queued by other players in your team
        for (BuildingPlacement placement : SHARED_CLIENT_PREVIEWS) {
            Building building = placement.getPlacementKey().building();
            if (building != null) {
                RENDERING_CACHE.getUnchecked(placement.getPlacementKey()).draw(placement.getPos(), event.getFrustum(), event.getPoseStack());
            }
        }

        matrixStack.popPose();
    }
}