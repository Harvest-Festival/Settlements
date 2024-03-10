package uk.joshiejack.settlements.world.entity.ai.action.quest;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.world.entity.ai.action.registry.AbstractActionRegistry;
import uk.joshiejack.settlements.world.level.QuestSavedData;
import uk.joshiejack.settlements.world.quest.Quest;

//@PenguinLoader("complete_quest")
public class CompleteQuestAction extends AbstractActionRegistry<Quest> {
    public CompleteQuestAction() {
        super(Settlements.Registries.QUESTS);
        this.resource = registryName;
    }

    public CompleteQuestAction(String resource) {
        super(Settlements.Registries.QUESTS);
    }

//    public Path getParent(Path path, int count) {
//        for (int i = 0; i <= count; i++) {
//            path = path.getParent();
//        }
//
//        return path;
//    }

//    @Override
//    public CompleteQuestAction withData(Object... params) {
//        if (params.length == 0) {
//            this.resource = registryName;
//            return this;
//        } else {
//            String param = (String) params[0];
//            if (param.contains(":")) { //Absolute Path
//                return (CompleteQuestAction) super.withData(params);
//            } else { //Relative path to THIS quest
//                //TODO: Possibly re-enable relative quests?
////                /* ../ means go up a route level for this quest */
////                // Location can be STRING or File
////                String filepath = registry.get(registryName).getInterpreter().getLocation().toString();
////                // This will be "harvestfestival:npcs/harvest_goddess/0/axe" //TODO: FILE VERSION
////                // Converted to
////                String name = getParent(Paths.get(filepath.split(":")[1]), StringUtils.countMatches(param, "../")).toString();
////                this.resource = new ResourceLocation(name.replace("/", "_") + "_" + param);
//            }
//        }
//
//        return this;
//    }

    @Override
    public void performAction(Level world, Quest object) {
        if (isQuest) {
            QuestSavedData.get((ServerLevel) world).markCompleted(player, object);
        }
    }
}
