package uk.joshiejack.settlements.world.quest.settings;

import net.minecraft.world.item.ItemStack;
import uk.joshiejack.penguinlib.util.PenguinGroup;
import uk.joshiejack.penguinlib.scripting.wrapper.ItemStackJS;

public class Information {
    private final PenguinGroup group;
    private String name = "Invalid Script Name";
    private ItemStack icon = ItemStack.EMPTY;
    private String description = "Invalid Script Description";
    private String unlocalized;

    public Information(PenguinGroup group) { this.group = group; }
    public Information(PenguinGroup group, String name, ItemStack icon, String description) {
        this(group);
        this.name = name;
        this.icon = icon;
        this.description = description;
    }

    public Information setIcon(ItemStackJS icon) { this.icon = icon.get(); return this; }
    public Information setTitle(String name) { this.name = name; return this; }
    public Information setDescription(String description) { this.description = description; return this; }

    public PenguinGroup getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }
}
