package uk.joshiejack.settlements.world.entity.ai.action.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.apache.logging.log4j.Level;
import uk.joshiejack.penguinlib.scripting.wrapper.ItemStackJS;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.ActionMental;

//@PenguinLoader("give_item")
public class ActionGiftItem extends ActionMental {
    private ItemStack stack;

    @Override
    public ActionGiftItem withData(Object... params) {
        if (params[0] instanceof String) {
            Settlements.LOGGER.log(Level.WARN, "Tried to use a string for a gift item action instead of creating an item!");
        }
        
        if (params[0] instanceof ItemStack) this.stack = (ItemStack) params[0];
        else {
            assert params[0] instanceof ItemStackJS;
            this.stack = ((ItemStackJS) params[0]).get();
        }
        return this;
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
