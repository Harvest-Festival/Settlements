package uk.joshiejack.settlements.world.level.town;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.UsernameCache;
import net.neoforged.neoforge.common.util.INBTSerializable;
import uk.joshiejack.penguinlib.util.helper.TimeHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

/*Information about the town*/
public class TownCharter implements INBTSerializable<CompoundTag> {
    private Component name;
    private long founded;
    private Component founder;
    private UUID teamUUID;
    private Component mayor;

    public void setFoundingInformation(Component name, Component founder, long founded, UUID id) {
        this.name = name;
        this.founder = founder;
        this.founded = founded;
        this.teamUUID = id;
        this.mayor = founder;
    }

    public Component getName() {
        return name;
    }

    public void setName(Component name) {
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
    public Component getFounder() {
        return founder;
    }

    public void setMayorString(Component mayor) {
        this.mayor = mayor;
    }

    public void setMayor(UUID mayor) {
        if (mayor == null) {
            this.mayor = null;
        } else this.mayor = Component.literal(Objects.requireNonNull(UsernameCache.getLastKnownUsername(mayor)));
    }

    public boolean hasMayor() {
        return mayor != null;
    }

    @Nonnull
    public Component getMayor() {
        return hasMayor() ? mayor : Component.literal("Unclaimed");
    } //TODO: TRANSLATE

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
