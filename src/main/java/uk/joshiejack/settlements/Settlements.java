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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
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
import uk.joshiejack.settlements.world.entity.SettlementsEntities;
import uk.joshiejack.settlements.world.entity.ai.action.Action;
import uk.joshiejack.settlements.world.entity.ai.action.ErrorAction;
import uk.joshiejack.settlements.world.entity.ai.action.chat.*;
import uk.joshiejack.settlements.world.entity.ai.action.item.GiftItemAction;
import uk.joshiejack.settlements.world.entity.ai.action.item.TakeItemAction;
import uk.joshiejack.settlements.world.entity.ai.action.item.TakeHeldItemAction;
import uk.joshiejack.settlements.world.entity.ai.action.level.SpawnEntityAction;
import uk.joshiejack.settlements.world.entity.ai.action.move.*;
import uk.joshiejack.settlements.world.entity.ai.action.quest.CompleteQuestAction;
import uk.joshiejack.settlements.world.entity.ai.action.status.AdjustNPCStatusAction;
import uk.joshiejack.settlements.world.entity.ai.action.status.SetNPCStatusAction;
import uk.joshiejack.settlements.world.entity.ai.action.status.SetTeamStatusAction;
import uk.joshiejack.settlements.world.entity.npc.NPC;
import uk.joshiejack.settlements.world.entity.npc.gifts.GiftQuality;
import uk.joshiejack.settlements.world.item.SettlementsCreativeTab;
import uk.joshiejack.settlements.world.item.SettlementsItems;
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
        SettlementsEntities.ENTITIES.register(eventBus);
        SettlementsItems.ITEMS.register(eventBus);
        SettlementsCreativeTab.CREATIVE_MODE_TABS.register(eventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SettlementsConfig.create());
    }

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        //Error
        Action.register("error", ErrorAction.class);
        //Chat
        Action.register("ask", AskAction.class);
        Action.register("greet", GreetAction.class);
        Action.register("look", LookAction.class);
        Action.register("next", NextAction.class);
        Action.register("say", SayAction.class);
        //Item
        Action.register("gift_item", GiftItemAction.class);
        Action.register("take_item", TakeItemAction.class);
        Action.register("take_held_item", TakeHeldItemAction.class);
        //Level
        Action.register("spawn_entity", SpawnEntityAction.class);
        //Move
        Action.register("attack", AttackAction.class);
        Action.register("move", MoveAction.class);
        Action.register("sleep", SleepAction.class);
        Action.register("teleport", TeleportAction.class);
        Action.register("wait", WaitAction.class);
        Action.register("wakeup", WakeupAction.class);
        //Quest
        Action.register("complete_quest", CompleteQuestAction.class);
        //Status
        Action.register("adjust_npc_status", AdjustNPCStatusAction.class);
        Action.register("set_npc_status", SetNPCStatusAction.class);
        Action.register("set_team_status", SetTeamStatusAction.class);
        //Tasks

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

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MODID, name);
    }

    public static class Registries {
        public static final ResourceLocation NONE = new ResourceLocation(Settlements.MODID, "none");
        public static final ReloadableRegistry<Building> BUILDINGS = new ReloadableRegistry<>(Settlements.MODID, "buildings", Building.CODEC, new Building(Template.EMPTY, Component.empty()), true);
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
