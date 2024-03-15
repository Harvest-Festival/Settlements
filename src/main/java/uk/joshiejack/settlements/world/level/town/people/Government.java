package uk.joshiejack.settlements.world.level.town.people;

import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public class Government implements INBTSerializable<CompoundTag> {
    private final EnumSet<Ordinance> laws = EnumSet.noneOf(Ordinance.class);
    private final Set<UUID> applications = Sets.newHashSet();
    private Citizenship citizenship = Citizenship.APPLICATION;

    public boolean hasLaw(Ordinance law) {
        return laws.contains(law);
    }

    public void addApplication(UUID uuid) {
        applications.add(uuid);
    }

    public Set<UUID> getApplications() {
        return applications;
    }

    public Citizenship getCitizenship() {
        return citizenship;
    }

    public void toggleCitizenship() {
        citizenship = citizenship.next();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        CompoundTag laws = tag.getCompound("Laws");
        for (Ordinance ordinance : Ordinance.values()) {
            if (laws.getBoolean(ordinance.name())) this.laws.add(ordinance);
            if (!laws.contains(ordinance.name()) && ordinance.isEnabledByDefault()) {
                this.laws.add(ordinance);
            }
        }

        if (tag.contains("Applications")) {
            ListTag apps = tag.getList("Applications", 8);
            for (int i = 0; i < apps.size(); i++) {
                applications.add(UUID.fromString(apps.getString(i)));
            }
        }

        citizenship = tag.contains("Citizenship") ? Citizenship.valueOf(tag.getString("Citizenship")) : Citizenship.OPEN;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        CompoundTag laws = new CompoundTag();
        for (Ordinance ordinance : Ordinance.values()) {
            laws.putBoolean(ordinance.name(), this.laws.contains(ordinance));
        }

        tag.put("Laws", laws);

        if (!applications.isEmpty()) {
            ListTag apps = new ListTag();
            applications.forEach(uuid -> apps.add(StringTag.valueOf(uuid.toString())));
            tag.put("Applications", apps);
        }

        tag.putString("Citizenship", citizenship.name());

        return tag;
    }

    public void setLaw(Ordinance ordinance, boolean enact) {
        if (enact) laws.add(ordinance);
        else laws.remove(ordinance);
    }
}
