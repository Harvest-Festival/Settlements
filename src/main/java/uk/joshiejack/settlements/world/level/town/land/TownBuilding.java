package uk.joshiejack.settlements.world.level.town.land;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.init.Blocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import uk.joshiejack.penguinlib.scripting.Interpreter;
import uk.joshiejack.penguinlib.scripting.ScriptFactory;
import uk.joshiejack.penguinlib.scripting.Scripting;
import uk.joshiejack.penguinlib.template.Placeable;
import uk.joshiejack.penguinlib.template.Template;
import uk.joshiejack.penguinlib.template.blocks.PlaceableBlock;
import uk.joshiejack.penguinlib.template.blocks.PlaceableWaypoint;
import uk.joshiejack.penguinlib.template.entities.PlaceableItemFrame;
import uk.joshiejack.penguinlib.util.helpers.minecraft.BlockPosHelper;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.building.Building;
import uk.joshiejack.settlements.world.building.Building;
import uk.joshiejack.settlements.world.building.Template;
import uk.joshiejack.settlements.world.level.town.Town;
import uk.joshiejack.settlements.world.town.Town;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class TownBuilding {
    public static final TownBuilding NULL = new TownBuilding(Settlements.Registries.BUILDINGS.emptyEntry(), BlockPos.ZERO, Rotation.NONE, false);
    private static final List<BlockPos> EMPTY = Lists.newArrayList();
    private final Cache<BlockState, List<BlockPos>> posByType = CacheBuilder.newBuilder().build();
    private final Cache<String, List<BlockPos>> posByWaypoint = CacheBuilder.newBuilder().build();
    private final Cache<Interaction, List<BlockPos>> footprints = CacheBuilder.newBuilder().build();
    private final Building building;
    private final BlockPos pos;
    private final Rotation rotation;
    private boolean built;

    public TownBuilding(Building building, BlockPos pos, Rotation rotation, boolean built) {
        this.building = building;
        this.pos = pos;
        this.rotation = rotation;
        this.built = built;
    }

    public void newDay(Level world, Town<?> town) {
        ResourceLocation resource = building.getDailyHandler();
        if (resource != null) {
            Interpreter<?> script = ScriptFactory.getScript(resource);
            if (script != null) {
                script.callFunction("newDay", world, town, this);
            }
        }
    }

    public Building getBuilding() {
        return building;
    }

    public TownBuilding setBuilt() {
        this.built = true;
        return this;
    }

    public BlockPos getPosition() {
        return pos;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public BlockPos getCentre() {
        return pos.offset(building.template().offset().getX(), building.template().offset().getY(), building.template().offset().getZ());
    }

    public List<BlockPos> getFootprint(Level world, Interaction interaction) {
        try {
            return footprints.get(interaction, () -> {
                List<BlockPos> footprint = Lists.newArrayList();
                Template template = building.template();
                for (Placeable placeable: template.getComponents()) {
                    if (interaction == Interaction.ENTITY_INTERACT) {
                        if (placeable instanceof PlaceableItemFrame) {
                            footprint.add(BlockPosHelper.getTransformedPosition(placeable.getOffsetPos(), pos, rotation));
                        }
                    } else {
                        if (!(placeable instanceof PlaceableBlock)) continue;
                        PlaceableBlock block = (PlaceableBlock) placeable;
                        BlockPos transformed = BlockPosHelper.getTransformedPosition(block.getOffsetPos(), pos, rotation);
                        if (interaction == Interaction.FOOTPRINT) footprint.add(transformed);
                        else {
                            IBlockState external = world.getBlockState(transformed);
                            IBlockState internal = block.getTransformedState(rotation);
                            if (external.getBlock() != Blocks.AIR && internal.getBlock() != Blocks.AIR
                                    && internal.getMaterial().getMaterialMapColor() != MapColor.FOLIAGE) {
                                // ^ Allow for destruction of plants and air, at all times
                                if (interaction == Interaction.RIGHT_CLICK && Protection.RIGHT_CLICK_PREVENTION_LIST.contains(internal.getBlock())) {
                                    if (!block.isInteractable()) footprint.add(transformed);
                                } else if (interaction == Interaction.BREAK) {
                                    footprint.addAll(block.getPositions(transformed));
                                }
                            }
                        }
                    }
                }

                return footprint;
            });
        } catch (ExecutionException e) {
            return EMPTY;
        }
    }

    public List<BlockPos> getBlockPosByType(BlockState state) {
        try {
            return posByType.get(state, () -> {
                Set<BlockPos> f = Sets.newHashSet();
                for (Placeable placeable : building.template().getComponents()) {
                    if (placeable instanceof PlaceableBlock && ((PlaceableBlock) placeable).getTransformedState(rotation).equals(state)) {
                        f.add(BlockPosHelper.getTransformedPosition(placeable.getOffsetPos(), pos, rotation));
                    }
                }

                return Lists.newArrayList(f);
            });
        } catch (ExecutionException e) {
            return Lists.newArrayList();
        }
    }

    public List<BlockPos> getWaypointsByPrefix(String waypoint) {
        try {
            return posByWaypoint.get(waypoint, () -> {
                Set<BlockPos> f = Sets.newHashSet();
                for (Placeable placeable : building.template().getComponents()) {
                    if (placeable instanceof PlaceableWaypoint && ((PlaceableWaypoint) placeable).getName().startsWith(waypoint)) {
                        f.add(BlockPosHelper.getTransformedPosition(placeable.getOffsetPos(), pos, rotation));
                    }
                }

                return Lists.newArrayList(f);
            });
        } catch (ExecutionException e) {
            return Lists.newArrayList();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TownBuilding that = (TownBuilding) o;
        return Objects.equals(building, that.building) &&
                Objects.equals(pos, that.pos) &&
                rotation == that.rotation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(building, pos, rotation);
    }

    public boolean isBuilt() {
        return built;
    }
}
