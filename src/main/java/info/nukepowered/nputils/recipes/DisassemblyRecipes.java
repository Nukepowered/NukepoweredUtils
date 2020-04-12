package info.nukepowered.nputils.recipes;

import static gregtech.common.blocks.BlockMachineCasing.MachineCasingType.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.metatileentity.ITieredMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.BlockMachineCasing.MachineCasingType;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import info.nukepowered.nputils.NPUConfig;
import info.nukepowered.nputils.NPULog;
import info.nukepowered.nputils.NPUMaterials;
import info.nukepowered.nputils.api.NPULib;
import info.nukepowered.nputils.item.NPUMetaItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public class DisassemblyRecipes {
	public static void init() {
		long time = System.currentTimeMillis();
		
		// Machine recycling
		for (MetaTileEntity machine : GregTechAPI.META_TILE_ENTITY_REGISTRY) {
			if (machine instanceof ITieredMetaTileEntity) {
				List<IRecipe> recipes = NPULib.getRecipesByOutput(machine.getStackForm());
				if (recipes.isEmpty()) continue;
				if (recipes.size() == 1) {
					List<ItemStack> recipeResult = getAllowedOutput(NPULib.getRecipeInputs(recipes.get(0)), ((ITieredMetaTileEntity) machine).getTier());
					if (recipeResult == null) continue;
					if (recipeResult.isEmpty()) continue;
					int tier = ((ITieredMetaTileEntity) machine).getTier();
					RecipeBuilder<?> builder = NPURecipeMaps.DISASSEMBLING_RECIPES.recipeBuilder()
							.duration((int) Math.floor(Math.pow(2.5, tier) * 20))
							.EUt(tier == 0 ? 8 : tier == 1 ? 30 : (int) Math.ceil((GTValues.V[tier] * (1 / tier + 0.4))))
							.inputs(machine.getStackForm());
					recipeResult.forEach(element -> builder.chancedOutput(element, 5000, 750));
					builder.buildAndRegister();
				} else {
					List<List<ItemStack>> recipesOutput = new ArrayList<List<ItemStack>>();
					recipes.forEach(recipe -> recipesOutput.add(NPULib.format(getAllowedOutput(NPULib.getRecipeInputs(recipe), ((ITieredMetaTileEntity) machine).getTier()))));
					List<ItemStack> finalResult = NPULib.equalizeRecipes(recipesOutput);
					if (finalResult == null) continue;
					if (finalResult.isEmpty()) continue;
					finalResult = NPULib.finalizeRecipe(finalResult);
					NPULog.warn("More than one crafting recipe for machine: " + machine.getStackForm().getDisplayName() + ", big losses are possible while disassembling");
					int tier = ((ITieredMetaTileEntity) machine).getTier();
					RecipeBuilder<?> builder = NPURecipeMaps.DISASSEMBLING_RECIPES.recipeBuilder()
							.duration((int) Math.floor(Math.pow(2.5, tier) * 20))
							.EUt(tier == 0 ? 8 : tier == 1 ? 30 : (int) Math.ceil((GTValues.V[tier] * (1 / tier + 0.4))))
							.inputs(machine.getStackForm());
					finalResult.forEach(component -> builder.chancedOutput(component, 5000, 750));
					builder.buildAndRegister();
				}
			}
		}
		
		// Steam machines recycling
		if (NPUConfig.enableSteamMachineRecycling) {
			RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaTileEntities.STEAM_BOILER_COAL_BRONZE.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Bronze, 5)).buildAndRegister();
			RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaTileEntities.STEAM_BOILER_SOLAR_BRONZE.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Bronze, 8), OreDictUnifier.get(OrePrefix.ingot, Materials.Silver, 3)).buildAndRegister();
			RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaTileEntities.STEAM_BOILER_LAVA_BRONZE.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Bronze, 10)).buildAndRegister();
			RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaTileEntities.STEAM_EXTRACTOR_BRONZE.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Bronze, 17)).buildAndRegister();
			RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaTileEntities.STEAM_MACERATOR_BRONZE.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Bronze, 14), OreDictUnifier.get(OrePrefix.ingot, Materials.WroughtIron, 2)).buildAndRegister();
			RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaTileEntities.STEAM_COMPRESSOR_BRONZE.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Bronze, 17), OreDictUnifier.get(OrePrefix.ingot, Materials.WroughtIron, 2)).buildAndRegister();
			RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaTileEntities.STEAM_HAMMER_BRONZE.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Bronze, 17), OreDictUnifier.get(OrePrefix.ingot, Materials.WroughtIron, 28)).buildAndRegister();
			RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaTileEntities.STEAM_FURNACE_BRONZE.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Bronze, 15), OreDictUnifier.get(OrePrefix.nugget, Materials.Bronze, 4)).buildAndRegister();
			RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaTileEntities.STEAM_ALLOY_SMELTER_BRONZE.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Bronze, 14)).buildAndRegister();
			
			RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaTileEntities.STEAM_BOILER_COAL_STEEL.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Steel, 5)).buildAndRegister();
			RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaTileEntities.STEAM_BOILER_LAVA_STEEL.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Steel, 10)).buildAndRegister();
			RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaTileEntities.STEAM_EXTRACTOR_STEEL.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Steel, 17)).buildAndRegister();
			RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaTileEntities.STEAM_MACERATOR_STEEL.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Steel, 14), OreDictUnifier.get(OrePrefix.ingot, Materials.WroughtIron, 2)).buildAndRegister();
			RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaTileEntities.STEAM_COMPRESSOR_STEEL.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Steel, 17), OreDictUnifier.get(OrePrefix.ingot, Materials.WroughtIron, 2)).buildAndRegister();
			RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaTileEntities.STEAM_HAMMER_STEEL.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Steel, 17), OreDictUnifier.get(OrePrefix.ingot, Materials.WroughtIron, 28)).buildAndRegister();
			RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaTileEntities.STEAM_FURNACE_STEEL.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Steel, 5), OreDictUnifier.get(OrePrefix.ingot, Materials.Bronze, 9), OreDictUnifier.get(OrePrefix.nugget, Materials.Bronze, 4)).buildAndRegister();
			RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaTileEntities.STEAM_ALLOY_SMELTER_STEEL.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Steel, 14)).buildAndRegister();
		}
		
		//Machine casings
		RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaBlocks.MACHINE_CASING.getItemVariant(ULV)).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.WroughtIron, 8)).buildAndRegister();
		RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaBlocks.MACHINE_CASING.getItemVariant(LV)).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Steel, 8)).buildAndRegister();
		RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaBlocks.MACHINE_CASING.getItemVariant(MV)).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Aluminium, 8)).buildAndRegister();
		RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaBlocks.MACHINE_CASING.getItemVariant(HV)).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.StainlessSteel, 8)).buildAndRegister();
		RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaBlocks.MACHINE_CASING.getItemVariant(EV)).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Titanium, 8)).buildAndRegister();
		RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaBlocks.MACHINE_CASING.getItemVariant(IV)).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.TungstenSteel, 8)).buildAndRegister();
		RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaBlocks.MACHINE_CASING.getItemVariant(LuV)).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Chrome, 8)).buildAndRegister();
		RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaBlocks.MACHINE_CASING.getItemVariant(ZPM)).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Iridium, 8)).buildAndRegister();
		RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaBlocks.MACHINE_CASING.getItemVariant(UV)).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Osmium, 8)).buildAndRegister();
		RecipeMaps.ARC_FURNACE_RECIPES.recipeBuilder().duration(200).EUt(32).inputs(MetaBlocks.MACHINE_CASING.getItemVariant(MAX)).outputs(OreDictUnifier.get(OrePrefix.ingot, NPUMaterials.Neutronium, 8)).buildAndRegister();
		
		//Component recycling LV-IV tiers
		for (Components component : Components.values()) {
			if (component == Components.MOTOR && NPUConfig.gameplay.enableRealisticMotorCraft) continue;
			for (int tier = 1; tier < 6; tier++) {
				ItemStack currentItem = component.getComponent(tier);
				List<IRecipe> recipes = NPULib.getRecipesByOutput(currentItem);
				List<ItemStack> result = new ArrayList<>();
				if (recipes == null) continue;
				if (recipes.isEmpty()) continue;
				if (recipes.size() == 1) {
					result = getAllowedOutputForComponents(NPULib.getRecipeInputs(recipes.get(0)));
					if (result == null) continue;
					if (result.isEmpty()) continue;
					RecipeBuilder<?> builder = NPURecipeMaps.DISASSEMBLING_RECIPES.recipeBuilder()
							.duration((int) Math.floor(Math.pow(2.5, tier) * 20))
							.EUt(tier == 0 ? 8 : tier == 1 ? 30 : (int) Math.ceil((GTValues.V[tier] * (1 / tier + 0.4))))
							.inputs(currentItem);
					result.forEach(item -> builder.chancedOutput(item, 5000, 750));
					builder.buildAndRegister();
				} else {
					NPULog.warn("More than one crafting recipe for item: " + currentItem.getDisplayName() + ", big losses are possible while disassembling");
					List<List<ItemStack>> recipesOutput = new ArrayList<>();
					recipes.forEach(recipe -> recipesOutput.add(NPULib.format(getAllowedOutputForComponents(NPULib.getRecipeInputs(recipe)))));
					result = NPULib.equalizeRecipes(recipesOutput);
					if (result == null) continue;
					if (result.isEmpty()) continue;
					result = NPULib.finalizeRecipe(result);
					RecipeBuilder<?> builder = NPURecipeMaps.DISASSEMBLING_RECIPES.recipeBuilder()
							.duration((int) Math.floor(Math.pow(2.5, tier) * 20))
							.EUt(tier == 0 ? 8 : tier == 1 ? 30 : (int) Math.ceil((GTValues.V[tier] * (1 / tier + 0.4))))
							.inputs(currentItem);
					result.forEach(item -> builder.chancedOutput(item, 5000, 750));
					builder.buildAndRegister();
				}
			}
		}
		
		//Motors when Realistic
		if (NPUConfig.gameplay.enableRealisticMotorCraft) {
			for (Components component : Components.values()) {
				if (component == Components.MOTOR) {
					for (int tier = 1; tier < 6; tier++) {
						List<Recipe> recipes = NPULib.getGTRecipeByOutput(RecipeMaps.ASSEMBLER_RECIPES, component.getComponent(tier));
						List<ItemStack> result = new ArrayList<>();
						if (recipes == null) continue;
						if (recipes.isEmpty() || recipes.size() > 1) continue;
						result = NPULib.parseGTRecipe(recipes.get(0));
						if (result.isEmpty()) continue;
						//Removing HULL from LV motor disassemble
//						if (component.getComponent(tier).isItemEqual(Components.MOTOR.getComponent(1))) result.remove(NPULib.getItemIndex(result, MotorComponent.HULL.getComponent(1))); 
						RecipeBuilder<?> builder = NPURecipeMaps.DISASSEMBLING_RECIPES.recipeBuilder()
								.duration((int) Math.floor(Math.pow(2.5, tier) * 20))
								.EUt(tier == 0 ? 8 : tier == 1 ? 30 : (int) Math.ceil((GTValues.V[tier] * (1 / tier + 0.4))))
								.inputs(component.getComponent(tier));
						result.forEach(stack -> builder.chancedOutput(stack, 5000, 750));
						builder.buildAndRegister();
					}
				}
			}

			for (MotorComponent component : MotorComponent.values()) {
				for (int tier = 1; tier < 6; tier++) {
					List<Recipe> recipes = NPULib.getGTRecipeByOutput(RecipeMaps.ASSEMBLER_RECIPES, component.getComponent(tier));
					List<ItemStack> result = new ArrayList<>();
					if (recipes == null) continue;
//					if (recipes.isEmpty() || (recipes.size() > 1 && !component.getComponent(tier).isItemEqual(MotorComponent.STATOR.getComponent(1)))) continue;
					if (recipes.isEmpty() || (recipes.size() > 1)) continue;
					result = NPULib.parseGTRecipe(recipes.get(0));
					RecipeBuilder<?> builder = NPURecipeMaps.DISASSEMBLING_RECIPES.recipeBuilder()
							.duration((int) Math.floor(Math.pow(2.5, tier) * 20))
							.EUt(tier == 0 ? 8 : tier == 1 ? 30 : (int) Math.ceil((GTValues.V[tier] * (1 / tier + 0.4))))
							.inputs(component.getComponent(tier));
					result.forEach(stack -> builder.chancedOutput(stack, 5000, 750));
					builder.buildAndRegister();
				}
			}
		}
		
		NPULib.printEventFinish("Disassembling recipes registration finished in %.3f seconds", time, System.currentTimeMillis());
	}

	protected static List<ItemStack> getAllowedOutput(List<ItemStack> list, int tier) {
		Iterator<ItemStack> iter = list.iterator();
		List<ItemStack> result = new ArrayList<>();
		while (iter.hasNext()) {
			ItemStack testItem = iter.next();
			UnificationEntry entry = OreDictUnifier.getUnificationEntry(testItem);
			if (entry != null) {
				if (entry.material != Materials.Glass && entry.material != Materials.Blaze && entry.material != Materials.Diamond) {
					if (!testItem.isItemEqual(OreDictUnifier.get("blockGlass")))
						result.add(testItem);
				}
			} else {
				if (Components.contains(testItem)) {
					result.add(testItem);
				} else if (MachineBlocks.contains(testItem)) {
					result.add(testItem);
				}
			}
		}
		return result;
	}

	protected static List<ItemStack> getAllowedOutputForComponents(List<ItemStack> list) {
		List<ItemStack> result = new ArrayList<>();
		Iterator<ItemStack> iter = list.iterator();
		while (iter.hasNext()) {
			ItemStack item = iter.next();
			if (isItemAllowed(item))
				result.add(item.copy());
		}
		return result;
	}
	
	protected static boolean isItemAllowed(ItemStack item) {
		if (OreDictUnifier.getUnificationEntry(item) != null) {
			UnificationEntry entry = OreDictUnifier.getUnificationEntry(item);
			if (entry.material != Materials.Rubber && entry.material != Materials.SiliconeRubber && entry.material != Materials.StyreneButadieneRubber) {
				return true;
			}
			return false;
		} else {
			return Components.contains(item);
		}
	}
	
	public enum Components {
		MOTOR {
			@Override
			public ItemStack getComponent(int tier) {
				switch(tier) {
				case 0:
                case 1:
                    return MetaItems.ELECTRIC_MOTOR_LV.getStackForm();
                case 2:
                    return MetaItems.ELECTRIC_MOTOR_MV.getStackForm();
                case 3:
                    return MetaItems.ELECTRIC_MOTOR_HV.getStackForm();
                case 4:
                    return MetaItems.ELECTRIC_MOTOR_EV.getStackForm();
                case 5:
                    return MetaItems.ELECTRIC_MOTOR_IV.getStackForm();
                case 6:
                    return MetaItems.ELECTRIC_MOTOR_LUV.getStackForm();
                case 7:
                    return MetaItems.ELECTRIC_MOTOR_ZPM.getStackForm();
                default:
                    return MetaItems.ELECTRIC_MOTOR_UV.getStackForm();
				}
			}
		},
		PUMP {
			@Override
	        public ItemStack getComponent(int tier) {
	            switch (tier) {
	                case 0:
	                case 1:
	                    return MetaItems.ELECTRIC_PUMP_LV.getStackForm();
	                case 2:
	                    return MetaItems.ELECTRIC_PUMP_MV.getStackForm();
	                case 3:
	                    return MetaItems.ELECTRIC_PUMP_HV.getStackForm();
	                case 4:
	                    return MetaItems.ELECTRIC_PUMP_EV.getStackForm();
	                case 5:
	                    return MetaItems.ELECTRIC_PUMP_IV.getStackForm();
	                case 6:
	                    return MetaItems.ELECTRIC_PUMP_LUV.getStackForm();
	                case 7:
	                    return MetaItems.ELECTRIC_PUMP_ZPM.getStackForm();
	                default:
	                    return MetaItems.ELECTRIC_PUMP_UV.getStackForm();
	            }
	        }
		},
		PISTON {
			@Override
			public ItemStack getComponent(int tier) {
				switch(tier) {
				case 0:
                case 1:
                    return MetaItems.ELECTRIC_PISTON_LV.getStackForm();
                case 2:
                    return MetaItems.ELECTRIC_PISTON_MV.getStackForm();
                case 3:
                    return MetaItems.ELECTRIC_PISTON_HV.getStackForm();
                case 4:
                    return MetaItems.ELECTRIC_PISTON_EV.getStackForm();
                case 5:
                    return MetaItems.ELECTRIC_PISTON_IV.getStackForm();
                case 6:
                    return MetaItems.ELECTRIC_PISTON_LUV.getStackForm();
                case 7:
                    return MetaItems.ELECTRIC_PISTON_ZPM.getStackForm();
                default:
                    return MetaItems.ELECTRIC_PISTON_UV.getStackForm();
				}
			}
		},
		EMITTER {
			@Override
			public ItemStack getComponent(int tier) {
				switch(tier) {
				case 0:
                case 1:
                    return MetaItems.EMITTER_LV.getStackForm();
                case 2:
                    return MetaItems.EMITTER_MV.getStackForm();
                case 3:
                    return MetaItems.EMITTER_HV.getStackForm();
                case 4:
                    return MetaItems.EMITTER_EV.getStackForm();
                case 5:
                    return MetaItems.EMITTER_IV.getStackForm();
                case 6:
                    return MetaItems.EMITTER_LUV.getStackForm();
                case 7:
                    return MetaItems.EMITTER_ZPM.getStackForm();
                default:
                    return MetaItems.EMITTER_UV.getStackForm();
				}
			}
		},
		ROBOTIC_ARM {
			@Override
			public ItemStack getComponent(int tier) {
				switch(tier) {
				case 0:
                case 1:
                    return MetaItems.ROBOT_ARM_LV.getStackForm();
                case 2:
                    return MetaItems.ROBOT_ARM_MV.getStackForm();
                case 3:
                    return MetaItems.ROBOT_ARM_HV.getStackForm();
                case 4:
                    return MetaItems.ROBOT_ARM_EV.getStackForm();
                case 5:
                    return MetaItems.ROBOT_ARM_IV.getStackForm();
                case 6:
                    return MetaItems.ROBOT_ARM_LUV.getStackForm();
                case 7:
                    return MetaItems.ROBOT_ARM_ZPM.getStackForm();
                default:
                    return MetaItems.ROBOT_ARM_UV.getStackForm();
				}
			}
		},
		SENSOR {
			@Override
			public ItemStack getComponent(int tier) {
				switch(tier) {
				case 0:
                case 1:
                    return MetaItems.SENSOR_LV.getStackForm();
                case 2:
                    return MetaItems.SENSOR_MV.getStackForm();
                case 3:
                    return MetaItems.SENSOR_HV.getStackForm();
                case 4:
                    return MetaItems.SENSOR_EV.getStackForm();
                case 5:
                    return MetaItems.SENSOR_IV.getStackForm();
                case 6:
                    return MetaItems.SENSOR_LUV.getStackForm();
                case 7:
                    return MetaItems.SENSOR_ZPM.getStackForm();
                default:
                    return MetaItems.SENSOR_UV.getStackForm();
				}
			}
		},
		FIELD_GENERATOR {
			@Override
	        public ItemStack getComponent(int tier) {
	            switch (tier) {
	                case 0:
	                case 1:
	                    return MetaItems.FIELD_GENERATOR_LV.getStackForm();
	                case 2:
	                    return MetaItems.FIELD_GENERATOR_MV.getStackForm();
	                case 3:
	                    return MetaItems.FIELD_GENERATOR_HV.getStackForm();
	                case 4:
	                    return MetaItems.FIELD_GENERATOR_EV.getStackForm();
	                case 5:
	                    return MetaItems.FIELD_GENERATOR_IV.getStackForm();
	                case 6:
	                    return MetaItems.FIELD_GENERATOR_LUV.getStackForm();
	                case 7:
	                    return MetaItems.FIELD_GENERATOR_ZPM.getStackForm();
	                default:
	                    return MetaItems.FIELD_GENERATOR_UV.getStackForm();
	            }
	        }
		},
		CONVEYOR {
			@Override
			public ItemStack getComponent(int tier) {
				switch(tier) {
				case 0:
                case 1:
                    return MetaItems.CONVEYOR_MODULE_LV.getStackForm();
                case 2:
                    return MetaItems.CONVEYOR_MODULE_MV.getStackForm();
                case 3:
                    return MetaItems.CONVEYOR_MODULE_HV.getStackForm();
                case 4:
                    return MetaItems.CONVEYOR_MODULE_EV.getStackForm();
                case 5:
                    return MetaItems.CONVEYOR_MODULE_IV.getStackForm();
                case 6:
                    return MetaItems.CONVEYOR_MODULE_LUV.getStackForm();
                case 7:
                    return MetaItems.CONVEYOR_MODULE_ZPM.getStackForm();
                default:
                    return MetaItems.CONVEYOR_MODULE_UV.getStackForm();
				}
			}
		};
		
		public static boolean contains(ItemStack stack) {
			for (Components component : Components.values()) {
				for (int i = 0; i < 9; i++) {
					if (component.getComponent(i).isItemEqual(stack)) {
						return true;
					}
				}
			}
			return false;
		}
		
		abstract public ItemStack getComponent(int tier);
	}
	
	public enum MachineBlocks {
		HULL {
			@Override
			public ItemStack getComponent(int tier) {
				return MetaTileEntities.HULL[tier].getStackForm();
			}
		},
		MACHINE_CASING {
			@Override
			public ItemStack getComponent(int tier) {
				switch(tier) {
				case 0:
					return MetaBlocks.MACHINE_CASING.getItemVariant(MachineCasingType.ULV);
				case 1:
					return MetaBlocks.MACHINE_CASING.getItemVariant(MachineCasingType.LV);
				case 2:
					return MetaBlocks.MACHINE_CASING.getItemVariant(MachineCasingType.MV);
				case 3:
					return MetaBlocks.MACHINE_CASING.getItemVariant(MachineCasingType.HV);
				case 4:
					return MetaBlocks.MACHINE_CASING.getItemVariant(MachineCasingType.EV);
				case 5:
					return MetaBlocks.MACHINE_CASING.getItemVariant(MachineCasingType.IV);
				case 6:
					return MetaBlocks.MACHINE_CASING.getItemVariant(MachineCasingType.LuV);
				case 7:
					return MetaBlocks.MACHINE_CASING.getItemVariant(MachineCasingType.ZPM);
				case 8:
					return MetaBlocks.MACHINE_CASING.getItemVariant(MachineCasingType.UV);
				default:
					return MetaBlocks.MACHINE_CASING.getItemVariant(MachineCasingType.MAX);
				}
			}
		};
		
		public static boolean contains(ItemStack stack) {
			for (MachineBlocks block : MachineBlocks.values()) {
				for (int i = 0; i < 10; i++) {
					if (block.getComponent(i).isItemEqual(stack)) {
						return true;
					}
				}
			}
			return false;
		}
		
		abstract public ItemStack getComponent(int tier);
	}
	
	public enum MotorComponent {
		ROTOR {
			@Override
			public ItemStack getComponent(int tier) {
				switch(tier) {
				case 0:
				case 1:
					return NPUMetaItems.ROTOR_LV.getStackForm();
				case 2:
					return NPUMetaItems.ROTOR_MV.getStackForm();
				case 3:
					return NPUMetaItems.ROTOR_HV.getStackForm();
				case 4:
					return NPUMetaItems.ROTOR_EV.getStackForm();
				case 5:
					return NPUMetaItems.ROTOR_IV.getStackForm();
				case 6:
					return NPUMetaItems.ROTOR_LuV.getStackForm();
				case 7:
					return NPUMetaItems.ROTOR_ZPM.getStackForm();
				default:
					return NPUMetaItems.ROTOR_UV.getStackForm();
				}
			}
		},
		STATOR {
			@Override
			public ItemStack getComponent(int tier) {
				switch(tier) {
				case 0:
				case 1:
					return NPUMetaItems.STATOR_LV.getStackForm();
				case 2:
					return NPUMetaItems.STATOR_MV.getStackForm();
				case 3:
					return NPUMetaItems.STATOR_HV.getStackForm();
				case 4:
					return NPUMetaItems.STATOR_EV.getStackForm();
				case 5:
					return NPUMetaItems.STATOR_IV.getStackForm();
				case 6:
					return NPUMetaItems.STATOR_LuV.getStackForm();
				case 7:
					return NPUMetaItems.STATOR_ZPM.getStackForm();
				default:
					return NPUMetaItems.STATOR_UV.getStackForm();
				}
			}
		},
		HULL {
			@Override
			public ItemStack getComponent(int tier) {
				switch(tier) {
				case 0:
				case 1:
					return NPUMetaItems.MOTOR_HULL_LV.getStackForm();
				case 2:
					return NPUMetaItems.MOTOR_HULL_MV.getStackForm();
				case 3:
					return NPUMetaItems.MOTOR_HULL_HV.getStackForm();
				case 4:
					return NPUMetaItems.MOTOR_HULL_EV.getStackForm();
				case 5:
					return NPUMetaItems.MOTOR_HULL_IV.getStackForm();
				case 6:
					return NPUMetaItems.MOTOR_HULL_LuV.getStackForm();
				case 7:
					return NPUMetaItems.MOTOR_HULL_ZPM.getStackForm();
				default:
					return NPUMetaItems.MOTOR_HULL_UV.getStackForm();
				}
			}
		};
		
		public static boolean contains(ItemStack stack) {
			for (MotorComponent part : MotorComponent.values()) {
				for (int i = 0; i < 9; i++) {
					if (part.getComponent(i).isItemEqual(stack)) {
						return true;
					}
				}
			}
			return false;
		}
		
		abstract public ItemStack getComponent(int tier);
	}
}
