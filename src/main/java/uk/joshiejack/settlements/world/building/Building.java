package uk.joshiejack.settlements.world.building;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;
import uk.joshiejack.penguinlib.world.item.PenguinRegistryItem;
import uk.joshiejack.settlements.Settlements;

public record Building(Template template, Component name) implements ReloadableRegistry.PenguinRegistry<Building>, PenguinRegistryItem.Nameable {
    public static final Codec<Building> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Template.CODEC.fieldOf("template").forGetter(Building::template),
            ComponentSerialization.CODEC.fieldOf("Name").forGetter(data -> data.name)
    ).apply(instance, Building::new));


    @Override
    public ResourceLocation id() {
        return Settlements.Registries.BUILDINGS.getID(this);
    }

    @Override
    public Building fromNetwork(FriendlyByteBuf buf) {
        return new Building(Template.EMPTY.fromNetwork(buf), buf.readComponent());
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        template.toNetwork(buf);
        buf.writeComponent(name);
    }

    public static Building getBuildingFromItem(ItemStack item) {
        return Settlements.Registries.BUILDINGS.stream().findFirst().orElse(Settlements.Registries.BUILDINGS.emptyEntry()); //First entryTEST_BUILDING.get(item.getItem());
    }
}