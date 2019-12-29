package info.nukepowered.nputils.recipes;

import appeng.api.AEApi;
import appeng.api.definitions.IMaterials;
import appeng.core.AppEng;
import appeng.items.misc.ItemCrystalSeed;
import gregtech.api.items.ToolDictNames;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import info.nukepowered.nputils.NPUConfig;
import info.nukepowered.nputils.NPULog;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class AEIntegration {
	
	public static void init() {
		NPULog.info("Applied Energistics 2 integarion enabled");
		
		// Recipe removal
		if (NPUConfig.AE.recipeRemoval) {
			ModHandler.removeRecipeByName(new ResourceLocation("appliedenergistics2", "network/blocks/inscribers"));
			
		}
		
		// Recipe generation
		final IMaterials materials = AEApi.instance().definitions().materials();
		final Item crystalSeedCertus = Item.getByNameOrId(AppEng.MOD_ID + ":" + (ItemCrystalSeed.getResolver(ItemCrystalSeed.CERTUS).itemName));
		final ItemStack crystalSeedNether  = new ItemStack(crystalSeedCertus, 1, 600);
		final ItemStack crystalSeedFluix  = new ItemStack(crystalSeedCertus, 1, 1200);
		if (NPUConfig.AE.formingProcessorsPrintRecipes) {
			RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(16).input(OrePrefix.plate, Materials.Silicon).notConsumable(materials.siliconPress().maybeStack(1).get()).outputs(materials.siliconPrint().maybeStack(1).get()).buildAndRegister();
			RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(16).inputs(materials.purifiedCertusQuartzCrystal().maybeStack(1).get()).notConsumable(materials.calcProcessorPress().maybeStack(1).get()).outputs(materials.calcProcessorPrint().maybeStack(1).get()).buildAndRegister();
			RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(16).input(OrePrefix.plate, Materials.CertusQuartz).notConsumable(materials.calcProcessorPress().maybeStack(1).get()).outputs(materials.calcProcessorPrint().maybeStack(1).get()).buildAndRegister();
			RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(16).input(OrePrefix.plate, Materials.Gold).notConsumable(materials.logicProcessorPress().maybeStack(1).get()).outputs(materials.logicProcessorPrint().maybeStack(1).get()).buildAndRegister();
			RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(16).input(OrePrefix.plate, Materials.Diamond).notConsumable(materials.engProcessorPress().maybeStack(1).get()).outputs(materials.engProcessorPrint().maybeStack(1).get()).buildAndRegister();
		}
		if (NPUConfig.AE.processorAssemlerRecipes) {
			RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(64).EUt(32).inputs(materials.calcProcessorPrint().maybeStack(1).get(), materials.siliconPrint().maybeStack(1).get()).fluidInputs(Materials.Redstone.getFluid(144)).outputs(materials.calcProcessor().maybeStack(1).get()).buildAndRegister();
			RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(64).EUt(32).inputs(materials.logicProcessorPrint().maybeStack(1).get(), materials.siliconPrint().maybeStack(1).get()).fluidInputs(Materials.Redstone.getFluid(144)).outputs(materials.logicProcessor().maybeStack(1).get()).buildAndRegister();
			RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(64).EUt(32).inputs(materials.engProcessorPrint().maybeStack(1).get(), materials.siliconPrint().maybeStack(1).get()).fluidInputs(Materials.Redstone.getFluid(144)).outputs(materials.engProcessor().maybeStack(1).get()).buildAndRegister();
		}
		RecipeMaps.ELECTROLYZER_RECIPES.recipeBuilder().duration(2000).EUt(30).input(OrePrefix.gem, Materials.CertusQuartz).outputs(materials.certusQuartzCrystalCharged().maybeStack(1).get()).buildAndRegister();
		RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(20).EUt(16).inputs(materials.certusQuartzCrystalCharged().maybeStack(1).get(), OreDictUnifier.get(OrePrefix.gem, Materials.NetherQuartz), OreDictUnifier.get(OrePrefix.dust, Materials.Redstone)).fluidInputs(Materials.Water.getFluid(500)).outputs(materials.fluixCrystal().maybeStack(2).get()).buildAndRegister();
		RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(15).EUt(16).inputs(materials.certusQuartzCrystalCharged().maybeStack(1).get(), OreDictUnifier.get(OrePrefix.gem, Materials.NetherQuartz), OreDictUnifier.get(OrePrefix.dust, Materials.Redstone)).fluidInputs(Materials.DistilledWater.getFluid(500)).outputs(materials.fluixCrystal().maybeStack(2).get()).buildAndRegister();
		RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder().duration(2000).EUt(24).inputs(new ItemStack(crystalSeedCertus)).fluidInputs(Materials.Water.getFluid(200)).outputs(materials.purifiedCertusQuartzCrystal().maybeStack(1).get()).buildAndRegister();
		RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder().duration(2000).EUt(24).inputs(crystalSeedNether).fluidInputs(Materials.Water.getFluid(200)).outputs(materials.purifiedNetherQuartzCrystal().maybeStack(1).get()).buildAndRegister();
		RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder().duration(2000).EUt(24).inputs(crystalSeedFluix).fluidInputs(Materials.Water.getFluid(200)).outputs(materials.purifiedFluixCrystal().maybeStack(1).get()).buildAndRegister();
		RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder().duration(2000).EUt(24).inputs(materials.fluixDust().maybeStack(1).get()).fluidInputs(Materials.Water.getFluid(200)).chancedOutput(materials.fluixCrystal().maybeStack(1).get(), 7000, 2500).buildAndRegister();
		RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder().duration(1000).EUt(24).inputs(new ItemStack(crystalSeedCertus)).fluidInputs(Materials.DistilledWater.getFluid(200)).outputs(materials.purifiedCertusQuartzCrystal().maybeStack(1).get()).buildAndRegister();
		RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder().duration(1000).EUt(24).inputs(crystalSeedNether).fluidInputs(Materials.DistilledWater.getFluid(200)).outputs(materials.purifiedNetherQuartzCrystal().maybeStack(1).get()).buildAndRegister();
		RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder().duration(1000).EUt(24).inputs(crystalSeedFluix).fluidInputs(Materials.DistilledWater.getFluid(200)).outputs(materials.purifiedFluixCrystal().maybeStack(1).get()).buildAndRegister();
		RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder().duration(1500).EUt(24).inputs(materials.fluixDust().maybeStack(1).get()).fluidInputs(Materials.DistilledWater.getFluid(200)).chancedOutput(materials.fluixCrystal().maybeStack(1).get(), 7000, 2500).buildAndRegister();
		RecipeMaps.MACERATOR_RECIPES.recipeBuilder().duration(10).EUt(16).inputs(materials.fluixCrystal().maybeStack(1).get()).outputs(materials.fluixDust().maybeStack(1).get()).buildAndRegister();
		RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder().duration(7).EUt(20).inputs(materials.fluixCrystal().maybeStack(1).get()).outputs(materials.fluixDust().maybeStack(1).get()).buildAndRegister();
		ModHandler.addShapelessRecipe("nputils:fluix_dust", materials.fluixDust().maybeStack(1).get(), materials.fluixCrystal().maybeStack(1).get(), ToolDictNames.craftingToolHardHammer);
	}
}
