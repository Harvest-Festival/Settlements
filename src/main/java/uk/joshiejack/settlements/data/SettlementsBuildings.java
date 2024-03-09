package uk.joshiejack.settlements.data;

import net.minecraft.core.BlockPos;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import uk.joshiejack.penguinlib.data.generator.AbstractPenguinRegistryProvider;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.building.Building;
import uk.joshiejack.settlements.world.building.Template;

import java.util.HashMap;
import java.util.Map;

public class SettlementsBuildings extends AbstractPenguinRegistryProvider<Building> {
    public SettlementsBuildings(PackOutput output, ReloadableRegistry<Building> registry) {
        super(output, registry);
    }

    @Override
    protected void buildRegistry(Map<ResourceLocation, Building> map) {
        Map<BlockPos, Template.BlockData> original = new HashMap<>(2);
        int tempX = 5;
        int tempZ = 3;
        int tempY = 2;
        for (short y = 0; y < tempY; y++) {
            for (short z = 0; z < tempZ; z++) {
                for (short x = 0; x < tempX; x++) {
                    final BlockPos tempPos = new BlockPos(x, y, z);
                    final Template.BlockData blockInfo = new Template.BlockData(x == 0 || z == 0 || x == tempX - 1 || z == tempZ - 1 ? Blocks.OAK_LOG.defaultBlockState() : Blocks.OAK_TRAPDOOR.defaultBlockState(), null, null);
                    original.put(tempPos, blockInfo);
                    if (x == 0 && z == 0) {
                        original.put(tempPos, new Template.BlockData(Blocks.WATER.defaultBlockState(), null, null));
                    }
                }
            }
        }

        map.put(new ResourceLocation(Settlements.MODID, "test"), new Building(new Template(original, tempX, tempY, tempZ), Component.literal("Test Building")));
    }
}