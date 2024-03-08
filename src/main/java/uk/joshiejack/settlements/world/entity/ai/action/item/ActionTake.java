package uk.joshiejack.settlements.world.entity.ai.action.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import uk.joshiejack.penguinlib.util.helper.InventoryHelper;
import uk.joshiejack.settlements.world.entity.EntityNPC;
import uk.joshiejack.settlements.world.entity.ai.action.ActionMental;

import java.util.function.Function;

//TODO: @PenguinLoader("take_item")
public class ActionTake extends ActionMental {
    private String holder;
    private int amount;

    @Override
    public ActionTake withData(Object... params) {
        this.holder = (String) params[0];
        this.amount = (int) params[1];
        return this;
    }

    @Override
    public InteractionResult execute(EntityNPC npc) {
        if (player != null) InventoryHelper.takeItemsInInventory(player, asFunction(), amount);
        return InteractionResult.SUCCESS;
    }

    public Function<ItemStack, Boolean> asFunction() {
        ResourceLocation key = holder.startsWith("#") ? new ResourceLocation(holder.substring(1)) : new ResourceLocation(holder);
        return (s) -> {
            if (holder.startsWith("#")) {
                return s.is(ItemTags.create(key));
            } else return s.getItem() == BuiltInRegistries.ITEM.get(key);
        };
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Holder", holder);
        tag.putShort("Amount", (short) amount);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        holder = nbt.getString("Holder");
        amount = nbt.getShort("Amount");
    }
}
