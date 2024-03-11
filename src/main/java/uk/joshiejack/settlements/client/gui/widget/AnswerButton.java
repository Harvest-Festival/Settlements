package uk.joshiejack.settlements.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.client.gui.widget.AbstractButton;
import uk.joshiejack.settlements.client.gui.NPCAskScreen;
import uk.joshiejack.settlements.client.gui.NPCChatScreen;

public class AnswerButton extends AbstractButton<NPCAskScreen> {
    public AnswerButton(NPCAskScreen screen, int selectionID, Component component, int x, int y) {
        super(screen, x, y, 217, 10, component, (gui) -> {
            if (screen.selected == selectionID) {
                screen.setFinished(); //Mark it as finished
            } else screen.selected = selectionID;
        });
    }

    @Override
    protected void renderButton(@NotNull GuiGraphics guiGraphics, int i, int i1, float v, boolean b) {
        guiGraphics.drawString(NPCChatScreen.font.get(), getMessage(), getX(), getY(), 0xFFFFFF);
    }
}
