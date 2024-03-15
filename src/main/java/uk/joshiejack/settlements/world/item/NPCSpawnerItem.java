package uk.joshiejack.settlements.world.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.world.item.PenguinRegistryItem;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.SettlementsEntities;
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
    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        Level level = context.getLevel();
        ItemStack stack = player.getItemInHand(hand);
        NPC npc = fromStack(stack);
        if (npc != null) {
            if (!level.isClientSide) {
                //TODO: Town
//                TownServer town = TownFinder.getFinder(world).findOrCreate(player, pos);
//                EntityNPC entity = town.getCensus().getSpawner().getNPC(world, npc, npc.getRegistryName(), null, pos.up());
//                if (entity != null) {
//                    world.spawnEntity(entity);
//                }

                NPCMob mob = new NPCMob(SettlementsEntities.NPC.get(), level, npc);
                mob.setPos(context.getClickedPos().getX() + 0.5D, context.getClickedPos().getY() + 1, context.getClickedPos().getZ() + 0.5D);
                level.addFreshEntity(mob);
            }

            stack.shrink(1);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
