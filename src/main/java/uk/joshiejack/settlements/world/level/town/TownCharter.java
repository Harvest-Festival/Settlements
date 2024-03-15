package uk.joshiejack.settlements.world.level.town;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.UsernameCache;
import net.neoforged.neoforge.common.util.INBTSerializable;
import uk.joshiejack.penguinlib.util.helper.TimeHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/*Information about the town*/
public class TownCharter implements INBTSerializable<CompoundTag> {
    private String name;
    private long founded;
    private String founder;
    private UUID teamUUID;
    private String mayor;

    public void setFoundingInformation(String name, String founder, long founded, UUID id) {
        this.name = name;
        this.founder = founder;
        this.founded = founded;
        this.teamUUID = id;
        this.mayor = founder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nonnull
    public UUID getTeamID() {
        return teamUUID;
    }

    public long getFoundingDate() {
        return founded;
    }

    @Nullable
    public String getFounder() {
        return founder;
    }

    public void setMayorString(String mayor) {
        this.mayor = mayor;
    }

    public void setMayor(UUID mayor) {
        if (mayor == null) {
            this.mayor = null;
        } else this.mayor = UsernameCache.getLastKnownUsername(mayor);
    }

    public boolean hasMayor() {
        return mayor != null;
    }

    @Nonnull
    public String getMayor() {
        return hasMayor() ? mayor : "Unclaimed";
    }

    public int getAge(long time) {
        int created = TimeHelper.getElapsedDays(founded);
        int current = TimeHelper.getElapsedDays(time);
        return current - created;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        name = tag.getString("Name");
        founded = tag.getLong("FoundingDate");
        founder = tag.getString("Founder");
        teamUUID = UUID.fromString(tag.getString("Team"));
        mayor = tag.contains("Mayor") ? tag.getString("Mayor") : null;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Name", name);
        tag.putLong("FoundingDate", founded);
        tag.putString("Team", teamUUID.toString());
        tag.putString("Founder", founder);
        if (mayor != null) tag.putString("Mayor", mayor);
        return tag;
    }
}
