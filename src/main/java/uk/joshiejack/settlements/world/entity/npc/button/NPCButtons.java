package uk.joshiejack.settlements.world.entity.npc.button;

import com.google.common.collect.Lists;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import uk.joshiejack.penguinlib.scripting.Interpreter;
import uk.joshiejack.penguinlib.scripting.ScriptFactory;
import uk.joshiejack.penguinlib.scripting.wrapper.ItemStackJS;
import uk.joshiejack.settlements.world.entity.EntityNPC;

import java.util.List;

public class NPCButtons {
    private static final List<ButtonData> BUTTONS = Lists.newArrayList();

    public static void register(ResourceLocation scriptID) {
        BUTTONS.add(new ButtonData(scriptID));
    }

    //Called on the server to let the client know
    public static List<ButtonData> getForDisplay(EntityNPC npc, Player player) {
        List<ButtonData> list = Lists.newArrayList();
        for (ButtonData button: BUTTONS) {
            Interpreter <?> interpreter = ScriptFactory.getScript(button.getScript());
            if (interpreter != null && interpreter.isTrue("canDisplay", npc, player)) {
                ButtonData add = new ButtonData(button, interpreter);
                interpreter.callFunction("setupButton", npc, player, add);
                list.add(add);
            }
        }

        return list;
    }

    public static class ButtonData {
        private final ResourceLocation scriptID;
        private Component name;
        private ItemStack icon;
        private Interpreter<?> interpreter;

        /* For data transfer */
        public ButtonData (ButtonData data, Interpreter<?> interpreter) {
            this.name = Component.literal("Unitialized Button");
            this.icon = ItemStack.EMPTY;
            this.scriptID = data.scriptID;
            this.interpreter = interpreter;
        }

        /* For registry purposes only */
        public ButtonData (ResourceLocation scriptID) {
            this.scriptID = scriptID;
        }

        public ButtonData(FriendlyByteBuf buf) {
            this.name = buf.readComponent();
            this.icon = buf.readItem();
            this.scriptID = buf.readResourceLocation();
        }

        public void toBytes(FriendlyByteBuf buf) {
            buf.writeComponent(name);
            buf.writeItem(icon);
            buf.writeResourceLocation(scriptID);
        }

        public ResourceLocation getScript() {
            return scriptID;
        }

        public void setTranslatableName(String name) {
            this.name = Component.translatable(name);
        }

        public void setLiteralName(String name) {
            this.name = Component.literal(name);
        }

        public void setIcon(ItemStackJS stack) {
            this.icon = stack.get().copy();
            this.icon.setCount(1);
        }

        public ItemStack getIcon() {
            return icon;
        }

        public Component getName() {
            return name;
        }
    }
}
