package uk.joshiejack.settlements.world.entity.ai.action.status;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import uk.joshiejack.penguinlib.scripting.wrapper.ServerLevelJS;
import uk.joshiejack.penguinlib.scripting.wrapper.TeamJS;
import uk.joshiejack.penguinlib.world.team.PenguinTeams;
import uk.joshiejack.settlements.world.entity.EntityNPC;
import uk.joshiejack.settlements.world.entity.ai.action.ActionMental;

//TODO: @PenguinLoader("set_team_status")
public class ActionSetTeamStatus extends ActionMental {
    private String status;
    private int value;

    @Override
    public ActionSetTeamStatus withData(Object... params) {
        this.status = (String) params[0];
        this.value = (Integer) params[1];
        return this;
    }

    @Override
    public InteractionResult execute(EntityNPC npc) {
        if (player != null) {
            new TeamJS(PenguinTeams.getTeamForPlayer(player)).status().set(new ServerLevelJS((ServerLevel) npc.level()), status, value);
        }

        return InteractionResult.SUCCESS;
    }
}
