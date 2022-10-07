package info.nukepowered.nputils.recipes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import forestry.api.recipes.ICentrifugeRecipe;
import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.ModuleCore;
import forestry.core.items.EnumElectronTube;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import info.nukepowered.nputils.NPULog;
import info.nukepowered.nputils.api.NPULib;
import info.nukepowered.nputils.item.NPUMetaItems;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public class ForestryIntegration {

    public static final int CHANCE_PER_TIER = 250; // 2.5%


	public static void removeFabricatorRecipes() {
		// Recipe removal
		Iterator<IFabricatorRecipe> fabIter = RecipeManagers.fabricatorManager.recipes().iterator();
		List<IFabricatorRecipe> recipesToRemove = new ArrayList<>();
		while (fabIter.hasNext()) {
			IFabricatorRecipe recipe = fabIter.next();
			if (recipe.getRecipeOutput().toString().contains("thermionic_tubes"))
				recipesToRemove.add(recipe);
		}
				
		for (IFabricatorRecipe recipe : recipesToRemove) {
					RecipeManagers.fabricatorManager.removeRecipe(recipe);
		}
	}
	
	public static void parseCentrifugeRecipes() {
		long time = System.currentTimeMillis();
		for (ICentrifugeRecipe recipe : RecipeManagers.centrifugeManager.recipes()) {
            Map<ItemStack, Float> products = recipe.getAllProducts();
            float avgProducts = 1.0F;
            for (float f : products.values()) {
                avgProducts += f;
            }

            RecipeBuilder<?> builder = RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder()
                .EUt(14)
                .duration(Math.max(1, (int)(avgProducts * (recipe.getProcessingTime() / 4 * 20))))
                .inputs(recipe.getInput());

			if (products.isEmpty() || recipe.getInput() == null || recipe.getInput().isEmpty()) {
				continue;
			}

			if (products.size() > RecipeMaps.CENTRIFUGE_RECIPES.getMaxOutputs()) {
				NPULog.error("Failed to parse forestry centrifuge recipe for input: " + recipe.getInput() + " - output items more than " + RecipeMaps.CENTRIFUGE_RECIPES.getMaxOutputs());
				continue;
			}

            products.forEach((key, value) -> {
                if (value < 1.0f)
                    builder.chancedOutput(key, (int) (value * 10000), CHANCE_PER_TIER);
                else
                    builder.outputs(key);
            });

			builder.buildAndRegister();
		}
		
		NPULib.printEventFinish("Finished copying centrifuge recipes for %.3f seconds", time, System.currentTimeMillis());
	}

    public static void parseSqueezerRecipes() {
        long time = System.currentTimeMillis();
        for (ISqueezerRecipe recipe : RecipeManagers.squeezerManager.recipes()) {
            List<ItemStack> inputs = recipe.getResources();
            if (inputs.isEmpty()) {
                continue;
            }

            RecipeMap<?> map = inputs.size() > 1 ?
                RecipeMaps.MIXER_RECIPES : RecipeMaps.FLUID_EXTRACTION_RECIPES;

            RecipeBuilder<?> builder = map.recipeBuilder()
                .EUt(14)
                .duration(Math.max(1, recipe.getProcessingTime() / 4 * 20))
                .inputs(recipe.getResources())
                .fluidOutputs(recipe.getFluidOutput());

            ItemStack remnants = recipe.getRemnants();
            float chance = recipe.getRemnantsChance();
            if (remnants != null && !remnants.isEmpty()) {
                if (chance == 1.0F) {
                    builder.outputs(remnants);
                } else {
                    builder.chancedOutput(remnants, (int) (chance * 10000), CHANCE_PER_TIER);
                }
            }

            builder.buildAndRegister();
        }

        NPULib.printEventFinish("Finished copying squeezer recipes for %.3f seconds", time, System.currentTimeMillis());
    }
	
	public static void init() {
		// Recipe generation
		RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(NPUMetaItems.ELECTRODE_APATITE.getStackForm()).fluidInputs(Materials.Glass.getFluid(144)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.APATITE, 1)).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(OrePrefix.stick, Materials.Apatite, 2).input(OrePrefix.bolt, Materials.Apatite).input(OrePrefix.dustSmall, Materials.Redstone, 2).outputs(NPUMetaItems.ELECTRODE_APATITE.getStackForm()).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(OrePrefix.stick, Materials.Apatite, 4).input(OrePrefix.bolt, Materials.Apatite, 2).input(OrePrefix.dust, Materials.Redstone).outputs(NPUMetaItems.ELECTRODE_APATITE.getStackForm(2)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(NPUMetaItems.ELECTRODE_BLAZE.getStackForm()).fluidInputs(Materials.Glass.getFluid(144)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.BLAZE, 1)).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(OrePrefix.stick, Materials.Blaze, 2).input(OrePrefix.dustSmall, Materials.Blaze, 2).input(OrePrefix.dust, Materials.Redstone).outputs(NPUMetaItems.ELECTRODE_BLAZE.getStackForm(2)).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(400).EUt(24).input(OrePrefix.stick, Materials.Blaze, 5).input(OrePrefix.dust, Materials.Redstone, 2).outputs(NPUMetaItems.ELECTRODE_BLAZE.getStackForm(4)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(NPUMetaItems.ELECTRODE_BRONZE.getStackForm()).fluidInputs(Materials.Glass.getFluid(144)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.BRONZE, 1)).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(OrePrefix.stick, Materials.Bronze, 2).input(OrePrefix.bolt, Materials.Bronze).input(OrePrefix.dustSmall, Materials.Redstone, 2).outputs(NPUMetaItems.ELECTRODE_BRONZE.getStackForm()).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(OrePrefix.stick, Materials.Bronze, 4).input(OrePrefix.bolt, Materials.Bronze, 2).input(OrePrefix.dust, Materials.Redstone).outputs(NPUMetaItems.ELECTRODE_BRONZE.getStackForm(2)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(NPUMetaItems.ELECTRODE_COPPER.getStackForm()).fluidInputs(Materials.Glass.getFluid(144)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.COPPER, 1)).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(OrePrefix.stick, Materials.Copper, 2).input(OrePrefix.bolt, Materials.Copper).input(OrePrefix.dustSmall, Materials.Redstone, 2).outputs(NPUMetaItems.ELECTRODE_COPPER.getStackForm()).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(OrePrefix.stick, Materials.Copper, 4).input(OrePrefix.bolt, Materials.Copper, 2).input(OrePrefix.dust, Materials.Redstone).outputs(NPUMetaItems.ELECTRODE_COPPER.getStackForm(2)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(NPUMetaItems.ELECTRODE_DIAMOND.getStackForm()).fluidInputs(Materials.Glass.getFluid(144)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.DIAMOND, 1)).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(OrePrefix.stick, Materials.Diamond, 2).input(OrePrefix.bolt, Materials.Diamond).input(OrePrefix.dustSmall, Materials.Redstone, 2).outputs(NPUMetaItems.ELECTRODE_DIAMOND.getStackForm()).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(OrePrefix.stick, Materials.Diamond, 4).input(OrePrefix.bolt, Materials.Diamond, 2).input(OrePrefix.dust, Materials.Redstone).outputs(NPUMetaItems.ELECTRODE_DIAMOND.getStackForm(2)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(NPUMetaItems.ELECTRODE_EMERALD.getStackForm()).fluidInputs(Materials.Glass.getFluid(144)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.EMERALD, 1)).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(OrePrefix.stick, Materials.Emerald, 2).input(OrePrefix.bolt, Materials.Emerald).input(OrePrefix.dustSmall, Materials.Redstone, 2).outputs(NPUMetaItems.ELECTRODE_EMERALD.getStackForm()).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(OrePrefix.stick, Materials.Emerald, 4).input(OrePrefix.bolt, Materials.Emerald, 2).input(OrePrefix.dust, Materials.Redstone).outputs(NPUMetaItems.ELECTRODE_EMERALD.getStackForm(2)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(NPUMetaItems.ELECTRODE_ENDER.getStackForm()).fluidInputs(Materials.Glass.getFluid(144)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.ENDER, 1)).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(OrePrefix.dust, Materials.Endstone, 2).input(OrePrefix.dustSmall, Materials.Endstone, 2).input(OrePrefix.dust, Materials.EnderEye).outputs(NPUMetaItems.ELECTRODE_ENDER.getStackForm(2)).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(400).EUt(24).input(OrePrefix.dust, Materials.Endstone, 5).input(OrePrefix.dust, Materials.EnderEye, 2).outputs(NPUMetaItems.ELECTRODE_ENDER.getStackForm(4)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(NPUMetaItems.ELECTRODE_GOLD.getStackForm()).fluidInputs(Materials.Glass.getFluid(144)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.GOLD, 1)).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(OrePrefix.stick, Materials.Gold, 2).input(OrePrefix.bolt, Materials.Gold).input(OrePrefix.dustSmall, Materials.Redstone, 2).outputs(NPUMetaItems.ELECTRODE_GOLD.getStackForm()).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(OrePrefix.stick, Materials.Gold, 4).input(OrePrefix.bolt, Materials.Gold, 2).input(OrePrefix.dust, Materials.Redstone).outputs(NPUMetaItems.ELECTRODE_GOLD.getStackForm(2)).buildAndRegister();
        if (Loader.isModLoaded("ic2") || Loader.isModLoaded("binniecore")) {
            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(NPUMetaItems.ELECTRODE_IRON.getStackForm()).fluidInputs(Materials.Glass.getFluid(144)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.IRON, 1)).buildAndRegister();
            RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(OrePrefix.stick, Materials.Iron, 2).input(OrePrefix.bolt, Materials.Iron).input(OrePrefix.dustSmall, Materials.Redstone, 2).outputs(NPUMetaItems.ELECTRODE_IRON.getStackForm()).buildAndRegister();
            RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(OrePrefix.stick, Materials.Iron, 4).input(OrePrefix.bolt, Materials.Iron, 2).input(OrePrefix.dust, Materials.Redstone).outputs(NPUMetaItems.ELECTRODE_IRON.getStackForm(2)).buildAndRegister();
        }
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(NPUMetaItems.ELECTRODE_LAPIS.getStackForm()).fluidInputs(Materials.Glass.getFluid(144)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.LAPIS, 1)).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(OrePrefix.stick, Materials.Lapis, 2).input(OrePrefix.bolt, Materials.Lapis).input(OrePrefix.dustSmall, Materials.Redstone, 2).outputs(NPUMetaItems.ELECTRODE_LAPIS.getStackForm()).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(OrePrefix.stick, Materials.Lapis, 4).input(OrePrefix.bolt, Materials.Lapis, 2).input(OrePrefix.dust, Materials.Redstone).outputs(NPUMetaItems.ELECTRODE_LAPIS.getStackForm(2)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(NPUMetaItems.ELECTRODE_OBSIDIAN.getStackForm()).fluidInputs(Materials.Glass.getFluid(144)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.OBSIDIAN, 1)).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(OrePrefix.dust, Materials.Obsidian, 2).input(OrePrefix.dustSmall, Materials.Obsidian, 2).input(OrePrefix.dust, Materials.Redstone).outputs(NPUMetaItems.ELECTRODE_OBSIDIAN.getStackForm(2)).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(400).EUt(24).input(OrePrefix.dust, Materials.Obsidian, 5).input(OrePrefix.dust, Materials.Redstone, 2).outputs(NPUMetaItems.ELECTRODE_OBSIDIAN.getStackForm(4)).buildAndRegister();
        if (Loader.isModLoaded("extrautils2")) {
            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(NPUMetaItems.ELECTRODE_ORCHID.getStackForm()).fluidInputs(Materials.Glass.getFluid(144)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.ORCHID, 1)).buildAndRegister();
            RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(400).EUt(24).inputs(new ItemStack(Blocks.REDSTONE_ORE, 5), OreDictUnifier.get(OrePrefix.dust, Materials.Redstone)).outputs(NPUMetaItems.ELECTRODE_ORCHID.getStackForm(4)).buildAndRegister();
        }
        if (Loader.isModLoaded("ic2") || Loader.isModLoaded("techreborn")) {
            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(NPUMetaItems.ELECTRODE_RUBBER.getStackForm()).fluidInputs(Materials.Glass.getFluid(144)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.RUBBER, 1)).buildAndRegister();
            RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(OrePrefix.stick, Materials.Rubber, 2).input(OrePrefix.bolt, Materials.Rubber).input(OrePrefix.dustSmall, Materials.Redstone, 2).outputs(NPUMetaItems.ELECTRODE_RUBBER.getStackForm()).buildAndRegister();
            RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(OrePrefix.stick, Materials.Rubber, 4).input(OrePrefix.bolt, Materials.Rubber, 2).input(OrePrefix.dust, Materials.Redstone).outputs(NPUMetaItems.ELECTRODE_RUBBER.getStackForm(2)).buildAndRegister();
        }
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(16).inputs(NPUMetaItems.ELECTRODE_TIN.getStackForm()).fluidInputs(Materials.Glass.getFluid(144)).outputs(ModuleCore.getItems().tubes.get(EnumElectronTube.TIN, 1)).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(24).input(OrePrefix.stick, Materials.Tin, 2).input(OrePrefix.bolt, Materials.Tin).input(OrePrefix.dustSmall, Materials.Redstone, 2).outputs(NPUMetaItems.ELECTRODE_TIN.getStackForm()).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(24).input(OrePrefix.stick, Materials.Tin, 4).input(OrePrefix.bolt, Materials.Tin, 2).input(OrePrefix.dust, Materials.Redstone).outputs(NPUMetaItems.ELECTRODE_TIN.getStackForm(2)).buildAndRegister();
	}
}
