package uk.joshiejack.settlements.world.entity.npc.gifts;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class GiftCategory {
    public static final Object2IntMap<String> REGISTRY = new Object2IntOpenHashMap<>();

    public static void register(String name, int value) {
        REGISTRY.put(name, value);
    }

    public static int getValue(String category) {
        return REGISTRY.getInt(category);
    }

    public static class DefaultValues {
        public static final int AWESOME = 1000;
        public static final int GOOD = 500;
        public static final int DECENT = 200;
        public static final int DISLIKE = -200;
        public static final int BAD = -400;
        public static final int TERRIBLE = -800;
    }
}
