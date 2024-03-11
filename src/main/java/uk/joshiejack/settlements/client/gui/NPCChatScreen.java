package uk.joshiejack.settlements.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.Lazy;
import uk.joshiejack.penguinlib.client.gui.SimpleChatter;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.npc.button.NPCButtons;

import java.util.List;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class NPCChatScreen extends NPCScreen {
    private static final ResourceLocation CHAT_FONT = new ResourceLocation(Settlements.MODID, "chat_font");
    public static final Lazy<Font> font = Lazy.of(NPCChatScreen::createChatFont);
    private final SimpleChatter script;
    private final String[] objects;

    private static Font createChatFont() {
        FontSet missingFontSet = Minecraft.getInstance().fontManager.missingFontSet;
        Map<ResourceLocation, FontSet> fontSets = Minecraft.getInstance().fontManager.fontSets;
        return new Font((m) -> fontSets.getOrDefault(CHAT_FONT, missingFontSet), false);
    }

    public NPCChatScreen(NPCMob npc, List<NPCButtons.ButtonData> buttons, SimpleChatter chatter, String... objects) {
        super(npc, buttons);
        this.script = chatter.withWidth(220).withSpeed(1F).withFormatting(null);
        this.objects = objects;
    }

    @Override
    public void tick() {
        script.update(font.get());
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int i, int i1, float v) {
        script.draw(graphics, font.get(), leftPos + 20, window.getGuiScaledHeight() - 93, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double x, double y, int mouseButton) {
        if (getFocused() == null && script.mouseClicked(mouseButton)) {
            minecraft.player.closeContainer();
            return true;
        }

        return super.mouseClicked(x, y, mouseButton);
    }



//    @Override
//    protected void drawOverlay(int x, int y) {
//        script.draw(fontRenderer, 20, (sr.getScaledHeight() / 2) + 32, 0xFFFFFF);
//    }
//
//    private void skip(int button) {
//        if (selectedButton == null && script.mouseClicked(button)) {
//            mc.player.closeScreen();
//        }
//    }
//
//    @Override
//    public void keyTyped(char character, int key) throws IOException {
//        super.keyTyped(character, key);
//        //Enter or Spacebar or Q
//        if (key == 28 || key == 57 || character == 'q') {
//            skip(0); //Forwads
//        }
//    }
//
//    @Override
//    protected void mouseClicked(int x, int y, int mouseButton) throws IOException {
//        super.mouseClicked(x, y, mouseButton);
//        skip(mouseButton);
}
