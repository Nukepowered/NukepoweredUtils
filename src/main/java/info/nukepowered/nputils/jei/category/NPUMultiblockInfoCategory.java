package info.nukepowered.nputils.jei.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gregtech.integration.jei.multiblock.MultiblockInfoRecipeWrapper;

import info.nukepowered.nputils.NPUtils;
import info.nukepowered.nputils.jei.info.AsseblyLineInfo;
import info.nukepowered.nputils.jei.info.FusionReactor1Info;
import info.nukepowered.nputils.jei.info.FusionReactor2Info;
import info.nukepowered.nputils.jei.info.FusionReactor3Info;
import info.nukepowered.nputils.jei.info.InductionBlastFurnanceInfo;
import info.nukepowered.nputils.jei.info.ProcessingArrayInfo;
import net.minecraft.client.resources.I18n;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.gui.recipes.RecipeLayout;

public class NPUMultiblockInfoCategory implements IRecipeCategory<MultiblockInfoRecipeWrapper> {
	
	public static final List<MultiblockInfoRecipeWrapper> multiblocksInfo;
	
	private final IDrawable background;
	private final IGuiHelper guiHelper;
	
	public NPUMultiblockInfoCategory(IJeiHelpers helpers) {
		this.background = helpers.getGuiHelper().createBlankDrawable(176, 166);
		this.guiHelper = helpers.getGuiHelper();
	}
	
	static {
		List<MultiblockInfoRecipeWrapper> multiWrappers = new ArrayList<>();
		
		multiWrappers.add(new MultiblockInfoRecipeWrapper(new AsseblyLineInfo()));
		multiWrappers.add(new MultiblockInfoRecipeWrapper(new FusionReactor1Info()));
		multiWrappers.add(new MultiblockInfoRecipeWrapper(new FusionReactor2Info()));
		multiWrappers.add(new MultiblockInfoRecipeWrapper(new FusionReactor3Info()));
		multiWrappers.add(new MultiblockInfoRecipeWrapper(new ProcessingArrayInfo()));
		multiWrappers.add(new MultiblockInfoRecipeWrapper(new InductionBlastFurnanceInfo()));
		
		multiblocksInfo = Collections.unmodifiableList(multiWrappers);
	}
	
	public static void registerRecipes(IModRegistry registry) {
		registry.addRecipes(multiblocksInfo, "nputils:multiblock_info");
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
		recipeWrapper.setRecipeLayout((RecipeLayout) recipeLayout, this.guiHelper);
	}
}
