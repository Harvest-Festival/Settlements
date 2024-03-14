package uk.joshiejack.settlements.world.entity.npc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
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
import java.util.List;

public final class NPC implements ReloadableRegistry.PenguinRegistry<NPC>, NPCInfo {
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
                    ResourceLocation.CODEC.optionalFieldOf("lootTable", null).forGetter(NPC::getLootTable),
                    ResourceLocation.CODEC.optionalFieldOf("script", null).forGetter(obj -> obj.script),
                    Codec.STRING.optionalFieldOf("playerSkin", null).forGetter(obj -> obj.playerSkin),
                    Codec.STRING.optionalFieldOf("occupation", "villager").forGetter(NPC::getOccupation),
                    NPCClass.CODEC.optionalFieldOf("class", NPCClass.NULL).forGetter(obj -> obj.clazz),
                    Codec.INT.optionalFieldOf("insideColor", 0xFFFFFF).forGetter(NPC::getInsideColor),
                    Codec.INT.optionalFieldOf("outsideColor", 0x000000).forGetter(NPC::getOutsideColor),
                    ItemData.CODEC.listOf().optionalFieldOf("giftItemOverrides", null).forGetter(obj -> obj.giftItemOverrides.object2IntEntrySet().stream().map(e -> new ItemData(e.getKey(), e.getIntValue())).toList()),
                    CategoryData.CODEC.listOf().optionalFieldOf("giftCategoryOverrides", null).forGetter(obj -> obj.giftCategoryOverrides.object2IntEntrySet().stream().map(e -> new CategoryData(e.getKey(), e.getIntValue())).toList()))
            .apply(inst, NPC::new));
    public static final ResourceLocation MISSING_TEXTURE = new ResourceLocation(Settlements.MODID, "textures/entity/missing.png");
    private static final ResourceLocation NULL_ID = new ResourceLocation(Settlements.MODID, "null");
    public static final NPC NULL = new NPC(null, null, null, null, NPCClass.NULL, 0xFFFFFF, 0x000000, null, null).setOccupation(Strings.EMPTY).setNPCClass(NPCClass.NULL);
    private final Object2IntMap<String> data = new Object2IntOpenHashMap<>();
    private final Object2IntMap<String> giftCategoryOverrides = new Object2IntOpenHashMap<>();
    private final Object2IntMap<Ingredient> giftItemOverrides = new Object2IntOpenHashMap<>();
    private ResourceLocation lootTable;
    private ResourceLocation script;
    private String localizedName;
    private ResourceLocation skin;
    private String playerSkin;
    private String occupation;
    private NPCClass clazz;
    private int insideColor;
    private int outsideColor;

    public NPC(@Nullable ResourceLocation lootTable,
               @Nullable ResourceLocation script,
               @Nullable String playerSkin,
               @Nullable String occupation,
               @Nonnull NPCClass clazz,
               int insideColor,
               int outsideColor,
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

    public void setScript(ResourceLocation script) {
        this.script = script;
    }

    //Load a skin from the net
    public void setSkin(String playerSkin) {
        this.playerSkin = playerSkin;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public void setLootTable(ResourceLocation lootTable) {
        this.lootTable = lootTable;
    }

    public String getUnlocalizedKey() {
        return id().getNamespace() + ".npc." + id().getPath();
    }

    public int getData(String name) {
        return data.getInt(name);
    }

    @Nullable
    @Override
    public ResourceLocation getLootTable() {
        return lootTable;
    }

    public void callScript(String function, Object... params) {
        if (script != null) {
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

    public void setColors(int inside, int outside) {
        this.insideColor = inside;
        this.outsideColor = outside;
    }

    public NPCClass getNPCClass() {
        return clazz;
    }

    public int getInsideColor() {
        return insideColor;
    }

    @Override
    public Component getGreeting(RandomSource random) {
        return SpeechHelper.getRandomSpeech(random, getUnlocalizedGreeting(), 20);
    }

    public int getGiftQuality(ItemStack stack) {
        if (giftItemOverrides.containsKey(stack.getItem())) {
            return giftItemOverrides.get(stack.getItem());
        }

        String category = stack.getItemHolder().getData(Settlements.Data.GIFT_CATEGORIES);
        if (giftCategoryOverrides.containsKey(category)) {
            return giftCategoryOverrides.get(category);
        }

        return GiftCategory.getValue(category);
    }

    @Override
    public String substring(String name) {
        return getUnlocalizedKey() + "." + name;
    }

    public int getOutsideColor() {
        return outsideColor;
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
            if (playerSkin != null) {
                //TODO skin = RenderNPC.getSkinFromUsernameOrUUID(null, playerSkin);
                //We've loaded in the proper skin so let's remove this
                playerSkin = null;
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
                buf.readBoolean() ? buf.readResourceLocation() : null,
                buf.readBoolean() ? buf.readResourceLocation() : null,
                buf.readBoolean() ? buf.readUtf(32767) : null,
                buf.readBoolean() ? buf.readUtf(32767) : null,
                clazz.fromNetwork(buf),
                buf.readInt(),
                buf.readInt(),
                buf.readList(buf2 -> new ItemData(Ingredient.fromNetwork(buf2), buf2.readInt())),
                buf.readList(buf2 -> new CategoryData(buf2.readUtf(32767), buf2.readInt())));
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeBoolean(lootTable != null);
        if (lootTable != null)
            buf.writeResourceLocation(lootTable);
        buf.writeBoolean(script != null);
        if (script != null)
            buf.writeResourceLocation(script);
        buf.writeBoolean(playerSkin != null);
        if (playerSkin != null)
            buf.writeUtf(playerSkin);
        buf.writeBoolean(occupation != null);
        if (occupation != null)
            buf.writeUtf(occupation);

        clazz.toNetwork(buf);
        buf.writeInt(insideColor);
        buf.writeInt(outsideColor);
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
