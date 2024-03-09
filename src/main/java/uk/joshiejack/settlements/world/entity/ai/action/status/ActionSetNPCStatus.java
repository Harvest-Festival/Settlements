package uk.joshiejack.settlements.world.entity.ai.action.status;

import joptsimple.internal.Strings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import uk.joshiejack.penguinlib.scripting.wrapper.WrapperRegistry;
import uk.joshiejack.settlements.scripting.wrapper.NPCStatusJS;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.ActionMental;

//@PenguinLoader("set_npc_status")
public class ActionSetNPCStatus extends ActionMental {
    private String npcRegistryName = Strings.EMPTY;
    private String status;
    private int value;

    @Override
    public ActionSetNPCStatus withData(Object... params) {
        String var1 = (String) params[0];
        int i = 0;
        // If the first var is an npc we will use that npc
        // Otherwise we just take the current NPC instead
        if (var1.contains(":")) {
            this.npcRegistryName = var1;
            i++;
        }
        
        this.status = (String) params[i];
        this.value = Integer.parseInt(String.valueOf(params[i + 1]));
        return this;
    }

    @Override
    public InteractionResult execute(NPCMob npc) {
        if (player != null) {
            NPCStatusJS wrapper = WrapperRegistry.wrap(npcRegistryName == null ? npc.getBaseNPC() : new ResourceLocation(npcRegistryName));
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
