package uk.joshiejack.settlements.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import uk.joshiejack.settlements.Settlements;

public class SettlementsItemModels extends ItemModelProvider {
    public SettlementsItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Settlements.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
//        HusbandryItems.ITEMS.getEntries().stream()
//                .map(DeferredHolder::get)
//                .forEach(item -> {
//                    String path = BuiltInRegistries.ITEM.getKey(item).getPath();
//                    if (item instanceof BlockItem)
//                        getBuilder(path).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + path)));
//                    else {
//                        if (path.contains("spawn_egg")) {
//                            withExistingParent(path, mcLoc("item/template_spawn_egg"));
//                        } else {
//                            String subdir =
//                                    item.getFoodProperties(new ItemStack(item), null) != null ? "food/"
//                                            : path.contains("treat") ? "treat/"
//                                            : isFeed(path) ? "feed/"
//                                            : Strings.EMPTY;
//                            singleTexture(path, mcLoc("item/generated"), "layer0", modLoc("item/" + subdir + path.replace("_treat", "")));
//                        }
//                    }
//                });
    }
}
