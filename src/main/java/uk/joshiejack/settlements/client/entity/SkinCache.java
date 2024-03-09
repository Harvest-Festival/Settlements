package uk.joshiejack.settlements.client.entity;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.Services;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.UsernameCache;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

public class SkinCache {
    @Nullable
    private static Executor mainThreadExecutor;
    @Nullable
    private static LoadingCache<String, CompletableFuture<Optional<GameProfile>>> profileCache;
    private static final Executor CHECKED_MAIN_THREAD_EXECUTOR = p_294078_ -> {
        Executor executor = mainThreadExecutor;
        if (executor != null) {
            executor.execute(p_294078_);
        }
    };
    @Nullable
    private GameProfile owner;

    public static void setup(final Services services, Executor executor) {
        mainThreadExecutor = executor;
        final BooleanSupplier booleansupplier = () -> profileCache == null;
        profileCache = CacheBuilder.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(10L))
                .maximumSize(256L)
                .build(
                        new CacheLoader<>() {
                            public CompletableFuture<Optional<GameProfile>> load(String p_304652_) {
                                return booleansupplier.getAsBoolean()
                                        ? CompletableFuture.completedFuture(Optional.empty())
                                        : SkinCache.loadProfile(p_304652_, services, booleansupplier);
                            }
                        }
                );
    }

    public static void clear() {
        mainThreadExecutor = null;
        profileCache = null;
    }

    static CompletableFuture<Optional<GameProfile>> loadProfile(String p_304754_, Services p_304699_, BooleanSupplier p_304484_) {
        return p_304699_.profileCache().getAsync(p_304754_).thenApplyAsync(p_304381_ -> {
            if (p_304381_.isPresent() && !p_304484_.getAsBoolean()) {
                UUID uuid = p_304381_.get().getId();
                ProfileResult profileresult = p_304699_.sessionService().fetchProfile(uuid, true);
                return profileresult != null ? Optional.ofNullable(profileresult.profile()) : p_304381_;
            } else {
                return Optional.empty();
            }
        }, Util.backgroundExecutor());
    }

    @Nullable
    public GameProfile getOwnerProfile() {
        return this.owner;
    }

    public void setOwner(@Nullable GameProfile p_59770_) {
        synchronized(this) {
            this.owner = p_59770_;
        }

        this.updateOwnerProfile();
    }

    private void updateOwnerProfile() {
        if (this.owner != null && !Util.isBlank(this.owner.getName()) && !hasTextures(this.owner)) {
            fetchGameProfile(this.owner.getName()).thenAcceptAsync(p_294081_ -> {
                this.owner = p_294081_.orElse(this.owner);
            }, CHECKED_MAIN_THREAD_EXECUTOR);
        }
    }

    public static GameProfile getOrResolveGameProfile(UUID uuid) {
        return new GameProfile(uuid, Objects.requireNonNull(UsernameCache.getLastKnownUsername(uuid)));
    }

    public static void resolveGameProfile(CompoundTag p_294797_) {
        String s = p_294797_.getString("SkullOwner");
        if (!Util.isBlank(s)) {
            resolveGameProfile(p_294797_, s);
        }
    }

    private static void resolveGameProfile(CompoundTag p_294887_, String p_296231_) {
        fetchGameProfile(p_296231_)
                .thenAccept(
                        p_294077_ -> p_294887_.put(
                                "SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), p_294077_.orElse(new GameProfile(Util.NIL_UUID, p_296231_)))
                        )
                );
    }

    private static CompletableFuture<Optional<GameProfile>> fetchGameProfile(String p_295932_) {
        LoadingCache<String, CompletableFuture<Optional<GameProfile>>> loadingcache = profileCache;
        return loadingcache != null && Player.isValidUsername(p_295932_)
                ? loadingcache.getUnchecked(p_295932_)
                : CompletableFuture.completedFuture(Optional.empty());
    }

    private static boolean hasTextures(GameProfile p_295602_) {
        return p_295602_.getProperties().containsKey("textures");
    }
}
