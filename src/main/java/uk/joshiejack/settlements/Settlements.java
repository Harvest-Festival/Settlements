package uk.joshiejack.settlements;

import com.mojang.serialization.Codec;
import net.minecraft.DetectedVersion;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;
import uk.joshiejack.settlements.data.SettlementsBuildings;
import uk.joshiejack.settlements.data.SettlementsQuests;
import uk.joshiejack.settlements.world.building.Building;
import uk.joshiejack.settlements.world.building.Template;
import uk.joshiejack.settlements.world.entity.npc.NPC;
import uk.joshiejack.settlements.world.entity.npc.gifts.GiftQuality;
import uk.joshiejack.settlements.world.quest.Quest;
import uk.joshiejack.settlements.world.quest.settings.Settings;

import java.util.Optional;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(Settlements.MODID)
public class Settlements {
    public static final String MODID = "settlements";
    public static final Logger LOGGER = LogManager.getLogger();

    public Settlements(IEventBus eventBus) {
        Registries.init();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SettlementsConfig.create());
    }

    @SubscribeEvent
    public static void onDataGathering(final GatherDataEvent event) {
        final DataGenerator generator = event.getGenerator();
        final PackOutput output = event.getGenerator().getPackOutput();
        //Add the datapack entries
//        //Client
//        generator.addProvider(event.includeClient(), new SettlementsBlockStates(output, event.getExistingFileHelper()));
//        generator.addProvider(event.includeClient(), new SettlementsItemModels(output, event.getExistingFileHelper()));
//        generator.addProvider(event.includeClient(), new SettlementsLanguage(output));
//        generator.addProvider(event.includeClient(), new SettlementsSoundDefinitions(output, event.getExistingFileHelper()));
//
//        //Server
//        SettlementsBlockTags blocktags = new SettlementsBlockTags(output, event.getLookupProvider(), event.getExistingFileHelper());
//        generator.addProvider(event.includeServer(), blocktags);
//        generator.addProvider(event.includeServer(), new SettlementsLootTables(output));
//        generator.addProvider(event.includeServer(), new SettlementsItemTags(output, event.getLookupProvider(), blocktags.contentsGetter(), event.getExistingFileHelper()));
//        generator.addProvider(event.includeServer(), new SettlementsRecipes(output));
//        generator.addProvider(event.includeServer(), new SettlementsDatabase(output));
//        generator.addProvider(event.includeServer(), new SettlementsNotes(output));
        generator.addProvider(event.includeServer(), new SettlementsBuildings(output, Registries.BUILDINGS));
        generator.addProvider(event.includeServer(), new SettlementsQuests(output, Registries.QUESTS));
        generator.addProvider(true, new PackMetadataGenerator(output).add(PackMetadataSection.TYPE, new PackMetadataSection(
                Component.literal("Resources for Settlements"),
                DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA),
                Optional.of(new InclusiveRange<>(0, Integer.MAX_VALUE)))));
    }

    public static class Registries {
        public static final ResourceLocation NONE = new ResourceLocation(Settlements.MODID, "none");
        public static final ReloadableRegistry<Building> BUILDINGS = new ReloadableRegistry<>(Settlements.MODID, "buildings", Building.CODEC, new Building(Template.EMPTY, Items.AIR), true);
        public static final ReloadableRegistry<Quest> QUESTS = new ReloadableRegistry<>(Settlements.MODID, "quests", Quest.CODEC, new Quest(Settings.DEFAULT), true);
        public static final ReloadableRegistry<GiftQuality> GIFT_QUALITIES = new ReloadableRegistry<>(Settlements.MODID, "gift_qualities", GiftQuality.CODEC, GiftQuality.NORMAL, true);
        public static final ReloadableRegistry<NPC> NPCS = new ReloadableRegistry<>(Settlements.MODID, "npcs", NPC.CODEC, NPC.NULL, true);
        public static void init() { }
    }

    @Mod.EventBusSubscriber(modid = Settlements.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Data {
        public static final DataMapType<Item, String> GIFT_CATEGORIES = DataMapType.builder(new ResourceLocation(Settlements.MODID, "gift_category"), net.minecraft.core.registries.Registries.ITEM,
                Codec.STRING).synced(Codec.STRING, true).build();

        @SubscribeEvent
        public static void onDataMap(RegisterDataMapTypesEvent event) {
            event.register(GIFT_CATEGORIES);
        }
    }

    public static class SettlementsConfig {
        SettlementsConfig(ModConfigSpec.Builder builder) {
            builder.push("General Settings");
            builder.pop();
        }

        public static ModConfigSpec create() {
            return new ModConfigSpec.Builder().configure(SettlementsConfig::new).getValue();
        }
    }
}
