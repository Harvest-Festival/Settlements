package uk.joshiejack.settlements.world.entity.animation;

import uk.joshiejack.settlements.world.entity.NPCMob;

//@PenguinLoader("wakeup")
public class AnimationWake extends Animation {
    @Override
    public void play(NPCMob npc) {
        npc.renderOffsetX = 0;
        npc.renderOffsetZ = 0;
    }
}
