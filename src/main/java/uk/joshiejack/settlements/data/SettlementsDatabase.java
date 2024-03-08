package uk.joshiejack.settlements.data;

import net.minecraft.data.PackOutput;
import uk.joshiejack.penguinlib.data.generator.AbstractDatabaseProvider;
import uk.joshiejack.settlements.Settlements;

public class SettlementsDatabase extends AbstractDatabaseProvider {
    public SettlementsDatabase(PackOutput output) {
        super(output, Settlements.MODID);
    }

    @Override
    protected void addDatabaseEntries() {

    }
}
