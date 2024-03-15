package uk.joshiejack.settlements.world.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import uk.joshiejack.settlements.Settlements;

public class SettlementsCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Settlements.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register(Settlements.MODID, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + Settlements.MODID))
            .icon(SettlementsItems.DESTROY::toStack)
            .displayItems((params, output) -> {
                //Add all the item found in HCItems.ITEMS that don't conform to GATHERING_BLOCKS
                SettlementsItems.ITEMS.getEntries().stream()
                        .map(DeferredHolder::get)
                        .forEach(item -> {
                            if (item == SettlementsItems.NPC_SPAWNER.get()) {
                                Settlements.Registries.NPCS.stream()
                                        .forEach(npc -> output.accept(SettlementsItems.NPC_SPAWNER.get().toStack(npc)));
                            } else
                                output.accept(item.asItem());
                        });
            }).build());
}