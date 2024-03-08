package uk.joshiejack.settlements.world.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.rhino.Context;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import uk.joshiejack.penguinlib.scripting.ScriptFactory;
import uk.joshiejack.penguinlib.scripting.ScriptLoader;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.quest.data.QuestTracker;
import uk.joshiejack.settlements.world.quest.scripting.wrapper.QuestJS;
import uk.joshiejack.settlements.world.quest.scripting.wrapper.QuestTrackerJS;
import uk.joshiejack.settlements.world.quest.settings.Settings;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Quest implements ReloadableRegistry.PenguinRegistry<Quest> {
    public static final Codec<Quest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Settings.CODEC.optionalFieldOf("settings", Settings.DEFAULT).forGetter(q -> q.settings)
    ).apply(instance, Quest::new));

    public final Map<String, String> methodToFire = new HashMap<>();
    private final Settings settings;
    private Interpreter interpreter;

    public Quest(Settings settings) {
        this.settings = settings;
    }

    @Override
    public Quest init(ResourceManager resourceManager, ResourceLocation id) {
        interpreter = new Interpreter(id, this, QuestHelper.getJavascriptFromResourceLocation(resourceManager, id));
        return this;
    }

    public Settings getSettings() {
        return settings;
    }

    public Set<String> getTriggers() {
        return methodToFire.keySet();
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }

    public void fire(String method, Player player, QuestTracker tracker) {
        methodToFire.forEach((k, v) -> System.out.println(k + " : " + v));
        String function = method.equals("canStart") ? "canStart" : methodToFire.get(method); //This is the function we have to call
        if (function.equals("canStart")) {
            //If we are the canStart function get ready to mark the script as active
            if (ScriptFactory.getResult(interpreter, function, true, new QuestJS(this), new QuestTrackerJS(tracker))) {
                tracker.start(this);
            }
        } else interpreter.callFunction(function, player, new QuestTrackerJS(tracker));
    }

    @Override
    public ResourceLocation id() {
        return Settlements.Registries.QUESTS.getID(this);
    }

    @Override
    public Quest fromNetwork(FriendlyByteBuf buf) {
        return new Quest(Settings.fromNetwork(buf));
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        settings.toNetwork(buf);
    }

    public static class Interpreter extends uk.joshiejack.penguinlib.scripting.Interpreter<Quest> {
        private static final ScriptLoader.ScriptLocation QUESTS = new ScriptLoader.ScriptLocation(Settlements.MODID + "/quests",
                (rl) -> Settlements.Registries.QUESTS.get(rl).getInterpreter());

        public Interpreter(@Nonnull ResourceLocation id, @Nonnull Quest quest, @Nonnull String javascript) {
            super(id, javascript, QUESTS, quest);
        }

        @Override
        protected void addGlobals(Quest quest) {
            //We need to add in the global functions for this quest?
            super.addGlobals(quest);
            context.addToScope(localScope, "quest", Context.javaToJS(context, new QuestJS(quest), localScope));
            callFunction("setup", quest.getSettings());
            String commaList = quest.getSettings().getTriggers();
            String[] commaSplit = commaList.replace(" ", "").split(",");
            for (String c : commaSplit) {
                if (c.contains(":")) {
                    String method = c.split(":")[0];
                    String function = c.split(":")[1];
                    quest.methodToFire.put(method, function);
                } else quest.methodToFire.put(c, "canStart");
            }

            //"Forbidden Keyword >>> Unwrap"
        }

        public Interpreter copy() {
            Interpreter original = this;
            String javascript = QuestHelper.getJavascriptFromResourceLocation(ServerLifecycleHooks.getCurrentServer().getResourceManager(), original.data.id());
            return new Interpreter(original.data.id(), original.data, javascript);
        }
    }
}