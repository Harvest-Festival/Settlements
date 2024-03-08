//package uk.joshiejack.settlements.world.entity.ai.action.economy;
//
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.InteractionResult;
//import uk.joshiejack.settlements.Settlements;
//import uk.joshiejack.settlements.entity.EntityNPC;
//import uk.joshiejack.settlements.entity.ai.action.ActionMental;
//import uk.joshiejack.economy.api.EconomyAPI;
//import uk.joshiejack.penguinlib.util.PenguinLoader;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.util.EnumActionResult;
//import org.apache.logging.log4j.Level;
//
//@PenguinLoader("give_gold") //TODO:
//public class ActionGiveGold extends ActionMental {
//    private int gold;
//
//    @Override
//    public ActionGiveGold withData(Object... params) {
//        if (params.length != 1 || !(params[0] instanceof Integer)) {
//            Settlements.logger.log(Level.WARN, "Tried to use a non integer to give gold!");
//        } else this.gold = (int) params[0];
//
//        return this;
//    }
//
//    @Override
//    public InteractionResult execute(EntityNPC npc) {
//        if (player != null) {
//            EconomyAPI.instance.getVaultForPlayer(player.world, player).increaseGold(player.world, gold);
//        }
//
//        return EnumActionResult.SUCCESS;
//    }
//
//    @Override
//    public CompoundTag serializeNBT() {
//        CompoundTag tag = new CompoundTag();
//        tag.putInt("Gold", gold);
//        return tag;
//    }
//
//    @Override
//    public void deserializeNBT(NBTTagCompound tag) {
//        gold = tag.getInteger("Gold");
//    }
//}
