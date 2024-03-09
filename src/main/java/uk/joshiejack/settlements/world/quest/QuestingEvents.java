package uk.joshiejack.settlements.world.quest;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import uk.joshiejack.penguinlib.event.NewDayEvent;
import uk.joshiejack.penguinlib.util.helper.TimeHelper;
import uk.joshiejack.penguinlib.event.ScriptingEvents;
import uk.joshiejack.settlements.world.level.QuestSavedData;
import uk.joshiejack.settlements.world.quest.data.QuestData;
import uk.joshiejack.settlements.world.quest.data.QuestTracker;

import java.util.List;

import static uk.joshiejack.settlements.Settlements.MODID;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MODID)
public class QuestingEvents {
    @SubscribeEvent
    public static void onNewDay(NewDayEvent event) {
        QuestSavedData data = QuestSavedData.get(event.getLevel());
        data.getScriptTrackers().forEach(q -> q.onNewDay(event.getLevel()));
        //Sync the daily quests to everyone when they change
        event.getLevel().getServer().getPlayerList().getPlayers()
                .forEach(data::syncDailies);
        data.setDirty();
    }

    @SubscribeEvent
    public static void onPlayedLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        int day = TimeHelper.getElapsedDays(event.getEntity().level());
        QuestSavedData data = QuestSavedData.get((ServerLevel) event.getEntity().level());
        data.setDay(day);
        data.getTrackers(event.getEntity())
                .forEach(tracker -> tracker.setupOrRefresh(day));
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.level.dimension() == ServerLevel.OVERWORLD
                && event.phase == TickEvent.Phase.END && event.level.getDayTime() % TimeHelper.TICKS_PER_DAY == 1) {
            int day = TimeHelper.getElapsedDays(event.level);
            QuestSavedData.get((ServerLevel) event.level).setDay(day); //Set the day for refresh on load purposes
        }
    }

//    @SubscribeEvent
//    public static void onNPCRightClicked(NPCEvent.NPCRightClickedEvent event) {
//        MinecraftForge.EVENT_BUS.post(new ScriptingTriggerFired("onRightClickedNPC", event.getEntityPlayer(), event.getNPCEntity(), event.getHand()));
//    }
//  //TODO
//    @SubscribeEvent
//    public static void onNPCFinishedTalking(NPCEvent.NPCFinishedSpeakingEvent event) {
//        runOnStorage("onNPCFinishedTalking", event.getScript(), event.getEntityPlayer(), event.getNPCEntity());
//    }

    @SubscribeEvent
    public static void onReload(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) return;
        //TODO? Test if this is called before or after datapacks are loaded by my mod?
        MinecraftServer server = event.getPlayerList().getServer();
        QuestSavedData data = QuestSavedData.get((ServerLevel) event.getPlayer().level());
        data.getScriptTrackers().forEach(tracker -> tracker.reload(data.getDay()));
        for (ServerPlayer player : server.getPlayerList().getPlayers())
            data.clearCache(player.getUUID());
    }

    @SubscribeEvent
    public static void onTriggerFired(ScriptingEvents.TriggerFired event) {
        //Only fire these when we have a player assigned
        Player player = event.getPlayer();
        if (player instanceof ServerPlayer) {
            fireTrigger(event.getMethod(), player);
            QuestHelper.getActive(player, event.getMethod())
                    .forEach(script -> script.callFunction(event.getMethod(), event.getObjects()));
            QuestSavedData.get((ServerLevel) player.level()).setDirty();
        }
    }

    public static void runOnStorage(String function, Quest script, Player player, Object... args) {
        fireTrigger(function, player);
        QuestData data = QuestHelper.getData(player, script);

        if (data != null) {
            Object[] args2 = new Object[args.length + 1];
            System.arraycopy(args, 0, args2, 1, args.length);
            args2[0] = player; //Set the first param to the player
            data.getInterpreter().callFunction(function, args2);
        }

        QuestSavedData.get((ServerLevel) player.level()).setDirty();
    }

    private static void fireTrigger(String method, Player player) {
        //Get All quests for Player
        List<QuestTracker> trackers = QuestSavedData.get((ServerLevel) player.level()).getTrackers(player);
        trackers.forEach((t) -> {
            //We have all the trackers, now we need to call the fire method
            t.fire(method, player);
        });
    }
}
