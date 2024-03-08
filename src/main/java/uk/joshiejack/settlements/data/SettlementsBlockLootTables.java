package uk.joshiejack.settlements.data;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Set;

public class SettlementsBlockLootTables extends BlockLootSubProvider {
    protected SettlementsBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return new ArrayList<>();
    }

    @Override
    protected void generate() {

    }
}
