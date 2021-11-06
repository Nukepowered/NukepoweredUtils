package info.nukepowered.nputils.jei.category;

import gregtech.api.gui.GuiTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import info.nukepowered.nputils.NPUtils;
import info.nukepowered.nputils.jei.IIBFCoolantsRecipeWrapper;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;


/**
 * @author TheDarkDnKTv
 *
 * Copyright (C) 2021 TheDarkDnKTv. All rights reversed.
 */
public class IIBFCoolantCategory implements IRecipeCategory<IIBFCoolantsRecipeWrapper> {

	public final static String UID = NPUtils.MODID + ":induction_blast_furnance_coolants";
	
	private final IDrawable background;
	
	public IIBFCoolantCategory(IJeiHelpers helpers) {
		this.background = helpers.getGuiHelper().createBlankDrawable(176, 166);
	}
	
	@Override
	public String getUid() {
		return UID;
	}

	@Override
	public String getTitle() {
		return I18n.format("jei.induction_blast_furnance_coolant.category");
	}

	@Override
	public String getModName() {
		return NPUtils.MODID;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IIBFCoolantsRecipeWrapper recipeWrapper, IIngredients ingredients) {
		recipeLayout.getFluidStacks().init(0, true, 52, 7, 16, 16, recipeWrapper.getFluid().amount, false, null);
		recipeLayout.getFluidStacks().set(ingredients);
	}
	
	@Override
    public void drawExtras(Minecraft minecraft) {
        GuiTextures.PROGRESS_BAR_ARROW.drawSubArea(77, 5, 20, 20, 0.0, 0.0, 1.0, 0.5);
        GuiTextures.FLUID_SLOT.draw(51, 6, 18, 18);
    }
}
