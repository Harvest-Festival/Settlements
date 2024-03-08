package uk.joshiejack.settlements.world.entity.npc;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.scripting.Interpreter;
import uk.joshiejack.penguinlib.scripting.ScriptFactory;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.entity.npc.gifts.GiftCategory;
import uk.joshiejack.settlements.world.entity.npc.gifts.GiftQuality;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;

public class DynamicNPC implements NPCInfo {
    public static final List<String> NAMES = Lists.newArrayList();
    private final Object2IntMap<String> giftCategoryOverrides = new Object2IntOpenHashMap<>();
    private final Object2IntMap<Item> giftItemOverrides = new Object2IntOpenHashMap<>();
    private final Map<String, String> responses = Maps.newHashMap();
    private final Object2IntMap<String> data = new Object2IntOpenHashMap<>();
    private final List<String> greetings = Lists.newArrayList();
    private final ResourceLocation uniqueID;
    private final NPCClass npcClass;
    private final String name, occupation, playerSkin, resourceSkin;
    private final int insideColor, outsideColor;
    private final ItemStack icon;
    private final ResourceLocation script;
    private Interpreter<?> it;
    private ResourceLocation skin;

    private DynamicNPC(ResourceLocation uniqueID, NPCClass npcClass, String name, String occupation, ResourceLocation schedulerScript,
                       @Nullable String playerSkin, @Nullable String resourceSkin, int insideColor, int outsideColor) {
        this.uniqueID = uniqueID;
        this.npcClass = npcClass;
        this.name = name;
        this.occupation = occupation;
        this.script = schedulerScript;
        this.playerSkin = playerSkin;
        this.resourceSkin = resourceSkin;
        this.insideColor = insideColor;
        this.outsideColor = outsideColor;
        this.icon = new ItemStack(Items.ACACIA_BOAT); //TODO :new ItemStack(AdventureItems.NPC_SPAWNER);
        CompoundTag tag = new CompoundTag();
        if (playerSkin != null) {
            tag.putString("PlayerSkin", playerSkin);
        } else if (resourceSkin != null) {
            tag.putString("ResourceSkin", resourceSkin);
        }

        this.icon.setTag(tag);
    }

    public static NPCInfo fromTag(CompoundTag custom) {
        CompoundTag npcClass = custom.getCompound("Class");
        DynamicNPC info = new DynamicNPC(
                new ResourceLocation(custom.getString("UniqueID")),
                new NPCClass(
                        Age.valueOf(npcClass.getString("Age")),
                        npcClass.getBoolean("SmallArms"),
                        npcClass.getFloat("Height"),
                        npcClass.getFloat("Offset"),
                        npcClass.getBoolean("Invulnerable"),
                        npcClass.getBoolean("Immovable"),
                        npcClass.getBoolean("Underwater"),
                        npcClass.getBoolean("Floats"),
                        npcClass.getBoolean("Invitable"),
                        npcClass.getInt("Lifespan"),
                        npcClass.getBoolean("HideHearts")),
                custom.getString("Name"),
                custom.getString("Occupation"),
                custom.contains("Script") ? new ResourceLocation(custom.getString("Script")) : null,
                custom.contains("PlayerSkin") ? custom.getString("PlayerSkin") : null,
                custom.contains("ResourceSkin") ? custom.getString("ResourceSkin") : null,
                custom.getInt("InsideColor"),
                custom.getInt("OutsideColor")
        );

        if (custom.contains("Gifts")) {
            //Deserialize itemOverrides
            CompoundTag tag = custom.getCompound("Gifts");
            if (tag.contains("ItemOverrides")) {
                //Build the data
                ListTag iOverrideList = tag.getList("ItemOverrides", Tag.TAG_COMPOUND);
                for (int i = 0; i < iOverrideList.size(); i++) {
                    CompoundTag data = iOverrideList.getCompound(i);
                    Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(data.getString("Item")));
                    int quality = data.getInt("Quality");
                    info.giftItemOverrides.put(item, quality);
                }
            }

            //Deserialize categoryOverrides
            if (tag.contains("CategoryOverrides")) {
                ListTag cOverrideList = tag.getList("CategoryOverrides", Tag.TAG_COMPOUND);
                for (int i = 0; i < cOverrideList.size(); i++) {
                    CompoundTag data = cOverrideList.getCompound(i);
                    String category = data.getString("Category");
                    int quality = data.getInt("Quality");
                    info.giftCategoryOverrides.put(category, quality);
                }
            }
        }

        //Deserialize responses
        if (custom.contains("Speech")) {
            CompoundTag tag = custom.getCompound("Speech");
            for (String s: tag.getAllKeys()) {
                info.responses.put(s, tag.getString(s));
            }
        }

        //Deserialize data
        if (custom.contains("Data")) {
            CompoundTag tag = custom.getCompound("Data");
            for (String s: tag.getAllKeys()) {
                info.data.put(s, tag.getInt(s));
            }
        }

        //Deserialize greetings
        custom.getList("Greetings", Tag.TAG_STRING).forEach(e -> info.greetings.add(e.getAsString()));
        if (info.greetings.isEmpty()) info.greetings.add("Hello"); //TODO random greetings if there are none
        return info;
    }

    @Override
    public NPCClass getNPCClass() {
        return npcClass;
    }

    @Override
    public ResourceLocation id() {
        return uniqueID;
    }

    @Override
    public Component getLocalizedName() {
        return Component.literal(name);
    }

    @Override
    public ItemStack getIcon() {
        return icon;
    }

    public String getOccupation() {
        return occupation;
    }

    @Override
    public ResourceLocation getSkin() {
        if (skin != null) return skin;
        if (playerSkin != null) {
            skin = Client.getSkinFromUsernameOrUUID(null, playerSkin);
        } else if (resourceSkin != null) skin = new ResourceLocation(resourceSkin);

        return skin;
    }

    public static class Client {
        public static ResourceLocation getSkinFromUsernameOrUUID(@Nullable UUID uuid, @Nullable String playerSkin) {
            return null; //TODO: Fix this
//            GameProfile profile = SkullBlockEntity.updateGameprofile(new GameProfile(uuid, playerSkin));
//            Minecraft minecraft = Minecraft.getInstance();
//            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getOrLoad(profile);
//            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
//                return minecraft.getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
//            } else return DefaultPlayerSkin.get(Player.getUUID(profile));
        }
    }

    @Override
    public int getOutsideColor() {
        return outsideColor;
    }

    @Override
    public int getInsideColor() {
        return insideColor;
    }

    @Override
    public Component getGreeting(RandomSource random) {
        return Component.literal(greetings.get(random.nextInt(greetings.size())));
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
        return responses.get(name);
    }

    @Override
    public int getData(String name) {
        return data.get(name);
    }

    @Override
    public void callScript(String function, Object... params) {
        if (script != null) {
            if (it == null) it = ScriptFactory.getScript(script);
            it.callFunction(function, params);
        }
    }

    public static class Builder {
        private final List<Pair<Item, Integer>> itemOverrides = Lists.newArrayList();
        private final Map<String, Integer> categoryOverrides = Maps.newHashMap();
        private final Map<String, String> giftResponses = Maps.newHashMap();
        private final Object2IntMap<String> data = new Object2IntOpenHashMap<>();
        private final List<String> greetings = Lists.newArrayList();
        private final ResourceLocation uniqueID;
        private NPCClass npcClass = NPCClass.NULL;
        private String name = "CustomNPC";
        private String occupation = "villager";
        private String playerSkin = "uk/joshiejack";
        private String resourceSkin = Strings.EMPTY;
        private int insideColor = 0xFFFFFFFF;
        private int outsideColor = 0xFF000000;

        public Builder(ResourceLocation uniqueID) {
            this.uniqueID = uniqueID;
        }

        public Builder(RandomSource rand, ResourceLocation uniqueID) {
            this(uniqueID);
            //Randomiser
            {
                //TODO: Add a config for excluded classes
                List<NPCClass> keys = Settlements.Registries.NPCS.stream().map(NPC::getNPCClass)
                        .filter(aClass -> !aClass.floats()).distinct().toList();
                setClass(keys.get(rand.nextInt(keys.size())));
            }

            //Let's get a random number of items
            int itemOverrides = 5 + rand.nextInt(10);
            for (int i = 0; i < itemOverrides; i++) {
                List<GiftQuality> keys = Settlements.Registries.GIFT_QUALITIES.stream().toList();
                List<Item> items = new java.util.ArrayList<>(BuiltInRegistries.ITEM.stream().toList());
                Collections.shuffle(items);
                addItemOverride(items.get(0), keys.get(0).value());
            }

            //Categories
            int category = 3 + rand.nextInt(5);
            for (int i = 0; i < category; i++) {
                List<String> keys = Lists.newArrayList(GiftCategory.REGISTRY.keySet());
                List<Integer> values = Lists.newArrayList(new HashSet<>(GiftCategory.REGISTRY.values()));
                Collections.shuffle(keys);
                Collections.shuffle(values);
                addCategoryOverride(keys.get(0), values.get(0));
            }

            //Random Gift thanks?
            for (GiftQuality quality : Settlements.Registries.GIFT_QUALITIES.registry().values()) {
                setGiftResponse(quality.name(), "RANDOM TEXT");
            }

            //Random Greeting
            for (int i = 0; i < 10; i++) {
                addGreeting("RANDOM" + i);
            }

            //Random skin
            String name = NAMES.get(rand.nextInt(NAMES.size()));
            setName(name).setPlayerSkin(name);

            //Color
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();
            Color randomColor = new Color(r, g, b);
            setInsideColor(randomColor.brighter().getRGB())
                    .setOutsideColor(randomColor.darker().getRGB());
        }

        public void addItemOverride(Item string, int quality) {
            itemOverrides.add(Pair.of(string, quality));
        }

        public void addCategoryOverride(String category, int quality) {
            categoryOverrides.put(category, quality);
        }

        public void setGiftResponse(String quality, String text) {
            giftResponses.put(quality, text);
        }

        public Builder setData(String name, int value) {
            data.put(name, value);
            return this;
        }

        public void addGreeting(String text) {
            greetings.add(text);
        }

        public Builder setClass(NPCClass clazz) {
            this.npcClass = clazz;
            return this;
        }

        public Builder setOccupation(String occupation) {
            this.occupation = occupation;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public void setPlayerSkin(String playerSkin) {
            this.playerSkin = playerSkin;
        }

        public Builder setResourceSkin(String resourceSkin) {
            this.resourceSkin = resourceSkin;
            return this;
        }

        public Builder setInsideColor(int insideColor) {
            this.insideColor = insideColor;
            return this;
        }

        public void setOutsideColor(int outsideColor) {
            this.outsideColor = outsideColor;
        }

        public CompoundTag build() {
            CompoundTag tag = new CompoundTag();
            tag.putString("UniqueID", uniqueID.toString());
            CompoundTag npcClass = getCompoundTag();
            tag.put("Class", npcClass);
            tag.putString("Name", name);
            tag.putString("Occupation", occupation);
            tag.putString(!resourceSkin.isEmpty() ? "ResourceSkin" : "PlayerSkin", !resourceSkin.isEmpty() ? resourceSkin : playerSkin);
            tag.putInt("InsideColor", insideColor);
            tag.putInt("OutsideColor", outsideColor);
            //Gifts
            {
                CompoundTag gifts = new CompoundTag();
                {
                    ListTag iOverrideList = new ListTag();
                    itemOverrides.forEach(pair -> {
                        CompoundTag override = new CompoundTag();
                        override.putString("Item", BuiltInRegistries.ITEM.getKey(pair.getLeft()).toString());
                        override.putInt("Quality", pair.getRight());
                        iOverrideList.add(override);
                    });
                    gifts.put("ItemOverrides", iOverrideList);
                }
                {
                    ListTag cOverrideList = new ListTag();
                    categoryOverrides.forEach((key, value) -> {
                        CompoundTag override = new CompoundTag();
                        override.putString("Category", key);
                        override.putInt("Quality", value);
                        cOverrideList.add(override);
                    });

                    gifts.put("CategoryOverrides", cOverrideList);
                }
                tag.put("Gifts", gifts);
            }

            //Greetings
            {
                ListTag greetings = new ListTag();
                this.greetings.forEach(g -> greetings.add(StringTag.valueOf(g)));
                tag.put("Greetings", greetings);
            }

            //Thanks
            {
                CompoundTag speech = new CompoundTag();
                giftResponses.forEach((key, value) -> speech.putString("gift." + key, value));
                tag.put("Speech", speech);
            }

            //Data
            {
                CompoundTag data = new CompoundTag();
                this.data.forEach((k, v) -> data.putInt(k, this.data.get(k)));

                tag.put("Data", data);
            }

            return tag;
        }

        @NotNull
        private CompoundTag getCompoundTag() {
            CompoundTag npcClass = new CompoundTag();
            npcClass.putString("Age", this.npcClass.age().name());
            npcClass.putBoolean("SmallArms", this.npcClass.smallArms());
            npcClass.putFloat("Height", this.npcClass.height());
            npcClass.putFloat("Offset", this.npcClass.offset());
            npcClass.putBoolean("Invulnerable", this.npcClass.invulnerable());
            npcClass.putBoolean("Immovable", this.npcClass.immovable());
            npcClass.putBoolean("Underwater", this.npcClass.underwater());
            npcClass.putBoolean("Floats", this.npcClass.floats());
            npcClass.putBoolean("Invitable", this.npcClass.invitable());
            npcClass.putInt("Lifespan", this.npcClass.lifespan());
            npcClass.putBoolean("HideHearts", this.npcClass.hideHearts());
            return npcClass;
        }

        public void randomise() {

        }
    }
}
