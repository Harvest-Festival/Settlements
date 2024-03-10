package uk.joshiejack.settlements.scripting.wrapper;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import uk.joshiejack.penguinlib.scripting.ScriptFactory;
import uk.joshiejack.penguinlib.scripting.wrapper.ItemStackJS;
import uk.joshiejack.penguinlib.scripting.wrapper.LivingEntityJS;
import uk.joshiejack.penguinlib.scripting.wrapper.PlayerJS;
import uk.joshiejack.penguinlib.scripting.wrapper.WrapperRegistry;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.entity.NPCMob;

import java.util.Locale;

public class EntityNPCJS extends LivingEntityJS<NPCMob> {
    private final NPCTaskJS tasks;

    public EntityNPCJS(NPCMob npcEntity) {
        super(npcEntity);
        this.tasks = new NPCTaskJS(Lists.newLinkedList(), Lists.newLinkedList(), this);
    }

//    public String substring(String name) {
//        return penguinScriptingObject.substring(name);
//    }

    public void setHeldItem(InteractionHand hand, ItemStackJS stack) {
        penguinScriptingObject.setItemInHand(hand, stack.get());
    }

    public void chat(ResourceLocation quest, PlayerJS playerW) {
        NPCMob object = penguinScriptingObject;
        ScriptFactory.IGNORE.add(quest); //Skip to another quest
        object.getMentalAI().tick(); //Clear out any leftover actions
        Player player = playerW.get();
        object.interact(player, player.getUsedItemHand());
    }

    public boolean hasAction(String actionType) {
        return penguinScriptingObject.getPhysicalAI().all().stream().anyMatch(t -> t.getType().equals(actionType));
    }

    public boolean hasAction(String actionType, String... data) {
        return penguinScriptingObject.getPhysicalAI().all().stream().filter(t -> t.getType().equals(actionType)).anyMatch(a -> a.has(data));
    }

//    public AbstractTownJS<?> town() { //TODO:
//        return WrapperRegistry.wrap(AdventureDataLoader.get(penguinScriptingObject.world).getTownByID(penguinScriptingObject.world.provider.getDimension(), penguinScriptingObject.getTown()));
//    }

    @Override
    public boolean is(String string) {
        //boolean bool = string.contains(":") && string.toLowerCase(Locale.ENGLISH).equals(penguinScriptingObject.getCachedUniqueIdString().toLowerCase(Locale.ENGLISH));
        if (penguinScriptingObject.getNPC().id().equals(new ResourceLocation(Settlements.MODID, "custom"))) {
            return false;
        } else return string.contains(":") && string.toLowerCase(Locale.ENGLISH).equals(penguinScriptingObject.getNPC().id().toString().toLowerCase(Locale.ENGLISH));
    }

    public NPCStatusJS status() {
        return WrapperRegistry.wrap(penguinScriptingObject.getNPC().id());
    }

    public NPCTaskJS tasks(ResourceLocation quest, PlayerJS player) {
        return tasks.start(quest, player);
    }

    public int data(String name) {
        return penguinScriptingObject.getNPC().getData(name);
    }
}
