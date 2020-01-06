package info.nukepowered.nputils.recipes;

import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;

public class RecipeMapProcessingArray<R extends RecipeBuilder<R>> extends RecipeMap<R> {

	public RecipeMapProcessingArray(String unlocalizedName, int minInputs, int maxInputs, int minOutputs,
			int maxOutputs, int minFluidInputs, int maxFluidInputs, int minFluidOutputs, int maxFluidOutputs,
			R defaultRecipe) {
		super(unlocalizedName, minInputs, maxInputs, minOutputs, maxOutputs, minFluidInputs, maxFluidInputs, minFluidOutputs,
				maxFluidOutputs, defaultRecipe);
		// TODO Auto-generated constructor stub
	}

}
