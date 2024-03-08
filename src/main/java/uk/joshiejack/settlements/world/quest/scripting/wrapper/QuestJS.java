package uk.joshiejack.settlements.world.quest.scripting.wrapper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import uk.joshiejack.penguinlib.scripting.wrapper.AbstractJS;
import uk.joshiejack.penguinlib.scripting.wrapper.PlayerJS;
import uk.joshiejack.settlements.world.level.QuestSavedData;
import uk.joshiejack.settlements.world.quest.Quest;

@SuppressWarnings("unused")
public class QuestJS extends AbstractJS<Quest> {
    public QuestJS(Quest quest) {
        super(quest);
    }

    public void complete(PlayerJS player) {
        QuestSavedData.get((ServerLevel) player.get().level()).markCompleted(player.get(), penguinScriptingObject);
    }

    @SuppressWarnings("all")
    public String localize(String suffix) {
        ResourceLocation script = penguinScriptingObject.id();
        String text = script.getNamespace() + ".quest." + script.getPath().replace("/", ".");
        return text + "." + suffix;
    }
}
