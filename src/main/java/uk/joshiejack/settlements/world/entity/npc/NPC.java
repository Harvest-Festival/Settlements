package uk.joshiejack.settlements.world.entity.npc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.logging.log4j.util.Strings;
import uk.joshiejack.penguinlib.scripting.ScriptFactory;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.entity.npc.gifts.GiftCategory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.minecraft.network.chat.TextColor.parseColor;

public final class NPC implements ReloadableRegistry.PenguinRegistry<NPC>, NPCInfo {
    private static final ResourceLocation NULL_ID = new ResourceLocation(Settlements.MODID, "null");
    public record ItemData(Ingredient item, int value) {
        public static final Codec<ItemData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                        Ingredient.CODEC.fieldOf("item").forGetter(ItemData::item),
                        Codec.INT.fieldOf("value").forGetter(ItemData::value))
                .apply(inst, ItemData::new));
    }

    public record CategoryData(String category, int value) {
        public static final Codec<CategoryData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                        Codec.STRING.fieldOf("category").forGetter(CategoryData::category),
                        Codec.INT.fieldOf("value").forGetter(CategoryData::value))
                .apply(inst, CategoryData::new));
    }

    public static final Codec<NPC> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                    ResourceLocation.CODEC.optionalFieldOf("loot_table", NULL_ID).forGetter(NPC::getLootTable),
                    ResourceLocation.CODEC.optionalFieldOf("script", NULL_ID).forGetter(obj -> obj.script),
                    Codec.STRING.optionalFieldOf("player_skin", Strings.EMPTY).forGetter(obj -> obj.playerSkin),
                    Codec.STRING.optionalFieldOf("occupation", "villager").forGetter(NPC::getOccupation),
                    NPCClass.CODEC.optionalFieldOf("class", NPCClass.NULL).forGetter(obj -> obj.clazz),
                    Codec.STRING.optionalFieldOf("inside_color", "#FFFFFF").forGetter(n -> n.insideColor),
                    Codec.STRING.optionalFieldOf("outside_color", "#000000").forGetter(n -> n.outsideColor),
                    ItemData.CODEC.listOf().optionalFieldOf("gift_item_overrides", new ArrayList<>()).forGetter(obj -> obj.giftItemOverrides.object2IntEntrySet().stream().map(e -> new ItemData(e.getKey(), e.getIntValue())).toList()),
                    CategoryData.CODEC.listOf().optionalFieldOf("gift_category_overrides", new ArrayList<>()).forGetter(obj -> obj.giftCategoryOverrides.object2IntEntrySet().stream().map(e -> new CategoryData(e.getKey(), e.getIntValue())).toList()))
            .apply(inst, NPC::new));
    public static final ResourceLocation MISSING_TEXTURE = new ResourceLocation(Settlements.MODID, "textures/entity/missing.png");
    private static final Object2IntMap<NPC> INSIDE_COLORS = new Object2IntOpenHashMap<>();
    private static final Object2IntMap<NPC> OUTSIDE_COLORS = new Object2IntOpenHashMap<>();
    public static final NPC NULL = new NPC(NULL_ID, NULL_ID, Strings.EMPTY, Strings.EMPTY, NPCClass.NULL, "#FFFFFF", "#000000", Collections.emptyList(), Collections.emptyList()).setOccupation(Strings.EMPTY).setNPCClass(NPCClass.NULL);
    private final Object2IntMap<String> data = new Object2IntOpenHashMap<>();
    private final Object2IntMap<String> giftCategoryOverrides = new Object2IntOpenHashMap<>();
    private final Object2IntMap<Ingredient> giftItemOverrides = new Object2IntOpenHashMap<>();
    private final ResourceLocation lootTable;
    private final ResourceLocation script;
    private String localizedName;
    private ResourceLocation skin;
    private String playerSkin;
    private String occupation;
    private NPCClass clazz;
    private final String insideColor;
    private final String outsideColor;

    public NPC(ResourceLocation lootTable,
               ResourceLocation script,
               String playerSkin,
               String occupation,
               @Nonnull NPCClass clazz,
               String insideColor,
               String outsideColor,
               List<ItemData> itemGiftOverrides,
               List<CategoryData> categoryGiftOverrides) {
        this.lootTable = lootTable;
        this.script = script;
        this.playerSkin = playerSkin;
        this.occupation = occupation;
        this.clazz = clazz;
        this.insideColor = insideColor;
        this.outsideColor = outsideColor;
        if (itemGiftOverrides != null) {
            itemGiftOverrides.forEach(data -> giftItemOverrides.put(data.item(), data.value()));
        }

        if (categoryGiftOverrides != null) {
            categoryGiftOverrides.forEach(data -> giftCategoryOverrides.put(data.category(), data.value()));
        }
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.EGG);//TODO:AdventureItems.NPC_SPAWNER.getStackFromResource(getRegistryName());
    }

    public void addData(String name, int value) {
        this.data.put(name, value);
    }

    //Set a resource skin

    public String getUnlocalizedKey() {
        return id().getNamespace() + ".npc." + id().getPath();
    }

    public int getData(String name) {
        return data.getInt(name);
    }

    @Nullable
    @Override
    public ResourceLocation getLootTable() {
        return lootTable.equals(NULL_ID) ? null : lootTable;
    }

    public void callScript(String function, Object... params) {
        if (!script.equals(NULL_ID)) {
            ScriptFactory.getScript(script).callFunction(function, params);
        }
    }

    @Nonnull
    public static NPC getNPCFromRegistry(ResourceLocation resource) {
        NPC npc = Settlements.Registries.NPCS.get(resource);
        return npc != null ? npc : Settlements.Registries.NPCS.emptyEntry();
    }

    public static List<NPC> all() {
        return Settlements.Registries.NPCS.stream().toList();
    }

    public String getOccupation() {
        return occupation;
    }

    public NPC setNPCClass(NPCClass clazz) {
        this.clazz = clazz;
        return this;
    }

    public NPC setOccupation(String occupation) {
        this.occupation = occupation;
        return this;
    }

    public NPCClass getNPCClass() {
        return clazz;
    }

    public int getInsideColor() {
        if (!insideColor.startsWith("#") && ChatFormatting.getByName(insideColor) == null) return -1;
        return INSIDE_COLORS.computeIfAbsent(this, k -> parseColor(insideColor).get().left().get().getValue());
    }

    @Override
    public Component getGreeting(RandomSource random) {
        return SpeechHelper.getRandomSpeech(random, getUnlocalizedGreeting(), 20);
    }

    public int getGiftQuality(ItemStack stack) {
        for (Ingredient ingredient: giftItemOverrides.keySet()) {
            if (ingredient.test(stack)) {
                return giftItemOverrides.getInt(ingredient);
            }
        }

        String category = stack.getItemHolder().getData(Settlements.Data.GIFT_CATEGORIES);
        if (giftCategoryOverrides.containsKey(category)) {
            return giftCategoryOverrides.getInt(category);
        }

        return GiftCategory.getValue(category);
    }

    @Override
    public String substring(String name) {
        return getUnlocalizedKey() + "." + name;
    }

    public int getOutsideColor() {
        if (!outsideColor.startsWith("#") && ChatFormatting.getByName(outsideColor) == null) return -1;
        return OUTSIDE_COLORS.computeIfAbsent(this, k -> parseColor(outsideColor).get().left().get().getValue());
    }

    public String getUnlocalizedGreeting() {
        return getUnlocalizedKey() + ".greeting";
    }

    @Override
    public Component name() {
        return localizedName != null ? Component.literal(localizedName) : Component.translatable(getUnlocalizedKey());
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getSkin() {
        if (skin == null) {
            if (!playerSkin.isEmpty()) {
                //TODO skin = RenderNPC.getSkinFromUsernameOrUUID(null, playerSkin);
                //We've loaded in the proper skin so let's remove this
                playerSkin = Strings.EMPTY;
            } else {
                ResourceLocation key = id();
                skin = new ResourceLocation(key.getNamespace(), "textures/entity/" + key.getPath() + ".png");
            }
        }

        return skin == null ? MISSING_TEXTURE : skin;
    }

    @Override
    public ResourceLocation id() {
        return this == NULL ? NULL_ID : Settlements.Registries.NPCS.getID(this);
    }

    @Override
    public NPC fromNetwork(FriendlyByteBuf buf) {
        return new NPC(
                buf.readResourceLocation(),
                buf.readResourceLocation(),
                buf.readUtf(32767),
                buf.readUtf(32767),
                clazz.fromNetwork(buf),
                buf.readUtf(),
                buf.readUtf(),
                buf.readList(buf2 -> new ItemData(Ingredient.fromNetwork(buf2), buf2.readInt())),
                buf.readList(buf2 -> new CategoryData(buf2.readUtf(32767), buf2.readInt())));
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeResourceLocation(lootTable);
        buf.writeResourceLocation(script);
        buf.writeUtf(playerSkin);
        buf.writeUtf(occupation);
        clazz.toNetwork(buf);
        buf.writeUtf(insideColor);
        buf.writeUtf(outsideColor);
        buf.writeCollection(giftItemOverrides.object2IntEntrySet(), (buf2, entry) -> {
            entry.getKey().toNetwork(buf2);
            buf2.writeInt(entry.getIntValue());
        });

        buf.writeCollection(giftCategoryOverrides.object2IntEntrySet(), (buf2, entry) -> {
            buf2.writeUtf(entry.getKey());
            buf2.writeInt(entry.getIntValue());
        });
    }

}
