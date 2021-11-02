package info.nukepowered.nputils.jei;


import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import info.nukepowered.nputils.jei.category.IIBFCoolantCategory;
import info.nukepowered.nputils.jei.category.NPUMultiblockInfoCategory;
import info.nukepowered.nputils.machines.NPUTileEntities;
import info.nukepowered.nputils.recipes.NPURecipeMaps;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

@JEIPlugin
public class NPUtilsJEIPlugin implements IModPlugin {
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(new NPUMultiblockInfoCategory(registry.getJeiHelpers()));
		registry.addRecipeCategories(new IIBFCoolantCategory(registry.getJeiHelpers()));
	}
	
	@Override
	public void register(IModRegistry registry) {
		NPUMultiblockInfoCategory.registerRecipes(registry);
		NPUMultiblockInfoCategory.multiblocksInfo.stream()
			.map(wrapper -> wrapper.getInfoPage())
			.map(info -> Pair.of(info.getDescription(), info))
			.filter(pair -> pair.getKey() != null && pair.getKey().length > 0)
			.map(pair -> pair.getValue())
			.forEach(info -> {
				registry.addIngredientInfo(info.getController().getStackForm(),
						VanillaTypes.ITEM,
						info.getDescription());
			});
		
		List<IIBFCoolantsRecipeWrapper> coolantRecipes = NPURecipeMaps.COOLANTS.entrySet().stream()
			.map(IIBFCoolantsRecipeWrapper::new)
			.collect(Collectors.toList());
		registry.addRecipes(coolantRecipes, IIBFCoolantCategory.UID);
		registry.addRecipeCatalyst(NPUTileEntities.INDUCTION_BLAST_FURNANCE.getStackForm(), IIBFCoolantCategory.UID);
	}
}
