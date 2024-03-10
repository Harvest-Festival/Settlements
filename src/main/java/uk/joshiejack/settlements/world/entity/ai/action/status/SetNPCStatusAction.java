package uk.joshiejack.settlements.world.entity.ai.action.status;

import joptsimple.internal.Strings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import uk.joshiejack.penguinlib.scripting.wrapper.WrapperRegistry;
import uk.joshiejack.settlements.scripting.wrapper.NPCStatusJS;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.ActionMental;

public class SetNPCStatusAction extends ActionMental {
    private String npcRegistryName = Strings.EMPTY;
    private String status;
    private int value;

    public SetNPCStatusAction() {}
    public SetNPCStatusAction(String npcRegistryName, String status, int value) {
        this.npcRegistryName = npcRegistryName.contains(":") ? npcRegistryName : Strings.EMPTY;
        this.status = status;
        this.value = value;
    }

    @Override
    public InteractionResult execute(NPCMob npc) {
        if (player != null) {
            NPCStatusJS wrapper = WrapperRegistry.wrap(npcRegistryName == null ? npc.getNPC() : new ResourceLocation(npcRegistryName));
            wrapper.set(WrapperRegistry.wrap(player), status, value);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("RegistryName", npcRegistryName);
        tag.putString("Status", status);
        tag.putInt("Value", value);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        npcRegistryName = tag.getString("RegistryName");
        status = tag.getString("Status");
        value = tag.getInt("Value");
    }
}
