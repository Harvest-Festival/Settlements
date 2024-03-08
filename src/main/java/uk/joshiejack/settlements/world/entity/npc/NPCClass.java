package uk.joshiejack.settlements.world.entity.npc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;


public record NPCClass(Age age, boolean smallArms, float height, float offset, boolean invulnerable, boolean immovable,
                       boolean underwater, boolean floats, boolean invitable, int lifespan, boolean hideHearts) {
    public static final Codec<NPCClass> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.xmap(str -> Objects.requireNonNull(Age.valueOf(str)), Age::name).fieldOf("age").forGetter(NPCClass::age),
            Codec.BOOL.fieldOf("smallArms").forGetter(NPCClass::smallArms),
            Codec.FLOAT.fieldOf("height").forGetter(NPCClass::height),
            Codec.FLOAT.fieldOf("offset").forGetter(NPCClass::offset),
            Codec.BOOL.fieldOf("invulnerable").forGetter(NPCClass::invulnerable),
            Codec.BOOL.fieldOf("immovable").forGetter(NPCClass::immovable),
            Codec.BOOL.fieldOf("underwater").forGetter(NPCClass::underwater),
            Codec.BOOL.fieldOf("floats").forGetter(NPCClass::floats),
            Codec.BOOL.fieldOf("invitable").forGetter(NPCClass::invitable),
            Codec.INT.fieldOf("lifespan").forGetter(NPCClass::lifespan),
            Codec.BOOL.fieldOf("hideHearts").forGetter(NPCClass::hideHearts)).apply(inst, NPCClass::new));
    public static final NPCClass ADULT = new NPCClass(Age.ADULT, false, 1, 0, true, false, false, false, true, 0, false);
    public static final NPCClass NULL = new NPCClass(Age.ADULT, false, 1, 0, true, false, false, false, false, 0, false);

    public NPCClass fromNetwork(FriendlyByteBuf buf) {
        return new NPCClass(buf.readEnum(Age.class),
                buf.readBoolean(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readVarInt(),
                buf.readBoolean());
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeEnum(age);
        buf.writeBoolean(smallArms);
        buf.writeFloat(height);
        buf.writeFloat(offset);
        buf.writeBoolean(invulnerable);
        buf.writeBoolean(immovable);
        buf.writeBoolean(underwater);
        buf.writeBoolean(floats);
        buf.writeBoolean(invitable);
        buf.writeVarInt(lifespan);
        buf.writeBoolean(hideHearts);
    }
}