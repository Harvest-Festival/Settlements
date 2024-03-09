package uk.joshiejack.settlements.network.npc;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import uk.joshiejack.penguinlib.PenguinLib;
import uk.joshiejack.penguinlib.util.registry.Packet;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.chat.ActionSay;

@Packet(PacketFlow.CLIENTBOUND)
public class PacketSay extends PacketButtonLoad<ActionSay> {
    public static final ResourceLocation ID = PenguinLib.prefix("npc_say");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public PacketSay(FriendlyByteBuf buf) {
        super(buf);
    }
    public PacketSay(Player player, NPCMob npc, ActionSay action) {
        super(player, npc, action);
    }

    @Override
    public void handle(Player player) {
        super.handle(player); //Super first!
        Entity entity = player.level().getEntity(npcID);
        if (entity instanceof NPCMob) {
           //TODO:  Minecraft.getMinecraft().displayGuiScreen(new GuiNPCChat((EntityNPC)entity, new Chatter(GuiNPCAsk.modify(action.registryName, action.isQuest, action.text)), action.formatting));
        }
    }
}
