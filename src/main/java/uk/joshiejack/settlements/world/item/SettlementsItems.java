package uk.joshiejack.settlements.world.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import uk.joshiejack.settlements.Settlements;


public class SettlementsItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Settlements.MODID);
    public static final DeferredItem<NPCSpawnerItem> NPC_SPAWNER = ITEMS.register("npc_spawner", () -> new NPCSpawnerItem(new Item.Properties()));
    public static final DeferredItem<RandomNPCSpawnerItem> CUSTOM_NPC_SPAWNER = ITEMS.register("custom_npc_spawner", () -> new RandomNPCSpawnerItem(new Item.Properties()));
    public static final DeferredItem<BuildingItem> BUILDING = ITEMS.register("building", () -> new BuildingItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<BlueprintItem> BLUEPRINT = ITEMS.register("blueprint", () -> new BlueprintItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<DestroyBuildingItem> DESTROY = ITEMS.register("destroy", () -> new DestroyBuildingItem(new Item.Properties()));
    //public static final DeferredItem<ItemJournal> JOURNAL = ITEMS.register("journal", () -> new ItemJournal(new Item.Properties().stacksTo(1));
    public static final DeferredItem<BuilderRendererItem> previewer = ITEMS.register("previewer", () -> new BuilderRendererItem(new Item.Properties().stacksTo(1)));
}