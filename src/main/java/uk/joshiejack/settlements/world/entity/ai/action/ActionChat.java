package uk.joshiejack.settlements.world.entity.ai.action;

import net.minecraft.world.entity.player.Player;
import uk.joshiejack.settlements.world.entity.NPCMob;

public interface ActionChat {
    void onGuiClosed(Player player, NPCMob npc, Object... parameters);
}
