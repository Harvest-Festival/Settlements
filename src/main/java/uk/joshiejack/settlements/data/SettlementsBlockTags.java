package uk.joshiejack.settlements.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.level.town.land.Protection;

import java.util.concurrent.CompletableFuture;

public class SettlementsBlockTags extends BlockTagsProvider {
    public SettlementsBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Settlements.MODID, existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(Protection.RIGHT_CLICK_PREVENTION).addTags(BlockTags.BUTTONS, BlockTags.TRAPDOORS);
    }
}
