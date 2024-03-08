package uk.joshiejack.settlements.world.quest.settings;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum Repeat implements StringRepresentable {
    NONE(-1), ALWAYS(0), DAILY(1), WEEKLY(7), SEASONLY(28), YEARLY(365);

    private final int days;

    Repeat(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
