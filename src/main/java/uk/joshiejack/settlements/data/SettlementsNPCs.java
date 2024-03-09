package uk.joshiejack.settlements.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.penguinlib.data.generator.AbstractPenguinRegistryProvider;
import uk.joshiejack.penguinlib.util.registry.ReloadableRegistry;
import uk.joshiejack.settlements.world.quest.Quest;
import uk.joshiejack.settlements.world.quest.settings.Settings;

import java.util.Map;

public class SettlementsNPCs extends AbstractPenguinRegistryProvider<Quest> {
    public SettlementsNPCs(PackOutput output, ReloadableRegistry<Quest> registry) {
        super(output, registry);
    }

    @Override
    protected void buildRegistry(Map<ResourceLocation, Quest> map) {
        map.put(new ResourceLocation("settlements", "test"), new Quest(Settings.DEFAULT));
        map.put(new ResourceLocation("settlements", "test/test2"), new Quest(Settings.DEFAULT));
        map.put(new ResourceLocation("settlements", "test/test3"), new Quest(Settings.DEFAULT));
    }
}
