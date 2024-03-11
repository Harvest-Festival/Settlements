package uk.joshiejack.settlements.client.gui;

import com.google.common.base.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.settlements.client.gui.widget.AnswerButton;
import uk.joshiejack.settlements.network.npc.PacketAnswer;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.npc.button.NPCButtons;

import java.util.List;

public class NPCAskScreen extends NPCScreen {
    private final ResourceLocation quest;
    private final boolean isQuest;
    private final Component question;
    private final Component[] answers;
    public int selected;
    private final boolean hasQuestion;
    private int startY;
    private boolean finished;
    private final int chatboxHeight;

    public NPCAskScreen(NPCMob npc, List<NPCButtons.ButtonData> buttons, ResourceLocation quest, boolean isQuest, boolean translate, String question, String[] answers, String... formatting) {
        super(npc, buttons);
        this.quest = quest;
        this.isQuest = isQuest;
        this.question = translate ? modify(quest, question, formatting): Component.literal(question);
        this.hasQuestion = !Strings.isNullOrEmpty(question);
        this.answers = new Component[answers.length];
        for (int i = 0; i < answers.length; i++) {
            this.answers[i] = translate ? modify(quest, answers[i], formatting): Component.literal(answers[i]);
        }

        chatboxHeight = Math.max(0, (answers.length + (hasQuestion ? 1 : 0)) - 5);
    }

    public static Component modify(ResourceLocation quest, String text, String... formatting) {
        String key = quest.getNamespace() + "." + quest.getPath() + "." + text;
        return Component.translatable(key, (Object[]) formatting);
    }

    @Override
    protected void initScreen(@NotNull Minecraft minecraft, @NotNull Player player) {
        super.initScreen(minecraft, player);
        this.startY = hasQuestion ? window.getGuiScaledHeight() - 84 : window.getGuiScaledHeight() - 93;
        if (hasQuestion) addRenderableOnly(new AnswerButton(this, -1, question, leftPos + 4, window.getGuiScaledHeight() - 93));
        for (int i = 0; i < answers.length; i++) {
            addRenderableWidget(new AnswerButton(this, i, answers[i], leftPos + 20, startY + (i * 9)));
        }
    }

//    @Override
//    public void initGui() {
//        super.initGui();
//        this.startY = hasQuestion ? (sr.getScaledHeight() / 2) + 40 : (sr.getScaledHeight() / 2) + 32;
//        for (int i = 0; i < answers.length; i++) {
//            buttonList.add(new ButtonAnswer(this, i, buttonList.size(), guiLeft + 20, guiTop - 1 + startY + (i * 9)));
//        }
//    }

    private void adjustSelection(int amount) {
        int newSelection = selected + amount;
        if (newSelection >= answers.length) {
            newSelection = answers.length - 1;
        } else if (newSelection < 0) {
            newSelection = 0;
        }

        selected = newSelection;
    }

    @SuppressWarnings("ConstantConditions")
    public void setFinished() {
        this.finished = true;
        minecraft.player.closeContainer();
    }

    //Scale the background
    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float pPartialTick) {
        //GlStateManager.pushMatrix();
        //BLACK BOX
        //mc.renderEngine.bindTexture(CHATBOX);
        int x = leftPos;
        //TOP
        graphics.blit(CHATBOX, x, window.getGuiScaledHeight() - 101, 0, 150, 256, 25); //51 total  TOP =
        //BOTTOM
        graphics.blit(CHATBOX, x, window.getGuiScaledHeight() - 76 + (chatboxHeight * 9), 0, 173, 256, 26);
        //GAP FILLER
        for (int i = 0; i < chatboxHeight; i++) {
            graphics.blit(CHATBOX, x, window.getGuiScaledHeight() - 76 + (i * 9), 0, 167, 256, 9);
        }

        //GlStateManager.enableBlend();
        //ChatFontRenderer.colorise(inside);
        setColor(graphics, inside);
        //INSIDE COLOURS
        //TOP
        graphics.blit(CHATBOX, x, window.getGuiScaledHeight() - 100, 0, 100, 256, 25); //51 total  TOP =
        //BOTTOM
        graphics.blit(CHATBOX, x, window.getGuiScaledHeight() - 75 + (chatboxHeight * 9), 0, 125, 256, 26);
        //GAP FILLER
        for (int i = 0; i < chatboxHeight; i++) {
            graphics.blit(CHATBOX, x, window.getGuiScaledHeight() - 75 + (i * 9), 0, 127, 256, 9);
        }

        setColor(graphics, outside);
        //ChatFontRenderer.colorise(outside);
        //OUTSIDE COLOURS
        //TOP
        graphics.blit(CHATBOX, x, window.getGuiScaledHeight() - 100, 0, 50, 256, 25); //51 total  TOP =
        //BOTTOM
        graphics.blit(CHATBOX, x, window.getGuiScaledHeight() - 75 + (chatboxHeight * 9), 0, 75, 256, 26);
        //GAP FILLER
        for (int i = 0; i < chatboxHeight; i++) {
            graphics.blit(CHATBOX, x, window.getGuiScaledHeight() - 75 + (i * 9), 0, 78, 256, 9);
        }

        graphics.setColor(1F, 1F, 1F, 1F);
        renderForeground(graphics, mouseX, mouseY, pPartialTick);
    }

    @Override
    protected void renderForeground(GuiGraphics guiGraphics, int i, int i1, float v) {
        //TODO Draw NPC Name
//        GlStateManager.color(1F, 1F, 1F, 1F);
//        GlStateManager.disableBlend();
//        ChatFontRenderer.render(this, x, window.getGuiScaledHeight() - 101 - 150, npc.getName(), inside, outside);
//        GlStateManager.color(1F, 1F, 1F, 1F);
//        GlStateManager.popMatrix();
    }

    @Override
    public boolean charTyped(char character, int key) {
        if (character == 'w' || key == 200) {
            adjustSelection(-1);
            return true;
        }

        if (character == 's' || key == 208) {
            adjustSelection(+1);
            return true;
        }

        if (key == 28 || key == 57 || character == 'q') {
            setFinished();
            return true;
        }

        return super.charTyped(character, key);
    }

//    @Override
//    public void keyTyped(char character, int key) throws IOException {
//        super.keyTyped(character, key);
//
//        //W or Up
//        if (character == 'w' || key == 200) {
//            adjustSelection(-1);
//        }
//
//        //S or Down
//        if (character == 's' || key == 208) {
//            adjustSelection(+1);
//        }
//
//        //Enter or Spacebar or Q
//        if (key == 28 || key == 57 || character == 'q') {
//            setFinished(); //Selection has been marked
//        }
//    }

    @Override
    public void removed() {
        if (finished) {
            this.finished = false; //Reset the value
            PenguinNetwork.sendToServer(new PacketAnswer(npc.getId(), quest, isQuest, selected)); //Remove the player
        } else super.removed();
    }

//    @Override
//    protected void drawOverlay(int x, int y) {
//        if (hasQuestion) fontRenderer.drawString(TextFormatting.BOLD + question, 20, (sr.getScaledHeight() / 2) + 32, 0xFFFFFF);
//        for (int i = 0; i < answers.length; i++) {
//            fontRenderer.drawString(TextFormatting.BOLD + answers[i], 40, startY + (i * 9), 0xFFFFFF);
//        }
//
//        GlStateManager.color(1F, 1F, 1F);
//        mc.renderEngine.bindTexture(ELEMENTS);
//        drawTexturedModalRect(20, startY + 1 + (selected * 9), 0, 32, 19, 8);
//    }
}
