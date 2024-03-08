package uk.joshiejack.settlements.world.entity.npc;

import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import uk.joshiejack.penguinlib.event.DatabaseLoadedEvent;

import java.util.Map;

import static uk.joshiejack.settlements.Settlements.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class HomeOverrides {
    private static final Map<ResourceLocation, String> waypoints = Maps.newHashMap();

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onDatabaseLoaded(DatabaseLoadedEvent event) {//LOW PRIORITY, TODO: Move to "AdventureNPCs"
        event.table("npc_home_overrides").rows()
                .forEach(override -> waypoints.put(new ResourceLocation(override.get("npc_id")), override.get("home")));
    }

    public static String get(ResourceLocation npc) {
        return waypoints.containsKey(npc) ? waypoints.get(npc) : npc.toString();
    }
}
