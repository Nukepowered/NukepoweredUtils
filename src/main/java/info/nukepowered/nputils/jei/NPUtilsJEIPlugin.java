package info.nukepowered.nputils.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

@JEIPlugin
public class NPUtilsJEIPlugin implements IModPlugin {
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(new NPUMultiblockInfoCategory(registry.getJeiHelpers()));
	}
	
	@Override
	public void register(IModRegistry registry) {
		NPUMultiblockInfoCategory.registerRecipes(registry);
	}
}
