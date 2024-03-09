package uk.joshiejack.settlements.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.client.entity.RenderNPC;
import uk.joshiejack.settlements.world.entity.SettlementsEntities;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Settlements.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SettlementsClient {
    @SubscribeEvent
    public static void onEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SettlementsEntities.NPC.get(), RenderNPC::new);
    }
}
