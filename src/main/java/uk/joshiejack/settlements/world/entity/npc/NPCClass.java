package uk.joshiejack.settlements.world.entity.npc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.StringRepresentable;


public record NPCClass(Age age, boolean smallArms, float height, float offset, boolean invulnerable, boolean immovable,
                       boolean underwater, boolean floats, boolean invitable, int lifespan, boolean hideHearts) {
    public static final Codec<NPCClass> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            StringRepresentable.fromEnum(Age::values).optionalFieldOf("age", Age.ADULT).forGetter(NPCClass::age),
            Codec.BOOL.optionalFieldOf("small_arms", false).forGetter(NPCClass::smallArms),
            Codec.FLOAT.optionalFieldOf("height", 1F).forGetter(NPCClass::height),
            Codec.FLOAT.optionalFieldOf("offset", 0F).forGetter(NPCClass::offset),
            Codec.BOOL.optionalFieldOf("invulnerable", false).forGetter(NPCClass::invulnerable),
            Codec.BOOL.optionalFieldOf("immovable", false).forGetter(NPCClass::immovable),
            Codec.BOOL.optionalFieldOf("underwater", false).forGetter(NPCClass::underwater),
            Codec.BOOL.optionalFieldOf("floats", false).forGetter(NPCClass::floats),
            Codec.BOOL.optionalFieldOf("invitable", true).forGetter(NPCClass::invitable),
            Codec.INT.optionalFieldOf("lifespan", 0).forGetter(NPCClass::lifespan),
            Codec.BOOL.optionalFieldOf("hide_hearts", false).forGetter(NPCClass::hideHearts)).apply(inst, NPCClass::new));
    public static final NPCClass ADULT = new NPCClass(Age.ADULT, false, 1, 0, false, false, false, false, true, 0, false);
    public static final NPCClass CHILD = new NPCClass(Age.CHILD, false, 1F, 0, false, false, false, false, true, 0, false);
    public static final NPCClass NULL = new NPCClass(Age.ADULT, false, 1, 0, true, false, false, false, false, 0, true);

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