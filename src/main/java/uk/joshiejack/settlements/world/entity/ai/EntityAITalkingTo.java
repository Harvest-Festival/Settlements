package uk.joshiejack.settlements.world.entity.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import uk.joshiejack.settlements.world.entity.NPCMob;

import java.util.EnumSet;

public class EntityAITalkingTo extends Goal {
    private final NPCMob npc;

    public EntityAITalkingTo(NPCMob npc) {
        this.npc = npc;
        this.setFlags(EnumSet.of(Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        for (Player player: npc.getTalkingTo()) {
            if (npc.distanceTo(player) < 3D) return true;
        }

        return false;
    }

    @Override
    public void start() {
        //TODO? Prevent moving?
        //npc.getNavigator().clearPath();
    }
}
