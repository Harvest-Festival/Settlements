package uk.joshiejack.settlements.world.entity.npc;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface DisplayData {
    NPCClass getNPCClass();

    ResourceLocation id();

    Component getLocalizedName();

    ItemStack getIcon();
}
