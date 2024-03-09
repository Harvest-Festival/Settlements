package uk.joshiejack.settlements.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.quest.Quest;

public class NPCEvent extends PlayerEvent implements ICancellableEvent {
    private final NPCMob npcEntity;

    public NPCEvent(NPCMob npcEntity, Player player) {
        super(player);
        this.npcEntity = npcEntity;
    }

    public NPCMob getNPCEntity() {
        return npcEntity;
    }

    public static class NPCRightClickedEvent extends NPCEvent {
        private final InteractionHand hand;

        public NPCRightClickedEvent(NPCMob npcEntity, Player player, InteractionHand hand) {
            super(npcEntity, player);
            this.hand = hand;
        }

        public InteractionHand getHand() {
            return hand;
        }
    }

    public static class NPCFinishedSpeakingEvent extends NPCEvent {
        private final Quest script;

        public NPCFinishedSpeakingEvent(NPCMob npcEntity, Player player, Quest script) {
            super(npcEntity, player);
            this.script = script;
        }

        public Quest getScript() {
            return script;
        }
    }
}
