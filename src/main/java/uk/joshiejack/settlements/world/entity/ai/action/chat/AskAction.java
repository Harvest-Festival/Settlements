package uk.joshiejack.settlements.world.entity.ai.action.chat;

import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.scripting.Interpreter;
import uk.joshiejack.penguinlib.scripting.ScriptFactory;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.network.npc.PacketAsk;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.ActionChat;
import uk.joshiejack.settlements.world.entity.ai.action.MentalAction;
import uk.joshiejack.settlements.world.level.QuestSavedData;

import java.util.List;
import java.util.Objects;

//TODO@PenguinLoader("ask")
public class AskAction extends MentalAction implements ActionChat {
    public String question;
    public String[] answers;
    public String[] formatting;
    private String[] functions;
    public boolean translate;
    private boolean asked;
    private boolean answered;

    public AskAction() {}
    public AskAction(boolean translate, String... params) {
        this.translate = translate;
        this.question = params[0];
        //Stuff
        List<String> answers = Lists.newArrayList();
        List<String> functions = Lists.newArrayList();
        List<String> formatting = Lists.newArrayList();
        boolean hitBreakPoint = false;
        for (int i = 1; i < params.length; i++) {
            String s1 = Objects.toString(params[i]);
            if (s1.equals("#")) {
                hitBreakPoint = true;
            } else {
                if (hitBreakPoint) formatting.add(s1);
                else if (s1.contains("->")) {
                    answers.add(s1.split("->")[0]);
                    functions.add(s1.split("->")[1]);
                } else answers.add(s1);
            }
        }

        this.answers = answers.toArray(new String[0]);
        this.functions = functions.toArray(new String[0]);
        this.formatting = formatting.toArray(new String[0]);
    }

    @Override
    public void onGuiClosed(Player player, NPCMob npc, Object... parameters) {
        answered = true; //To allow this to exit the forever loop
        if (parameters.length == 1) {
            int option = (int) parameters[0]; //Always the case
            Interpreter<?> it = isQuest ? QuestSavedData.get((ServerLevel) player.level()).getTrackerForQuest(player, Settlements.Registries.QUESTS.get(registryName)).getData(registryName).getInterpreter() : ScriptFactory.getScript(registryName);
            if (it != null)
                it.callFunction(functions[option], player, npc, option);//Call this function with the option number
        }

        npc.removeTalking(player); //Close it no matter the circumstance
    }

    @Override
    public InteractionResult execute(NPCMob npc) {
        System.out.println("Execute AskAction");
        System.out.println("Asked: " + asked);
        System.out.println("Player: " + player);
        if (!asked && player != null) {
            asked = true; //Mark asked as true
            System.out.println("Sending PacketAsk");
            PenguinNetwork.sendToClient(player, new PacketAsk(player, npc, this));
            npc.addTalking(player); //We're talk to this player
        }

        //If the player no longer exists, then finish this action
        return player == null || npc.IsNotTalkingTo(player) || answered ? InteractionResult.SUCCESS : InteractionResult.PASS; //Always pass
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Translate", translate);
        tag.putString("RegistryName", registryName.toString());
        tag.putBoolean("IsQuest", isQuest);
        tag.putString("Question", question);
        tag.putByte("AnswersLength", (byte) answers.length);
        for (int i = 0; i < answers.length; i++) {
            tag.putString("Answer" + i, answers[i]);
        }

        tag.putByte("FormattingLength", (byte) formatting.length);
        for (int i = 0; i < formatting.length; i++) {
            tag.putString("Formatting" + i, formatting[i]);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        translate = nbt.getBoolean("Translate");
        registryName = new ResourceLocation(nbt.getString("RegistryName"));
        isQuest = nbt.getBoolean("IsQuest");
        question = nbt.getString("Question");
        int length = nbt.getByte("AnswersLength");
        answers = new String[length];
        for (int i = 0; i < length; i++) {
            answers[i] = nbt.getString("Answer" + i);
        }

        formatting = new String[nbt.getByte("FormattingLength")];
        for (int i = 0; i < formatting.length; i++) {
            formatting[i] = nbt.getString("Formatting" + i);
        }
    }
}
