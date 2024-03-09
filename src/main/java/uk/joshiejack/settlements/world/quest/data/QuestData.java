package uk.joshiejack.settlements.world.quest.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.util.PenguinGroup;
import uk.joshiejack.settlements.world.quest.settings.Information;
import uk.joshiejack.settlements.world.quest.Quest;

public class QuestData implements INBTSerializable<CompoundTag> {
    private final Quest.Interpreter interpreter;
    private final PenguinGroup group;

    public QuestData(Quest script) {
        this.interpreter = script.getInterpreter().copy(); //ignore?
        this.group = script.getSettings().getType();
        if (script.getSettings().isDaily()) { //If this is a daily quest, copy over the data from the main line quest
            CompoundTag tag = new CompoundTag();
            script.getInterpreter().callFunction("saveData", tag);
            this.deserializeNBT(tag);
        }
    }

    public Quest.Interpreter getInterpreter() {
        return interpreter;
    }

    public void callFunction(String function, Object... objects) {
        interpreter.callFunction(function, objects);
    }

    public Information toInformation(Player player) {
        Information information = new Information(group);
        if (interpreter.hasMethod("display")) {
            interpreter.callFunction("display", information, player);
        }

        return information;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (interpreter.hasMethod("saveData")) {
            interpreter.callFunction("saveData", tag);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundTag tag) {
        if (interpreter.hasMethod("loadData")) {
            interpreter.callFunction("loadData", tag);
        }
    }
}
