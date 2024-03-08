package uk.joshiejack.settlements.world.entity.npc.gifts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;
import uk.joshiejack.settlements.Settlements;

import java.util.List;

public record GiftQuality(String name, int value) implements ReloadableRegistry.PenguinRegistry<GiftQuality> {
    public static final Codec<GiftQuality> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(GiftQuality::name),
            Codec.INT.fieldOf("value").forGetter(GiftQuality::value))
            .apply(instance, GiftQuality::new));
    public static final GiftQuality NORMAL = new GiftQuality("normal", 0);

    public static GiftQuality getQualityFromValue(int value) {
        List<GiftQuality> qualities = Settlements.Registries.GIFT_QUALITIES.stream().toList();
        //Find the quality that is closest to the value
        GiftQuality closest = NORMAL;
        int closestValue = Integer.MAX_VALUE;
        for (GiftQuality quality: qualities) {
            int difference = Math.abs(quality.value() - value);
            if (difference < closestValue) {
                closest = quality;
                closestValue = difference;
            }
        }

        return closest;
    }

    @Override
    public ResourceLocation id() {
        return Settlements.Registries.GIFT_QUALITIES.getID(this);
    }

    @Override
    public GiftQuality fromNetwork(FriendlyByteBuf buf) {
        return new GiftQuality(buf.readUtf(32767), buf.readInt());
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeUtf(name, 32767);
        buf.writeInt(value);
    }
}