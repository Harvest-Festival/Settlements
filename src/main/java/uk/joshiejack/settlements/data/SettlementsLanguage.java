package uk.joshiejack.settlements.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import uk.joshiejack.settlements.Settlements;

public class SettlementsLanguage extends LanguageProvider {
    public SettlementsLanguage(PackOutput output) {
        super(output, Settlements.MODID, "en_us");
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void addTranslations() {
        add("itemGroup.settlements", "Settlements");
       // HusbandryItems.ITEMS.getEntries().forEach((item) -> addItem(item, WordUtils.capitalizeFully(item.getId().getPath().replace("_", " "))));
    }
}
