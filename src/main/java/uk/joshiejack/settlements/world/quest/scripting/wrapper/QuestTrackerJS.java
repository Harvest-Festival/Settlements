package uk.joshiejack.settlements.world.quest.scripting.wrapper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.penguinlib.scripting.wrapper.AbstractJS;
import uk.joshiejack.penguinlib.scripting.wrapper.PlayerJS;
import uk.joshiejack.settlements.world.level.QuestSavedData;
import uk.joshiejack.settlements.world.quest.Quest;
import uk.joshiejack.settlements.world.quest.data.QuestData;
import uk.joshiejack.settlements.world.quest.data.QuestTracker;

public class QuestTrackerJS extends AbstractJS<QuestTracker> {
    public QuestTrackerJS(QuestTracker tracker) {
        super(tracker);
    }

    public boolean completed(String quest) {
        return penguinScriptingObject.hasCompleted(new ResourceLocation(quest));
    }

    public boolean completed(String quest, int amount) {
        return penguinScriptingObject.hasCompleted(new ResourceLocation(quest), amount);
    }

    public QuestJS byName(String name) {
        return new QuestJS(Settlements.Registries.QUESTS.get(new ResourceLocation(name)));
    }

    public void complete(PlayerJS player, String name) {
        QuestSavedData.get((ServerLevel) player.penguinScriptingObject.level()).markCompleted(player.penguinScriptingObject, Settlements.Registries.QUESTS.get(new ResourceLocation(name)));
    }

    public void call(String q, String function, Object... data) {
        Quest quest = Settlements.Registries.QUESTS.get(new ResourceLocation(q));
        if (quest != null) {
            QuestData storage = penguinScriptingObject.getData(quest.id());
            if (storage != null) {
                storage.callFunction(function, data);
            }
        }
    }
}
