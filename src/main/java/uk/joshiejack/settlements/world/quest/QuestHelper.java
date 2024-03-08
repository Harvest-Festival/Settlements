package uk.joshiejack.settlements.world.quest;

import joptsimple.internal.Strings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.apache.commons.io.IOUtils;
import uk.joshiejack.penguinlib.scripting.ScriptLoader;
import uk.joshiejack.penguinlib.util.PenguinGroup;
import uk.joshiejack.penguinlib.util.helper.TagHelper;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.entity.npc.status.StatusTracker;
import uk.joshiejack.settlements.world.level.QuestSavedData;
import uk.joshiejack.settlements.world.quest.data.QuestData;
import uk.joshiejack.settlements.world.quest.data.QuestTracker;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class QuestHelper {
    public static List<QuestData> getActive(Player player, String method) {
        return QuestSavedData.get((ServerLevel) player.level()).getActive(player, method);
    }

    public static QuestData getData(Player player, Quest script) {
        return QuestSavedData.get((ServerLevel) player.level()).getData(player, script);
    }

    public static ListTag writeUUIDToTrackerMap(Map<UUID, ? extends INBTSerializable<CompoundTag>> map) {
        return TagHelper.writeMap(map,
                (tag, tracker) -> tag.putString("UUID", tracker.toString()),
                (tag, tracker) -> tag.put("Data", tracker.serializeNBT()));
    }

    public static void readUUIDtoTrackerMap(PenguinGroup type, ListTag list, Map<UUID, QuestTracker> map) {
        TagHelper.readMap(list, map,
                (tag1) -> UUID.fromString(tag1.getString("UUID")),
                (tag2) -> {
                    QuestTracker tracker1 = new QuestTracker(type);
                    tracker1.deserializeNBT(tag2.getCompound("Data"));
                    return tracker1;
                });
    }

    public static void readUUIDToStatusTrackerMap(ListTag list, Map<UUID, StatusTracker> map) {
        TagHelper.readMap(list, map,
                (tag1) -> UUID.fromString(tag1.getString("UUID")),
                (tag2, uuid) -> {
                    StatusTracker tracker1 = new StatusTracker(uuid);
                    tracker1.deserializeNBT(tag2);
                    return tracker1;
                });
    }

    public static ListTag writeQuestMap(Map<ResourceLocation, QuestData> map) {
        return TagHelper.writeMap(map,
                (tag, quest) -> tag.putString("Quest", quest.toString()),
                (tag, data) -> tag.put("Data", data.serializeNBT()));
    }

    public static void readQuestMap(ListTag list, Map<ResourceLocation, QuestData> map) {
        TagHelper.readMap(list, map,
                (tag1) -> new ResourceLocation(tag1.getString("Quest")),
                (tag2, value) -> {
                    QuestData storage = new QuestData(Settlements.Registries.QUESTS.get(value));
                    storage.deserializeNBT(tag2.getCompound("Data"));
                    return storage;
                });
    }

    public static String getJavascriptFromResourceLocation(ResourceManager manager, ResourceLocation resourcelocation) {
        String namespace = resourcelocation.getNamespace();
        String path = Settlements.MODID + "/quests/" + resourcelocation.getPath();
        ResourceLocation location = new ResourceLocation(namespace, path + (path.contains(".js") ? Strings.EMPTY : ".js"));
        System.out.println("Looking for " + location);
        try (Reader reader = manager.getResource(location).get().openAsReader()) {
            String javascript = IOUtils.toString(reader);
            return ScriptLoader.requireToJavascript(manager, javascript);
        } catch (IOException | NoSuchElementException e) {
            return "print('Could not find a quest script @ %s')".formatted(location); //Nothing found so let's return a simple print
        }
    }
}
