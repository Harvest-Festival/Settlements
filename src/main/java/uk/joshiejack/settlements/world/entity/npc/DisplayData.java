package uk.joshiejack.settlements.world.entity.npc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import uk.joshiejack.penguinlib.world.item.PenguinRegistryItem;

public interface DisplayData extends PenguinRegistryItem.Nameable {
    NPCClass getNPCClass();

    ResourceLocation id();

    ItemStack getIcon();
}
