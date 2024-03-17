package uk.joshiejack.settlements.world.level.town.people;

import net.minecraft.network.chat.Component;
import uk.joshiejack.settlements.Settlements;

import java.util.Locale;

public enum Citizenship {
    CLOSED, OPEN, APPLICATION;

    public Component getName() {
        return Component.translatable(Settlements.MODID + ".citizenship." + name().toLowerCase(Locale.ENGLISH) + ".name");
    }

    public Component getType() {
        return Component.translatable(Settlements.MODID + ".citizenship.name");
    }

    public Component getDescription() {
        return Component.translatable(Settlements.MODID + ".citizenship." + name().toLowerCase(Locale.ENGLISH) + ".description");
    }

    public Component getTooltip() {
        return Component.translatable(Settlements.MODID + ".citizenship." + name().toLowerCase(Locale.ENGLISH) + ".tooltip");
    }

    public Citizenship next() {
        return this == Citizenship.OPEN ? Citizenship.APPLICATION : this == Citizenship.APPLICATION ? Citizenship.CLOSED : Citizenship.OPEN;
    }
}
