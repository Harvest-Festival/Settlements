package uk.joshiejack.settlements.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import uk.joshiejack.settlements.client.town.TownClient;
import uk.joshiejack.settlements.world.level.town.Town;

import java.util.Collection;

public class WorldMap {
    private static final Object2ObjectMap<ResourceKey<Level>, Int2ObjectMap<TownClient>> towns = new Object2ObjectOpenHashMap<>();

    public static void setTowns(ResourceKey<Level> dim, Collection<Town<?>> towns) {
        towns.forEach(t -> WorldMap.getTownMap(dim).put(t.getID(), (TownClient) t));
    }

    private static Int2ObjectMap<TownClient> getTownMap(ResourceKey<Level> dim) {
        Int2ObjectMap<TownClient> map = towns.get(dim);
        if (map == null) {
            map = new Int2ObjectOpenHashMap<>();
            towns.put(dim, map);
        }

        return map;
    }

    public static Collection<TownClient> getTowns(Level world) {
        return getTownMap(world.dimension()).values();
    }

    public static TownClient getTownByID(ResourceKey<Level> dimension, int id) {
        return getTownMap(dimension).get(id);
    }

    public static void addTown(ResourceKey<Level> dimension, TownClient town) {
        getTownMap(dimension).put(town.getID(), town);
    }
}
