package uk.joshiejack.settlements.world.entity.npc.gifts;

import net.minecraft.world.item.ItemStack;
import uk.joshiejack.settlements.world.entity.npc.NPC;

public class GiftRegistry {
    //We assign the default quality of items by using item tags
    public static GiftQuality getQualityForNPC(NPC npc, ItemStack stack) {
        return GiftQuality.getQualityFromValue(npc.getGiftQuality(stack));
    }
//
//    @SuppressWarnings("ConstantConditions")
//    @SubscribeEvent(priority = EventPriority.LOW)
//    public static void onDatabaseLoaded(DatabaseLoadedEvent.LoadComplete event) { //LOW PRIORITY //TODO: Move to Adventure NPCs
//        event.table("gift_quality").rows().forEach(row -> new GiftQuality(row.get("name"), row.get("value")));
//        event.table("gift_categories").rows().forEach(category -> new GiftCategory(category.get("name"), GiftQuality.get(category.get("quality"))));
//        CATEGORY_REGISTRY = new HolderRegistry<>(GiftCategory.get("none")); //Default to no value, should not be removed from the list
//        GiftCategory.REGISTRY.values().forEach(theCategory -> event.table(theCategory.name() + "_gift_mappings").rows()
//                .forEach(mapping -> {
//                    Holder holder = mapping.holder();
//                    if (!holder.isEmpty()) {
//                        CATEGORY_REGISTRY.register(holder, theCategory);
//                        //TODO: CONFIG Adventure.logger.log(Level.INFO, "Registered " + holder + " as the gift category " + theCategory.name());
//                    }
//        }));
//
//        NPC.all().forEach(npc -> {
//            CATEGORY_OVERRIDES.put(npc, Maps.newHashMap()); // Insert the map for this npc on the overrides
//            event.table(npc.getRegistryName().getResourcePath() + "_gift_preferences").rows().forEach(row -> {
//                GiftQuality quality = GiftQuality.get(row.get("quality"));
//                if (quality != null) {
//                    //We're going through all the data for this npc now
//                    String item = row.get("item");
//                    if (item.startsWith("cat#")) {
//                        CATEGORY_OVERRIDES.get(npc).put(GiftCategory.get(item.substring(4)), quality);
//                        //TODO: CONFIG Adventure.logger.log(Level.INFO, "Registered gift preference for " + npc.getLocalizedName() + ": "
//                               // + GiftCategory.get(item.substring(4)).name() + " as " + quality.name());
//                    } else {
//                        Holder holder = Holder.getFromString(item);
//                        ITEM_OVERRIDES.get(npc).add(Pair.of(holder, quality));
//                        Settlements.logger.log(Level.INFO, "Registered gift preference for " + npc.getLocalizedName() + ": "
//                                + holder + " as : " + quality.name());
//                    }
//                } else {
//                    Settlements.logger.log(Level.INFO, "Failed to register gift preference for " + npc.getLocalizedName() + ": with the item as"
//                            + row.get("item") + " because " + quality.name() + " is not a valid quality");
//                }
//            });
//        });
//    }
//
//    public static boolean isValidMod(String modid) {
//        for (String s : AdventureConfig.enableGiftLoggingForModIDs.split(",")) {
//            if (s.equals(modid)) return true;
//        }
//
//        return false;
//    }
}
