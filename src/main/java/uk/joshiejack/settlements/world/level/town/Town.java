package uk.joshiejack.settlements.world.level.town;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import uk.joshiejack.settlements.world.level.town.land.LandRegistry;
import uk.joshiejack.settlements.world.level.town.people.AbstractCensus;
import uk.joshiejack.settlements.world.level.town.people.Government;

import java.util.Objects;

public abstract class Town<C extends AbstractCensus> implements INBTSerializable<CompoundTag> {
    private final TownCharter charter = new TownCharter();
    private final Government government = new Government();
    private final LandRegistry landRegistry = new LandRegistry(this);
    protected final C census;
    private final BlockPos centre;
    private final int id;

    public Town(int id, BlockPos centre) {
        this.id = id;
        this.centre = centre;
        this.census = createCensus();
    }

    @SuppressWarnings("unchecked")
    protected abstract C createCensus();

    public TownCharter getCharter() {
        return charter;
    }

    public LandRegistry getLandRegistry() {
         return landRegistry;
    }

    public Government getGovernment() {
        return government;
    }

    public C getCensus() {
        return census;
    }

    public int getID() {
        return id;
    }

    public BlockPos getCentre() {
        return centre;
    }

    public CompoundTag getTagForSync() {
        return serializeNBT();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("Charter", charter.serializeNBT());
        tag.put("Government", government.serializeNBT());
        tag.put("LandRegistry", landRegistry.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        charter.deserializeNBT(tag.getCompound("Charter"));
        government.deserializeNBT(tag.getCompound("Government"));
        landRegistry.deserializeNBT(tag.getCompound("LandRegistry"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Town<?> town = (Town<?>) o;
        return id == town.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    //TODO: ???
//    public int getRadius() {
//        return HCConfig.maxWildernessDistance;
//    }
}
