package uk.joshiejack.settlements.world.entity.ai.action.status;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import uk.joshiejack.penguinlib.scripting.wrapper.ServerLevelJS;
import uk.joshiejack.penguinlib.scripting.wrapper.TeamJS;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.MentalAction;

public class SetTeamStatusAction extends MentalAction {
    private String status;
    private int value;

    public SetTeamStatusAction() {}
    public SetTeamStatusAction(String status, int value) {
        this.status = status;
        this.value = value;
    }

    @Override
    public InteractionResult execute(NPCMob npc) {
        if (player != null) {
            new TeamJS(PenguinTeams.getTeamForPlayer(player)).status().set(new ServerLevelJS((ServerLevel) npc.level()), status, value);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Status", status);
        tag.putInt("Value", value);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        status = tag.getString("Status");
        value = tag.getInt("Value");
    }
}
