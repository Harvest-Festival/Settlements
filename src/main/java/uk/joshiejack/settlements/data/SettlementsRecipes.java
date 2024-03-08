package uk.joshiejack.settlements.data;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.settlements.Settlements;

public class SettlementsRecipes extends RecipeProvider {
    public SettlementsRecipes(PackOutput packOutput) {
        super(packOutput);
    }

    private ResourceLocation rl (String name) {
        return new ResourceLocation(Settlements.MODID, name);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput consumer) {

    }
}
