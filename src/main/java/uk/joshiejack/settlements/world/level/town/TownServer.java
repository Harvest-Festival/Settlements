package uk.joshiejack.settlements.world.level.town;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.level.Level;
import uk.joshiejack.settlements.world.level.town.people.CensusServer;
import uk.joshiejack.settlements.world.town.people.CensusServer;

public class TownServer extends Town<CensusServer> {
    public static final TownServer NULL = new TownServer(0, BlockPos.ZERO);
    public TownServer(int id, BlockPos centre) {
        super(id, centre);
    }

    @Override
    protected CensusServer createCensus() {
        return new CensusServer(this);
    }

    public void newDay(Level world) {
        getLandRegistry().onNewDay(world);
        census.onNewDay(world);
    }

    @Override
    public CompoundTag getTagForSync() {
        return super.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        super.deserializeNBT(tag);
        census.deserializeNBT(tag.getCompound("Census"));
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag data = super.serializeNBT();
        data.put("Census", census.serializeNBT());
        return data;
    }
}
