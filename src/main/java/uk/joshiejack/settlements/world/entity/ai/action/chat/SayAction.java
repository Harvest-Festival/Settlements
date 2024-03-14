package uk.joshiejack.settlements.world.entity.ai.action.chat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.network.npc.PacketSay;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.ActionChat;
import uk.joshiejack.settlements.world.entity.ai.action.MentalAction;

public class SayAction extends MentalAction implements ActionChat {
    public Component text;
    public String[] formatting;
    private boolean displayed;
    private boolean read;

    public SayAction() {}
    public SayAction(Component text) {
        this.text = text;
        this.formatting = new String[0];
    }

    public SayAction(Component text, String... params) {
        this.text = text;
        this.formatting = new String[params.length - 1];
        for (int i = 1; i < params.length; i++) {
            this.formatting[i - 1] = String.valueOf(params[i]);
        }
    }

    @Override
    public void onGuiClosed(Player player, NPCMob npc, Object... parameters) {
        read = true;
    }

    @Override
    public InteractionResult execute(NPCMob npc) {
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
        ComponentSerialization.FLAT_CODEC
                .encodeStart(NbtOps.INSTANCE, text)
                .resultOrPartial(Settlements.LOGGER::error)
                .ifPresent(data -> tag.put("Text", data));
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
        if (tag.contains("Text")) {
            ComponentSerialization.FLAT_CODEC
                    .parse(NbtOps.INSTANCE, tag.getCompound("Text"))
                    .resultOrPartial(Settlements.LOGGER::error)
                    .ifPresent(data -> this.text = data);
        }
        formatting = new String[tag.getByte("FormattingLength")];
        for (int i = 0; i < formatting.length; i++) {
            formatting[i] = tag.getString("Formatting" + i);
        }
    }
}
