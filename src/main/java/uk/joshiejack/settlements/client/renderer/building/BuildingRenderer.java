package uk.joshiejack.settlements.client.renderer.building;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import uk.joshiejack.penguinlib.client.level.ghost.GhostBlockRenderer;
import uk.joshiejack.settlements.world.building.Template;

import java.util.Map;

public class BuildingRenderer extends GhostBlockRenderer<Map<BlockPos, Template.BlockData>, BuildingBlockGetter> {
    public BuildingRenderer(BuildingBlockGetter blockAccess) {
        super(blockAccess);
    }

    @Override
    protected void setupRenderer(SectionBufferBuilderPack sectionBuffer, BlockRenderDispatcher blockRenderer, PoseStack poseStack, RandomSource randomSource) {
        blockGetter.getData().forEach((pos, data) -> addRender(sectionBuffer, blockRenderer, poseStack, data.state(), pos, randomSource));
    }
}