package uk.joshiejack.settlements.world.level.town.land;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraftforge.common.util.INBTSerializable;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.Pair;
import uk.joshiejack.penguinlib.util.helpers.minecraft.BlockPosHelper;
import uk.joshiejack.settlements.AdventureConfig;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.building.Building;
import uk.joshiejack.settlements.building.BuildingUpgrades;
import uk.joshiejack.settlements.util.TownFinder;
import uk.joshiejack.settlements.world.building.Building;
import uk.joshiejack.settlements.world.level.town.Town;
import uk.joshiejack.settlements.world.level.town.TownFinder;
import uk.joshiejack.settlements.world.town.Town;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static uk.joshiejack.settlements.world.level.town.land.Interaction.FOOTPRINT;
import static uk.joshiejack.settlements.world.town.land.Interaction.FOOTPRINT;

public class LandRegistry implements INBTSerializable<CompoundTag> {
    private final Multimap<Building, TownBuilding> buildings = HashMultimap.create();
    private final Cache<BlockPos, TownBuilding> closest = CacheBuilder.newBuilder().build();
    private final EnumMap<Interaction, List<BlockPos>> footprint = new EnumMap<>(Interaction.class);
    private final Town<?> town;

    public LandRegistry(Town<?> town) {
        this.town = town;
    }

    public void addBuilding(Level world, TownBuilding townBuilding) {
        buildings.get(townBuilding.getBuilding()).add(townBuilding);
        if (BuildingUpgrades.overrides(townBuilding.getBuilding())) { //Remove the overidden building?
            Building original = BuildingUpgrades.getTargetForUpgrade(townBuilding.getBuilding());
            buildings.get(original).removeIf(t -> t.getPosition().equals(townBuilding.getPosition()));
        }

        onBuildingsChanged(); //Reload them bitch
        TownFinder.getFinder(world).clearCache(); //Reset the cache for the towns
    }

    public void removeBuilding(Level world, TownBuilding townBuilding) {
        buildings.get(townBuilding.getBuilding()).remove(townBuilding);
        onBuildingsChanged(); //Reload them bitch
        TownFinder.getFinder(world).clearCache(); //Reset the cache for the towns
    }

    public void onBuildingsChanged() {
        closest.invalidateAll();
        footprint.clear();
        town.getCensus().onBuildingsChanged(buildings);
    }

    public void onNewDay(Level world) {
        buildings.values().forEach(b -> b.newDay(world, town));
    }

    public int getBuildingCount(Building building) {
        return buildings.get(building).size();
    }

    public int uniqueBuildingsCount() {
        return buildings.size();
    }

    @Nonnull
    public TownBuilding getBuildingLocation(Building building) {
        return buildings.containsKey(building) ? buildings.get(building).iterator().next() : TownBuilding.NULL;
    }


    public Pair<BlockPos, Rotation> getWaypoint(String waypoint) {
        for (Map.Entry<Building, TownBuilding> entry : buildings.entries()) {
            if (entry.getKey().hasWaypoint(waypoint)) {
                return Pair.of(BlockPosHelper.getTransformedPosition(entry.getKey().getWaypoint(waypoint), entry.getValue().getPosition(), entry.getValue().getRotation()), entry.getValue().getRotation());
            }
        }

        return Pair.of(town.getCentre(), Rotation.NONE);
    }

    @Nonnull
    public TownBuilding getClosestBuilding(Level world, BlockPos target) {
        try {
            return closest.get(target, () -> {
                TownBuilding closest = TownBuilding.NULL;
                double distance = Double.MAX_VALUE;
                for (TownBuilding location : buildings.values()) {
                    //BlockPos buildingPos = location.getCentre();
                    for (BlockPos buildingPos : location.getFootprint(world, FOOTPRINT)) {
                        double between = target.distToCenterSqr(buildingPos.getX(), buildingPos.getY(), buildingPos.getZ());
                        if (between < distance) {
                            closest = location;
                            distance = between;
                        }
                    }
                }

                return closest;
            });
        } catch (ExecutionException e) {
            return null;
        }
    }

    public double getDistance(BlockPos pos) {
        double distance = Double.MAX_VALUE;
        for (TownBuilding building : buildings.values()) {
            double placementDistance = building.getCentre().distToCenterSqr(pos.getX(), pos.getY(), pos.getZ());
            if (placementDistance < AdventureConfig.townDistance && placementDistance < distance) {
                distance = placementDistance;
            }
        }

        return distance;
    }

    public List<BlockPos> getFootprints(Level world, Interaction interaction) {
        if (!footprint.containsKey(interaction)) {
            List<BlockPos> list = Lists.newArrayList();
            buildings.values().forEach(b -> list.addAll(b.getFootprint(world, interaction)));
            footprint.put(interaction, list);
            return list;
        } else return footprint.get(interaction);
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        ListTag list = tag.getList("Buildings", 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag nbt = list.getCompound(i);
            Building building = Settlements.Registries.BUILDINGS.get(new ResourceLocation(nbt.getString("Building")));
            BlockPos pos = BlockPos.of(nbt.getLong("Pos"));
            Rotation rotation = Rotation.values()[nbt.getByte("Rotation")];
            boolean built = nbt.getBoolean("Built");
            TownBuilding townBuilding = new TownBuilding(building, pos, rotation, built);
            buildings.get(building).add(townBuilding);
        }

        onBuildingsChanged();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag buildingList = new ListTag();
        buildings.values().forEach((b) -> {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("Building", b.getBuilding().id().toString());
            nbt.putLong("Pos", b.getPosition().asLong());
            nbt.putByte("Rotation", (byte) b.getRotation().ordinal());
            nbt.putBoolean("Built", b.isBuilt());
            buildingList.add(nbt);
        });

        tag.put("Buildings", buildingList);
        return tag;
    }
}


