package uk.joshiejack.settlements.world.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import uk.joshiejack.penguinlib.world.item.PenguinRegistryItem;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.entity.npc.NPC;

import java.util.UUID;

public class NPCSpawnerItem extends PenguinRegistryItem<NPC> {
    public NPCSpawnerItem(Item.Properties properties) {
        super(Settlements.Registries.NPCS, "NPC", properties);
    }

    public ItemStack withPlayerSkin(UUID player) {
        ItemStack stack = new ItemStack(this);
        stack.setTag(new CompoundTag());
        stack.getTag().putString("UUID", player.toString());
        return stack;
    }
//
//    public ResourceLocation getSkinFromStack(NPC npc, ItemStack stack) {
//        ResourceLocation skin = null;
//        if (stack.hasTagCompound()) {
//            if (stack.getTagCompound().hasKey("UUID")) {
//                UUID uuid = UUID.fromString(stack.getTagCompound().getString("UUID"));
//                skin = RenderNPC.getSkinFromUsernameOrUUID(uuid, UsernameCache.getLastKnownUsername(uuid));
//            } else if (stack.getTagCompound().hasKey("PlayerSkin")) {
//                skin =  RenderNPC.getSkinFromUsernameOrUUID(null, stack.getTagCompound().getString("PlayerSkin"));
//            } else if (stack.getTagCompound().hasKey("ResourceSkin")) {
//                skin = new ResourceLocation(stack.getTagCompound().getString("ResourceSkin"));
//            } else skin = npc.getSkin();
//        }
//
//        return skin != null && RenderNPC.textureExists(skin) ? skin : RenderNPC.MISSING;
//    }
//
//    @Override
//    @Nonnull
//    public EnumActionResult onItemUse(EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
//        ItemStack stack = player.getHeldItem(hand);
//        NPC npc = getObjectFromStack(stack);
//        if (npc != null) {
//            if (!world.isRemote) {
//                TownServer town = TownFinder.getFinder(world).findOrCreate(player, pos);
//                EntityNPC entity = town.getCensus().getSpawner().getNPC(world, npc, npc.getRegistryName(), null, pos.up());
//                if (entity != null) {
//                    world.spawnEntity(entity);
//                }
//            }
//
//            stack.splitStack(1);
//            return EnumActionResult.SUCCESS;
//        }
//
//        return EnumActionResult.PASS;
//    }
}
