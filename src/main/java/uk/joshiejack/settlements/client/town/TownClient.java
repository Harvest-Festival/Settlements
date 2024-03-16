package uk.joshiejack.settlements.client.town;

import net.minecraft.core.BlockPos;
import uk.joshiejack.settlements.world.level.town.Town;

public class TownClient extends Town<CensusClient> {
    public static final Town<?> NULL = new TownClient(0, BlockPos.ZERO);

    public TownClient(int id, BlockPos centre) {
        super(id, centre);
    }

    @Override
    protected CensusClient createCensus() {
        return new CensusClient();
    }
}
