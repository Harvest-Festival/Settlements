package uk.joshiejack.settlements.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import uk.joshiejack.settlements.Settlements;

public class SettlementsSoundDefinitions extends SoundDefinitionsProvider {
    public SettlementsSoundDefinitions(PackOutput output, ExistingFileHelper helper) {
        super(output, Settlements.MODID, helper);
    }

    @Override
    public void registerSounds() {

    }

    protected static SoundDefinition.Sound sound(final String name) {
        return sound(new ResourceLocation(Settlements.MODID, name));
    }
}
