package uk.joshiejack.settlements.world.quest.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.StringRepresentable;
import uk.joshiejack.penguinlib.util.PenguinGroup;

import java.util.Locale;

public class Settings {
    public static final Codec<Settings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StringRepresentable.fromEnum(Repeat::values).optionalFieldOf("repeat", Repeat.NONE).forGetter(Settings::getRepeat),
            StringRepresentable.fromEnum(PenguinGroup::values).optionalFieldOf("type", PenguinGroup.PLAYER).forGetter(Settings::getType),
            Codec.BOOL.optionalFieldOf("daily", false).forGetter(Settings::isDaily),
            Codec.BOOL.optionalFieldOf("isDefault", false).forGetter(Settings::isDefault),
            Codec.STRING.optionalFieldOf("triggers", "onRightClickBlock").forGetter(Settings::getTriggers)
    ).apply(instance, Settings::new));

    public static final Settings DEFAULT = new Settings(Repeat.NONE, PenguinGroup.PLAYER, false, false, "onRightClickBlock");
    private Repeat repeat;
    private PenguinGroup type;
    private boolean daily;
    private boolean isDefault;
    private String triggers;

    public Settings(Repeat repeat, PenguinGroup type, boolean daily, boolean isDefault, String triggers) {
        this.repeat = repeat;
        this.type = type;
        this.daily = daily;
        this.isDefault = isDefault;
        this.triggers = triggers;
    }

    public Repeat getRepeat() {
        return repeat;
    }

    public PenguinGroup getType() {
        return type;
    }

    public boolean isDaily() {
        return daily;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public String getTriggers() {
        return triggers;
    }

    public Settings setRepeat(String repeat) {
        this.repeat = Repeat.valueOf(repeat.toUpperCase(Locale.ENGLISH));
        return this;
    }

    public Settings setType(String type) {
        this.type = PenguinGroup.valueOf(type.toUpperCase(Locale.ENGLISH));
        if (this.type == PenguinGroup.GLOBAL) {
            this.isDefault = true;
        }

        return this;
    }

    public Settings setDaily() {
        this.isDefault = false;
        this.daily = true;
        return this;
    }

    public Settings setDefault() {
        this.isDefault = true;
        return this;
    }

    public Settings setTriggers(String triggers) {
        this.triggers = triggers;
        return this;
    }

    public static Settings fromNetwork(FriendlyByteBuf buf) {
        return new Settings(buf.readEnum(Repeat.class),
                buf.readEnum(PenguinGroup.class),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readUtf(32767));
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeEnum(repeat);
        buf.writeEnum(type);
        buf.writeBoolean(daily);
        buf.writeBoolean(isDefault);
        buf.writeUtf(triggers);
    }
}
