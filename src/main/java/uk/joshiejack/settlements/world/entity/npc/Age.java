package uk.joshiejack.settlements.world.entity.npc;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum Age implements StringRepresentable {
    CHILD, ADULT;

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
