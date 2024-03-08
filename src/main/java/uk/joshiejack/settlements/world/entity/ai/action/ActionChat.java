package uk.joshiejack.settlements.world.entity.ai.action;

import net.minecraft.world.entity.player.Player;
import uk.joshiejack.settlements.world.entity.EntityNPC;

public interface ActionChat {
    void onGuiClosed(Player player, EntityNPC npc, Object... parameters);
}
