package uk.joshiejack.settlements.data;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

public class SettlementsLootTables extends LootTableProvider {
    public SettlementsLootTables(PackOutput output) {
        super(output, Set.of(), List.of(new SubProviderEntry(SettlementsBlockLootTables::new, LootContextParamSets.BLOCK)));
    }
}