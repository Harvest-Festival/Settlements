package uk.joshiejack.settlements.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.client.gui.widget.AbstractButton;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.client.gui.NPCAskScreen;
import uk.joshiejack.settlements.client.gui.NPCChatScreen;

public class AnswerButton extends AbstractButton<NPCAskScreen> {
    private static final ResourceLocation SELECTED = Settlements.prefix("selected");
    private final int selectionID;

    public AnswerButton(NPCAskScreen screen, int selectionID, Component component, int x, int y) {
        super(screen, x, y, 217, 10, component, (gui) -> {
            if (screen.selected == selectionID) {
                screen.setFinished(); //Mark it as finished
            } else screen.selected = selectionID;
        });

        this.selectionID = selectionID;
    }

    @Override
    protected void renderButton(@NotNull GuiGraphics guiGraphics, int i, int i1, float v, boolean b) {
        if (screen.selected == selectionID)
            guiGraphics.blitSprite(SELECTED, getX()-2, getY()-3, 16, 8);
        guiGraphics.drawString(NPCChatScreen.font.get(), getMessage(), getX() + 16, getY(), 0xFFFFFF);
    }
}
