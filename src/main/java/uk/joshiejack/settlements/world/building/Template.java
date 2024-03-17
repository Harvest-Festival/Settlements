package uk.joshiejack.settlements.world.building;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import uk.joshiejack.penguinlib.client.level.ghost.GhostStateGetter;
import uk.joshiejack.settlements.world.entity.npc.NPC;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record Template(BlockPos size, BlockPos offset, Map<BlockPos, BlockData> original) {
    public static final Template EMPTY = new Template(BlockPos.ZERO, BlockPos.ZERO, Maps.newHashMap());
    public static final Codec<Template> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            //Map?
            BlockPos.CODEC.fieldOf("size").forGetter(Template::size),
            BlockPos.CODEC.fieldOf("offset").forGetter(Template::offset),
            //TODO: Use Palette?
            Codec.unboundedMap(BlockPos.CODEC, BlockData.CODEC).fieldOf("data").forGetter(Template::original)
    ).apply(instance, Template::new)); //TODO: Proper codec
//    private final int sizeX; //Size of the template
//    private final int sizeZ; //Size of the template
//    private final int sizeY; //Size of the template
//    private final int[] blockData = new int[0]; //data is a 1 dimensional array, we will use % to get the x, z and y
//    private final Int2ObjectMap<BlockState> blockPalette = new Int2ObjectOpenHashMap<>();
//    private final Int2ObjectMap<EntityType<?>> entityPalette = new Int2ObjectOpenHashMap<>();
//    private final Int2ObjectMap<CompoundTag> blockEntityData = new Int2ObjectOpenHashMap<>();
//    private final Int2ObjectMap<CompoundTag> entityData = new Int2ObjectOpenHashMap<>();
//    private final Int2ObjectMap<ResourceLocation> blockLootTables = new Int2ObjectOpenHashMap<>();
//    private final Int2ObjectMap<ResourceLocation> entityLootTables = new Int2ObjectOpenHashMap<>();
//    private final Map<BlockPos, BlockData> original; //Temporary


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

    //TODO: Implement
    public List<NPC> getResidents() {
        return new ArrayList<>();
    }


    //TODO: Implement
    public Template fromNetwork(FriendlyByteBuf friendlyByteBuf) {
        return new Template(BlockPos.ZERO, BlockPos.ZERO, Maps.newHashMap());
    }

    //TODO: Implement
    public void toNetwork(FriendlyByteBuf friendlyByteBuf) {

    }

    public record BlockData(BlockState state, @Nullable CompoundTag beData, @Nullable ResourceLocation lootTable) implements GhostStateGetter {
        public static final Codec<BlockData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockState.CODEC.fieldOf("state").forGetter(BlockData::state),
                CompoundTag.CODEC.optionalFieldOf("block_entity_data", null).forGetter(data -> data.beData),
                ResourceLocation.CODEC.optionalFieldOf("loot_table", null).forGetter(data -> data.lootTable)
        ).apply(instance, BlockData::new));
    }
    public record EntityData(EntityType<?> type, @Nullable CompoundTag entityData, @Nullable ResourceLocation lootTable) {}
}
