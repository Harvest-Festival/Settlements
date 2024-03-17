package uk.joshiejack.settlements.world.level.town.people;

import net.minecraft.network.chat.Component;
import uk.joshiejack.settlements.Settlements;

import java.util.Locale;

public enum Ordinance {
    INTERNAL_PROTECTION(true), //Protect buildings from being destroyed by your own members
    EXTERNAL_PROTECTION(false), //Protect buildings from being destroyed by non members
    NO_KILL(false),  //Disable PVP in town limits
    BAN_CONSTRUCTION(false), //Non members cannot build on your land
    BAN_COMMUNICATION(false), //Non members cannot talk with your residents
    BAN_INTERACTION(false), //Non members cannot talk with your blocks
    BAN_ITEM_USAGE(false); //Non members cannot use any items within your town except for food

    private final boolean enabled;

    Ordinance(boolean enabled) {
        this.enabled = enabled;
    }

    public Component getName() {
        return Component.translatable(Settlements.MODID + ".ordinance." + name().toLowerCase(Locale.ENGLISH) + ".name");
    }

    public Component getDescription() {
        return Component.translatable(Settlements.MODID + ".ordinance." + name().toLowerCase(Locale.ENGLISH) + ".description");
    }

    public Component getTooltip() {
        return Component.translatable(Settlements.MODID + ".ordinance." + name().toLowerCase(Locale.ENGLISH) + ".tooltip");
    }

    public boolean isEnabledByDefault() {
        return enabled;
    }
}
