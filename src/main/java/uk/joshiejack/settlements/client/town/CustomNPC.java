package uk.joshiejack.settlements.client.town;

import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.joshiejack.settlements.client.gui.NPCDisplayData;
import uk.joshiejack.settlements.data.database.NPCLoader;
import uk.joshiejack.settlements.npcs.NPC;
import uk.joshiejack.settlements.world.entity.npc.NPC;
import uk.joshiejack.settlements.world.entity.npc.NPCClass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public class CustomNPC implements NPCDisplayData {
    private final NPC baseNPC;
    private final ResourceLocation uniqueName;
    private final NPCClass npcClass;
    private final String name;
    private final ItemStack icon;

    @SuppressWarnings("ConstantConditions")
    public CustomNPC(@Nonnull NPC baseNPC, @Nullable ResourceLocation uniqueName, @Nullable NPCClass npcClass, @Nullable String name, @Nullable String playerSkin, @Nullable String resourceSkin) {
        this.baseNPC = baseNPC;
        this.uniqueName = uniqueName;
        this.npcClass = npcClass;
        this.name = name;
        this.icon = baseNPC.getIcon();
        if (playerSkin != null) {
            icon.getTag().putString("PlayerSkin", playerSkin);
        } else if (resourceSkin != null) {
            icon.getTag().putString("ResourceSkin", resourceSkin);
        }
    }

    @Override
    public NPCClass getNPCClass() {
        return npcClass == null ? baseNPC.getNPCClass() : npcClass;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return uniqueName == null ? baseNPC.id() : uniqueName;
    }

    @Override
    public Component getLocalizedName() {
        return name == null ? baseNPC.name() : name;
    }

    @Override
    public ItemStack getIcon() {
        return icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomNPC customNPC = (CustomNPC) o;
        return Objects.equals(getRegistryName(), customNPC.getRegistryName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRegistryName());
    }
}
