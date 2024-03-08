package uk.joshiejack.settlements.world.building;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;
import uk.joshiejack.settlements.Settlements;

public class Building implements ReloadableRegistry.PenguinRegistry<Building> {
    public static final Codec<Building> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Template.CODEC.fieldOf("template").forGetter(Building::getTemplate),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(b -> b.item)
    ).apply(instance, Building::new));

    private final Template template;
    private final Item item;

    public Building(Template template, Item item) {
        this.template = template;
        this.item = item;
    }

    @Override
    public ResourceLocation id() {
        return Settlements.Registries.BUILDINGS.getID(this);
    }

    @Override
    public Building fromNetwork(FriendlyByteBuf friendlyByteBuf) {
        return new Building(Template.EMPTY.fromNetwork(friendlyByteBuf), friendlyByteBuf.readById(BuiltInRegistries.ITEM));
    }

    @Override
    public void toNetwork(FriendlyByteBuf friendlyByteBuf) {
        template.toNetwork(friendlyByteBuf);
        friendlyByteBuf.writeId(BuiltInRegistries.ITEM, item);
    }

    public Template getTemplate() {
        return template;
    }

    public static Building getBuildingFromItem(ItemStack item) {
        return Settlements.Registries.BUILDINGS.stream().findFirst().orElse(Settlements.Registries.BUILDINGS.emptyEntry()); //First entryTEST_BUILDING.get(item.getItem());
    }
}