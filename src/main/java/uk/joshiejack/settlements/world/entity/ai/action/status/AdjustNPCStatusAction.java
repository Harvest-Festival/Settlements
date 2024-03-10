package uk.joshiejack.settlements.world.entity.ai.action.status;

import joptsimple.internal.Strings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import uk.joshiejack.penguinlib.scripting.wrapper.PlayerJS;
import uk.joshiejack.penguinlib.scripting.wrapper.WrapperRegistry;
import uk.joshiejack.settlements.scripting.wrapper.NPCStatusJS;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.ActionMental;

//TODO: @PenguinLoader("adjust_ncp_status")
public class AdjustNPCStatusAction extends ActionMental {
    private String npcRegistryName = Strings.EMPTY;
    private String status;
    private Integer min;
    private Integer max;
    private int value;

    public AdjustNPCStatusAction() {}
    public AdjustNPCStatusAction(String npcRegistryName, String status, int value) {
        this(npcRegistryName, status, value, null, null);
    }

    public AdjustNPCStatusAction(String npcRegistryName, String status, int value, Integer min, Integer max) {
        this.npcRegistryName = npcRegistryName.contains(":") ? npcRegistryName : Strings.EMPTY;
        this.status = status;
        this.value = value;
        this.min = min;
        this.max = max;
    }

    @Override
    public InteractionResult execute(NPCMob npc) {
        if (player != null) {
            NPCStatusJS wrapper = WrapperRegistry.wrap(npcRegistryName.isEmpty() ? npc.getNPC() : new ResourceLocation(npcRegistryName));
            PlayerJS wrappedPlayer = WrapperRegistry.wrap(player);
            if (min != null && max != null) {
                wrapper.adjustWithRange(wrappedPlayer, status, value, min, max);
            } else wrapper.adjust(wrappedPlayer, status, value);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("RegistryName", npcRegistryName);
        tag.putString("Status", status);
        if (min != null && max != null) {
            tag.putInt("Min", min);
            tag.putInt("Max", max);
        }

        tag.putInt("Value", value);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        npcRegistryName = tag.getString("RegistryName");
        status = tag.getString("Status");
        value = tag.getInt("Value");
        if (tag.contains("Min") && tag.contains("Max")) {
            min = tag.getInt("Min");
            max = tag.getInt("Max");
        }
    }
}
