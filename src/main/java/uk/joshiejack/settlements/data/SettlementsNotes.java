package uk.joshiejack.settlements.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.penguinlib.data.generator.AbstractNoteProvider;
import uk.joshiejack.penguinlib.world.note.Category;
import uk.joshiejack.penguinlib.world.note.Note;

import java.util.Map;

public class SettlementsNotes extends AbstractNoteProvider {
    public SettlementsNotes(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildNotes(Map<ResourceLocation, Category> categories, Map<ResourceLocation, Note> notes) {

    }
}
