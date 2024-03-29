package uk.joshiejack.settlements.world.entity.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import uk.joshiejack.penguinlib.util.helper.TimeHelper;
import uk.joshiejack.settlements.world.entity.NPCMob;

import java.util.EnumSet;

public class EntityAISchedule extends Goal {
    private int cooldown = 0;
    private final NPCMob npc;

    public EntityAISchedule(NPCMob npc) {
        this.npc = npc;
        this.setFlags(EnumSet.of(Flag.TARGET));
        //this.setMutexBits(1);
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
        }

        return cooldown == 0 && TimeHelper.getTimeOfDay(npc.level().getDayTime() % 250) == 0;
    }

    @Override
    public void tick() {
        cooldown = 25;
        npc.getNPC().callScript("onNPCScheduleUpdate", npc, TimeHelper.getTimeOfDay(npc.level().getDayTime()));
    }

    @Override
    public void start() {
        //TODO: Prevent moving?npc.getNavigator().clearPath();
    }
}
