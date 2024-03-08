package uk.joshiejack.settlements.client.renderer.building;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.client.level.ghost.GhostBlockGetter;
import uk.joshiejack.penguinlib.util.helper.BlockPosHelper;
import uk.joshiejack.settlements.world.building.Template;

import java.util.HashMap;
import java.util.Map;

public class BuildingBlockGetter extends GhostBlockGetter<Map<BlockPos, Template.BlockData>> {
    public BuildingBlockGetter(Template building, Rotation rotation) {
        super(rotate(building.getBlockData(), rotation), building.getXSize(), building.getYSize(), building.getZSize());
    }

    @SuppressWarnings("deprecation")
    private static Map<BlockPos, Template.BlockData> rotate(Map<BlockPos, Template.BlockData> data, Rotation rotation) {
        Map<BlockPos, Template.BlockData> blocks = new HashMap<>();
        data.forEach((originalPos, inf) -> {
            BlockState state = inf.state().rotate(rotation);
            BlockPos pos = BlockPosHelper.transformBlockPos(originalPos, rotation);
            Template.BlockData infoR = new Template.BlockData(state, null, null);
            blocks.put(pos, infoR);
        });

        return blocks;
    }

    @Override
    public @NotNull BlockState getBlockState(@NotNull BlockPos pos) {
        return data.containsKey(pos) ? data.get(pos).state() : Blocks.AIR.defaultBlockState();
    }
}