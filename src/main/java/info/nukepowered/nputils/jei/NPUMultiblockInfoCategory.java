package info.nukepowered.nputils.jei;

import com.google.common.collect.Lists;

import gregtech.integration.jei.multiblock.MultiblockInfoRecipeWrapper;
import info.nukepowered.nputils.NPUtils;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.gui.recipes.RecipeLayout;
import net.minecraft.client.resources.I18n;

public class NPUMultiblockInfoCategory implements IRecipeCategory<MultiblockInfoRecipeWrapper> {
	private final IDrawable background;
	
	public NPUMultiblockInfoCategory(IJeiHelpers helpers) {
		this.background = helpers.getGuiHelper().createBlankDrawable(176, 166);
	}
	
	public static void registerRecipes(IModRegistry registry) {
		registry.addRecipes(Lists.newArrayList(
				new MultiblockInfoRecipeWrapper(new AsseblyLineInfo()),
				new MultiblockInfoRecipeWrapper(new FusionReactor1Info()),
				new MultiblockInfoRecipeWrapper(new FusionReactor2Info()),
				new MultiblockInfoRecipeWrapper(new FusionReactor3Info())
		), "nputils:multiblock_info");
	}
	
	public String getUid() {
		return "nputils:multiblock_info";
	}
	
	public String getTitle() {
		return I18n.format("gregtech.multiblock.title", new Object[0]);
	}
	
	public String getModName() {
		return NPUtils.MODID;
	}
	
	public IDrawable getBackground() {
		return this.background;
	}
	
	public void setRecipe(IRecipeLayout recipeLayout, MultiblockInfoRecipeWrapper recipeWrapper, IIngredients ingredients) {
		recipeWrapper.setRecipeLayout((RecipeLayout) recipeLayout);
	}
}
