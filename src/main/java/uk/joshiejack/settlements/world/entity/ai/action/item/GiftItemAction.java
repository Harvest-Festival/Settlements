package uk.joshiejack.settlements.world.entity.ai.action.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import uk.joshiejack.penguinlib.scripting.wrapper.ItemStackJS;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.ActionMental;

//@PenguinLoader("give_item")
public class GiftItemAction extends ActionMental {
    private ItemStack stack;

    public GiftItemAction() {}
    public GiftItemAction(ItemStack stack) {
        this.stack = stack;
    }

    public GiftItemAction(ItemStackJS stack) {
        this.stack = stack.get();
    }

    @Override
    public InteractionResult execute(NPCMob npc) {
        if (player != null) {
            ItemHandlerHelper.giveItemToPlayer(player, stack);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public CompoundTag serializeNBT() {
        return stack.save(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        stack = ItemStack.of(tag);
    }
}
