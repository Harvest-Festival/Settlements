package uk.joshiejack.settlements.world.entity.npc;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface NPCInfo extends DisplayData {
    @Nullable
    ResourceLocation getSkin();
    int getOutsideColor();
    int getInsideColor();
    Component getGreeting(RandomSource random);
    int getGiftQuality(ItemStack stack);
    String substring(String name);
    int getData(String name);
    void callScript(String function, Object... params);

    String getOccupation();

    @Nullable
    default ResourceLocation getLootTable() {
        return null;
    }
}
