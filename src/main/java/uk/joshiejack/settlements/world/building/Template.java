package uk.joshiejack.settlements.world.building;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import uk.joshiejack.penguinlib.client.level.ghost.GhostStateGetter;

import java.util.Map;

public class Template {
    public static final Template EMPTY = new Template(Maps.newHashMap(), 0, 0, 0);
    public static final Codec<Template> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            //Map?
            Codec.INT.fieldOf("sizeX").forGetter(Template::getXSize),
            Codec.INT.fieldOf("sizeY").forGetter(Template::getYSize),
            Codec.INT.fieldOf("sizeZ").forGetter(Template::getZSize)
    ).apply(instance, (x, y, z) -> new Template(Maps.newHashMap(), x, y, z))); //TODO: Proper codec
    private final int sizeX; //Size of the template
    private final int sizeZ; //Size of the template
    private final int sizeY; //Size of the template
    private final int[] blockData = new int[0]; //data is a 1 dimensional array, we will use % to get the x, z and y
    private final Int2ObjectMap<BlockState> blockPalette = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<EntityType<?>> entityPalette = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<CompoundTag> blockEntityData = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<CompoundTag> entityData = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<ResourceLocation> blockLootTables = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<ResourceLocation> entityLootTables = new Int2ObjectOpenHashMap<>();
    private final Map<BlockPos, BlockData> original; //Temporary

    public Template(Map<BlockPos, BlockData> data, int sizeX, int sizeY, int sizeZ) {
        //Converts the map of placeables to template data
       this.original = data;
         this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }


    public Map<BlockPos, BlockData> getBlockData() {
//        Map<BlockPos, BlockData> data = new HashMap<>();
//        for (int i = 0; i < blockData.length; i++) {
//            int x = i % sizeX;
//            int z = (i / sizeX) % sizeZ;
//            int y = i / (sizeX * sizeZ);
//            BlockState state = blockPalette.get(blockData[i]);
//            if (state != null) {
//                //Create a new block data
//                data.put(new BlockPos(x, y, z), new BlockData(state, blockEntityData.get(i), blockLootTables.get(i)));
//            }
//        }
//
//        //Converts the template data to a map of placeables
        //Temporary
        return original;
    }

    public int getXSize() {
        return sizeX;
    }

    public int getYSize() {
        return sizeY;
    }

    public int getZSize() {
        return sizeZ;
    }

    //TODO: Implement
    public Template fromNetwork(FriendlyByteBuf friendlyByteBuf) {
        return new Template(Maps.newHashMap(), 0, 0, 0);
    }

    //TODO: Implement
    public void toNetwork(FriendlyByteBuf friendlyByteBuf) {

    }

    public record BlockData(BlockState state, @Nullable CompoundTag beData, @Nullable ResourceLocation lootTable) implements GhostStateGetter {}
    public record EntityData(EntityType<?> type, @Nullable CompoundTag entityData, @Nullable ResourceLocation lootTable) {}
}
