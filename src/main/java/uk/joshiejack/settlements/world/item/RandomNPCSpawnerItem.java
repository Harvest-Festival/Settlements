package uk.joshiejack.settlements.world.item;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.npc.DynamicNPC;
import uk.joshiejack.settlements.world.level.town.TownFinder;
import uk.joshiejack.settlements.world.level.town.TownServer;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class RandomNPCSpawnerItem extends Item {
    public static final List<String> NAMES = Lists.newArrayList();

    public RandomNPCSpawnerItem(Item.Properties properties) {
        super(properties);
    }
    @Override
    @Nonnull
    public InteractionResult useOn(UseOnContext context) {
        InteractionHand hand = context.getHand();
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        if (!level.isClientSide && context.getPlayer() != null) {
            //TownServer town = TownFinder.getFinder(level).findOrCreate(player, pos);
            //Let's create a custom npc to show up in the book?
            ResourceLocation uniqueID = new ResourceLocation("custom", UUID.randomUUID().toString());
            DynamicNPC npc = new DynamicNPC.Builder(level.getRandom(), uniqueID).build();
            TownServer town = TownFinder.getFinder(level).findOrCreate(context.getPlayer(), context.getClickedPos());
            town.getCensus().createCustomNPCFromData(level, uniqueID, npc);
            NPCMob entity = town.getCensus().getSpawner().getNPC(level, npc, context.getClickedPos().above());
            if (entity != null) {
                level.addFreshEntity(entity);
            }
//            NPCMob mob = new NPCMob(SettlementsEntities.NPC.get(), level, builder.build());
//            mob.setPos(context.getClickedPos().getX() + 0.5D, context.getClickedPos().getY() + 1, context.getClickedPos().getZ() + 0.5D);
//            level.addFreshEntity(mob);
//            town.getCensus().createCustomNPCFromData(world, uniqueID, NPC.CUSTOM_NPC, data);
//            EntityNPC entity = town.getCensus().getSpawner().getNPC(level, NPC.CUSTOM_NPC, uniqueID, data, pos.up());
//            if (entity != null) {
//                level.spawnEntity(entity);
//            }
        }

        stack.shrink(1);
        return InteractionResult.SUCCESS;
    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    public void registerModels() {
//        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Objects.requireNonNull(getRegistryName()), "inventory"));
//        AdventureItems.CUSTOM_NPC_SPAWNER.setTileEntityItemStackRenderer(new NPCSpawnerRenderer());
//    }
}
