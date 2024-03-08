package uk.joshiejack.settlements.world.building;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;

public class BuildingPlacement {
    private final PlacementKey key;
    private BlockPos pos;

    public PlacementKey getPlacementKey() {
        return key;
    }

    public BuildingPlacement(PlacementKey key, BlockPos pos) {
        this.key = key;
        this.pos = pos;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BuildingPlacement setPos(BlockPos pos) {
        this.pos = pos;
        return this;
    }

    public record PlacementKey(Rotation rotation, Building building) { }
}