package uk.joshiejack.settlements.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.settlements.Settlements;

import java.util.concurrent.CompletableFuture;

public class SettlementsItemTags extends ItemTagsProvider {
    public SettlementsItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagLookup<Block>> blockLookup, ExistingFileHelper existingFileHelper) {
        super(output, provider, blockLookup, Settlements.MODID, existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

    }
}
