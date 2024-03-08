package uk.joshiejack.settlements.world.entity.ai.action.chat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.util.Strings;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.settlements.network.npc.PacketSay;
import uk.joshiejack.settlements.world.entity.EntityNPC;
import uk.joshiejack.settlements.world.entity.ai.action.ActionChat;
import uk.joshiejack.settlements.world.entity.ai.action.ActionMental;

//TODO: @PenguinLoader("say")
public class ActionSay extends ActionMental implements ActionChat {
    public String text;
    public String[] formatting;
    private boolean displayed;
    private boolean read;

    @Override
    public ActionSay withData(Object... params) {
        if (params.length == 0) {
             this.text = "text";
             this.formatting = new String[0];
        } else {
            this.text = (String) params[0];
            this.formatting = new String[params.length - 1];
            for (int i = 1; i < params.length; i++) {
                this.formatting[i - 1] = String.valueOf(params[i]);
            }
        }

        if (this.text == null) this.text = Strings.EMPTY; //No null
        return this;
    }

    @Override
    public void onGuiClosed(Player player, EntityNPC npc, Object... parameters) {
        read = true;
    }

    @Override
    public InteractionResult execute(EntityNPC npc) {
        if (!displayed && player != null) {
            displayed = true; //Marked it as displayed
            npc.addTalking(player); //Add the talking
            PenguinNetwork.sendToClient(player, new PacketSay(player, npc, this));
        }

        return player == null || npc.IsNotTalkingTo(player) || read ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("RegistryName", registryName.toString());
        tag.putBoolean("IsQuest", isQuest);
        tag.putString("Text", text);
        tag.putByte("FormattingLength", (byte) formatting.length);
        for (int i = 0; i < formatting.length; i++) {
            tag.putString("Formatting" + i, formatting[i]);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        registryName = new ResourceLocation(tag.getString("RegistryName"));
        isQuest = tag.getBoolean("IsQuest");
        text = tag.getString("Text");
        formatting = new String[tag.getByte("FormattingLength")];
        for (int i = 0; i < formatting.length; i++) {
            formatting[i] = tag.getString("Formatting" + i);
        }
    }
}