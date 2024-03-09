package uk.joshiejack.settlements.world.quest.data;

import com.google.common.collect.*;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import uk.joshiejack.penguinlib.scripting.ScriptFactory;
import uk.joshiejack.penguinlib.util.PenguinGroup;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.level.QuestSavedData;
import uk.joshiejack.settlements.world.quest.Quest;
import uk.joshiejack.settlements.world.quest.settings.Repeat;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuestTracker implements INBTSerializable<CompoundTag> {
    private final Object2IntMap<ResourceLocation> completed = new Object2IntOpenHashMap<>(); //Save this
    private final Object2IntMap<ResourceLocation> completedDay = new Object2IntOpenHashMap<>(); //Save this
    private final Map<ResourceLocation, QuestData> active = Maps.newHashMap(); //Save this
    private static final RandomSource rand = RandomSource.create();
    private final PenguinGroup type;
    private Quest daily;

    //List of all the active triggers
    private final Multimap<String, Quest> triggers = HashMultimap.create();
    //List of all active methods, aka for quests that aren't active
    private final Multimap<String, QuestData> methods = HashMultimap.create();

    public QuestTracker(PenguinGroup type) {
        this.type = type;
        this.addDefault(type);
    }

    private void addDefault(PenguinGroup type) {
        List<Quest> scripts = Settlements.Registries.QUESTS.stream().filter((script -> script.getSettings().getType() == type)).toList();
        for (Quest script : scripts) {
            if (script.getSettings().isDefault() && !completed.containsKey(script.id())) {
                active.put(script.id(), new QuestData(script));
            }
        }
    }

    public Collection<QuestData> getActive(String method) {
        return methods.get(method);
    }

    @SuppressWarnings("ConstantConditions")
    public void onNewDay(@Nullable ServerLevel world) {
        List<Quest> dailies = Settlements.Registries.QUESTS.stream().filter(q -> q.getSettings().getType() == type && q.getSettings().isDaily()
                && !active.containsKey(q.id())).toList();
        if (!dailies.isEmpty()) {
            daily = dailies.get((world == null ? rand : world.random).nextInt(dailies.size()));
            daily.getInterpreter().callFunction("onTaskCreation");
        } else daily = null;
    }

    @Nullable
    public Quest getDaily() {
        return daily;
    }

    public QuestData getData(ResourceLocation quest) {
        return active.get(quest);
    }

    public boolean hasCompleted(ResourceLocation questID) {
        return completed.containsKey(questID);
    }

    public boolean hasCompleted(ResourceLocation questID, int amount) {
        return hasCompleted(questID) && completed.getInt(questID) >= amount;
    }

    public void reload(int day) {
        onNewDay(null); //Reset the selected daily quest
        for (Map.Entry<ResourceLocation, QuestData> entry : Sets.newHashSet(active.entrySet())) {
            CompoundTag tag = entry.getValue().serializeNBT(); //Save the old data
            QuestData nD = new QuestData(Settlements.Registries.QUESTS.get(entry.getKey()));
            nD.deserializeNBT(tag); //Copy in the new data
            active.put(entry.getKey(), nD);
        }

        //Reload shit
        setupOrRefresh(day);
    }

    public void setupOrRefresh(int day) {
        triggers.clear();
        methods.clear();
        //Build a list of all quests that are not currently active, but that are able to be started i.e. completed < repeat amount
        for (Quest quest : Settlements.Registries.QUESTS.registry().values()) {
            if (quest.getSettings().getType() != type) continue;
            if (completed.containsKey(quest.id())) {
                if (quest.getSettings().getRepeat() == Repeat.NONE)
                    continue; //If we have completed the quest and we don't repeat again, skip this
                else {
                    int lastCompletion = completedDay.getInt(completedDay);
                    int daysBetween = day - lastCompletion;
                    if (daysBetween < quest.getSettings().getRepeat().getDays()) continue; //We can't readd so don't
                }
            }

            //Now we know that things can't be added to the triggers, we now need to check if the quest is active
            if (active.containsKey(quest.id())) {
                //Ok we found a match so we don't want to add "ActiveTriggers" for "ActiveMethods"
                ScriptFactory.getMethods().stream().filter(m -> quest.getInterpreter().hasMethod(m)).forEach((method) -> methods.get(method).add(active.get(quest.id())));
            } else {
                //We know the quest isn't currently active, and in theory it can be started as we are just ignoring the prereqs, and checking in the canStartMethod instead
                //Therefore we want to call the canStart method/other methods where applicable so let's add those to the triggers
                quest.getTriggers().forEach((m) -> triggers.get(m).add(quest));
            }
        }
    }

    public void start(Quest original) {
        active.put(original.id(), new QuestData(original));
        if (daily != null && daily.id().equals(original.id())) {
            daily = null; //Empty out the daily quests as they have been cleared out
            //Update the world about the daily having changed
            ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(p -> QuestSavedData.get((ServerLevel) p.level()).syncDailies(p));
        }

        setupOrRefresh(0); //Refresh everything as we have added a new script
    }

    public void fire(String method, Player player) {
        HashMultimap.create(triggers).get(method).forEach(script -> script.fire(method, player, this));
    }

    public void markCompleted(int day, Quest quest) {
        completed.mergeInt(quest.id(), 1, Integer::sum); //TODO: Test?
        completedDay.put(quest.id(), day);
        active.remove(quest.id());
        setupOrRefresh(day);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag questData = new ListTag();
        Streams.concat(active.keySet().stream(), completed.keySet().stream(), completedDay.keySet().stream())
                .collect(Collectors.toSet())
                .forEach((id) -> {
                    CompoundTag data = new CompoundTag();
                    data.putString("Quest", id.toString());
                    //If we are currently active attempt to save the data
                    if (active.containsKey(id)) {
                        CompoundTag nbt = active.get(id).serializeNBT();
                        if (nbt.isEmpty())
                            data.putBoolean("Data", false);
                        else
                            data.put("Data", nbt);
                    }

                    if (completed.containsKey(id)) data.putInt("CompletedCount", completed.getInt(id));
                    if (completedDay.containsKey(id)) data.putInt("DayLastCompleted", completedDay.getInt(id));
                    questData.add(data);
                });

        if (!questData.isEmpty()) tag.put("QuestData", questData);
        if (daily != null) {
            tag.putString("Daily", daily.id().toString());
            CompoundTag data = new CompoundTag();
            daily.getInterpreter().callFunction("saveData", data);
            tag.put("DailyData", data);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("QuestData", Tag.TAG_LIST)) {
            ListTag compound = nbt.getList("QuestData", Tag.TAG_COMPOUND);
            for (int i = 0; i < compound.size(); i++) {
                CompoundTag data = compound.getCompound(i);
                ResourceLocation id = new ResourceLocation(data.getString("Quest"));
                if (data.contains("Data")) {
                    QuestData questData = new QuestData(Settlements.Registries.QUESTS.get(id));
                    if (data.contains("Data", Tag.TAG_COMPOUND))
                        questData.deserializeNBT(data.getCompound("Data"));
                    active.put(id, questData);
                }

                if (data.contains("CompletedCount")) completed.put(id, data.getInt("CompletedCount"));
                if (data.contains("DayLastCompleted")) completedDay.put(id, data.getInt("DayLastCompleted"));
            }
        }

        if (nbt.contains("Daily"))
            daily = Settlements.Registries.QUESTS.get(new ResourceLocation(nbt.getString("Daily")));
        else onNewDay(null);
    }
}
