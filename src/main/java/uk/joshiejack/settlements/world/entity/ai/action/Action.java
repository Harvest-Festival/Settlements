package uk.joshiejack.settlements.world.entity.ai.action;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.entity.NPCMob;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public abstract class Action implements INBTSerializable<CompoundTag> {
    public static final BiMap<String, Class<? extends Action>> ACTIONS = HashBiMap.create();
    protected ServerPlayer player;
    public ResourceLocation registryName;
    public boolean isQuest;
    private boolean memorable;

    public static void register(String type, Class<? extends Action> clazz) {
        ACTIONS.put(type, clazz);
    }

    public static Action logError(String type) {
        Settlements.LOGGER.error("Attempted to create an action of type '" + type + "' that does not exist. Typo?");
        return ErrorAction.INSTANCE;
    }

    public static Action createOfType(String type, CompoundTag tag) {
        if (!ACTIONS.containsKey(type))
            return logError(type);

        try {
            Action action = ACTIONS.get(type).getDeclaredConstructor().newInstance();
            action.deserializeNBT(tag);
            return action;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            return ErrorAction.INSTANCE;
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract boolean isPhysical();

    public boolean has(String... data) {
        return false;
    }

    public Action withPlayer(ServerPlayer player) {
        this.player = player;
        return this;
    }

    public Action withScript(ResourceLocation registryName, boolean isQuest) {
        this.registryName = registryName;
        this.isQuest = isQuest;
        return this;
    }

    public String getType() {
        return ACTIONS.inverse().get(this.getClass());
    }

    public void setMemorable() {
        this.memorable = true;
    }

    public boolean isMemorable() {
        return memorable;
    }

    /**
     * @return EnumActionResult.SUCCESS to go to the next queued item, "succeeded in finishing"
     * EnumActionResult.FAIL to cancel the rest of the queue,  "there was an error"
     * EnumActionResult.PASS to continue executing,            "pass to the next step of this action"
     */
    public abstract InteractionResult execute(NPCMob npc);

    @Override
    public @UnknownNullability CompoundTag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return Objects.equals(getType(), action.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType());
    }
}
