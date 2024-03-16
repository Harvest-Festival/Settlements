package uk.joshiejack.settlements.world.level.town;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import uk.joshiejack.settlements.client.WorldMap;
import uk.joshiejack.settlements.client.town.TownClient;
import uk.joshiejack.settlements.world.level.TownSavedData;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class TownFinder {
    private static final Object2ObjectMap<ResourceKey<Level>, TownFinder> FINDERS_CLIENT = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectMap<ResourceKey<Level>, TownFinder> FINDERS_SERVER = new Object2ObjectOpenHashMap<>();
    private final Cache<BlockPos, Town<?>> closest = CacheBuilder.newBuilder().build();

    public static TownFinder getFinder(Level world) {
        TownFinder finder = findersMap(world).get(world.dimension());
        if (finder == null) {
            finder = new TownFinder();
            findersMap(world).put(world.dimension(), finder);
        }

        return finder;
    }

    private static Object2ObjectMap<ResourceKey<Level>, TownFinder>findersMap(Level world) {
        return world.isClientSide ? FINDERS_CLIENT: FINDERS_SERVER;
    }

    private static BlockPos getOverworldPos(Level world, BlockPos pos) {
        return pos;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Town<?>> T find(Level world, BlockPos pos) {
        return (T) getFinder(world).find(world.isClientSide ? WorldMap.getTowns(world) : TownSavedData.get((ServerLevel) world).getTowns(world), pos, world.isClientSide ? TownClient.NULL : TownServer.NULL);
    }

    public static Collection<? extends Town<?>> all(Level world) {
        return world.isClientSide ? WorldMap.getTowns(world) : TownSavedData.get((ServerLevel) world).getTowns(world);
    }

    public static Town<?>[] towns(Level world) {
        return TownSavedData.get((ServerLevel) world).getTowns(world).toArray(new Town[0]);
    }

    public TownServer findOrCreate(Player player, BlockPos pos) {
        TownSavedData data = TownSavedData.get((ServerLevel) player.level());
        Collection<TownServer> towns = data.getTowns(player.level());
        Town<?> town = find(towns, pos, TownServer.NULL);
        if (town == TownServer.NULL) {
            town = data.createTown(player.level(), player);
        }

        return (TownServer) town;
    }

    public Town<?> find(Collection<? extends Town<?>> towns, BlockPos pos, Town<?> NULL) {
        try {
            return closest.get(pos, () -> {
                double distance = Double.MAX_VALUE;
                Town<?> ret = NULL;
                for (Town<?> town: towns) {
                    double townDistance = town.getLandRegistry().getDistance(pos);
                    if (townDistance < distance) {
                        distance = townDistance;
                        ret = town;
                    }
                }

                return ret;
            });
        } catch (ExecutionException e) {
            return NULL;
        }
    }

    public void clearCache() {
        closest.invalidateAll();
    }
}
