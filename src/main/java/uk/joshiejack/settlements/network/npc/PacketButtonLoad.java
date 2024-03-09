package uk.joshiejack.settlements.network.npc;

import com.google.common.collect.Lists;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.network.packet.PenguinPacket;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.Action;
import uk.joshiejack.settlements.world.entity.npc.button.NPCButtons;

import java.util.List;

public class PacketButtonLoad<A extends Action> implements PenguinPacket {
    public static final ResourceLocation ID = PenguinLib.prefix("button_load");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    private final List<NPCButtons.ButtonData> list;
    protected A action;
    protected int npcID;

    public PacketButtonLoad(Player player, NPCMob npc, A action) {
        this.npcID = npc.getId();
        this.action = action;
        this.list = NPCButtons.getForDisplay(npc, player);
    }

    @SuppressWarnings("unchecked, ConstantConditions")
    public PacketButtonLoad(FriendlyByteBuf buf) {
        action = (A) Action.createOfType(buf.readUtf());
        action.deserializeNBT(buf.readNbt());
        npcID = buf.readInt();
        list = Lists.newArrayList();
        int count = buf.readByte();
        for (int i = 0; i < count; i++) {
            list.add(new NPCButtons.ButtonData(buf));
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(action.getType());
        buf.writeNbt(action.serializeNBT());
        buf.writeInt(npcID);
        buf.writeByte(list.size());
        list.forEach(b-> b.toBytes(buf));
    }


    @Override
    public void handle(Player player) {
        //TODO: GUI STUFF GuiNPC.setButtons(list);
    }
}
