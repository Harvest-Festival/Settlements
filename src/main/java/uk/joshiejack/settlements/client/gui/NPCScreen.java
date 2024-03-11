package uk.joshiejack.settlements.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.client.gui.AbstractScreen;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.npc.button.NPCButtons;

import java.util.List;

import static uk.joshiejack.settlements.Settlements.MODID;

public abstract class NPCScreen extends AbstractScreen {
    private static final ResourceLocation BACKGROUND = guiTexture(MODID, "chat");
    public static final ResourceLocation CHATBOX = guiTexture(MODID, "chatbox");
    public static final ResourceLocation ELEMENTS = guiTexture(MODID, "npc_elements");
    protected final List<NPCButtons.ButtonData> BUTTONS = Lists.newArrayList();
    protected final NPCMob npc;
    protected final int inside;
    protected final int outside;
    protected Window window;

    public NPCScreen(NPCMob npc, List<NPCButtons.ButtonData> buttons) {
        super(Component.empty(), BACKGROUND, 256, 256);
        this.npc = npc;
        short factor = 200;// 0-255;
        //super.inside = (factor << 24) | (npc.getNPC().getInsideColor() & 0x00ffffff);
        //super.outside = (220 << 24) | (npc.getNPC().getOutsideColor() & 0x00ffffff);
        this.inside = npc.getNPC().getInsideColor();
        this.outside = npc.getNPC().getOutsideColor();
    }

    public static void setButtons(List<NPCButtons.ButtonData> buttons) {
//        BUTTONS.clear();
//        BUTTONS.addAll(buttons);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int x, int y, float pPartialTick) {
      //  GlStateManager.pushMatrix();
//        mc.renderEngine.bindTexture(CHATBOX);
//        drawTexturedModalRect(x, sr.getScaledHeight() - 101, 0, 150, 256, 51);
//        GlStateManager.enableBlend();
//        ChatFontRenderer.colorise(inside);
//        drawTexturedModalRect(x, sr.getScaledHeight() - 101, 0, 100, 256, 51);
//        ChatFontRenderer.colorise(outside);
//        drawTexturedModalRect(x, sr.getScaledHeight() - 101, 0, 50, 256, 51);
//        GlStateManager.color(1F, 1F, 1F, 1F);
//        GlStateManager.disableBlend();
//        ChatFontRenderer.render(this, x, sr.getScaledHeight() - 101 - 150, npc.getName(), inside, outside);
//        GlStateManager.color(1F, 1F, 1F, 1F);
//        GlStateManager.popMatrix();


        graphics.blit(CHATBOX, leftPos, window.getGuiScaledHeight() - 101, 0, 150, 256, 51);
        setColor(graphics, inside);
        graphics.blit(CHATBOX, leftPos, window.getGuiScaledHeight() - 101, 0, 100, 256, 51);
        setColor(graphics, outside);
        graphics.blit(CHATBOX, leftPos, window.getGuiScaledHeight() - 101, 0, 50, 256, 51);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        //TODO: Add Chat Font Renderer
        this.renderForeground(graphics, x, y, pPartialTick);
    }

    private void setColor(GuiGraphics graphics, int color) {
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        graphics.setColor(red, green, blue, 1.0F);
    }

    @Override
    protected void initScreen(@NotNull Minecraft minecraft, @NotNull Player player) {
        this.window = minecraft.getWindow();
        //TODO: BUTTONS.addAll(buttons);
    }

    @Override
    public void renderTransparentBackground(@NotNull GuiGraphics pGuiGraphics) {}


//
//    @Override
//    public boolean doesGuiPauseGame() {
//        return false;
//    }
//
//    @Override
//    public void onGuiClosed() {
//        PenguinNetwork.sendToServer(new PacketEndChat(npc.getEntityId())); //Remove the player
//    }
//
//    @Override
//    public void initGui() {
//        super.initGui();
//        for (int i = 0; i < BUTTONS.size(); i++) {
//            NPCButtons.ButtonData b = BUTTONS.get(i);
//            if (i < 2) {
//                buttonList.add(new ButtonScriptCaller(this, b, inside, outside, npc.getEntityId(), buttonList.size(), guiLeft + 241, sr.getScaledHeight() - 96 + (i * 21)));
//            } else
//                buttonList.add(new ButtonScriptCaller(this, b, inside, outside, npc.getEntityId(), buttonList.size(), guiLeft - 3, sr.getScaledHeight() - 96 + ((i - 2) * 21)).invert());
//        }
//    }
//
//    @Override
//    public void drawDefaultBackground() {
//    }
//
//
//    @Override
//    public void drawBackground(int x, int y) {
//        GlStateManager.pushMatrix();
//        mc.renderEngine.bindTexture(CHATBOX);
//        drawTexturedModalRect(x, sr.getScaledHeight() - 101, 0, 150, 256, 51);
//        GlStateManager.enableBlend();
//        ChatFontRenderer.colorise(inside);
//        drawTexturedModalRect(x, sr.getScaledHeight() - 101, 0, 100, 256, 51);
//        ChatFontRenderer.colorise(outside);
//        drawTexturedModalRect(x, sr.getScaledHeight() - 101, 0, 50, 256, 51);
//        GlStateManager.color(1F, 1F, 1F, 1F);
//        GlStateManager.disableBlend();
//        ChatFontRenderer.render(this, x, sr.getScaledHeight() - 101 - 150, npc.getName(), inside, outside);
//        GlStateManager.color(1F, 1F, 1F, 1F);
//        GlStateManager.popMatrix();
//    }
//
//    @Override
//    public void drawForeground(int x, int y) {
//        boolean originalFlag = fontRenderer.getUnicodeFlag();
//        fontRenderer.setUnicodeFlag(true);
//        mc.renderEngine.bindTexture(ELEMENTS);
//        GlStateManager.color(1F, 1F, 1F);
//        RenderHelper.enableGUIStandardItemLighting();
//        drawOverlay(x, y);
//        fontRenderer.setUnicodeFlag(originalFlag);
//    }
//
//    protected abstract void drawOverlay(int x, int y);
}
