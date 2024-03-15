package uk.joshiejack.settlements.world.level.town;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import uk.joshiejack.settlements.AdventureData;
import uk.joshiejack.settlements.AdventureDataLoader;
import uk.joshiejack.settlements.client.WorldMap;
import uk.joshiejack.settlements.client.town.TownClient;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class TownFinder {
    private static final Int2ObjectMap<TownFinder> FINDERS_CLIENT = new Int2ObjectOpenHashMap<>();
    private static final Int2ObjectMap<TownFinder> FINDERS_SERVER = new Int2ObjectOpenHashMap<>();
    private final Cache<BlockPos, Town<?>> closest = CacheBuilder.newBuilder().build();

    public static TownFinder getFinder(Level world) {
        TownFinder finder = findersMap(world).get(world.provider.getDimension());
        if (finder == null) {
            finder = new TownFinder();
            findersMap(world).put(world.provider.getDimension(), finder);
        }

        return finder;
    }

    private static Int2ObjectMap<TownFinder>findersMap(Level world) {
        return world.isClientSide ? FINDERS_CLIENT: FINDERS_SERVER;
    }

    private static BlockPos getOverworldPos(Level world, BlockPos pos) {
        return pos;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Town<?>> T find(Level world, BlockPos pos) {
        return (T) getFinder(world).find(world.isClientSide ? WorldMap.getTowns(world) : AdventureDataLoader.get(world).getTowns(world), pos, world.isRemote ? TownClient.NULL : TownServer.NULL);
    }

    public static Collection<? extends Town<?>> all(Level world) {
        return world.isClientSide ? WorldMap.getTowns(world) : AdventureDataLoader.get(world).getTowns(world);
    }

    public static Town<?>[] towns(Level world) {
        return AdventureDataLoader.get(world).getTowns(world).toArray(new Town[0]);
    }

    public TownServer findOrCreate(Player player, BlockPos pos) {
        AdventureData data = AdventureDataLoader.get(player.level());
        Collection<TownServer> towns = data.getTowns(player.level());
        Town<?> town = find(towns, pos, TownServer.NULL);
        if (town == TownServer.NULL) {
            town = data.createTown(player.world, player);
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
