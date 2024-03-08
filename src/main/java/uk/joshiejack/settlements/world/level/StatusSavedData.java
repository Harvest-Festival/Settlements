package uk.joshiejack.settlements.world.level;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.commons.lang3.tuple.Pair;
import uk.joshiejack.penguinlib.scripting.ScriptFactory;
import uk.joshiejack.penguinlib.util.PenguinGroup;
import uk.joshiejack.penguinlib.util.helper.TimeHelper;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;
import uk.joshiejack.settlements.world.quest.Quest;
import uk.joshiejack.settlements.world.quest.QuestHelper;
import uk.joshiejack.settlements.world.quest.data.QuestData;
import uk.joshiejack.settlements.world.quest.data.QuestTracker;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class QuestSavedData extends SavedData {
    private static final String DATA_NAME = "settlements_quests";
    private final Map<UUID, QuestTracker> playerQuests = Maps.newHashMap();
    private final Map<UUID, QuestTracker> teamQuests = Maps.newHashMap();
    private final QuestTracker globalQuests = new QuestTracker(PenguinGroup.GLOBAL);
    private final Cache<UUID, Cache<String, List<QuestData>>> allScripts = CacheBuilder.newBuilder().build(); //Clear the cache for a uuid whenever a quest in the list is completed/added
    private final Cache<UUID, List<QuestTracker>> allTrackers = CacheBuilder.newBuilder().build();
    private static final List<QuestData> EMPTY = Lists.newArrayList();
    private static final List<QuestTracker> EMPTY_TRACKERS = Lists.newArrayList();
    private int day;

    public static QuestSavedData get(ServerLevel world) {
        return world.getServer().overworld().getDataStorage().computeIfAbsent(new Factory<>(QuestSavedData::new, QuestSavedData::load), DATA_NAME);
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
        this.setDirty();
    }

    public void clearCache(UUID uuid) {
        allScripts.invalidate(uuid);
    }

    public List<Quest> getDailies(Player player) {
        return getTrackers(player).stream().map(QuestTracker::getDaily).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void syncDailies(Player player) {
        List<Quest> dailies = getDailies(player);
        Map<Quest, Pair<String, String>> map = Maps.newHashMap();

        for (Quest q: dailies) {
            map.put(q, Pair.of(ScriptFactory.getResult(q.getInterpreter(), "getTaskTitle", "Unknown"),
                    ScriptFactory.getResult(q.getInterpreter(), "getTaskDescription", "Unknown")));
        }

        //TODO: Packet PenguinNetwork.sendToClient(new PacketSyncDailies(map), player);
    }

    public List<QuestTracker> getScriptTrackers() {
        List<QuestTracker> trackers = Lists.newArrayList();
        trackers.add(globalQuests);
        trackers.addAll(teamQuests.values());
        trackers.addAll(playerQuests.values());
        return trackers;
    }

    public void markCompleted(Player player, Quest script) {
        getTrackerForQuest(player, script)
                .markCompleted(TimeHelper.getElapsedDays(player.level()), script);
        allScripts.invalidate(player.getUUID()); //Clear the cache
    }

    @Nonnull
    public QuestTracker getPlayerTracker(Player player) {
        //Create a tracker for the players if it doesn't exist yet
        UUID uuid = player.getUUID();
        if (!playerQuests.containsKey(uuid)) {
            playerQuests.put(uuid, new QuestTracker(PenguinGroup.PLAYER));
        }

        return playerQuests.get(uuid);
    }

    @Nonnull
    private QuestTracker getTeamTracker(Player player) {
        return getTeamTracker(PenguinTeams.getTeamUUIDForPlayer(player));
    }

    @Nonnull
    public QuestTracker getTeamTracker(UUID teamUUID) {
        if (!teamQuests.containsKey(teamUUID)) {
            teamQuests.put(teamUUID, new QuestTracker(PenguinGroup.TEAM));
        }

        return teamQuests.get(teamUUID);
    }

    public QuestTracker getServerTracker() {
        return globalQuests;
    }

    @Nonnull
    public QuestTracker getTrackerForQuest(Player player, Quest script) {
        return switch (script.getSettings().getType()) {
            case PLAYER -> getPlayerTracker(player);
            case TEAM -> getTeamTracker(player);
            default -> globalQuests;
        };
    }

    public List<QuestTracker> getTrackers(Player player) {
        try {
            return allTrackers.get(player.getUUID(), () -> {
                List<QuestTracker> trackers = Lists.newArrayList();
                trackers.add(getPlayerTracker(player));
                trackers.add(getTeamTracker(player));
                trackers.add(globalQuests);
                return trackers;
            });
        } catch (Exception ignored) {
            return EMPTY_TRACKERS;
        }
    }

    public QuestData getData(Player player, Quest quest) {
        return getTrackerForQuest(player, quest).getData(quest.id());
    }

    public List<QuestData> getActive(Player player, String method) {
        try {
            allScripts.invalidateAll();
            return allScripts.get(player.getUUID(), () -> CacheBuilder.newBuilder().build()).get(method, () -> {
                List<QuestData> list = new ArrayList<>();
                getTrackers(player).forEach((t) -> list.addAll(t.getActive(method)));
                return list;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return EMPTY;
        }
    }

    private static QuestSavedData load(CompoundTag compound) {
        QuestSavedData data = new QuestSavedData();
        data.day = compound.getInt("Day");
        QuestHelper.readUUIDtoTrackerMap(PenguinGroup.PLAYER, compound.getList("Player", 10), data.playerQuests);
        QuestHelper.readUUIDtoTrackerMap(PenguinGroup.TEAM, compound.getList("Team", 10), data.teamQuests);
        data.globalQuests.deserializeNBT(compound.getCompound("Global"));

        //Reload in the triggers, so yey!
        data.playerQuests.values().forEach((t) -> t.setupOrRefresh(data.day));
        data.teamQuests.values().forEach((t) -> t.setupOrRefresh(data.day));
        data.globalQuests.setupOrRefresh(data.day);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putInt("Day", day);
        compound.put("Player", QuestHelper.writeUUIDToTrackerMap(playerQuests));
        compound.put("Team", QuestHelper.writeUUIDToTrackerMap(teamQuests));
        compound.put("Global", globalQuests.serializeNBT()); //Server Quests
        return compound;
    }
}
