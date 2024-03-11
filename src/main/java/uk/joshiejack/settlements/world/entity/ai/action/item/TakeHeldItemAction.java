package uk.joshiejack.settlements.world.entity.ai.action.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.MentalAction;

//TODO@PenguinLoader("take_held")
public class TakeHeldItemAction extends MentalAction {
    private InteractionHand hand;
    private int amount;

    public TakeHeldItemAction() {}
    public TakeHeldItemAction(InteractionHand hand, int amount) {
        this.hand = hand;
        this.amount = amount;
    }

    @Override
    public InteractionResult execute(NPCMob npc) {
        if (player != null) player.getItemInHand(hand).shrink(amount);
        return InteractionResult.SUCCESS;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("MainHand", hand == InteractionHand.MAIN_HAND);
        tag.putByte("Amount", (byte) amount);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        hand = nbt.getBoolean("MainHand") ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        amount = nbt.getByte("Amount");
    }
}
