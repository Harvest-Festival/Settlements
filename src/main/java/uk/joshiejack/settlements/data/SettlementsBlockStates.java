package uk.joshiejack.settlements.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import uk.joshiejack.settlements.Settlements;

public class SettlementsBlockStates extends BlockStateProvider {
    public SettlementsBlockStates(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Settlements.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

    }
}
