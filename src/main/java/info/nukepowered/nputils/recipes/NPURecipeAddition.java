package info.nukepowered.nputils.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import gregtech.api.GTValues;
import gregtech.api.items.ToolDictNames;
import gregtech.api.recipes.CountableIngredient;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.ingredients.IntCircuitIngredient;
import gregtech.api.recipes.recipes.CokeOvenRecipe;
import gregtech.api.recipes.recipes.FuelRecipe;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.MarkerMaterials.Tier;
import gregtech.api.unification.material.type.DustMaterial;
import gregtech.api.unification.material.type.FluidMaterial;
import gregtech.api.unification.material.type.GemMaterial;
import gregtech.api.unification.material.type.IngotMaterial;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.MaterialStack;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.api.util.GTUtility;
import gregtech.common.blocks.BlockMetalCasing.MetalCasingType;
import gregtech.common.blocks.BlockMultiblockCasing.MultiblockCasingType;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.common.blocks.BlockWireCoil;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import info.nukepowered.nputils.NPUConfig;
import info.nukepowered.nputils.NPUMaterials;
import info.nukepowered.nputils.NPUtils;
import info.nukepowered.nputils.api.NPULib;
import info.nukepowered.nputils.armor.PowerlessJetpack;
import info.nukepowered.nputils.item.NPUMetaBlocks;
import info.nukepowered.nputils.item.NPUMetaItems;
import info.nukepowered.nputils.item.NPUMultiblockCasing;
import info.nukepowered.nputils.item.NPUTransparentCasing;
import info.nukepowered.nputils.item.NPUTransparentCasing.CasingType;
import info.nukepowered.nputils.machines.NPUTileEntities;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.oredict.OreDictionary;


public class NPURecipeAddition {
	private static final MaterialStack[] solderingList = {
			new MaterialStack(Materials.Tin, 2L),
			new MaterialStack(Materials.SolderingAlloy, 1L),
			new MaterialStack(Materials.Lead, 4L)
	};
	
	private static final MaterialStack[] sawLubricants =  {
			new MaterialStack(Materials.Lubricant, 1L),
			new MaterialStack(Materials.DistilledWater, 3L),
			new MaterialStack(Materials.Water, 4L)
	};
	
	private static final MaterialStack[] cableFluids = {
			new MaterialStack(Materials.Rubber, 144),
			new MaterialStack(Materials.StyreneButadieneRubber, 108),
			new MaterialStack(Materials.SiliconeRubber, 72)
	};
	
	private static final MaterialStack[] cableDusts = {
			new MaterialStack(Materials.Polydimethylsiloxane, 1),
			new MaterialStack(Materials.PolyvinylChloride, 1)
	};
	
	private static final List<Material> tieredCables = Arrays.asList(new Material[]{
			Materials.Tungsten, Materials.Osmium, Materials.Platinum, Materials.TungstenSteel, Materials.Graphene,
			Materials.VanadiumGallium, Materials.HSSG, Materials.YttriumBariumCuprate, Materials.NiobiumTitanium,
			Materials.Naquadah, Materials.NiobiumTitanium, Materials.NaquadahEnriched, Materials.Duranium,
			Materials.NaquadahAlloy
	});
	
	private static final List<Material> superconductors = Arrays.asList(new Material[]{
			NPUMaterials.MVSuperconductor, NPUMaterials.HVSuperconductor, NPUMaterials.EVSuperconductor,
			NPUMaterials.IVSuperconductor, NPUMaterials.LuVSuperconductor, NPUMaterials.ZPMSuperconductor
	});
	
	private static final List<Material> tieredSuperconductors = Arrays.asList(new Material[] {
			NPUMaterials.EVSuperconductor, NPUMaterials.IVSuperconductor,
			NPUMaterials.LuVSuperconductor, NPUMaterials.ZPMSuperconductor
	});
	
	private static final MaterialStack[] firstMetal = {
			new MaterialStack(Materials.Iron, 1),
			new MaterialStack(Materials.Nickel, 1),
			new MaterialStack(Materials.Invar, 2),
			new MaterialStack(Materials.Steel, 2),
			new MaterialStack(Materials.StainlessSteel, 3),
			new MaterialStack(Materials.Titanium, 3),
			new MaterialStack(Materials.Tungsten, 4),
			new MaterialStack(Materials.TungstenSteel, 5)
	};
	
	private static final MaterialStack[] lastMetal = {
			new MaterialStack(Materials.Tin, 0),
			new MaterialStack(Materials.Zinc, 0),
			new MaterialStack(Materials.Aluminium, 1)
	};
	
	private static final MaterialStack[] ironOres = {
            new MaterialStack(Materials.Pyrite, 1),
            new MaterialStack(Materials.BrownLimonite, 1),
            new MaterialStack(Materials.YellowLimonite, 1),
            new MaterialStack(Materials.Magnetite, 1),
            new MaterialStack(Materials.Iron, 1)
    };
	
//	private static final MaterialStack[] lubeDusts = {
//            new MaterialStack(Materials.Talc, 1),
//            new MaterialStack(Materials.Soapstone, 1),
//            new MaterialStack(Materials.Redstone, 1)
//    };
	
	private static final MaterialStack[] lapisLike = {
            new MaterialStack(Materials.Lapis, 1),
            new MaterialStack(Materials.Lazurite, 1),
            new MaterialStack(Materials.Sodalite, 1)
    };
	
	protected static final ItemStack[] molds = {
			MetaItems.SHAPE_MOLD_ANVIL.getStackForm(),
			MetaItems.SHAPE_MOLD_BALL.getStackForm(),
            MetaItems.SHAPE_MOLD_BLOCK.getStackForm(),
            MetaItems.SHAPE_MOLD_BOTTLE.getStackForm(),
            MetaItems.SHAPE_MOLD_CREDIT.getStackForm(),
            MetaItems.SHAPE_MOLD_CYLINDER.getStackForm(),
            MetaItems.SHAPE_MOLD_GEAR.getStackForm(),
            MetaItems.SHAPE_MOLD_INGOT.getStackForm(),
            MetaItems.SHAPE_MOLD_NAME.getStackForm(),
            MetaItems.SHAPE_MOLD_NUGGET.getStackForm(),
            MetaItems.SHAPE_MOLD_PLATE.getStackForm(),
            MetaItems.SHAPE_MOLD_GEAR_SMALL.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_AXE.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_BLOCK.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_BOLT.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_BOTTLE.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_CELL.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_FILE.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_GEAR.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_HAMMER.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_HOE.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_INGOT.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_PIPE_LARGE.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_PIPE_MEDIUM.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_PICKAXE.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_PLATE.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_RING.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_ROD.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_SAW.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_SHOVEL.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_PIPE_SMALL.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_SWORD.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_PIPE_TINY.getStackForm(),
            MetaItems.SHAPE_EXTRUDER_WIRE.getStackForm()
	};
	
	public static void init() {
		long time_c = System.currentTimeMillis();
		//GTNH
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech", "casing_coke_bricks"));
		ModHandler.removeRecipeByName(new ResourceLocation("gregtech", "compressed_clay"));
		ModHandler.removeFurnaceSmelting(MetaItems.COKE_OVEN_BRICK.getStackForm());
		ModHandler.removeFurnaceSmelting(new ItemStack(Items.CLAY_BALL, 1, OreDictionary.WILDCARD_VALUE));
		ModHandler.addSmeltingRecipe(NPUMetaItems.COMPRESSED_COKE_CLAY.getStackForm(), NPUMetaItems.COKE_BRICK.getStackForm());
		ModHandler.addSmeltingRecipe(NPUMetaItems.COMPRESSED_CLAY.getStackForm(), new ItemStack(Items.BRICK));
		RecipeMaps.ALLOY_SMELTER_RECIPES.recipeBuilder().duration(400).EUt(8).inputs(NPUMetaItems.COMPRESSED_CLAY.getStackForm(2), new ItemStack(Blocks.SAND)).outputs(NPUMetaItems.COKE_BRICK.getStackForm(2)).buildAndRegister();
		RecipeMaps.ALLOY_SMELTER_RECIPES.recipeBuilder().duration(200).EUt(2).inputs(new ItemStack(Items.CLAY_BALL)).notConsumable(MetaItems.SHAPE_MOLD_INGOT).outputs(new ItemStack(Items.BRICK)).buildAndRegister();
		ModHandler.addShapelessRecipe("nputils:clay_brick", NPUMetaItems.COMPRESSED_CLAY.getStackForm(), new ItemStack(Items.CLAY_BALL), MetaItems.WOODEN_FORM_BRICK);
		ModHandler.addShapedRecipe("nputils:eight_clay_brick", NPUMetaItems.COMPRESSED_CLAY.getStackForm(8), "BBB", "BFB", "BBB", 'B', new ItemStack(Items.CLAY_BALL), 'F', MetaItems.WOODEN_FORM_BRICK);
		ModHandler.addShapedRecipe("nputils:coke_brick", NPUMetaItems.COMPRESSED_COKE_CLAY.getStackForm(5), "SSS", "BFB", "BBB", 'B', new ItemStack(Items.CLAY_BALL), 'S', new ItemStack(Blocks.SAND), 'F', MetaItems.WOODEN_FORM_BRICK);
        ModHandler.addShapedRecipe("nputils:coke_bricks", MetaBlocks.METAL_CASING.getItemVariant(MetalCasingType.COKE_BRICKS), "BB", "BB", 'B', NPUMetaItems.COKE_BRICK.getStackForm());
        
        //GT5U Old Primitive Brick Processing
        ModHandler.removeFurnaceSmelting(MetaItems.FIRECLAY_BRICK.getStackForm());
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:casing_primitive_bricks"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:brick_to_dust"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:brick_block_to_dust"));
        ModHandler.addSmeltingRecipe(NPUMetaItems.COMPRESSED_FIRECLAY.getStackForm(), NPUMetaItems.FIRECLAY_BRICK.getStackForm());
        RecipeMaps.COMPRESSOR_RECIPES.recipeBuilder().input(OrePrefix.dust, Materials.Fireclay).outputs(NPUMetaItems.COMPRESSED_FIRECLAY.getStackForm()).duration(100).EUt(2).buildAndRegister();
        ModHandler.addShapedRecipe("nputils:quartz_sand", OreDictUnifier.get(OrePrefix.dust, NPUMaterials.QuartzSand), "S", "m", 'S', "sand");
        RecipeMaps.MACERATOR_RECIPES.recipeBuilder().duration(200).EUt(8).input("sand", 1).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.QuartzSand)).chancedOutput(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.QuartzSand), 2500, 750).chancedOutput(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.QuartzSand), 2000, 750).buildAndRegister();
        ModHandler.addShapelessRecipe("nputils:glass_dust_npu", OreDictUnifier.get(OrePrefix.dust, Materials.Glass), "dustSand", "dustFlint");
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(200).EUt(8).input(OrePrefix.dust, Materials.Flint).input(OrePrefix.dust, NPUMaterials.QuartzSand, 4).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Glass, 4)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(160).EUt(8).input(OrePrefix.dust, Materials.Flint).input(OrePrefix.dust, Materials.Quartzite, 4).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Glass, 4)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(100).EUt(16).input(OrePrefix.dust, Materials.Calcite, 2).input(OrePrefix.dust, Materials.Stone).input(OrePrefix.dust, Materials.Clay).input(OrePrefix.dust, NPUMaterials.QuartzSand).fluidInputs(Materials.Water.getFluid(2000)).fluidOutputs(Materials.Concrete.getFluid(2304)).buildAndRegister();
        
        //GT5U Misc Recipes
        ModHandler.addSmeltingRecipe(new ItemStack(Items.SLIME_BALL), MetaItems.RUBBER_DROP.getStackForm());
        ModHandler.removeRecipeByName(new ResourceLocation("minecraft:bone_meal_from_bone"));
        ModHandler.addShapelessRecipe("nputils:harder_bone_meal", new ItemStack(Items.DYE, 3, 15), new ItemStack(Items.BONE), ToolDictNames.craftingToolMortar);
        RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder().inputs(new ItemStack(Items.BONE)).outputs(new ItemStack(Items.DYE, 3, 15)).duration(16).EUt(10).buildAndRegister();
        RecipeMaps.MACERATOR_RECIPES.recipeBuilder().inputs(new ItemStack(Items.BONE)).outputs(new ItemStack(Items.DYE, 5, 15)).duration(300).EUt(2).buildAndRegister();
        
        //GT6 Bending recipes
        if (NPUConfig.GT6.BendingCurvedPlates && NPUConfig.GT6.BendingCylinders) {
        	ModHandler.removeRecipeByName(new ResourceLocation("gregtech:iron_bucket"));
        	ModHandler.removeRecipeByName(new ResourceLocation("minecraft:iron_helmet"));
        	ModHandler.removeRecipeByName(new ResourceLocation("minecraft:iron_chestplate"));
        	ModHandler.removeRecipeByName(new ResourceLocation("minecraft:iron_leggings"));
        	ModHandler.removeRecipeByName(new ResourceLocation("minecraft:iron_boots"));
        	ModHandler.removeRecipeByName(new ResourceLocation("minecraft:golden_helmet"));
        	ModHandler.removeRecipeByName(new ResourceLocation("minecraft:golden_chestplate"));
        	ModHandler.removeRecipeByName(new ResourceLocation("minecraft:golden_leggings"));
        	ModHandler.removeRecipeByName(new ResourceLocation("minecraft:golden_boots"));
        	ModHandler.addShapedRecipe("nputils:bucket", new ItemStack(Items.BUCKET), "ChC", " P ", 'C', "plateCurvedIron", 'P', "plateIron");
        	RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(4).input(OrePrefix.valueOf("plateCurved"), Materials.Iron, 2).input(OrePrefix.plate, Materials.Iron).outputs(new ItemStack(Items.BUCKET)).buildAndRegister();
            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(4).input(OrePrefix.valueOf("plateCurved"), Materials.WroughtIron, 2).input(OrePrefix.plate, Materials.WroughtIron).outputs(new ItemStack(Items.BUCKET)).buildAndRegister();
            ModHandler.addShapedRecipe("nputils:iron_helmet", new ItemStack(Items.IRON_HELMET), "PPP", "ChC", 'P', "plateIron", 'C', "plateCurvedIron");
            ModHandler.addShapedRecipe("nputils:iron_chestplate", new ItemStack(Items.IRON_CHESTPLATE), "PhP", "CPC", "CPC", 'P', "plateIron", 'C', "plateCurvedIron");
            ModHandler.addShapedRecipe("nputils:iron_leggings", new ItemStack(Items.IRON_LEGGINGS), "PCP", "ChC", "C C", 'P', "plateIron", 'C', "plateCurvedIron");
            ModHandler.addShapedRecipe("nputils:iron_boots", new ItemStack(Items.IRON_BOOTS), "P P", "ChC", 'P', "plateIron", 'C', "plateCurvedIron");
            ModHandler.addShapedRecipe("nputils:golden_helmet", new ItemStack(Items.GOLDEN_HELMET), "PPP", "ChC", 'P', "plateGold", 'C', "plateCurvedGold");
            ModHandler.addShapedRecipe("nputils:golden_chestplate", new ItemStack(Items.GOLDEN_CHESTPLATE), "PhP", "CPC", "CPC", 'P', "plateGold", 'C', "plateCurvedGold");
            ModHandler.addShapedRecipe("nputils:golden_leggings", new ItemStack(Items.GOLDEN_LEGGINGS), "PCP", "ChC", "C C", 'P', "plateGold", 'C', "plateCurvedGold");
            ModHandler.addShapedRecipe("nputils:golden_boots", new ItemStack(Items.GOLDEN_BOOTS), "P P", "ChC", 'P', "plateGold", 'C', "plateCurvedGold");
            ModHandler.addShapedRecipe("nputils:chain_helmet", new ItemStack(Items.CHAINMAIL_HELMET), "RRR", "RhR", 'R', "ringIron");
            ModHandler.addShapedRecipe("nputils:chain_chestplate", new ItemStack(Items.CHAINMAIL_CHESTPLATE), "RhR", "RRR", "RRR", 'R', "ringIron");
            ModHandler.addShapedRecipe("nputils:chain_leggings", new ItemStack(Items.CHAINMAIL_LEGGINGS), "RRR", "RhR", "R R", 'R', "ringIron");
            ModHandler.addShapedRecipe("nputils:chain_boots", new ItemStack(Items.CHAINMAIL_BOOTS), "R R", "RhR", 'R', "ringIron");
        }
        
        for (Material m : IngotMaterial.MATERIAL_REGISTRY) {
        	if (!OreDictUnifier.get(OrePrefix.ring, m).isEmpty() &&
        			!OreDictUnifier.get(OrePrefix.stick, m).isEmpty() &&
        			m != Materials.Rubber && m != Materials.StyreneButadieneRubber
        			&& m != Materials.SiliconeRubber && NPUConfig.GT6.BendingRings
        			&& NPUConfig.GT6.BendingCylinders) {
        		ModHandler.removeRecipes(OreDictUnifier.get(OrePrefix.ring, m));
        		ModHandler.addShapedRecipe(String.format("nputils:rod_to_ring_%s", m.toString()), OreDictUnifier.get(OrePrefix.ring, m), "hS", " C", 'S', OreDictUnifier.get(OrePrefix.stick, m), 'C', "craftingToolBendingCylinderSmall");
        	}
        	
        	if (!OreDictUnifier.get(OrePrefix.valueOf("plateCurved"), m).isEmpty() &&
        			NPUConfig.GT6.BendingCurvedPlates && NPUConfig.GT6.BendingCylinders) {
                ModHandler.addShapedRecipe(String.format("nputils:curved_plate_%s", m.toString()), OreDictUnifier.get(OrePrefix.valueOf("plateCurved"), m), "h", "P", "C", 'P', new UnificationEntry(OrePrefix.plate, m), 'C', "craftingToolBendingCylinder");
                ModHandler.addShapedRecipe(String.format("nputils:flatten_plate_%s", m.toString()), OreDictUnifier.get(OrePrefix.plate, m), "h", "C", 'C', new UnificationEntry(OrePrefix.valueOf("plateCurved"), m));
                RecipeMaps.BENDER_RECIPES.recipeBuilder().EUt(24).duration((int) m.getAverageMass()).input(OrePrefix.plate, m).circuitMeta(0).outputs(OreDictUnifier.get(OrePrefix.valueOf("plateCurved"), m)).buildAndRegister();
            }
        	
            if (!OreDictUnifier.get(OrePrefix.rotor, m).isEmpty() && NPUConfig.GT6.BendingRotors
            		&& NPUConfig.GT6.BendingCylinders) {
                ModHandler.removeRecipes(OreDictUnifier.get(OrePrefix.rotor, m));
                ModHandler.addShapedRecipe(String.format("nputils:npu_rotor_%s", m.toString()), OreDictUnifier.get(OrePrefix.rotor, m), "ChC", "SRf", "CdC", 'C', OreDictUnifier.get(OrePrefix.valueOf("plateCurved"), m), 'S', OreDictUnifier.get(OrePrefix.screw, m), 'R', OreDictUnifier.get(OrePrefix.ring, m));
                RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(240).EUt(24).inputs(OreDictUnifier.get(OrePrefix.valueOf("plateCurved"), m, 4), OreDictUnifier.get(OrePrefix.ring, m)).fluidInputs(Materials.SolderingAlloy.getFluid(32)).outputs(OreDictUnifier.get(OrePrefix.rotor, m)).buildAndRegister();
            }
            
            if (!OreDictUnifier.get(OrePrefix.foil, m).isEmpty()) {
                if (NPUConfig.GT6.BendingFoils && NPUConfig.GT6.BendingCylinders) {
                    ModHandler.addShapedRecipe(String.format("nputils:foil_%s", m.toString()), OreDictUnifier.get(OrePrefix.foil, m, 2), "hPC", 'P', new UnificationEntry(OrePrefix.plate, m), 'C', "craftingToolBendingCylinder");
                }
                if (NPUConfig.GT6.BendingFoilsAutomatic && NPUConfig.GT6.BendingCylinders) {
                	NPURecipeMaps.CLUSTER_MILL_RECIPES.recipeBuilder().EUt(24).duration((int) m.getAverageMass()).input(OrePrefix.plate, m).outputs(OreDictUnifier.get(OrePrefix.foil, m, 4)).buildAndRegister();
                } else if (!NPUConfig.GT6.BendingFoilsAutomatic || !NPUConfig.GT6.BendingCylinders) {
                    RecipeMaps.BENDER_RECIPES.recipeBuilder().EUt(24).duration((int) m.getAverageMass()).circuitMeta(4).input(OrePrefix.plate, m).outputs(OreDictUnifier.get(OrePrefix.foil, m, 4)).buildAndRegister();
                }
            }
            
            if (!OreDictUnifier.get(OrePrefix.valueOf("round"), m).isEmpty()) {
                ModHandler.addShapedRecipe(String.format("nputils:round%s", m.toString()), OreDictUnifier.get(OrePrefix.valueOf("round"), m), "fN", "N ", 'N', OreDictUnifier.get(OrePrefix.nugget, m));
                RecipeMaps.LATHE_RECIPES.recipeBuilder().EUt(8).duration((int) m.getAverageMass()).inputs(OreDictUnifier.get(OrePrefix.nugget, m)).outputs(OreDictUnifier.get(OrePrefix.valueOf("round"), m)).buildAndRegister();
            }
            
            //GT6 Plate Recipe
            if (m instanceof IngotMaterial && !OreDictUnifier.get(OrePrefix.plate, m).isEmpty() && !OreDictUnifier.get(OrePrefix.valueOf("ingotDouble"), m).isEmpty() && NPUConfig.GT6.PlateDoubleIngot) {
                ModHandler.removeRecipes(OreDictUnifier.get(OrePrefix.plate, m));
                ModHandler.addShapedRecipe(String.format("nputils:ingot_double_%s", m.toString()), OreDictUnifier.get(OrePrefix.valueOf("ingotDouble"), m), "h", "I", "I", 'I', new UnificationEntry(OrePrefix.ingot, m));
                ModHandler.addShapedRecipe(String.format("nputils:plate_%s", m.toString()), OreDictUnifier.get(OrePrefix.plate, m), "h", "I", 'I', OreDictUnifier.get(OrePrefix.valueOf("ingotDouble"), m));
            }
            
            //Cables
            if ((m instanceof IngotMaterial || superconductors.contains(m)) && !OreDictUnifier.get(OrePrefix.cableGtSingle, m).isEmpty() && m != Materials.RedAlloy && m != Materials.Cobalt && m != Materials.Zinc && m != Materials.SolderingAlloy && m != Materials.Tin && m != Materials.Lead && NPUConfig.gameplay.CablesGT5U) {
                for (MaterialStack stackFluid : cableFluids) {
                    IngotMaterial fluid = (IngotMaterial) stackFluid.material;
                    int multiplier = (int) stackFluid.amount;
                    // Low-tiered superconductors recipe
                    if (superconductors.contains(m) && !tieredSuperconductors.contains(m)) {
                    	RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtSingle, m)).fluidInputs(fluid.getFluid(multiplier)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtSingle, m)).buildAndRegister();
                        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtDouble, m)).fluidInputs(fluid.getFluid(multiplier * 2)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtDouble, m)).buildAndRegister();
                        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtQuadruple, m)).fluidInputs(fluid.getFluid(multiplier * 4)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtQuadruple, m)).buildAndRegister();
                        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtOctal, m)).fluidInputs(fluid.getFluid(multiplier * 8)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtOctal, m)).buildAndRegister();
                        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtHex, m)).fluidInputs(fluid.getFluid(multiplier * 16)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtHex, m)).buildAndRegister();
                        for (MaterialStack stackDust : cableDusts) {
                        	Material dust = stackDust.material;
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtSingle, m), OreDictUnifier.get(OrePrefix.dustSmall, dust)).fluidInputs(fluid.getFluid(multiplier / 2)).outputs(OreDictUnifier.get(OrePrefix.cableGtSingle, m)).buildAndRegister();
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtDouble, m), OreDictUnifier.get(OrePrefix.dustSmall, dust, 2)).fluidInputs(fluid.getFluid(multiplier)).outputs(OreDictUnifier.get(OrePrefix.cableGtDouble, m)).buildAndRegister();
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtQuadruple, m), OreDictUnifier.get(OrePrefix.dustSmall, dust, 4)).fluidInputs(fluid.getFluid(multiplier * 2)).outputs(OreDictUnifier.get(OrePrefix.cableGtQuadruple, m)).buildAndRegister();
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtOctal, m), OreDictUnifier.get(OrePrefix.dustSmall, dust, 8)).fluidInputs(fluid.getFluid(multiplier * 4)).outputs(OreDictUnifier.get(OrePrefix.cableGtOctal, m)).buildAndRegister();
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtHex, m), OreDictUnifier.get(OrePrefix.dustSmall, dust, 16)).fluidInputs(fluid.getFluid(multiplier * 8)).outputs(OreDictUnifier.get(OrePrefix.cableGtHex, m)).buildAndRegister();
                        }
                    } else
                    // EV+ tiered superconductors cable recipe
                    if (tieredSuperconductors.contains(m)) {
                    	RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtSingle, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide)).fluidInputs(fluid.getFluid(multiplier)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtSingle, m)).buildAndRegister();
                        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtDouble, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide, 2)).fluidInputs(fluid.getFluid(multiplier * 2)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtDouble, m)).buildAndRegister();
                        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtQuadruple, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide, 4)).fluidInputs(fluid.getFluid(multiplier * 4)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtQuadruple, m)).buildAndRegister();
                        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtOctal, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide, 8)).fluidInputs(fluid.getFluid(multiplier * 8)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtOctal, m)).buildAndRegister();
                        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtHex, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide, 16)).fluidInputs(fluid.getFluid(multiplier * 16)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtHex, m)).buildAndRegister();
                        for (MaterialStack stackDust : cableDusts) {
                        	Material dust = stackDust.material;
                        	RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtSingle, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide), OreDictUnifier.get(OrePrefix.dustSmall, dust)).fluidInputs(fluid.getFluid(multiplier / 2)).outputs(OreDictUnifier.get(OrePrefix.cableGtSingle, m)).buildAndRegister();
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtDouble, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide, 2), OreDictUnifier.get(OrePrefix.dustSmall, dust, 2)).fluidInputs(fluid.getFluid(multiplier)).outputs(OreDictUnifier.get(OrePrefix.cableGtDouble, m)).buildAndRegister();
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtQuadruple, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide, 4), OreDictUnifier.get(OrePrefix.dustSmall, dust, 4)).fluidInputs(fluid.getFluid(multiplier * 2)).outputs(OreDictUnifier.get(OrePrefix.cableGtQuadruple, m)).buildAndRegister();
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtOctal, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide, 8), OreDictUnifier.get(OrePrefix.dustSmall, dust, 8)).fluidInputs(fluid.getFluid(multiplier * 4)).outputs(OreDictUnifier.get(OrePrefix.cableGtOctal, m)).buildAndRegister();
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtHex, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide, 16), OreDictUnifier.get(OrePrefix.dustSmall, dust, 16)).fluidInputs(fluid.getFluid(multiplier * 8)).outputs(OreDictUnifier.get(OrePrefix.cableGtHex, m)).buildAndRegister();
                        }
                    } else
                    // EV+ tiered cables recipe
                    if (tieredCables.contains(m)) {
                        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtSingle, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide)).fluidInputs(fluid.getFluid(multiplier)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtSingle, m)).buildAndRegister();
                        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtDouble, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide, 2)).fluidInputs(fluid.getFluid(multiplier * 2)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtDouble, m)).buildAndRegister();
                        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtQuadruple, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide, 4)).fluidInputs(fluid.getFluid(multiplier * 4)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtQuadruple, m)).buildAndRegister();
                        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtOctal, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide, 8)).fluidInputs(fluid.getFluid(multiplier * 8)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtOctal, m)).buildAndRegister();
                        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtHex, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide, 16)).fluidInputs(fluid.getFluid(multiplier * 16)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtHex, m)).buildAndRegister();
                        for (MaterialStack stackDust : cableDusts) {
                            Material dust = stackDust.material;
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtSingle, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide), OreDictUnifier.get(OrePrefix.dustSmall, dust)).fluidInputs(fluid.getFluid(multiplier / 2)).outputs(OreDictUnifier.get(OrePrefix.cableGtSingle, m)).buildAndRegister();
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtDouble, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide, 2), OreDictUnifier.get(OrePrefix.dustSmall, dust, 2)).fluidInputs(fluid.getFluid(multiplier)).outputs(OreDictUnifier.get(OrePrefix.cableGtDouble, m)).buildAndRegister();
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtQuadruple, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide, 4), OreDictUnifier.get(OrePrefix.dustSmall, dust, 4)).fluidInputs(fluid.getFluid(multiplier * 2)).outputs(OreDictUnifier.get(OrePrefix.cableGtQuadruple, m)).buildAndRegister();
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtOctal, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide, 8), OreDictUnifier.get(OrePrefix.dustSmall, dust, 8)).fluidInputs(fluid.getFluid(multiplier * 4)).outputs(OreDictUnifier.get(OrePrefix.cableGtOctal, m)).buildAndRegister();
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtHex, m), OreDictUnifier.get(OrePrefix.foil, Materials.PolyphenyleneSulfide, 16), OreDictUnifier.get(OrePrefix.dustSmall, dust, 16)).fluidInputs(fluid.getFluid(multiplier * 8)).outputs(OreDictUnifier.get(OrePrefix.cableGtHex, m)).buildAndRegister();
                        }
                    } else
                    // Low-tier cable recipes
                    {
                        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtSingle, m)).fluidInputs(fluid.getFluid(multiplier)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtSingle, m)).buildAndRegister();
                        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtDouble, m)).fluidInputs(fluid.getFluid(multiplier * 2)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtDouble, m)).buildAndRegister();
                        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtQuadruple, m)).fluidInputs(fluid.getFluid(multiplier * 4)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtQuadruple, m)).buildAndRegister();
                        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtOctal, m)).fluidInputs(fluid.getFluid(multiplier * 8)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtOctal, m)).buildAndRegister();
                        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtHex, m)).fluidInputs(fluid.getFluid(multiplier * 16)).circuitMeta(24).outputs(OreDictUnifier.get(OrePrefix.cableGtHex, m)).buildAndRegister();
                        for (MaterialStack stackDust : cableDusts) {
                            Material dust = stackDust.material;
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtSingle, m), OreDictUnifier.get(OrePrefix.dustSmall, dust)).fluidInputs(fluid.getFluid(multiplier / 2)).outputs(OreDictUnifier.get(OrePrefix.cableGtSingle, m)).buildAndRegister();
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtDouble, m), OreDictUnifier.get(OrePrefix.dustSmall, dust, 2)).fluidInputs(fluid.getFluid(multiplier)).outputs(OreDictUnifier.get(OrePrefix.cableGtDouble, m)).buildAndRegister();
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtQuadruple, m), OreDictUnifier.get(OrePrefix.dustSmall, dust, 4)).fluidInputs(fluid.getFluid(multiplier * 2)).outputs(OreDictUnifier.get(OrePrefix.cableGtQuadruple, m)).buildAndRegister();
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtOctal, m), OreDictUnifier.get(OrePrefix.dustSmall, dust, 8)).fluidInputs(fluid.getFluid(multiplier * 4)).outputs(OreDictUnifier.get(OrePrefix.cableGtOctal, m)).buildAndRegister();
                            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(150).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtHex, m), OreDictUnifier.get(OrePrefix.dustSmall, dust, 16)).fluidInputs(fluid.getFluid(multiplier * 8)).outputs(OreDictUnifier.get(OrePrefix.cableGtHex, m)).buildAndRegister();
                        }
                    }
                }
            }
        }
        
        ModHandler.addShapedRecipe("nputils:plasma_pipe", OreDictUnifier.get(OrePrefix.pipeMedium, NPUMaterials.Plasma), "ESE", "NTN", "ESE", 'E', "platePlastic", 'S', OreDictUnifier.get(OrePrefix.wireGtDouble, Tier.Superconductor), 'N', "plateNeodymiumMagnetic", 'T', OreDictUnifier.get(OrePrefix.pipeSmall, Materials.Titanium));
        ModHandler.addShapedRecipe("nputils:insulating_tape", NPUMetaItems.INSULATING_TAPE.getStackForm(6), "RRR", "SSS", 'R', new ItemStack(Items.PAPER), 'S', MetaItems.RUBBER_DROP.getStackForm());
        ModHandler.addShapedRecipe("nputils:magnetic_plates_set", NPUMetaItems.MAGNETICALLY_PERMEABLE_PLATE_SET.getStackForm(4), "PP", "PP", "fx", 'P', "plateElectricalSteel");
        
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(40).EUt(16).input(OrePrefix.plate, NPUMaterials.ElectricalSteel).notConsumable(new IntCircuitIngredient(0)).outputs(NPUMetaItems.MAGNETICALLY_PERMEABLE_PLATE_SET.getStackForm(2)).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(40).EUt(16).input(OrePrefix.plate, Materials.NickelZincFerrite).notConsumable(new IntCircuitIngredient(0)).outputs(NPUMetaItems.FERRITE_PLATE_SET.getStackForm(2)).buildAndRegister();
        
        if (NPUConfig.GT6.BendingPipes && NPUConfig.GT6.BendingCylinders) {
            ModHandler.removeRecipes(OreDictUnifier.get(OrePrefix.pipeSmall, Materials.Wood));
            ModHandler.removeRecipes(OreDictUnifier.get(OrePrefix.pipeMedium, Materials.Wood));
            ModHandler.addShapedRecipe("nputils:pipe_npu_wood", OreDictUnifier.get(OrePrefix.pipeMedium, Materials.Wood, 2), "PPP", "sCh", "PPP", 'P', "plankWood", 'C', "craftingToolBendingCylinder");
            ModHandler.addShapedRecipe("nputils:pipe_npu_large_wood", OreDictUnifier.get(OrePrefix.pipeLarge, Materials.Wood), "PhP", "PCP", "PsP", 'P', "plankWood", 'C', "craftingToolBendingCylinder");
            ModHandler.addShapedRecipe("nputils:pipe_npu_small_wood", OreDictUnifier.get(OrePrefix.pipeSmall, Materials.Wood, 6), "PsP", "PCP", "PhP", 'P', "plankWood", 'C', "craftingToolBendingCylinder");
        }
        
        //Pipes
        for (Material m : IngotMaterial.MATERIAL_REGISTRY) {
            if (!OreDictUnifier.get(OrePrefix.pipeMedium, m).isEmpty() && NPUConfig.GT6.BendingPipes && NPUConfig.GT6.BendingCylinders) {
                ModHandler.removeRecipeByName(new ResourceLocation(String.format("gregtech:small_%s_pipe", m.toString())));
                ModHandler.removeRecipeByName(new ResourceLocation(String.format("gregtech:medium_%s_pipe", m.toString())));
                ModHandler.removeRecipeByName(new ResourceLocation(String.format("gregtech:large_%s_pipe", m.toString())));
                if (!OreDictUnifier.get(OrePrefix.valueOf("plateCurved"), m).isEmpty()) {
                    ModHandler.addShapedRecipe(String.format("nputils:pipe_%s", m.toString()), OreDictUnifier.get(OrePrefix.pipeMedium, m, 2), "PPP", "wCh", "PPP", 'P', OreDictUnifier.get(OrePrefix.valueOf("plateCurved"), m), 'C', "craftingToolBendingCylinder");
                    ModHandler.addShapedRecipe(String.format("nputils:pipe_large_%s", m.toString()), OreDictUnifier.get(OrePrefix.pipeLarge, m), "PhP", "PCP", "PwP", 'P', OreDictUnifier.get(OrePrefix.valueOf("plateCurved"), m), 'C', "craftingToolBendingCylinder");
                    ModHandler.addShapedRecipe(String.format("nputils:pipe_small_%s", m.toString()), OreDictUnifier.get(OrePrefix.pipeSmall, m, 4), "PwP", "PCP", "PhP", 'P', OreDictUnifier.get(OrePrefix.valueOf("plateCurved"), m), 'C', "craftingToolBendingCylinder");
                }
            }
        }
        
        //Copying Molds
        for (ItemStack mold : molds)
        	RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(200).EUt(256).inputs(MetaItems.SHAPE_EMPTY.getStackForm()).notConsumable(mold).outputs(mold).buildAndRegister();
        
        //Reinforced Glass
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:ingot_mixed_metal"));
        int multiplier2;
        for (MaterialStack metal1 : firstMetal) {
            IngotMaterial material1 = (IngotMaterial) metal1.material;
            int multiplier1 = (int) metal1.amount;
            for (MaterialStack metal2 : lastMetal) {
                IngotMaterial material2 = (IngotMaterial) metal2.material;
                if ((int) metal1.amount == 1)
                    multiplier2 = 0;
                else
                    multiplier2 = (int) metal2.amount;
                ModHandler.addShapedRecipe(String.format("nputils:mixed_metal_1_%s_%s", material1.toString(), material2.toString()), MetaItems.INGOT_MIXED_METAL.getStackForm(multiplier1 + multiplier2), "F", "M", "L", 'F', new UnificationEntry(OrePrefix.plate, material1), 'M', "plateBronze", 'L', OreDictUnifier.get(OrePrefix.plate, material2));
                ModHandler.addShapedRecipe(String.format("nputils:mixed_metal_2_%s_%s", material1.toString(), material2.toString()), MetaItems.INGOT_MIXED_METAL.getStackForm(multiplier1 + multiplier2), "F", "M", "L", 'F', new UnificationEntry(OrePrefix.plate, material1), 'M', "plateBrass", 'L', OreDictUnifier.get(OrePrefix.plate, material2));

                RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(40 * multiplier1 + multiplier2 * 40).EUt(8).input(OrePrefix.plate, material1).input(OrePrefix.plank, Materials.Bronze).input(OrePrefix.plate, material2).outputs(MetaItems.INGOT_MIXED_METAL.getStackForm(multiplier1 + multiplier2)).buildAndRegister();
                RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(40 * multiplier1 + multiplier2 * 40).EUt(8).input(OrePrefix.plate, material1).input(OrePrefix.plate, Materials.Brass).input(OrePrefix.plate, material2).outputs(MetaItems.INGOT_MIXED_METAL.getStackForm(multiplier1 + multiplier2)).buildAndRegister();
            }
        }
        
        RecipeMaps.ALLOY_SMELTER_RECIPES.recipeBuilder().duration(400).EUt(4).inputs(MetaItems.ADVANCED_ALLOY_PLATE.getStackForm()).input(OrePrefix.dust, Materials.Glass, 3).outputs(NPUMetaBlocks.TRANSPARENT_CASING.getItemVariant(NPUTransparentCasing.CasingType.REINFORCED_GLASS, 4)).buildAndRegister();
        RecipeMaps.ALLOY_SMELTER_RECIPES.recipeBuilder().duration(400).EUt(4).inputs(MetaItems.ADVANCED_ALLOY_PLATE.getStackForm(), new ItemStack(Blocks.GLASS, 3)).outputs(NPUMetaBlocks.TRANSPARENT_CASING.getItemVariant(NPUTransparentCasing.CasingType.REINFORCED_GLASS, 4)).buildAndRegister();
        
        
        
        //Iridium Alloy
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:ingot_iridium_alloy"));
        ModHandler.addShapedRecipe("nputils:iridium_alloy_plate", MetaItems.INGOT_IRIDIUM_ALLOY.getStackForm(), "AIA", "IDI", "AIA", 'A', MetaItems.ADVANCED_ALLOY_PLATE.getStackForm(), 'I', "plateIridium", 'D', "plateDiamond");
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(40).EUt(8).inputs(MetaItems.ADVANCED_ALLOY_PLATE.getStackForm(4)).input(OrePrefix.plate, Materials.Iridium, 4).input(OrePrefix.plate, Materials.Diamond).outputs(MetaItems.INGOT_IRIDIUM_ALLOY.getStackForm()).buildAndRegister();
        
        //Machine Component Recipes
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(40).EUt(30).input(OrePrefix.circuit, Tier.Basic, 4).input(OrePrefix.dust, Materials.EnderPearl).input(OrePrefix.wireGtSingle, Materials.Osmium, 4).outputs(MetaItems.FIELD_GENERATOR_LV.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(40).EUt(30).input(OrePrefix.circuit, Tier.Basic, 4).input(OrePrefix.gem, Materials.EnderPearl).input(OrePrefix.wireGtSingle, Materials.Osmium, 4).outputs(MetaItems.FIELD_GENERATOR_LV.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(80).EUt(120).input(OrePrefix.circuit, Tier.Good, 4).input(OrePrefix.dust, Materials.EnderEye).input(OrePrefix.wireGtDouble, Materials.Osmium, 4).outputs(MetaItems.FIELD_GENERATOR_MV.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(80).EUt(120).input(OrePrefix.circuit, Tier.Good, 4).input(OrePrefix.gem, Materials.EnderEye).input(OrePrefix.wireGtDouble, Materials.Osmium, 4).outputs(MetaItems.FIELD_GENERATOR_MV.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(160).EUt(480).input(OrePrefix.circuit, Tier.Advanced, 4).inputs(MetaItems.QUANTUM_EYE.getStackForm()).input(OrePrefix.wireGtQuadruple, Materials.Osmium, 4).outputs(MetaItems.FIELD_GENERATOR_HV.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(320).EUt(1920).input(OrePrefix.circuit, Tier.Extreme, 4).input(OrePrefix.dust, Materials.NetherStar).input(OrePrefix.wireGtOctal, Materials.Osmium, 4).outputs(MetaItems.FIELD_GENERATOR_EV.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(320).EUt(1920).input(OrePrefix.circuit, Tier.Extreme, 4).input(OrePrefix.gem, Materials.NetherStar).input(OrePrefix.wireGtOctal, Materials.Osmium, 4).outputs(MetaItems.FIELD_GENERATOR_EV.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(640).EUt(7680).input(OrePrefix.circuit, Tier.Elite, 4).inputs(MetaItems.QUANTUM_STAR.getStackForm()).input(OrePrefix.wireGtHex, Materials.Osmium, 4).outputs(MetaItems.FIELD_GENERATOR_IV.getStackForm()).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(40).EUt(30).input(OrePrefix.circuit, Tier.Basic, 2).inputs(OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Tin, 2), OreDictUnifier.get(OrePrefix.gem, Materials.Quartzite)).input(OrePrefix.stick, Materials.Brass, 4).outputs(MetaItems.EMITTER_LV.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(80).EUt(120).input(OrePrefix.circuit, Tier.Good, 2).inputs(OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Copper, 2), OreDictUnifier.get(OrePrefix.gem, Materials.NetherQuartz)).input(OrePrefix.stick, Materials.Electrum, 4).outputs(MetaItems.EMITTER_MV.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(160).EUt(480).input(OrePrefix.circuit, Tier.Advanced, 2).inputs(OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Gold, 2), OreDictUnifier.get(OrePrefix.gem, Materials.Emerald)).input(OrePrefix.stick, Materials.Chrome, 4).outputs(MetaItems.EMITTER_HV.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(320).EUt(1920).input(OrePrefix.circuit, Tier.Extreme, 2).inputs(OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Aluminium, 2), OreDictUnifier.get(OrePrefix.gem, Materials.EnderPearl)).input(OrePrefix.stick, Materials.Platinum, 4).outputs(MetaItems.EMITTER_EV.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(640).EUt(7680).input(OrePrefix.circuit, Tier.Elite, 2).inputs(OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Tungsten, 2), OreDictUnifier.get(OrePrefix.gem, Materials.EnderEye)).input(OrePrefix.stick, Materials.Osmium, 4).outputs(MetaItems.EMITTER_IV.getStackForm()).buildAndRegister();

		if (NPUConfig.gameplay.disableNewPumpCraft) {
			for (MaterialStack stackFluid : cableFluids) {
				IngotMaterial m = (IngotMaterial) stackFluid.material;
				RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(40).EUt(30).input(OrePrefix.cableGtSingle, Materials.Tin).input(OrePrefix.screw, Materials.Tin).input(OrePrefix.rotor, Materials.Tin).input(OrePrefix.pipeMedium, Materials.Copper).inputs(MetaItems.ELECTRIC_MOTOR_LV.getStackForm()).input(OrePrefix.ring, m).outputs(MetaItems.ELECTRIC_PUMP_LV.getStackForm()).buildAndRegister();
				RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(80).EUt(120).input(OrePrefix.cableGtSingle, Materials.Copper).input(OrePrefix.screw, Materials.Bronze).input(OrePrefix.rotor, Materials.Bronze).input(OrePrefix.pipeMedium, Materials.Steel).inputs(MetaItems.ELECTRIC_MOTOR_MV.getStackForm()).input(OrePrefix.ring, m).outputs(MetaItems.ELECTRIC_PUMP_MV.getStackForm()).buildAndRegister();
				RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(160).EUt(480).input(OrePrefix.cableGtSingle, Materials.Gold).input(OrePrefix.screw, Materials.Steel).input(OrePrefix.rotor, Materials.Steel).input(OrePrefix.pipeMedium, Materials.StainlessSteel).inputs(MetaItems.ELECTRIC_MOTOR_HV.getStackForm()).input(OrePrefix.ring, m).outputs(MetaItems.ELECTRIC_PUMP_HV.getStackForm()).buildAndRegister();
				RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(320).EUt(1920).input(OrePrefix.cableGtSingle, Materials.Aluminium).input(OrePrefix.screw, Materials.StainlessSteel).input(OrePrefix.rotor, Materials.StainlessSteel).input(OrePrefix.pipeMedium, Materials.Titanium).inputs(MetaItems.ELECTRIC_MOTOR_EV.getStackForm()).input(OrePrefix.ring, m).outputs(MetaItems.ELECTRIC_PUMP_EV.getStackForm()).buildAndRegister();
				RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(640).EUt(7680).input(OrePrefix.cableGtSingle, Materials.Tungsten).input(OrePrefix.screw, Materials.TungstenSteel).input(OrePrefix.rotor, Materials.TungstenSteel).input(OrePrefix.pipeMedium, Materials.TungstenSteel).inputs(MetaItems.ELECTRIC_MOTOR_IV.getStackForm()).input(OrePrefix.ring, m).outputs(MetaItems.ELECTRIC_PUMP_IV.getStackForm()).buildAndRegister();
				
				ModHandler.addShapedRecipe(NPUtils.MODID + ":electric_pump_lv_" + m.getUnlocalizedName(), MetaItems.ELECTRIC_PUMP_LV.getStackForm(),
						"SRG", "dPw", "GMC",
						'S', OreDictUnifier.get(OrePrefix.screw, Materials.Tin),
						'R', OreDictUnifier.get(OrePrefix.rotor, Materials.Tin),
						'G', OreDictUnifier.get(OrePrefix.ring, m),
						'P', OreDictUnifier.get(OrePrefix.pipeMedium, Materials.Bronze),
						'M', MetaItems.ELECTRIC_MOTOR_LV.getStackForm(),
						'C', OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Tin));
				ModHandler.addShapedRecipe(NPUtils.MODID + ":electric_pump_mv_" + m.getUnlocalizedName(), MetaItems.ELECTRIC_PUMP_MV.getStackForm(),
						"SRG", "dPw", "GMC",
						'S', OreDictUnifier.get(OrePrefix.screw, Materials.Bronze),
						'R', OreDictUnifier.get(OrePrefix.rotor, Materials.Bronze),
						'G', OreDictUnifier.get(OrePrefix.ring, m),
						'P', OreDictUnifier.get(OrePrefix.pipeMedium, Materials.Steel),
						'M', MetaItems.ELECTRIC_MOTOR_MV.getStackForm(),
						'C', OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Copper));
				ModHandler.addShapedRecipe(NPUtils.MODID + ":electric_pump_hv_" + m.getUnlocalizedName(), MetaItems.ELECTRIC_PUMP_HV.getStackForm(),
						"SRG", "dPw", "GMC",
						'S', OreDictUnifier.get(OrePrefix.screw, Materials.Steel),
						'R', OreDictUnifier.get(OrePrefix.rotor, Materials.Steel),
						'G', OreDictUnifier.get(OrePrefix.ring, m),
						'P', OreDictUnifier.get(OrePrefix.pipeMedium, Materials.StainlessSteel),
						'M', MetaItems.ELECTRIC_MOTOR_HV.getStackForm(),
						'C', OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Gold));
				ModHandler.addShapedRecipe(NPUtils.MODID + ":electric_pump_ev_" + m.getUnlocalizedName(), MetaItems.ELECTRIC_PUMP_EV.getStackForm(),
						"SRG", "dPw", "GMC",
						'S', OreDictUnifier.get(OrePrefix.screw, Materials.StainlessSteel),
						'R', OreDictUnifier.get(OrePrefix.rotor, Materials.StainlessSteel),
						'G', OreDictUnifier.get(OrePrefix.ring, m),
						'P', OreDictUnifier.get(OrePrefix.pipeMedium, Materials.Titanium),
						'M', MetaItems.ELECTRIC_MOTOR_EV.getStackForm(),
						'C', OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Aluminium));
				ModHandler.addShapedRecipe(NPUtils.MODID + ":electric_pump_iv_" + m.getUnlocalizedName(), MetaItems.ELECTRIC_PUMP_IV.getStackForm(),
						"SRG", "dPw", "GMC",
						'S', OreDictUnifier.get(OrePrefix.screw, Materials.TungstenSteel),
						'R', OreDictUnifier.get(OrePrefix.rotor, Materials.TungstenSteel),
						'G', OreDictUnifier.get(OrePrefix.ring, m),
						'P', OreDictUnifier.get(OrePrefix.pipeMedium, Materials.TungstenSteel),
						'M', MetaItems.ELECTRIC_MOTOR_IV.getStackForm(),
						'C', OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Tungsten));
			}
		}
        
        // Coke oven recipes
        RecipeMaps.COKE_OVEN_RECIPES.add(new CokeOvenRecipe(CountableIngredient.from("gemLignite"), OreDictUnifier.get(OrePrefix.gem, NPUMaterials.LigniteCoke), Materials.Creosote.getFluid(400), 900));
        
        //Pyrolise Oven Recipes
        RecipeMaps.PYROLYSE_RECIPES.recipeBuilder()
        		.input(OrePrefix.gem, Materials.Lignite, 16)
        		.circuitMeta(0)
        		.outputs(OreDictUnifier.get(OrePrefix.gem, NPUMaterials.LigniteCoke, 20))
        		.fluidOutputs(Materials.Creosote.getFluid(8000))
        		.duration(440)
        		.EUt(96)
        		.buildAndRegister();
        
        RecipeMaps.PYROLYSE_RECIPES.recipeBuilder()
                .inputs(new ItemStack(Items.SUGAR, 23))
                .circuitMeta(1)
                .outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Charcoal, 12))
                .fluidOutputs(Materials.Water.getFluid(1500))
                .duration(640)
                .EUt(64)
                .buildAndRegister();

        RecipeMaps.PYROLYSE_RECIPES.recipeBuilder()
                .inputs(new ItemStack(Items.SUGAR, 23))
                .circuitMeta(2)
                .fluidInputs(Materials.Nitrogen.getFluid(400))
                .outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Charcoal, 12))
                .fluidOutputs(Materials.Water.getFluid(1500))
                .duration(320)
                .EUt(96)
                .buildAndRegister();
        
        //Chemical Reactor Cracking
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.Ethane.getFluid(1000)).fluidOutputs(Materials.HydroCrackedEthane.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.Ethylene.getFluid(1000)).fluidOutputs(Materials.HydroCrackedEthylene.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.Propene.getFluid(1000)).fluidOutputs(Materials.HydroCrackedPropene.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.Propane.getFluid(1000)).fluidOutputs(Materials.HydroCrackedPropane.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.LightFuel.getFluid(1000)).fluidOutputs(Materials.HydroCrackedLightFuel.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.Butane.getFluid(1000)).fluidOutputs(Materials.HydroCrackedButane.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.Naphtha.getFluid(1000)).fluidOutputs(Materials.HydroCrackedNaphtha.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.HeavyFuel.getFluid(1000)).fluidOutputs(Materials.HydroCrackedHeavyFuel.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.Gas.getFluid(1000)).fluidOutputs(Materials.HydroCrackedGas.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.Butene.getFluid(1000)).fluidOutputs(Materials.HydroCrackedButene.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Hydrogen.getFluid(2000), Materials.Butadiene.getFluid(1000)).fluidOutputs(Materials.HydroCrackedButadiene.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.Ethane.getFluid(1000)).fluidOutputs(Materials.SteamCrackedEthane.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.Ethylene.getFluid(1000)).fluidOutputs(Materials.SteamCrackedEthylene.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.Propene.getFluid(1000)).fluidOutputs(Materials.SteamCrackedPropene.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.Propane.getFluid(1000)).fluidOutputs(Materials.SteamCrackedPropane.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.LightFuel.getFluid(1000)).fluidOutputs(Materials.CrackedLightFuel.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.Butane.getFluid(1000)).fluidOutputs(Materials.SteamCrackedButane.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.Naphtha.getFluid(1000)).fluidOutputs(Materials.SteamCrackedNaphtha.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.HeavyFuel.getFluid(1000)).fluidOutputs(Materials.CrackedHeavyFuel.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.Gas.getFluid(1000)).fluidOutputs(Materials.SteamCrackedGas.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.Butene.getFluid(1000)).fluidOutputs(Materials.SteamCrackedButene.getFluid(1000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(30).fluidInputs(Materials.Steam.getFluid(2000), Materials.Butadiene.getFluid(1000)).fluidOutputs(Materials.SteamCrackedButadiene.getFluid(1000)).buildAndRegister();

        //Fish Oil
        RecipeMaps.DISTILLATION_RECIPES.recipeBuilder().duration(16).EUt(96).fluidInputs(NPUMaterials.FishOil.getFluid(24)).fluidOutputs(Materials.Lubricant.getFluid(12)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(600).EUt(30).input(OrePrefix.dustTiny, Materials.SodiumHydroxide).fluidInputs(NPUMaterials.FishOil.getFluid(6000), Materials.Methanol.getFluid(1000)).fluidOutputs(Materials.Glycerol.getFluid(1000), Materials.BioDiesel.getFluid(6000)).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(600).EUt(30).input(OrePrefix.dustTiny, Materials.SodiumHydroxide).fluidInputs(NPUMaterials.FishOil.getFluid(6000), Materials.Ethanol.getFluid(1000)).fluidOutputs(Materials.Glycerol.getFluid(1000), Materials.BioDiesel.getFluid(6000)).buildAndRegister();

        //Fluid Heater Recipes
        RecipeMaps.FLUID_HEATER_RECIPES.recipeBuilder().duration(30).EUt(24).circuitMeta(1).fluidInputs(NPUMaterials.RawGrowthMedium.getFluid(500)).fluidOutputs(NPUMaterials.SterilizedGrowthMedium.getFluid(500)).buildAndRegister();
        
        //Centrifuge recipes TODO: Убрать это нафиг
        //Lutetium
        RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder().duration(500).EUt(96).input(OrePrefix.dust, Materials.Thorium).chancedOutput(OreDictUnifier.get(OrePrefix.dustSmall, Materials.Lutetium), 1650, 100).buildAndRegister();
        
        //Sodium Hydroxide Solution
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(80).EUt(40).fluidInputs(Materials.Water.getFluid(3000)).input(OrePrefix.dust, Materials.SodiumHydroxide).fluidOutputs(NPUMaterials.SodiumHydroxideSolution.getFluid(4000)).buildAndRegister();
        RecipeMaps.ELECTROLYZER_RECIPES.recipeBuilder().duration(50).EUt(512).fluidInputs(NPUMaterials.SodiumHydroxideSolution.getFluid(4000)).fluidOutputs(Materials.Hydrogen.getFluid(2000), Materials.Oxygen.getFluid(1000)).outputs(OreDictUnifier.get(OrePrefix.dustTiny, Materials.SodiumHydroxide, 8)).buildAndRegister();
        RecipeMaps.ELECTROLYZER_RECIPES.recipeBuilder().duration(160).EUt(120).input(OrePrefix.dust, Materials.SodiumHydroxide, 2).fluidOutputs(Materials.Hydrogen.getFluid(1000), Materials.Oxygen.getFluid(1000)).outputs(OreDictUnifier.get(OrePrefix.dustTiny, Materials.Sodium, 16)).buildAndRegister();
        
        //Oil Extractor Recipes
        RecipeMaps.FLUID_EXTRACTION_RECIPES.recipeBuilder().duration(160).EUt(4).inputs(new ItemStack(Items.FISH)).fluidOutputs(NPUMaterials.FishOil.getFluid(40)).buildAndRegister();
        RecipeMaps.FLUID_EXTRACTION_RECIPES.recipeBuilder().duration(160).EUt(4).inputs(new ItemStack(Items.FISH, 1, 1)).fluidOutputs(NPUMaterials.FishOil.getFluid(60)).buildAndRegister();
        RecipeMaps.FLUID_EXTRACTION_RECIPES.recipeBuilder().duration(160).EUt(4).inputs(new ItemStack(Items.FISH, 1, 2)).fluidOutputs(NPUMaterials.FishOil.getFluid(70)).buildAndRegister();
        RecipeMaps.FLUID_EXTRACTION_RECIPES.recipeBuilder().duration(160).EUt(4).inputs(new ItemStack(Items.FISH, 1, 3)).fluidOutputs(NPUMaterials.FishOil.getFluid(30)).buildAndRegister();
        
        //Misc Blast Furnace Recipes
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(120).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.Galena).notConsumable(new IntCircuitIngredient(1)).fluidInputs(Materials.Oxygen.getFluid(3000)).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.Massicot), OreDictUnifier.get(OrePrefix.nugget, Materials.Lead, 6)).fluidOutputs(Materials.SulfurDioxide.getFluid(1000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(120).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.Stibnite).fluidInputs(Materials.Oxygen.getFluid(3000)).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.AntimonyTrioxide), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash)).fluidOutputs(Materials.SulfurDioxide.getFluid(1000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(120).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.Sphalerite).fluidInputs(Materials.Oxygen.getFluid(3000)).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.Zincite), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash)).fluidOutputs(Materials.SulfurDioxide.getFluid(1000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(120).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.Cobaltite).fluidInputs(Materials.Oxygen.getFluid(3000)).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.CobaltOxide), OreDictUnifier.get(OrePrefix.dust, NPUMaterials.ArsenicTrioxide)).fluidOutputs(Materials.SulfurDioxide.getFluid(1000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(120).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.Tetrahedrite).fluidInputs(Materials.Oxygen.getFluid(3000)).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.CupricOxide), OreDictUnifier.get(OrePrefix.dustTiny, NPUMaterials.AntimonyTrioxide, 3)).fluidOutputs(Materials.SulfurDioxide.getFluid(1000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(120).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.Chalcopyrite).fluidInputs(Materials.Oxygen.getFluid(3000)).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.CupricOxide), OreDictUnifier.get(OrePrefix.dust, NPUMaterials.Ferrosilite)).fluidOutputs(Materials.SulfurDioxide.getFluid(1000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(120).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.Pentlandite).fluidInputs(Materials.Oxygen.getFluid(3000)).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Garnierite), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash)).fluidOutputs(Materials.SulfurDioxide.getFluid(1000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(120).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.Pyrite).fluidInputs(Materials.Oxygen.getFluid(3000)).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.BandedIron), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash)).fluidOutputs(Materials.SulfurDioxide.getFluid(1000)).buildAndRegister();
        
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(240).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, NPUMaterials.Massicot, 2).input(OrePrefix.dust, Materials.Carbon).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Lead, 3), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash, 2)).fluidOutputs(Materials.CarbonDioxide.getFluid(1000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(240).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, NPUMaterials.AntimonyTrioxide, 2).input(OrePrefix.dust, Materials.Carbon).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Antimony, 3), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash, 2)).fluidOutputs(Materials.CarbonDioxide.getFluid(3000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(240).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, NPUMaterials.CobaltOxide, 2).input(OrePrefix.dust, Materials.Carbon).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Cobalt, 3), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash, 2)).fluidOutputs(Materials.CarbonDioxide.getFluid(1000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(240).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, NPUMaterials.ArsenicTrioxide, 2).input(OrePrefix.dust, Materials.Carbon).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Arsenic, 3), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash, 2)).fluidOutputs(Materials.CarbonDioxide.getFluid(1000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(240).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, NPUMaterials.CupricOxide, 2).input(OrePrefix.dust, Materials.Carbon).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Copper, 3), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash, 2)).fluidOutputs(Materials.CarbonDioxide.getFluid(1000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(240).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.Garnierite, 2).input(OrePrefix.dust, Materials.Carbon).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Nickel, 3), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash, 2)).fluidOutputs(Materials.CarbonDioxide.getFluid(1000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(240).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.BandedIron, 2).input(OrePrefix.dust, Materials.Carbon).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Iron, 3), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash, 2)).fluidOutputs(Materials.CarbonDioxide.getFluid(1000)).buildAndRegister();

        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(240).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.SiliconDioxide).input(OrePrefix.dust, Materials.Carbon, 2).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Silicon), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash)).fluidOutputs(Materials.CarbonMonoxde.getFluid(2000)).buildAndRegister();

        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(240).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.Malachite, 2).input(OrePrefix.dust, Materials.Carbon).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Copper, 3), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash, 2)).fluidOutputs(Materials.CarbonDioxide.getFluid(3000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(240).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.Magnetite, 2).input(OrePrefix.dust, Materials.Carbon).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Iron, 3), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash, 2)).fluidOutputs(Materials.CarbonDioxide.getFluid(1000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(240).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.GraniticMineralSand, 2).input(OrePrefix.dust, Materials.Carbon).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Iron, 3), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash, 2)).fluidOutputs(Materials.CarbonDioxide.getFluid(1000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(240).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.BrownLimonite, 2).input(OrePrefix.dust, Materials.Carbon).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Iron, 3), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash, 2)).fluidOutputs(Materials.CarbonDioxide.getFluid(1000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(240).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.YellowLimonite, 2).input(OrePrefix.dust, Materials.Carbon).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Iron, 3), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash, 2)).fluidOutputs(Materials.CarbonDioxide.getFluid(1000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(240).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.BasalticMineralSand, 2).input(OrePrefix.dust, Materials.Carbon).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Iron, 3), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash, 2)).fluidOutputs(Materials.CarbonDioxide.getFluid(1000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(240).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.Cassiterite, 2).input(OrePrefix.dust, Materials.Carbon).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Tin, 3), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash, 2)).fluidOutputs(Materials.CarbonDioxide.getFluid(1000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(240).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.dust, Materials.CassiteriteSand, 2).input(OrePrefix.dust, Materials.Carbon).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Tin, 3), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Ash, 2)).fluidOutputs(Materials.CarbonDioxide.getFluid(1000)).buildAndRegister();
        
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(8020).EUt(120).blastFurnaceTemp(1200).input(OrePrefix.ingot, Materials.Steel, 26).input(OrePrefix.ingot, Materials.Silicon).outputs(OreDictUnifier.get(OrePrefix.ingot, NPUMaterials.ElectricalSteel, 27)).buildAndRegister();
        
        for (MaterialStack ore : ironOres) {
            Material materials = ore.material;
            RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(500).EUt(120).blastFurnaceTemp(1500).input(OrePrefix.ore, materials).input(OrePrefix.dust, Materials.Calcite).input(OrePrefix.dust, Materials.Coal, 4).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Iron, 3), OreDictUnifier.get(OrePrefix.dustSmall, Materials.DarkAsh)).buildAndRegister();
            RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(500).EUt(120).blastFurnaceTemp(1500).input(OrePrefix.ore, materials).input(OrePrefix.dustTiny, Materials.Quicklime, 3).input(OrePrefix.dust, Materials.Coal, 3).outputs(OreDictUnifier.get(OrePrefix.ingot, Materials.Iron, 2), OreDictUnifier.get(OrePrefix.dustSmall, Materials.DarkAsh)).buildAndRegister();
        }
        
        //Mince Meat Recipes
        RecipeMaps.MACERATOR_RECIPES.recipeBuilder().duration(60).EUt(16).inputs(new ItemStack(Items.PORKCHOP)).outputs(OreDictUnifier.get(OrePrefix.dustSmall, NPUMaterials.Meat, 6)).buildAndRegister();
        RecipeMaps.MACERATOR_RECIPES.recipeBuilder().duration(60).EUt(16).inputs(new ItemStack(Items.BEEF)).outputs(OreDictUnifier.get(OrePrefix.dustSmall, NPUMaterials.Meat, 6)).buildAndRegister();
        RecipeMaps.MACERATOR_RECIPES.recipeBuilder().duration(60).EUt(16).inputs(new ItemStack(Items.RABBIT)).outputs(OreDictUnifier.get(OrePrefix.dustSmall, NPUMaterials.Meat, 6)).buildAndRegister();
        RecipeMaps.MACERATOR_RECIPES.recipeBuilder().duration(40).EUt(16).inputs(new ItemStack(Items.CHICKEN)).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.Meat)).buildAndRegister();
        RecipeMaps.MACERATOR_RECIPES.recipeBuilder().duration(40).EUt(16).inputs(new ItemStack(Items.MUTTON)).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.Meat)).buildAndRegister();
        
        // TODO optional recipe removal
        //Circuit Rabbit Hole - Layer 1
        ModHandler.removeRecipes(MetaItems.BASIC_CIRCUIT_LV.getStackForm());
        ModHandler.addShapedRecipe("nputils:basic_circuit", MetaItems.BASIC_CIRCUIT_LV.getStackForm(), "RPR", "TBT", "CCC", 'R', MetaItems.RESISTOR, 'P', "plateSteel", 'T', MetaItems.VACUUM_TUBE, 'B', NPUMetaItems.BASIC_BOARD, 'C', new UnificationEntry(OrePrefix.cableGtSingle, Materials.RedAlloy));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:good_circuit"));
        ModHandler.addShapedRecipe("nputils:good_circuit", NPUMetaItems.GOOD_CIRCUIT.getStackForm(), "WPW", "CBC", "DCD", 'P', "plateSteel", 'C', MetaItems.BASIC_CIRCUIT_LV.getStackForm(), 'W', OreDictUnifier.get(OrePrefix.wireGtSingle, Materials.Copper), 'D', MetaItems.DIODE.getStackForm(), 'B', NPUMetaItems.GOOD_PHENOLIC_BOARD);

        for (MaterialStack stack : solderingList) {
            IngotMaterial material = (IngotMaterial) stack.material;
            int multiplier = (int) stack.amount;
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(16).inputs(NPUMetaItems.BASIC_BOARD.getStackForm(), MetaItems.RESISTOR.getStackForm(2), OreDictUnifier.get(OrePrefix.wireGtSingle, Materials.RedAlloy, 2), MetaItems.VACUUM_TUBE.getStackForm(2)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.BASIC_CIRCUIT_LV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(16).inputs(NPUMetaItems.BASIC_BOARD.getStackForm(), MetaItems.SMD_RESISTOR.getStackForm(2), OreDictUnifier.get(OrePrefix.wireGtSingle, Materials.RedAlloy, 2), MetaItems.VACUUM_TUBE.getStackForm(2)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.BASIC_CIRCUIT_LV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(300).EUt(30).inputs(NPUMetaItems.GOOD_PHENOLIC_BOARD.getStackForm(), MetaItems.BASIC_CIRCUIT_LV.getStackForm(2), MetaItems.DIODE.getStackForm(2)).input(OrePrefix.wireGtSingle, Materials.Copper, 2).fluidInputs(material.getFluid(72 * multiplier)).outputs(NPUMetaItems.GOOD_CIRCUIT.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(300).EUt(30).inputs(NPUMetaItems.GOOD_PHENOLIC_BOARD.getStackForm(), MetaItems.BASIC_CIRCUIT_LV.getStackForm(2), MetaItems.SMD_DIODE.getStackForm(2)).input(OrePrefix.wireGtSingle, Materials.Copper, 2).fluidInputs(material.getFluid(72 * multiplier)).outputs(NPUMetaItems.GOOD_CIRCUIT.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(8).inputs(NPUMetaItems.BASIC_BOARD.getStackForm(), MetaItems.INTEGRATED_LOGIC_CIRCUIT.getStackForm(), MetaItems.RESISTOR.getStackForm(2), OreDictUnifier.get(OrePrefix.wireFine, Materials.Copper)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.BASIC_ELECTRONIC_CIRCUIT_LV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(8).inputs(NPUMetaItems.BASIC_BOARD.getStackForm(), MetaItems.INTEGRATED_LOGIC_CIRCUIT.getStackForm(), MetaItems.SMD_RESISTOR.getStackForm(2), OreDictUnifier.get(OrePrefix.wireFine, Materials.Copper)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.BASIC_ELECTRONIC_CIRCUIT_LV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(60).inputs(NPUMetaItems.GOOD_PLASTIC_BOARD.getStackForm(), MetaItems.CENTRAL_PROCESSING_UNIT.getStackForm(4), MetaItems.RESISTOR.getStackForm(4), MetaItems.CAPACITOR.getStackForm(4), MetaItems.TRANSISTOR.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.Copper, 2)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.ADVANCED_CIRCUIT_PARTS_LV.getStackForm(4)).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(60).inputs(NPUMetaItems.GOOD_PLASTIC_BOARD.getStackForm(), MetaItems.CENTRAL_PROCESSING_UNIT.getStackForm(4), MetaItems.SMD_RESISTOR.getStackForm(4), MetaItems.SMD_CAPACITOR.getStackForm(4), MetaItems.SMD_TRANSISTOR.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.Copper, 2)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.ADVANCED_CIRCUIT_PARTS_LV.getStackForm(4)).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(50).EUt(600).inputs(NPUMetaItems.GOOD_PLASTIC_BOARD.getStackForm(), MetaItems.SYSTEM_ON_CHIP.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.Copper, 2)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.ADVANCED_CIRCUIT_PARTS_LV.getStackForm(4)).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(16).inputs(NPUMetaItems.GOOD_PHENOLIC_BOARD.getStackForm(), MetaItems.BASIC_ELECTRONIC_CIRCUIT_LV.getStackForm(2), MetaItems.RESISTOR.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.Electrum, 8)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.GOOD_INTEGRATED_CIRCUIT_MV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(16).inputs(NPUMetaItems.GOOD_PHENOLIC_BOARD.getStackForm(), MetaItems.BASIC_ELECTRONIC_CIRCUIT_LV.getStackForm(2), MetaItems.SMD_RESISTOR.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.Electrum, 8)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.GOOD_INTEGRATED_CIRCUIT_MV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(60).inputs(NPUMetaItems.GOOD_PLASTIC_BOARD.getStackForm(), MetaItems.CENTRAL_PROCESSING_UNIT.getStackForm(), MetaItems.RESISTOR.getStackForm(2), MetaItems.CAPACITOR.getStackForm(2), MetaItems.TRANSISTOR.getStackForm(2), OreDictUnifier.get(OrePrefix.wireFine, Materials.RedAlloy, 2)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.ADVANCED_CIRCUIT_MV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(60).inputs(NPUMetaItems.GOOD_PLASTIC_BOARD.getStackForm(), MetaItems.CENTRAL_PROCESSING_UNIT.getStackForm(), MetaItems.SMD_RESISTOR.getStackForm(2), MetaItems.SMD_CAPACITOR.getStackForm(2), MetaItems.SMD_TRANSISTOR.getStackForm(2), OreDictUnifier.get(OrePrefix.wireFine, Materials.RedAlloy, 2)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.ADVANCED_CIRCUIT_MV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(50).EUt(2400).inputs(NPUMetaItems.GOOD_PLASTIC_BOARD.getStackForm(), MetaItems.SYSTEM_ON_CHIP.getStackForm(), OreDictUnifier.get(OrePrefix.wireFine, Materials.RedAlloy, 2)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.ADVANCED_CIRCUIT_MV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(640).EUt(30).inputs(MetaItems.GOOD_INTEGRATED_CIRCUIT_MV.getStackForm(2), MetaItems.INTEGRATED_LOGIC_CIRCUIT.getStackForm(3), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(), MetaItems.TRANSISTOR.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.Electrum, 16)).fluidInputs(material.getFluid(72 * multiplier)).outputs(NPUMetaItems.ADVANCED_CIRCUIT.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(640).EUt(30).inputs(MetaItems.GOOD_INTEGRATED_CIRCUIT_MV.getStackForm(2), MetaItems.INTEGRATED_LOGIC_CIRCUIT.getStackForm(3), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(), MetaItems.SMD_TRANSISTOR.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.Electrum, 16)).fluidInputs(material.getFluid(72 * multiplier)).outputs(NPUMetaItems.ADVANCED_CIRCUIT.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(90).inputs(NPUMetaItems.GOOD_PLASTIC_BOARD.getStackForm(), MetaItems.ADVANCED_CIRCUIT_MV.getStackForm(2), MetaItems.SMALL_COIL.getStackForm(4), MetaItems.CAPACITOR.getStackForm(4), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.RedAlloy, 12)).fluidInputs(material.getFluid(144 * multiplier)).outputs(MetaItems.PROCESSOR_ASSEMBLY_HV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(80).inputs(NPUMetaItems.GOOD_PLASTIC_BOARD.getStackForm(), MetaItems.ADVANCED_CIRCUIT_MV.getStackForm(2), MetaItems.SMALL_COIL.getStackForm(4), MetaItems.SMD_CAPACITOR.getStackForm(4), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.RedAlloy, 12)).fluidInputs(material.getFluid(144 * multiplier)).outputs(MetaItems.PROCESSOR_ASSEMBLY_HV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(600).inputs(NPUMetaItems.ADVANCED_BOARD.getStackForm(), MetaItems.NANO_CENTRAL_PROCESSING_UNIT.getStackForm(), MetaItems.SMD_RESISTOR.getStackForm(2), MetaItems.SMD_CAPACITOR.getStackForm(2), MetaItems.SMD_TRANSISTOR.getStackForm(2), OreDictUnifier.get(OrePrefix.wireFine, Materials.Electrum, 2)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.NANO_PROCESSOR_HV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(50).EUt(9600).inputs(NPUMetaItems.ADVANCED_BOARD.getStackForm(), MetaItems.SYSTEM_ON_CHIP.getStackForm(), OreDictUnifier.get(OrePrefix.wireFine, Materials.Electrum, 2)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.NANO_PROCESSOR_HV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(90).inputs(NPUMetaItems.GOOD_PLASTIC_BOARD.getStackForm(2), MetaItems.PROCESSOR_ASSEMBLY_HV.getStackForm(2), MetaItems.DIODE.getStackForm(4), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.Electrum, 6)).fluidInputs(material.getFluid(144 * multiplier)).outputs(NPUMetaItems.INTEGRATED_COMPUTER.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(90).inputs(NPUMetaItems.GOOD_PLASTIC_BOARD.getStackForm(2), MetaItems.PROCESSOR_ASSEMBLY_HV.getStackForm(2), MetaItems.SMD_DIODE.getStackForm(4), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.Electrum, 6)).fluidInputs(material.getFluid(144 * multiplier)).outputs(NPUMetaItems.INTEGRATED_COMPUTER.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(600).inputs(NPUMetaItems.ADVANCED_BOARD.getStackForm(), MetaItems.NANO_PROCESSOR_HV.getStackForm(2), MetaItems.SMALL_COIL.getStackForm(4), MetaItems.SMD_CAPACITOR.getStackForm(4), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.Electrum, 6)).fluidInputs(material.getFluid(144 * multiplier)).outputs(MetaItems.NANO_PROCESSOR_ASSEMBLY_EV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(2400).inputs(NPUMetaItems.EXTREME_BOARD.getStackForm(), MetaItems.QBIT_CENTRAL_PROCESSING_UNIT.getStackForm(), MetaItems.NANO_CENTRAL_PROCESSING_UNIT.getStackForm(), MetaItems.SMD_CAPACITOR.getStackForm(2), MetaItems.SMD_TRANSISTOR.getStackForm(2), OreDictUnifier.get(OrePrefix.wireFine, Materials.Platinum, 2)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.QUANTUM_PROCESSOR_EV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(50).EUt(38400).inputs(NPUMetaItems.EXTREME_BOARD.getStackForm(), MetaItems.ADVANCED_SYSTEM_ON_CHIP.getStackForm(), OreDictUnifier.get(OrePrefix.wireFine, Materials.Platinum, 2)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.QUANTUM_PROCESSOR_EV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(1600).EUt(480).inputs(OreDictUnifier.get(OrePrefix.frameGt, Materials.Aluminium), NPUMetaItems.INTEGRATED_COMPUTER.getStackForm(2), MetaItems.SMALL_COIL.getStackForm(4), MetaItems.CAPACITOR.getStackForm(24), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(16), OreDictUnifier.get(OrePrefix.wireGtSingle, Materials.AnnealedCopper, 12)).fluidInputs(material.getFluid(288 * multiplier)).outputs(NPUMetaItems.INTEGRATED_MAINFRAME.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(1600).EUt(480).inputs(OreDictUnifier.get(OrePrefix.frameGt, Materials.Aluminium), NPUMetaItems.INTEGRATED_COMPUTER.getStackForm(2), MetaItems.SMALL_COIL.getStackForm(4), MetaItems.SMD_CAPACITOR.getStackForm(24), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(16), OreDictUnifier.get(OrePrefix.wireGtSingle, Materials.AnnealedCopper, 12)).fluidInputs(material.getFluid(288 * multiplier)).outputs(NPUMetaItems.INTEGRATED_MAINFRAME.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(600).inputs(NPUMetaItems.ADVANCED_BOARD.getStackForm(2), MetaItems.NANO_PROCESSOR_ASSEMBLY_EV.getStackForm(2), MetaItems.SMD_DIODE.getStackForm(4), MetaItems.NOR_MEMORY_CHIP.getStackForm(4), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.Electrum, 6)).fluidInputs(material.getFluid(144 * multiplier)).outputs(NPUMetaItems.NANO_COMPUTER.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(2400).inputs(NPUMetaItems.EXTREME_BOARD.getStackForm(), MetaItems.QUANTUM_PROCESSOR_EV.getStackForm(2), MetaItems.SMALL_COIL.getStackForm(4), MetaItems.SMD_CAPACITOR.getStackForm(4), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.Platinum, 6)).fluidInputs(material.getFluid(144 * multiplier)).outputs(MetaItems.DATA_CONTROL_CIRCUIT_IV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(9600).inputs(NPUMetaItems.ELITE_BOARD.getStackForm(), MetaItems.CRYSTAL_CENTRAL_PROCESSING_UNIT.getStackForm(), MetaItems.NANO_CENTRAL_PROCESSING_UNIT.getStackForm(), MetaItems.SMD_CAPACITOR.getStackForm(2), MetaItems.SMD_TRANSISTOR.getStackForm(2), OreDictUnifier.get(OrePrefix.wireFine, Materials.NiobiumTitanium, 2)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.CRYSTAL_PROCESSOR_IV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(50).EUt(153600).inputs(NPUMetaItems.ELITE_BOARD.getStackForm(), MetaItems.CRYSTAL_SYSTEM_ON_CHIP.getStackForm(), OreDictUnifier.get(OrePrefix.wireFine, Materials.NiobiumTitanium, 2)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.CRYSTAL_PROCESSOR_IV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(1600).EUt(1920).inputs(OreDictUnifier.get(OrePrefix.frameGt, Materials.Aluminium), NPUMetaItems.NANO_COMPUTER.getStackForm(2), MetaItems.SMALL_COIL.getStackForm(4), MetaItems.SMD_CAPACITOR.getStackForm(24), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(16), OreDictUnifier.get(OrePrefix.wireGtSingle, Materials.AnnealedCopper, 12)).fluidInputs(material.getFluid(288 * multiplier)).outputs(NPUMetaItems.NANO_MAINFRAME.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(2400).inputs(NPUMetaItems.EXTREME_BOARD.getStackForm(2), MetaItems.DATA_CONTROL_CIRCUIT_IV.getStackForm(2), MetaItems.SMD_DIODE.getStackForm(4), MetaItems.NOR_MEMORY_CHIP.getStackForm(4), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.Platinum, 6)).fluidInputs(material.getFluid(144 * multiplier)).outputs(NPUMetaItems.QUANTUM_COMPUTER.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(9600).inputs(NPUMetaItems.ELITE_BOARD.getStackForm(), MetaItems.CRYSTAL_PROCESSOR_IV.getStackForm(2), MetaItems.SMALL_COIL.getStackForm(4), MetaItems.SMD_CAPACITOR.getStackForm(4), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.NiobiumTitanium, 6)).fluidInputs(material.getFluid(144 * multiplier)).outputs(MetaItems.ENERGY_FLOW_CIRCUIT_LUV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(38400).inputs(NPUMetaItems.NEURO_PROCESSOR.getStackForm(), MetaItems.CRYSTAL_CENTRAL_PROCESSING_UNIT.getStackForm(), MetaItems.NANO_CENTRAL_PROCESSING_UNIT.getStackForm(), MetaItems.SMD_CAPACITOR.getStackForm(2), MetaItems.SMD_TRANSISTOR.getStackForm(2), OreDictUnifier.get(OrePrefix.wireFine, Materials.YttriumBariumCuprate, 2)).fluidInputs(material.getFluid(72 * multiplier)).outputs(MetaItems.WETWARE_PROCESSOR_LUV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(1600).EUt(7680).inputs(OreDictUnifier.get(OrePrefix.frameGt, Materials.Aluminium), NPUMetaItems.QUANTUM_COMPUTER.getStackForm(2), MetaItems.SMALL_COIL.getStackForm(4), MetaItems.SMD_CAPACITOR.getStackForm(24), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(16), OreDictUnifier.get(OrePrefix.wireGtSingle, Materials.AnnealedCopper, 12)).fluidInputs(material.getFluid(288 * multiplier)).outputs(NPUMetaItems.QUANTUM_MAINFRAME.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(9600).inputs(NPUMetaItems.ELITE_BOARD.getStackForm(2), MetaItems.ENERGY_FLOW_CIRCUIT_LUV.getStackForm(2), MetaItems.SMD_DIODE.getStackForm(4), MetaItems.NOR_MEMORY_CHIP.getStackForm(4), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.NiobiumTitanium, 6)).fluidInputs(material.getFluid(144 * multiplier)).outputs(NPUMetaItems.CRYSTAL_COMPUTER.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(38400).inputs(NPUMetaItems.MASTER_BOARD.getStackForm(), MetaItems.WETWARE_PROCESSOR_LUV.getStackForm(2), MetaItems.SMALL_COIL.getStackForm(4), MetaItems.SMD_CAPACITOR.getStackForm(4), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.YttriumBariumCuprate, 6)).fluidInputs(material.getFluid(144 * multiplier)).outputs(MetaItems.WETWARE_PROCESSOR_ASSEMBLY_ZPM.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(1600).EUt(30720).inputs(OreDictUnifier.get(OrePrefix.frameGt, Materials.Aluminium), NPUMetaItems.CRYSTAL_COMPUTER.getStackForm(2), MetaItems.SMALL_COIL.getStackForm(4), MetaItems.SMD_CAPACITOR.getStackForm(24), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(16), OreDictUnifier.get(OrePrefix.wireGtSingle, NPUMaterials.LuVSuperconductor, 12)).fluidInputs(material.getFluid(288 * multiplier)).outputs(NPUMetaItems.CRYSTAL_MAINFRAME.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(38400).inputs(NPUMetaItems.MASTER_BOARD.getStackForm(2), MetaItems.WETWARE_PROCESSOR_ASSEMBLY_ZPM.getStackForm(2), MetaItems.SMD_DIODE.getStackForm(4), MetaItems.NOR_MEMORY_CHIP.getStackForm(4), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.YttriumBariumCuprate, 6)).fluidInputs(material.getFluid(144 * multiplier)).outputs(MetaItems.WETWARE_SUPER_COMPUTER_UV.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(512).EUt(1024).inputs(NPUMetaItems.EXTREME_BOARD.getStackForm(), MetaItems.POWER_INTEGRATED_CIRCUIT.getStackForm(4), MetaItems.ENGRAVED_LAPOTRON_CHIP.getStackForm(18), MetaItems.NANO_CENTRAL_PROCESSING_UNIT.getStackForm(), OreDictUnifier.get(OrePrefix.wireFine, Materials.Platinum, 16)).fluidInputs(material.getFluid(144 * multiplier)).outputs(MetaItems.ENERGY_LAPOTRONIC_ORB.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(1024).EUt(4096).inputs(NPUMetaItems.EXTREME_BOARD.getStackForm(), MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(4), MetaItems.ENERGY_LAPOTRONIC_ORB.getStackForm(8), MetaItems.QBIT_CENTRAL_PROCESSING_UNIT.getStackForm(), OreDictUnifier.get(OrePrefix.wireFine, Materials.Platinum, 16)).input(OrePrefix.plate, Materials.Europium, 4).fluidInputs(material.getFluid(144 * multiplier)).outputs(MetaItems.ENERGY_LAPOTRONIC_ORB2.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(90).inputs(NPUMetaItems.GOOD_PLASTIC_BOARD.getStackForm(), MetaItems.ADVANCED_CIRCUIT_MV.getStackForm(), MetaItems.NAND_MEMORY_CHIP.getStackForm(32), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(4), OreDictUnifier.get(OrePrefix.wireFine, Materials.RedAlloy, 8)).input(OrePrefix.plate, Materials.Plastic, 4).fluidInputs(material.getFluid(144 * multiplier)).outputs(MetaItems.TOOL_DATA_STICK.getStackForm()).buildAndRegister();
            NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(1200).inputs(NPUMetaItems.ADVANCED_BOARD.getStackForm(), MetaItems.NANO_PROCESSOR_HV.getStackForm(), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(4), MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(32), MetaItems.NAND_MEMORY_CHIP.getStackForm(64), OreDictUnifier.get(OrePrefix.wireFine, Materials.Platinum, 32)).fluidInputs(material.getFluid(144 * multiplier)).outputs(MetaItems.TOOL_DATA_ORB.getStackForm()).buildAndRegister();
        }

        //Mixer Alloying
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(100).EUt(16).fluidInputs(Materials.Copper.getFluid(432), Materials.Tin.getFluid(144)).fluidOutputs(Materials.Bronze.getFluid(576)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(100).EUt(16).fluidInputs(Materials.AnnealedCopper.getFluid(432), Materials.Tin.getFluid(144)).fluidOutputs(Materials.Bronze.getFluid(576)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(100).EUt(16).fluidInputs(Materials.Copper.getFluid(432), Materials.Zinc.getFluid(144)).fluidOutputs(Materials.Brass.getFluid(576)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(100).EUt(16).fluidInputs(Materials.AnnealedCopper.getFluid(432), Materials.Zinc.getFluid(144)).fluidOutputs(Materials.Brass.getFluid(576)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(50).EUt(16).fluidInputs(Materials.Copper.getFluid(144), Materials.Nickel.getFluid(144)).fluidOutputs(Materials.Cupronickel.getFluid(288)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(50).EUt(16).fluidInputs(Materials.AnnealedCopper.getFluid(144), Materials.Nickel.getFluid(144)).fluidOutputs(Materials.Cupronickel.getFluid(288)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(25).EUt(16).fluidInputs(Materials.Redstone.getFluid(576), Materials.Copper.getFluid(144)).fluidOutputs(Materials.RedAlloy.getFluid(144)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(25).EUt(16).fluidInputs(Materials.Redstone.getFluid(576), Materials.AnnealedCopper.getFluid(144)).fluidOutputs(Materials.RedAlloy.getFluid(144)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(50).EUt(16).fluidInputs(Materials.Iron.getFluid(144), Materials.Tin.getFluid(144)).fluidOutputs(Materials.TinAlloy.getFluid(288)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(50).EUt(16).fluidInputs(Materials.WroughtIron.getFluid(144), Materials.Tin.getFluid(144)).fluidOutputs(Materials.TinAlloy.getFluid(288)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(75).EUt(16).fluidInputs(Materials.Iron.getFluid(288), Materials.Nickel.getFluid(144)).fluidOutputs(Materials.Invar.getFluid(432)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(75).EUt(16).fluidInputs(Materials.WroughtIron.getFluid(288), Materials.Nickel.getFluid(144)).fluidOutputs(Materials.Invar.getFluid(432)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(250).EUt(16).fluidInputs(Materials.Tin.getFluid(1296), Materials.Antimony.getFluid(144)).fluidOutputs(Materials.SolderingAlloy.getFluid(1440)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(125).EUt(16).fluidInputs(Materials.Lead.getFluid(576), Materials.Antimony.getFluid(144)).fluidOutputs(Materials.BatteryAlloy.getFluid(720)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(50).EUt(16).fluidInputs(Materials.Gold.getFluid(144), Materials.Silver.getFluid(144)).fluidOutputs(Materials.Electrum.getFluid(288)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(75).EUt(16).fluidInputs(Materials.Aluminium.getFluid(288), Materials.Magnesium.getFluid(144)).fluidOutputs(Materials.Magnalium.getFluid(432)).buildAndRegister();
        
        NPURecipeMaps.CIRCUIT_ASSEMBLER_RECIPES.recipeBuilder().duration(30).EUt(4).input(OrePrefix.dust, Materials.Tantalum).input(OrePrefix.foil, Materials.Manganese).fluidInputs(Materials.Plastic.getFluid(144)).outputs(MetaItems.BATTERY_RE_ULV_TANTALUM.getStackForm(8)).buildAndRegister();
        
        //Circuit Rabbit Hole - Layer 2
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(480).inputs(NPUMetaItems.ELITE_BOARD.getStackForm(), NPUMetaItems.PETRI_DISH.getStackForm(), MetaItems.ELECTRIC_PUMP_LV.getStackForm(), MetaItems.SENSOR_LV.getStackForm()).input(OrePrefix.circuit, Tier.Good).fluidInputs(NPUMaterials.SterilizedGrowthMedium.getFluid(250)).outputs(MetaItems.WETWARE_BOARD.getStackForm()).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(30).EUt(480).fluidInputs(NPUMaterials.PositiveMatter.getFluid(10), NPUMaterials.NeutralMatter.getFluid(10)).fluidOutputs(Materials.UUMatter.getFluid(20)).buildAndRegister();

        ModHandler.removeRecipes(MetaItems.COATED_BOARD.getStackForm(3));
        ModHandler.addShapedRecipe("nputils:coated_board_shaped", MetaItems.COATED_BOARD.getStackForm(3), "RRR", "BBB", "RRR", 'R', MetaItems.RUBBER_DROP, 'B', "plateWood");
        ModHandler.addShapelessRecipe("nputils:coated_board_shapeless", MetaItems.COATED_BOARD.getStackForm(), MetaItems.RUBBER_DROP, MetaItems.RUBBER_DROP, "plateWood");
        ModHandler.addShapedRecipe("nputils:basic_board", NPUMetaItems.BASIC_BOARD.getStackForm(), "WWW", "WBW", "WWW", 'W', new UnificationEntry(OrePrefix.wireGtSingle, Materials.Copper), 'B', MetaItems.COATED_BOARD);
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(40).EUt(20).input(OrePrefix.plate, Materials.Wood).input(OrePrefix.foil, Materials.Copper, 4).fluidInputs(Materials.Glue.getFluid(72)).outputs(NPUMetaItems.BASIC_BOARD.getStackForm()).buildAndRegister();
        ModHandler.addShapedRecipe("nputils:good_board", NPUMetaItems.GOOD_PHENOLIC_BOARD.getStackForm(), "WWW", "WBW", "WWW", 'W', new UnificationEntry(OrePrefix.wireGtSingle, Materials.Gold), 'B', MetaItems.PHENOLIC_BOARD);
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(480).EUt(30).inputs(MetaItems.PHENOLIC_BOARD.getStackForm()).input(OrePrefix.foil, Materials.Gold, 4).fluidInputs(Materials.SodiumPersulfate.getFluid(200)).outputs(NPUMetaItems.GOOD_PHENOLIC_BOARD.getStackForm()).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(480).EUt(30).inputs(MetaItems.PHENOLIC_BOARD.getStackForm()).input(OrePrefix.foil, Materials.Gold, 4).fluidInputs(NPUMaterials.IronChloride.getFluid(100)).outputs(NPUMetaItems.GOOD_PHENOLIC_BOARD.getStackForm()).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(600).EUt(30).inputs(MetaItems.PLASTIC_BOARD.getStackForm()).input(OrePrefix.foil, Materials.Copper, 6).fluidInputs(Materials.SodiumPersulfate.getFluid(500)).outputs(NPUMetaItems.GOOD_PLASTIC_BOARD.getStackForm()).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(600).EUt(30).inputs(MetaItems.PLASTIC_BOARD.getStackForm()).input(OrePrefix.foil, Materials.Copper, 6).fluidInputs(NPUMaterials.IronChloride.getFluid(250)).outputs(NPUMetaItems.GOOD_PLASTIC_BOARD.getStackForm()).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(800).EUt(30).inputs(MetaItems.EPOXY_BOARD.getStackForm()).input(OrePrefix.foil, Materials.Electrum, 8).fluidInputs(Materials.SodiumPersulfate.getFluid(1000)).outputs(NPUMetaItems.ADVANCED_BOARD.getStackForm()).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(800).EUt(30).inputs(MetaItems.EPOXY_BOARD.getStackForm()).input(OrePrefix.foil, Materials.Electrum, 8).fluidInputs(NPUMaterials.IronChloride.getFluid(500)).outputs(NPUMetaItems.ADVANCED_BOARD.getStackForm()).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(1500).EUt(30).inputs(MetaItems.FIBER_BOARD.getStackForm()).input(OrePrefix.foil, Materials.AnnealedCopper, 12).fluidInputs(Materials.SodiumPersulfate.getFluid(2000)).outputs(NPUMetaItems.EXTREME_BOARD.getStackForm()).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(1500).EUt(30).inputs(MetaItems.FIBER_BOARD.getStackForm()).input(OrePrefix.foil, Materials.AnnealedCopper, 12).fluidInputs(NPUMaterials.IronChloride.getFluid(1000)).outputs(NPUMetaItems.EXTREME_BOARD.getStackForm()).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(2400).EUt(120).inputs(MetaItems.MULTILAYER_FIBER_BOARD.getStackForm()).input(OrePrefix.foil, Materials.Platinum, 16).fluidInputs(Materials.SodiumPersulfate.getFluid(4000)).outputs(NPUMetaItems.ELITE_BOARD.getStackForm()).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(2400).EUt(120).inputs(MetaItems.MULTILAYER_FIBER_BOARD.getStackForm()).input(OrePrefix.foil, Materials.Platinum, 16).fluidInputs(NPUMaterials.IronChloride.getFluid(2000)).outputs(NPUMetaItems.ELITE_BOARD.getStackForm()).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(3000).EUt(480).inputs(MetaItems.WETWARE_BOARD.getStackForm()).input(OrePrefix.foil, Materials.NiobiumTitanium, 32).fluidInputs(Materials.SodiumPersulfate.getFluid(10000)).outputs(NPUMetaItems.MASTER_BOARD.getStackForm()).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(3000).EUt(480).inputs(MetaItems.WETWARE_BOARD.getStackForm()).input(OrePrefix.foil, Materials.NiobiumTitanium, 32).fluidInputs(NPUMaterials.IronChloride.getFluid(5000)).outputs(NPUMetaItems.MASTER_BOARD.getStackForm()).buildAndRegister();

        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:diode"));
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(480).EUt(30).input(OrePrefix.wireFine, Materials.Copper, 4).input(OrePrefix.dustSmall, Materials.GalliumArsenide).fluidInputs(Materials.Glass.getFluid(288)).outputs(MetaItems.DIODE.getStackForm(2)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(480).EUt(30).input(OrePrefix.wireFine, Materials.AnnealedCopper, 4).input(OrePrefix.dustSmall, Materials.GalliumArsenide).fluidInputs(Materials.Glass.getFluid(288)).outputs(MetaItems.DIODE.getStackForm(2)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(480).EUt(30).input(OrePrefix.wireFine, Materials.Copper, 4).input(OrePrefix.dustSmall, Materials.GalliumArsenide).fluidInputs(Materials.Plastic.getFluid(144)).outputs(MetaItems.DIODE.getStackForm(4)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(480).EUt(30).input(OrePrefix.wireFine, Materials.AnnealedCopper, 4).input(OrePrefix.dustSmall, Materials.GalliumArsenide).fluidInputs(Materials.Plastic.getFluid(144)).outputs(MetaItems.DIODE.getStackForm(4)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(480).EUt(30).input(OrePrefix.wireFine, Materials.Copper, 4).inputs(MetaItems.SILICON_WAFER.getStackForm()).fluidInputs(Materials.Glass.getFluid(288)).outputs(MetaItems.DIODE.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(480).EUt(30).input(OrePrefix.wireFine, Materials.AnnealedCopper, 4).inputs(MetaItems.SILICON_WAFER.getStackForm()).fluidInputs(Materials.Glass.getFluid(288)).outputs(MetaItems.DIODE.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(480).EUt(30).input(OrePrefix.wireFine, Materials.Copper, 4).inputs(MetaItems.SILICON_WAFER.getStackForm()).fluidInputs(Materials.Plastic.getFluid(144)).outputs(MetaItems.DIODE.getStackForm(2)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(480).EUt(30).input(OrePrefix.wireFine, Materials.AnnealedCopper, 4).inputs(MetaItems.SILICON_WAFER.getStackForm()).fluidInputs(Materials.Plastic.getFluid(144)).outputs(MetaItems.DIODE.getStackForm(2)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(600).EUt(30).input(OrePrefix.wireFine, Materials.Platinum, 8).input(OrePrefix.dust, Materials.GalliumArsenide).fluidInputs(Materials.Plastic.getFluid(288)).outputs(MetaItems.SMD_DIODE.getStackForm(32)).buildAndRegister();

        ModHandler.removeRecipes(MetaItems.RESISTOR.getStackForm(3));
        for (Material m : new Material[]{Materials.Coal, Materials.Charcoal, Materials.Carbon}) {
            ModHandler.addShapedRecipe(String.format("nputils:resistor_%s", m.toString()), MetaItems.RESISTOR.getStackForm(), "RWR", "CMC", " W ", 'M', new UnificationEntry(OrePrefix.dust, m), 'R', MetaItems.RUBBER_DROP, 'W', "wireFineCopper", 'C', "wireGtSingleCopper");
            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(320).EUt(16).input(OrePrefix.dust, m).input(OrePrefix.wireFine, Materials.Copper, 4).input(OrePrefix.wireGtSingle, Materials.Copper, 4).fluidInputs(Materials.Glue.getFluid(200)).outputs(MetaItems.RESISTOR.getStackForm(8)).buildAndRegister();
            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(320).EUt(16).input(OrePrefix.dust, m).input(OrePrefix.wireFine, Materials.AnnealedCopper, 4).input(OrePrefix.wireGtSingle, Materials.Copper, 4).fluidInputs(Materials.Glue.getFluid(200)).outputs(MetaItems.RESISTOR.getStackForm(8)).buildAndRegister();
        }
        
        //Cutting Machine Recipes
        for (MaterialStack stack : sawLubricants) {
            FluidMaterial material = (FluidMaterial) stack.material;
            int multiplier = (int) stack.amount;
            int time = multiplier == 1L ? 4 : 1;
            RecipeMaps.CUTTER_RECIPES.recipeBuilder().duration(960 / time).EUt(420).inputs(MetaItems.CRYSTAL_CENTRAL_PROCESSING_UNIT.getStackForm()).fluidInputs(material.getFluid(2 * multiplier)).outputs(NPUMetaItems.RAW_CRYSTAL_CHIP.getStackForm(2)).buildAndRegister();
        }
        
        //Assembling line Casing
        ModHandler.addShapedRecipe("nputils:assemblingline_casing", NPUMetaBlocks.MULTIBLOCK_CASING.getItemVariant(NPUMultiblockCasing.CasingType.TUNGSTENSTEEL_GEARBOX_CASING, 2), "PhP", "AFA", "PwP", 'P', "plateSteel", 'A', MetaItems.ROBOT_ARM_IV.getStackForm(), 'F', OreDictUnifier.get(OrePrefix.frameGt, Materials.TungstenSteel));
        
        //Circuit Rabbit Hole - Layer 3
        RecipeMaps.ELECTROLYZER_RECIPES.recipeBuilder().duration(900).EUt(30).fluidInputs(Materials.NickelSulfateSolution.getFluid(9000)).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Nickel)).fluidOutputs(Materials.Oxygen.getFluid(1000), Materials.SulfuricAcid.getFluid(8000)).buildAndRegister();
        RecipeMaps.ELECTROLYZER_RECIPES.recipeBuilder().duration(900).EUt(30).fluidInputs(Materials.CopperSulfateSolution.getFluid(9000)).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Copper)).fluidOutputs(Materials.Oxygen.getFluid(1000), Materials.SulfuricAcid.getFluid(8000)).buildAndRegister();
        RecipeMaps.FLUID_SOLIDFICATION_RECIPES.recipeBuilder().duration(160).EUt(16).fluidInputs(Materials.Polystyrene.getFluid(36)).notConsumable(MetaItems.SHAPE_MOLD_CYLINDER.getStackForm()).outputs(NPUMetaItems.PETRI_DISH.getStackForm()).buildAndRegister();
        RecipeMaps.FLUID_SOLIDFICATION_RECIPES.recipeBuilder().duration(160).EUt(16).fluidInputs(Materials.Polytetrafluoroethylene.getFluid(36)).notConsumable(MetaItems.SHAPE_MOLD_CYLINDER.getStackForm()).outputs(NPUMetaItems.PETRI_DISH.getStackForm()).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(900).EUt(480).blastFurnaceTemp(5000).inputs(NPUMetaItems.RAW_CRYSTAL_CHIP.getStackForm()).input(OrePrefix.plate, Materials.Emerald).fluidInputs(Materials.Helium.getFluid(1000)).outputs(MetaItems.ENGRAVED_CRYSTAL_CHIP.getStackForm()).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(900).EUt(480).blastFurnaceTemp(5000).inputs(NPUMetaItems.RAW_CRYSTAL_CHIP.getStackForm()).input(OrePrefix.plate, Materials.Olivine).fluidInputs(Materials.Helium.getFluid(1000)).outputs(MetaItems.ENGRAVED_CRYSTAL_CHIP.getStackForm()).buildAndRegister();
        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder().duration(300).EUt(1024).inputs(new ItemStack(Items.EGG)).chancedOutput(NPUMetaItems.STEMCELLS.getStackForm(), 500, 750).buildAndRegister();
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(400).EUt(30).input(OrePrefix.dust, Materials.Iron).fluidInputs(Materials.HydrochloricAcid.getFluid(2000)).fluidOutputs(NPUMaterials.IronChloride.getFluid(3000), Materials.Hydrogen.getFluid(3000)).buildAndRegister();

        //Circuit Rabbit Hole - Layer 4
        RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder().duration(12000).EUt(320).inputs(OreDictUnifier.get(OrePrefix.gemExquisite, Materials.Olivine)).fluidInputs(Materials.Europium.getFluid(16)).chancedOutput(NPUMetaItems.RAW_CRYSTAL_CHIP.getStackForm(), 1000, 750).buildAndRegister();
        RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder().duration(12000).EUt(320).inputs(OreDictUnifier.get(OrePrefix.gemExquisite, Materials.Emerald)).fluidInputs(Materials.Europium.getFluid(16)).chancedOutput(NPUMetaItems.RAW_CRYSTAL_CHIP.getStackForm(), 1000, 750).buildAndRegister();
        RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder().duration(150).EUt(6).input(OrePrefix.dust, Materials.Carbon).fluidInputs(Materials.Lutetium.getFluid(1)).chancedOutput(MetaItems.CARBON_FIBERS.getStackForm(2), 3333, 750).buildAndRegister();
        RecipeMaps.LASER_ENGRAVER_RECIPES.recipeBuilder().duration(100).EUt(10000).inputs(MetaItems.ENGRAVED_CRYSTAL_CHIP.getStackForm()).notConsumable(OrePrefix.craftingLens, MarkerMaterials.Color.Lime).outputs(MetaItems.CRYSTAL_CENTRAL_PROCESSING_UNIT.getStackForm()).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(160).EUt(16).inputs(new ItemStack(Items.SUGAR, 4), OreDictUnifier.get(OrePrefix.dust, NPUMaterials.Meat), OreDictUnifier.get(OrePrefix.dustTiny, Materials.Salt)).fluidInputs(Materials.DistilledWater.getFluid(4000)).fluidOutputs(NPUMaterials.RawGrowthMedium.getFluid(4000)).buildAndRegister();
        RecipeMaps.BLAST_RECIPES.recipeBuilder().duration(9000).EUt(120).blastFurnaceTemp(1784).input(OrePrefix.dust, Materials.Silicon, 32).input(OrePrefix.dustSmall, Materials.GalliumArsenide).outputs(MetaItems.SILICON_BOULE.getStackForm()).buildAndRegister();

        
        //Power inverters & Solar panels
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(600).EUt(32).input("circuitBasic", 4).inputs(MetaTileEntities.HULL[1].getStackForm(), OreDictUnifier.get(OrePrefix.wireGtHex, Materials.RedAlloy, 4), OreDictUnifier.get(OrePrefix.wireGtQuadruple, Materials.Tin), OreDictUnifier.get(OrePrefix.plate, Materials.Steel, 8)).outputs(NPUTileEntities.POWER_INVERTER[0].getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(600).EUt(128).input("circuitGood", 4).inputs(MetaTileEntities.HULL[2].getStackForm(), OreDictUnifier.get(OrePrefix.wireGtHex, Materials.Tin, 4), OreDictUnifier.get(OrePrefix.wireGtQuadruple, Materials.Copper), NPUMetaItems.MAGNETICALLY_PERMEABLE_PLATE_SET.getStackForm(16)).outputs(NPUTileEntities.POWER_INVERTER[1].getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(600).EUt(512).input("circuitAdvanced", 4).inputs(MetaTileEntities.HULL[3].getStackForm(), OreDictUnifier.get(OrePrefix.wireGtHex, Materials.Copper, 4), OreDictUnifier.get(OrePrefix.wireGtQuadruple, Materials.Gold), NPUMetaItems.FERRITE_PLATE_SET.getStackForm(16)).outputs(NPUTileEntities.POWER_INVERTER[2].getStackForm()).buildAndRegister();
        ModHandler.addShapedRecipe("nputils:solar_panel.basic", NPUTileEntities.SOLAR_PANEL[0].getStackForm(), "PPP", "CDC", "SSS", 'P', "paneGlass", 'C', OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.RedAlloy), 'S', OreDictUnifier.get(OrePrefix.plate, Materials.Steel), 'D', OreDictUnifier.get(OrePrefix.dustSmall, Materials.GalliumArsenide));
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(60).inputs(OreDictUnifier.get(OrePrefix.plate, Materials.Silicon), OreDictUnifier.get(OrePrefix.wireFine, Materials.Copper, 2)).outputs(NPUMetaItems.POLYCRYSTALLINE_SOLAR_CELL.getStackForm()).buildAndRegister();
        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(100).EUt(130).inputs(NPUMetaItems.MONOCRYSTALL_SILICON_PLATE.getStackForm(), OreDictUnifier.get(OrePrefix.wireFine, Materials.Silver, 2)).outputs(NPUMetaItems.MONOCRYSTALLINE_SOLAR_CELL.getStackForm()).buildAndRegister();
        RecipeMaps.CUTTER_RECIPES.recipeBuilder().duration(30).EUt(60).inputs(MetaItems.SILICON_WAFER.getStackForm()).outputs(NPUMetaItems.MONOCRYSTALL_SILICON_PLATE.getStackForm()).buildAndRegister();
        for (MaterialStack stackFluid : solderingList) {
        	IngotMaterial fluid = (IngotMaterial) stackFluid.material;
            int mult = (int) stackFluid.amount;
	        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(80).input("circuitGood", 1).inputs(OreDictUnifier.get(OrePrefix.plate, Materials.Aluminium, 3), OreDictUnifier.get(OrePrefix.plate, Materials.Glass), OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Tin), NPUMetaItems.POLYCRYSTALLINE_SOLAR_CELL.getStackForm(4)).fluidInputs(fluid.getFluid(mult * 72)).outputs(NPUTileEntities.SOLAR_PANEL[1].getStackForm()).buildAndRegister();
	        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(500).input("circuitAdvanced", 1).inputs(OreDictUnifier.get(OrePrefix.plate, Materials.Plastic, 3), OreDictUnifier.get(OrePrefix.plate, Materials.Glass), OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Copper), NPUMetaItems.MONOCRYSTALLINE_SOLAR_CELL.getStackForm(4)).fluidInputs(fluid.getFluid(mult * 72)).outputs(NPUTileEntities.SOLAR_PANEL[2].getStackForm()).buildAndRegister();
        }
        
        // TODO battery supporing recipes
        // Armor
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(100).input("circuitAdvanced", 6).inputs(MetaTileEntities.STEEL_TANK.getStackForm(), MetaItems.ELECTRIC_PUMP_MV.getStackForm(2), OreDictUnifier.get(OrePrefix.pipeSmall, Materials.Plastic, 2), OreDictUnifier.get(OrePrefix.pipeMedium, Materials.Steel, 2), OreDictUnifier.get(OrePrefix.plate, Materials.Aluminium), OreDictUnifier.get(OrePrefix.screw, Materials.Aluminium, 4), OreDictUnifier.get(OrePrefix.stick, Materials.Aluminium, 2)).outputs(NPUMetaItems.SEMIFLUID_JETPACK.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(100).input("circuitAdvanced", 6).inputs(MetaItems.BATTERY_RE_MV_CADMIUM.getStackForm(6), NPUMetaItems.IMPELLER_MV.getStackForm(4), OreDictUnifier.get(OrePrefix.plate, Materials.Aluminium), OreDictUnifier.get(OrePrefix.screw, Materials.Aluminium, 4), OreDictUnifier.get(OrePrefix.stick, Materials.Aluminium, 2)).outputs(NPUMetaItems.IMPELLER_JETPACK.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(60).inputs(OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Copper), MetaItems.ELECTRIC_MOTOR_MV.getStackForm(), OreDictUnifier.get(OrePrefix.stick, Materials.Steel), OreDictUnifier.get(OrePrefix.rotor, Materials.Plastic, 2), OreDictUnifier.get(OrePrefix.pipeMedium, Materials.Plastic)).outputs(NPUMetaItems.IMPELLER_MV.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(60).inputs(OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Gold), MetaItems.ELECTRIC_MOTOR_HV.getStackForm(), OreDictUnifier.get(OrePrefix.stick, Materials.StainlessSteel), OreDictUnifier.get(OrePrefix.rotor, Materials.Plastic, 2), OreDictUnifier.get(OrePrefix.pipeMedium, Materials.Plastic)).outputs(NPUMetaItems.IMPELLER_HV.getStackForm()).buildAndRegister();
        ModHandler.addShapedRecipe("nputils:battery_pack.lv", NPUMetaItems.BATPACK_LV.getStackForm(), "BPB", "BCB", "B B", 'B', MetaItems.BATTERY_RE_LV_LITHIUM.getStackForm(), 'C', "circuitBasic", 'P', OreDictUnifier.get(OrePrefix.plate, Materials.Steel));
        ModHandler.addShapedRecipe("nputils:battery_pack.mv", NPUMetaItems.BATPACK_MV.getStackForm(), "BPB", "BCB", "B B", 'B', MetaItems.BATTERY_RE_MV_LITHIUM.getStackForm(), 'C', "circuitGood", 'P', OreDictUnifier.get(OrePrefix.plate, Materials.Aluminium));
        ModHandler.addShapedRecipe("nputils:battery_pack.hv", NPUMetaItems.BATPACK_HV.getStackForm(), "BPB", "BCB", "B B", 'B', MetaItems.BATTERY_RE_HV_LITHIUM.getStackForm(), 'C', "circuitAdvanced", 'P', OreDictUnifier.get(OrePrefix.plate, Materials.StainlessSteel));
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(800).EUt(400).input("circuitAdvanced", 4).input("circuitExtreme", 1).inputs(NPUMetaItems.BATPACK_HV.getStackForm(), NPUMetaItems.IMPELLER_HV.getStackForm(6), MetaItems.BATTERY_RE_HV_CADMIUM.getStackForm(), OreDictUnifier.get(OrePrefix.plate, Materials.Aluminium), OreDictUnifier.get(OrePrefix.screw, Materials.Aluminium, 4), OreDictUnifier.get(OrePrefix.stick, Materials.Aluminium, 2)).outputs(NPUMetaItems.ADVANCED_IMPELLER_JETPACK.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(800).EUt(400).input("circuitExtreme", 1).inputs(NPUMetaItems.BATPACK_HV.getStackForm(), NPUMetaItems.IMPELLER_JETPACK.getStackForm(), NPUMetaItems.IMPELLER_HV.getStackForm(6)).outputs(NPUMetaItems.ADVANCED_IMPELLER_JETPACK.getStackForm()).buildAndRegister();
        
        // NanoMuscle Suite
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(1200).EUt(512).input("circuitAdvanced",  1).inputs(MetaItems.CARBON_PLATE.getStackForm(7), MetaItems.BATTERY_RE_HV_LITHIUM.getStackForm()).notConsumable(new IntCircuitIngredient(0)).outputs(NPUMetaItems.NANO_MUSCLE_SUITE_CHESTPLATE.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(1200).EUt(512).input("circuitAdvanced",  1).inputs(MetaItems.CARBON_PLATE.getStackForm(6), MetaItems.BATTERY_RE_HV_LITHIUM.getStackForm()).notConsumable(new IntCircuitIngredient(1)).outputs(NPUMetaItems.NANO_MUSCLE_SUITE_LEGGINS.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(1200).EUt(512).input("circuitAdvanced",  1).inputs(MetaItems.CARBON_PLATE.getStackForm(4), MetaItems.BATTERY_RE_HV_LITHIUM.getStackForm()).notConsumable(new IntCircuitIngredient(2)).outputs(NPUMetaItems.NANO_MUSCLE_SUITE_BOOTS.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(1200).EUt(512).input("circuitAdvanced",  2).inputs(NPUMetaBlocks.TRANSPARENT_CASING.getItemVariant(CasingType.REINFORCED_GLASS), MetaItems.SENSOR_HV.getStackForm(2), MetaItems.EMITTER_HV.getStackForm(2), MetaItems.CARBON_PLATE.getStackForm(4), MetaItems.BATTERY_RE_HV_LITHIUM.getStackForm()).notConsumable(new IntCircuitIngredient(3)).outputs(NPUMetaItems.NANO_MUSCLE_SUITE_HELMET.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(1500).EUt(1024).input("circuitExtreme", 2).inputs(NPUMetaItems.NANO_MUSCLE_SUITE_CHESTPLATE.getStackForm(), NPUMetaItems.ADVANCED_IMPELLER_JETPACK.getStackForm(), NPUMetaItems.INSULATING_TAPE.getStackForm(4), MetaItems.POWER_INTEGRATED_CIRCUIT.getStackForm(4)).outputs(NPUMetaItems.ADVANCED_NANO_MUSCLE_CHESTPLATE.getStackForm()).buildAndRegister();
        
        //QuarkTech Suite
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(2400).EUt(1600).input("circuitExtreme", 2).inputs(MetaItems.LAPOTRON_CRYSTAL.getStackForm(), MetaItems.LAPOTRON_CRYSTAL.getStackForm(), MetaItems.PLATE_IRIDIUM_ALLOY.getStackForm(4), MetaItems.ELECTRIC_PISTON_EV.getStackForm(2), NPUMetaItems.NANO_MUSCLE_SUITE_BOOTS.getStackForm()).outputs(NPUMetaItems.QUARK_TECH_SUITE_BOOTS.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(2400).EUt(1600).input("circuitExtreme", 4).inputs(MetaItems.LAPOTRON_CRYSTAL.getStackForm(), MetaItems.LAPOTRON_CRYSTAL.getStackForm(), MetaItems.PLATE_IRIDIUM_ALLOY.getStackForm(6), MetaItems.CONVEYOR_MODULE_EV.getStackForm(2), NPUMetaItems.NANO_MUSCLE_SUITE_LEGGINS.getStackForm()).outputs(NPUMetaItems.QUARK_TECH_SUITE_LEGGINS.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(2400).EUt(1600).input("circuitExtreme", 4).inputs(MetaItems.LAPOTRON_CRYSTAL.getStackForm(), MetaItems.LAPOTRON_CRYSTAL.getStackForm(), MetaItems.PLATE_IRIDIUM_ALLOY.getStackForm(8), MetaItems.FIELD_GENERATOR_EV.getStackForm(2), NPUMetaItems.NANO_MUSCLE_SUITE_CHESTPLATE.getStackForm()).outputs(NPUMetaItems.QUARK_TECH_SUITE_CHESTPLATE.getStackForm()).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(2400).EUt(1600).input("circuitExtreme", 2).inputs(MetaItems.LAPOTRON_CRYSTAL.getStackForm(), MetaItems.LAPOTRON_CRYSTAL.getStackForm(), MetaItems.PLATE_IRIDIUM_ALLOY.getStackForm(4), MetaItems.SENSOR_EV.getStackForm(), MetaItems.EMITTER_EV.getStackForm(), NPUMetaItems.NANO_MUSCLE_SUITE_HELMET.getStackForm()).outputs(NPUMetaItems.QUARK_TECH_SUITE_HELMET.getStackForm()).buildAndRegister();
        NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().duration(1800).EUt(7100).inputs(MetaItems.FIELD_GENERATOR_IV.getStackForm()).inputs(MetaItems.FIELD_GENERATOR_EV.getStackForm(2)).input("circuitMaster", 4).input(OrePrefix.cableGtSingle, NPUMaterials.IVSuperconductor, 4).inputs(MetaItems.POWER_INTEGRATED_CIRCUIT.getStackForm(4)).fluidInputs(Materials.SolderingAlloy.getFluid(1152)).outputs(NPUMetaItems.GRAVITATION_ENGINE.getStackForm()).buildAndRegister();
        NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().duration(3600).EUt(8192).inputs(MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(16)).input(OrePrefix.cableGtSingle, NPUMaterials.IVSuperconductor, 8).inputs(NPUMetaItems.GRAVITATION_ENGINE.getStackForm(2)).inputs(MetaItems.PLATE_IRIDIUM_ALLOY.getStackForm(12)).input("circuitElite", 4).inputs(NPUMetaItems.QUARK_TECH_SUITE_CHESTPLATE.getStackForm()).fluidInputs(Materials.SolderingAlloy.getFluid(1152)).outputs(NPUMetaItems.ADVANCED_QAURK_TECH_SUITE_CHESTPLATE.getStackForm()).buildAndRegister();
        NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().duration(3600).EUt(8192).inputs(MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(8)).input(OrePrefix.cableGtSingle, NPUMaterials.IVSuperconductor, 8).inputs(NPUMetaItems.GRAVITATION_ENGINE.getStackForm(2)).inputs(MetaItems.PLATE_IRIDIUM_ALLOY.getStackForm(16)).input("circuitElite", 2).inputs(NPUMetaItems.ADVANCED_NANO_MUSCLE_CHESTPLATE.getStackForm()).fluidInputs(Materials.SolderingAlloy.getFluid(1152)).outputs(NPUMetaItems.ADVANCED_QAURK_TECH_SUITE_CHESTPLATE.getStackForm()).buildAndRegister();
        
        // Jetpack cleaning recipes
        // could be better, but....
        for (FuelRecipe recipe : PowerlessJetpack.fuels) {
        	if (PowerlessJetpack.forbiddenFuels.contains(recipe.getRecipeFluid().getFluid())) continue;
        	ItemStack jetpack = NPUMetaItems.SEMIFLUID_JETPACK.getStackForm();
        	IFluidHandlerItem cont = jetpack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        	if (cont == null) continue;
        	FluidStack fluid = recipe.getRecipeFluid();
        	IFluidTankProperties[] prop = cont.getTankProperties();
        	if (prop.length < 1) continue;
        	if (prop[0] == null) continue;
        	fluid.amount = prop[0].getCapacity();
        	cont.fill(fluid, true);
        	ModHandler.addShapelessRecipe("nputils:clean_jetpack_" + fluid.getUnlocalizedName(), NPUMetaItems.SEMIFLUID_JETPACK.getStackForm(), jetpack);
        }
        
        // Wallet
        ModHandler.addShapedRecipe("nputils:wallet", NPUMetaItems.COIN_WALLET.getStackForm(), " L ", "S S", " L ", 'L', new ItemStack(Items.LEATHER), 'S', new ItemStack(Items.STRING));
        
        // High pressure pipes
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(300).EUt(96).input(OrePrefix.pipeTiny, Materials.TungstenSteel).inputs(MetaItems.ELECTRIC_PUMP_EV.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.pipeTiny, NPUMaterials.HighPressurePipe)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(148).input(OrePrefix.pipeSmall, Materials.TungstenSteel).inputs(MetaItems.ELECTRIC_PUMP_EV.getStackForm(2)).outputs(OreDictUnifier.get(OrePrefix.pipeSmall, NPUMaterials.HighPressurePipe)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(600).EUt(256).input(OrePrefix.pipeMedium, Materials.TungstenSteel).inputs(MetaItems.ELECTRIC_PUMP_IV.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.pipeMedium, NPUMaterials.HighPressurePipe)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(800).EUt(512).input(OrePrefix.pipeLarge, Materials.TungstenSteel).inputs(MetaItems.ELECTRIC_PUMP_IV.getStackForm(2)).outputs(OreDictUnifier.get(OrePrefix.pipeLarge, NPUMaterials.HighPressurePipe)).buildAndRegister();
        
        NPULib.printEventFinish("Recipes was registered for  %.3f seconds", time_c, System.currentTimeMillis());
	}
	
	public static void init1() {
		long time = System.currentTimeMillis();
		//Assembling Line Recipes
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                OreDictUnifier.get(OrePrefix.stickLong, Materials.NeodymiumMagnetic, 1),
                OreDictUnifier.get(OrePrefix.stickLong, Materials.HSSG, 2),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.AnnealedCopper, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.AnnealedCopper, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.AnnealedCopper, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.AnnealedCopper, 64),
                OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.YttriumBariumCuprate, 2)).fluidInputs(
                Materials.SolderingAlloy.getFluid(144),
                Materials.Lubricant.getFluid(250))
                .outputs(MetaItems.ELECTRIC_MOTOR_LUV.getStackForm()).duration(600).EUt(10240)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                OreDictUnifier.get(OrePrefix.stickLong, Materials.NeodymiumMagnetic, 2),
                OreDictUnifier.get(OrePrefix.stickLong, Materials.HSSE, 4),
                OreDictUnifier.get(OrePrefix.ring, Materials.HSSE, 4),
                OreDictUnifier.get(OrePrefix.valueOf("round"), Materials.HSSE, 16),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Platinum, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Platinum, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Platinum, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Platinum, 64),
                OreDictUnifier.get(OrePrefix.cableGtQuadruple, Materials.VanadiumGallium, 2)).fluidInputs(
                Materials.SolderingAlloy.getFluid(288),
                Materials.Lubricant.getFluid(750))
                .outputs(MetaItems.ELECTRIC_MOTOR_ZPM.getStackForm()).duration(600).EUt(40960)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                MetaItems.ELECTRIC_MOTOR_LUV.getStackForm(),
                OreDictUnifier.get(OrePrefix.pipeMedium, NPUMaterials.Enderium, 2),
                OreDictUnifier.get(OrePrefix.plate, Materials.HSSG, 2),
                OreDictUnifier.get(OrePrefix.screw, Materials.HSSG, 8),
                OreDictUnifier.get(OrePrefix.ring, Materials.SiliconeRubber, 4),
                OreDictUnifier.get(OrePrefix.rotor, Materials.HSSG, 2),
                OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.YttriumBariumCuprate, 2)).fluidInputs(
                Materials.SolderingAlloy.getFluid(144),
                Materials.Lubricant.getFluid(250))
                .outputs(MetaItems.ELECTRIC_PUMP_LUV.getStackForm()).duration(600).EUt(15360)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                MetaItems.ELECTRIC_MOTOR_ZPM.getStackForm(),
                OreDictUnifier.get(OrePrefix.pipeMedium, Materials.Naquadah, 2),
                OreDictUnifier.get(OrePrefix.plate, Materials.HSSE, 2),
                OreDictUnifier.get(OrePrefix.screw, Materials.HSSE, 8),
                OreDictUnifier.get(OrePrefix.ring, Materials.SiliconeRubber, 16),
                OreDictUnifier.get(OrePrefix.rotor, Materials.HSSE, 2),
                OreDictUnifier.get(OrePrefix.cableGtQuadruple, Materials.VanadiumGallium, 2)).fluidInputs(
                Materials.SolderingAlloy.getFluid(288),
                Materials.Lubricant.getFluid(750))
                .outputs(MetaItems.ELECTRIC_PUMP_ZPM.getStackForm()).duration(600).EUt(61440)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                MetaItems.ELECTRIC_MOTOR_UV.getStackForm(),
                OreDictUnifier.get(OrePrefix.pipeMedium, NPUMaterials.Neutronium, 2),
                OreDictUnifier.get(OrePrefix.plate, NPUMaterials.Neutronium, 2),
                OreDictUnifier.get(OrePrefix.screw, NPUMaterials.Neutronium, 8),
                OreDictUnifier.get(OrePrefix.ring, Materials.SiliconeRubber, 16),
                OreDictUnifier.get(OrePrefix.rotor, NPUMaterials.Neutronium, 2),
                OreDictUnifier.get(OrePrefix.cableGtQuadruple, Materials.NiobiumTitanium, 2)).fluidInputs(
                Materials.SolderingAlloy.getFluid(1296),
                Materials.Lubricant.getFluid(2000))
                .outputs(MetaItems.ELECTRIC_PUMP_UV.getStackForm()).duration(600).EUt(245760)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                MetaItems.ELECTRIC_MOTOR_LUV.getStackForm(2),
                OreDictUnifier.get(OrePrefix.plate, Materials.HSSG, 2),
                OreDictUnifier.get(OrePrefix.ring, Materials.HSSG, 4),
                OreDictUnifier.get(OrePrefix.valueOf("round"), Materials.HSSG, 32),
                OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.YttriumBariumCuprate, 2))
                .notConsumable(new IntCircuitIngredient(1)).fluidInputs(
                Materials.StyreneButadieneRubber.getFluid(1440),
                Materials.Lubricant.getFluid(250))
                .outputs(MetaItems.CONVEYOR_MODULE_LUV.getStackForm()).duration(600).EUt(15360)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                MetaItems.ELECTRIC_MOTOR_ZPM.getStackForm(2),
                OreDictUnifier.get(OrePrefix.plate, Materials.HSSE, 2),
                OreDictUnifier.get(OrePrefix.ring, Materials.HSSE, 4),
                OreDictUnifier.get(OrePrefix.valueOf("round"), Materials.HSSE, 32),
                OreDictUnifier.get(OrePrefix.cableGtQuadruple, Materials.VanadiumGallium, 2))
                .notConsumable(new IntCircuitIngredient(1)).fluidInputs(
                Materials.StyreneButadieneRubber.getFluid(2880),
                Materials.Lubricant.getFluid(750))
                .outputs(MetaItems.CONVEYOR_MODULE_ZPM.getStackForm()).duration(600).EUt(61440)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                MetaItems.ELECTRIC_MOTOR_UV.getStackForm(2),
                OreDictUnifier.get(OrePrefix.plate, NPUMaterials.Neutronium, 2),
                OreDictUnifier.get(OrePrefix.ring, NPUMaterials.Neutronium, 4),
                OreDictUnifier.get(OrePrefix.valueOf("round"), NPUMaterials.Neutronium, 32),
                OreDictUnifier.get(OrePrefix.cableGtQuadruple, Materials.NiobiumTitanium, 2))
                .notConsumable(new IntCircuitIngredient(1)).fluidInputs(
                Materials.StyreneButadieneRubber.getFluid(2880),
                Materials.Lubricant.getFluid(2000))
                .outputs(MetaItems.CONVEYOR_MODULE_UV.getStackForm()).duration(600).EUt(245760)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                MetaItems.ELECTRIC_MOTOR_LUV.getStackForm(),
                OreDictUnifier.get(OrePrefix.plate, Materials.HSSG, 6),
                OreDictUnifier.get(OrePrefix.ring, Materials.HSSG, 4),
                OreDictUnifier.get(OrePrefix.valueOf("round"), Materials.HSSG, 32),
                OreDictUnifier.get(OrePrefix.stick, Materials.HSSG, 4),
                OreDictUnifier.get(OrePrefix.gear, Materials.HSSG, 1),
                OreDictUnifier.get(OrePrefix.gearSmall, Materials.HSSG, 2),
                OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.YttriumBariumCuprate, 4))
                .notConsumable(new IntCircuitIngredient(2)).fluidInputs(
                Materials.SolderingAlloy.getFluid(144),
                Materials.Lubricant.getFluid(250))
                .outputs(MetaItems.ELECTRIC_PISTON_LUV.getStackForm()).duration(600).EUt(15360)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                MetaItems.ELECTRIC_MOTOR_ZPM.getStackForm(),
                OreDictUnifier.get(OrePrefix.plate, Materials.HSSE, 6),
                OreDictUnifier.get(OrePrefix.ring, Materials.HSSE, 4),
                OreDictUnifier.get(OrePrefix.valueOf("round"), Materials.HSSE, 32),
                OreDictUnifier.get(OrePrefix.stick, Materials.HSSE, 4),
                OreDictUnifier.get(OrePrefix.gear, Materials.HSSE, 1),
                OreDictUnifier.get(OrePrefix.gearSmall, Materials.HSSE, 2),
                OreDictUnifier.get(OrePrefix.cableGtQuadruple, Materials.VanadiumGallium, 4))
                .notConsumable(new IntCircuitIngredient(2)).fluidInputs(
                Materials.SolderingAlloy.getFluid(288),
                Materials.Lubricant.getFluid(750))
                .outputs(MetaItems.ELECTRIC_PISTON_ZPM.getStackForm()).duration(600).EUt(61440)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                MetaItems.ELECTRIC_MOTOR_UV.getStackForm(),
                OreDictUnifier.get(OrePrefix.plate, NPUMaterials.Neutronium, 6),
                OreDictUnifier.get(OrePrefix.ring, NPUMaterials.Neutronium, 4),
                OreDictUnifier.get(OrePrefix.valueOf("round"), NPUMaterials.Neutronium, 32),
                OreDictUnifier.get(OrePrefix.stick, NPUMaterials.Neutronium, 4),
                OreDictUnifier.get(OrePrefix.gear, NPUMaterials.Neutronium, 1),
                OreDictUnifier.get(OrePrefix.gearSmall, NPUMaterials.Neutronium, 2),
                OreDictUnifier.get(OrePrefix.cableGtQuadruple, Materials.NiobiumTitanium, 4))
                .notConsumable(new IntCircuitIngredient(2)).fluidInputs(
                Materials.SolderingAlloy.getFluid(1296),
                Materials.Lubricant.getFluid(2000))
                .outputs(MetaItems.ELECTRIC_PISTON_UV.getStackForm()).duration(600).EUt(245760)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                OreDictUnifier.get(OrePrefix.stickLong, Materials.HSSG, 4),
                OreDictUnifier.get(OrePrefix.gear, Materials.HSSG, 1),
                OreDictUnifier.get(OrePrefix.gearSmall, Materials.HSSG, 3),
                MetaItems.ELECTRIC_MOTOR_LUV.getStackForm(2),
                MetaItems.ELECTRIC_PISTON_LUV.getStackForm(),
                OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.YttriumBariumCuprate, 6))
                .input(OrePrefix.circuit, Tier.Master, 2)
                .input(OrePrefix.circuit, Tier.Elite, 2)
                .input(OrePrefix.circuit, Tier.Extreme, 6).fluidInputs(
                Materials.SolderingAlloy.getFluid(576),
                Materials.Lubricant.getFluid(250))
                .outputs(MetaItems.ROBOT_ARM_LUV.getStackForm()).duration(600).EUt(20480)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                OreDictUnifier.get(OrePrefix.stickLong, Materials.HSSE, 4),
                OreDictUnifier.get(OrePrefix.gear, Materials.HSSE, 1),
                OreDictUnifier.get(OrePrefix.gearSmall, Materials.HSSE, 3),
                MetaItems.ELECTRIC_MOTOR_ZPM.getStackForm(2),
                MetaItems.ELECTRIC_PISTON_ZPM.getStackForm(),
                OreDictUnifier.get(OrePrefix.cableGtQuadruple, Materials.VanadiumGallium, 6))
                .input(OrePrefix.circuit, Tier.Master, 4)
                .input(OrePrefix.circuit, Tier.Elite, 4)
                .input(OrePrefix.circuit, Tier.Extreme, 12).fluidInputs(
                Materials.SolderingAlloy.getFluid(1152),
                Materials.Lubricant.getFluid(750))
                .outputs(MetaItems.ROBOT_ARM_ZPM.getStackForm()).duration(600).EUt(81920)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                OreDictUnifier.get(OrePrefix.stickLong, NPUMaterials.Neutronium, 4),
                OreDictUnifier.get(OrePrefix.gear, NPUMaterials.Neutronium, 1),
                OreDictUnifier.get(OrePrefix.gearSmall, NPUMaterials.Neutronium, 3),
                MetaItems.ELECTRIC_MOTOR_UV.getStackForm(2),
                MetaItems.ELECTRIC_PISTON_UV.getStackForm(),
                OreDictUnifier.get(OrePrefix.cableGtQuadruple, Materials.NiobiumTitanium, 6))
                .input(OrePrefix.circuit, Tier.Master, 8)
                .input(OrePrefix.circuit, Tier.Elite, 8)
                .input(OrePrefix.circuit, Tier.Extreme, 24).fluidInputs(
                Materials.SolderingAlloy.getFluid(2304),
                Materials.Lubricant.getFluid(2000))
                .outputs(MetaItems.ROBOT_ARM_UV.getStackForm()).duration(600).EUt(327680)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                OreDictUnifier.get(OrePrefix.frameGt, Materials.HSSG, 1),
                MetaItems.EMITTER_IV.getStackForm(),
                MetaItems.EMITTER_EV.getStackForm(2),
                MetaItems.EMITTER_HV.getStackForm(4),
                OreDictUnifier.get(OrePrefix.foil, Materials.Electrum, 64),
                OreDictUnifier.get(OrePrefix.foil, Materials.Electrum, 64),
                OreDictUnifier.get(OrePrefix.foil, Materials.Electrum, 64),
                OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.YttriumBariumCuprate, 7))
                .input(OrePrefix.circuit, Tier.Extreme, 7).fluidInputs(
                Materials.SolderingAlloy.getFluid(576))
                .outputs(MetaItems.EMITTER_LUV.getStackForm()).duration(600).EUt(15360)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                OreDictUnifier.get(OrePrefix.frameGt, Materials.HSSE, 1),
                MetaItems.EMITTER_LUV.getStackForm(),
                MetaItems.EMITTER_IV.getStackForm(2),
                MetaItems.EMITTER_EV.getStackForm(4),
                OreDictUnifier.get(OrePrefix.foil, Materials.Platinum, 64),
                OreDictUnifier.get(OrePrefix.foil, Materials.Platinum, 64),
                OreDictUnifier.get(OrePrefix.foil, Materials.Platinum, 64),
                OreDictUnifier.get(OrePrefix.cableGtQuadruple, Materials.VanadiumGallium, 7))
                .input(OrePrefix.circuit, Tier.Elite, 7).fluidInputs(
                Materials.SolderingAlloy.getFluid(576))
                .outputs(MetaItems.EMITTER_ZPM.getStackForm()).duration(600).EUt(61440)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                OreDictUnifier.get(OrePrefix.frameGt, NPUMaterials.Neutronium, 1),
                MetaItems.EMITTER_ZPM.getStackForm(),
                MetaItems.EMITTER_LUV.getStackForm(2),
                MetaItems.EMITTER_IV.getStackForm(4),
                OreDictUnifier.get(OrePrefix.foil, Materials.Osmiridium, 64),
                OreDictUnifier.get(OrePrefix.foil, Materials.Osmiridium, 64),
                OreDictUnifier.get(OrePrefix.foil, Materials.Osmiridium, 64),
                OreDictUnifier.get(OrePrefix.cableGtQuadruple, Materials.NiobiumTitanium, 7))
                .input(OrePrefix.circuit, Tier.Master, 7).fluidInputs(
                Materials.SolderingAlloy.getFluid(576))
                .outputs(MetaItems.EMITTER_UV.getStackForm()).duration(600).EUt(245760)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                OreDictUnifier.get(OrePrefix.frameGt, Materials.HSSG, 1),
                MetaItems.SENSOR_IV.getStackForm(),
                MetaItems.SENSOR_EV.getStackForm(2),
                MetaItems.SENSOR_HV.getStackForm(4),
                OreDictUnifier.get(OrePrefix.foil, Materials.Electrum, 64),
                OreDictUnifier.get(OrePrefix.foil, Materials.Electrum, 64),
                OreDictUnifier.get(OrePrefix.foil, Materials.Electrum, 64),
                OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.YttriumBariumCuprate, 7))
                .input(OrePrefix.circuit, Tier.Extreme, 7).fluidInputs(
                Materials.SolderingAlloy.getFluid(576))
                .outputs(MetaItems.SENSOR_LUV.getStackForm()).duration(600).EUt(15360)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                OreDictUnifier.get(OrePrefix.frameGt, Materials.HSSE, 1),
                MetaItems.SENSOR_LUV.getStackForm(),
                MetaItems.SENSOR_IV.getStackForm(2),
                MetaItems.SENSOR_EV.getStackForm(4),
                OreDictUnifier.get(OrePrefix.foil, Materials.Platinum, 64),
                OreDictUnifier.get(OrePrefix.foil, Materials.Platinum, 64),
                OreDictUnifier.get(OrePrefix.foil, Materials.Platinum, 64),
                OreDictUnifier.get(OrePrefix.cableGtQuadruple, Materials.VanadiumGallium, 7))
                .input(OrePrefix.circuit, Tier.Elite, 7).fluidInputs(
                Materials.SolderingAlloy.getFluid(576))
                .outputs(MetaItems.SENSOR_ZPM.getStackForm()).duration(600).EUt(61440)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                OreDictUnifier.get(OrePrefix.frameGt, NPUMaterials.Neutronium, 1),
                MetaItems.SENSOR_ZPM.getStackForm(),
                MetaItems.SENSOR_LUV.getStackForm(2),
                MetaItems.SENSOR_IV.getStackForm(4),
                OreDictUnifier.get(OrePrefix.foil, Materials.Osmiridium, 64),
                OreDictUnifier.get(OrePrefix.foil, Materials.Osmiridium, 64),
                OreDictUnifier.get(OrePrefix.foil, Materials.Osmiridium, 64),
                OreDictUnifier.get(OrePrefix.cableGtQuadruple, Materials.NiobiumTitanium, 7))
                .input(OrePrefix.circuit, Tier.Master, 7).fluidInputs(
                Materials.SolderingAlloy.getFluid(576))
                .outputs(MetaItems.SENSOR_UV.getStackForm()).duration(600).EUt(245760)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                OreDictUnifier.get(OrePrefix.frameGt, Materials.HSSG, 1),
                OreDictUnifier.get(OrePrefix.plate, Materials.HSSG, 6),
                MetaItems.QUANTUM_STAR.getStackForm(),
                MetaItems.EMITTER_LUV.getStackForm(4),
                NPUMetaItems.NEURO_PROCESSOR.getStackForm(8),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.YttriumBariumCuprate, 8))
                .input(OrePrefix.circuit, Tier.Master, 8)
                .input(OrePrefix.circuit, Tier.Elite, 8).fluidInputs(
                Materials.SolderingAlloy.getFluid(576))
                .outputs(MetaItems.FIELD_GENERATOR_LUV.getStackForm()).duration(600).EUt(30720)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                OreDictUnifier.get(OrePrefix.frameGt, Materials.HSSE, 1),
                OreDictUnifier.get(OrePrefix.plate, Materials.HSSE, 6),
                MetaItems.QUANTUM_STAR.getStackForm(4),
                MetaItems.EMITTER_ZPM.getStackForm(4),
                MetaItems.CRYSTAL_PROCESSOR_IV.getStackForm(16),
                NPUMetaItems.NEURO_PROCESSOR.getStackForm(16),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.cableGtQuadruple, Materials.VanadiumGallium, 8))
                .input(OrePrefix.circuit, Tier.Elite, 16).fluidInputs(
                Materials.SolderingAlloy.getFluid(1152))
                .outputs(MetaItems.FIELD_GENERATOR_ZPM.getStackForm()).duration(600).EUt(122880)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                OreDictUnifier.get(OrePrefix.frameGt, NPUMaterials.Neutronium, 1),
                OreDictUnifier.get(OrePrefix.plate, NPUMaterials.Neutronium, 6),
                MetaItems.GRAVI_STAR.getStackForm(),
                MetaItems.EMITTER_UV.getStackForm(4),
                NPUMetaItems.NEURO_PROCESSOR.getStackForm(64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.wireFine, Materials.Osmium, 64),
                OreDictUnifier.get(OrePrefix.cableGtQuadruple, Materials.NiobiumTitanium, 8)).fluidInputs(
                Materials.SolderingAlloy.getFluid(2304))
                .outputs(MetaItems.FIELD_GENERATOR_UV.getStackForm()).duration(600).EUt(491520)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                NPUMetaItems.MASTER_BOARD.getStackForm(),
                NPUMetaItems.STEMCELLS.getStackForm(8),
                MetaItems.GLASS_TUBE.getStackForm(8),
                OreDictUnifier.get(OrePrefix.foil, Materials.SiliconeRubber, 64))
                .input(OrePrefix.plate, Materials.Gold, 8)
                .input(OrePrefix.plate, Materials.StainlessSteel, 4).fluidInputs(
                NPUMaterials.SterilizedGrowthMedium.getFluid(250),
                Materials.UUMatter.getFluid(100), Materials.Water.getFluid(250), Materials.Lava.getFluid(1000))
                .outputs(NPUMetaItems.NEURO_PROCESSOR.getStackForm()).duration(200).EUt(80000)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                OreDictUnifier.get(OrePrefix.frameGt, Materials.Tritanium, 4),
                MetaItems.WETWARE_SUPER_COMPUTER_UV.getStackForm(4),
                MetaItems.SMALL_COIL.getStackForm(4),
                MetaItems.SMD_CAPACITOR.getStackForm(24),
                MetaItems.SMD_RESISTOR.getStackForm(64),
                MetaItems.SMD_TRANSISTOR.getStackForm(32),
                MetaItems.SMD_DIODE.getStackForm(16),
                MetaItems.RANDOM_ACCESS_MEMORY.getStackForm(16),
                OreDictUnifier.get(OrePrefix.wireGtSingle, Materials.Platinum, 64),
                OreDictUnifier.get(OrePrefix.foil, Materials.SiliconeRubber, 64)).fluidInputs(
                Materials.SolderingAlloy.getFluid(2880), Materials.Water.getFluid(10000))
                .outputs(MetaItems.WETWARE_MAINFRAME_MAX.getStackForm()).duration(2000).EUt(300000)
                .buildAndRegister();
		
		ItemStack last_bat = (NPUConfig.gameplay.replaceUVwithMAXBat ? NPUMetaItems.MAX_BATTERY : MetaItems.ZPM2).getStackForm();
		if (NPUConfig.gameplay.enableZPMandUVBats) {
            NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                    OreDictUnifier.get(OrePrefix.plate, Materials.Europium, 16),
                    MetaItems.WETWARE_SUPER_COMPUTER_UV.getStackForm(),
                    MetaItems.WETWARE_SUPER_COMPUTER_UV.getStackForm(),
                    MetaItems.WETWARE_SUPER_COMPUTER_UV.getStackForm(),
                    MetaItems.WETWARE_SUPER_COMPUTER_UV.getStackForm(),
                    MetaItems.ENERGY_LAPOTRONIC_ORB2.getStackForm(8),
                    MetaItems.FIELD_GENERATOR_LUV.getStackForm(2),
                    MetaItems.NANO_CENTRAL_PROCESSING_UNIT.getStackForm(64),
                    MetaItems.NANO_CENTRAL_PROCESSING_UNIT.getStackForm(64),
                    MetaItems.SMD_DIODE.getStackForm(8),
                    OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.Naquadah, 32)).fluidInputs(
                    Materials.SolderingAlloy.getFluid(2880),
                    Materials.Water.getFluid(8000))
                    .outputs(NPUMetaItems.ENERGY_MODULE.getStackForm()).duration(2000).EUt(100000)
                    .buildAndRegister();

            NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                    OreDictUnifier.get(OrePrefix.plate, Materials.Americium, 16),
                    MetaItems.WETWARE_SUPER_COMPUTER_UV.getStackForm(),
                    MetaItems.WETWARE_SUPER_COMPUTER_UV.getStackForm(),
                    MetaItems.WETWARE_SUPER_COMPUTER_UV.getStackForm(),
                    MetaItems.WETWARE_SUPER_COMPUTER_UV.getStackForm(),
                    NPUMetaItems.ENERGY_MODULE.getStackForm(8),
                    MetaItems.FIELD_GENERATOR_ZPM.getStackForm(2),
                    MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(64),
                    MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(64),
                    MetaItems.SMD_DIODE.getStackForm(16),
                    OreDictUnifier.get(OrePrefix.cableGtSingle, Materials.NaquadahAlloy, 32)).fluidInputs(
                    Materials.SolderingAlloy.getFluid(2880),
                    Materials.Water.getFluid(16000))
                    .outputs(NPUMetaItems.ENERGY_CLUSTER.getStackForm()).duration(2000).EUt(200000)
                    .buildAndRegister();

            NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                    OreDictUnifier.get(OrePrefix.plate, NPUMaterials.Neutronium, 16),
                    MetaItems.WETWARE_MAINFRAME_MAX.getStackForm(),
                    MetaItems.WETWARE_MAINFRAME_MAX.getStackForm(),
                    MetaItems.WETWARE_MAINFRAME_MAX.getStackForm(),
                    MetaItems.WETWARE_MAINFRAME_MAX.getStackForm(),
                    NPUMetaItems.ENERGY_CLUSTER.getStackForm(8),
                    MetaItems.FIELD_GENERATOR_UV.getStackForm(2),
                    MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(64),
                    MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(64),
                    MetaItems.SMD_DIODE.getStackForm(16),
                    OreDictUnifier.get(OrePrefix.wireGtSingle, Tier.Superconductor, 32)).fluidInputs(
                    Materials.SolderingAlloy.getFluid(2880),
                    Materials.Water.getFluid(16000),
                    Materials.Naquadria.getFluid(1152))
                    .outputs(last_bat).duration(2000).EUt(300000)
                    .buildAndRegister();
        } else
        	NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                    OreDictUnifier.get(OrePrefix.plate, NPUMaterials.Neutronium, 16),
                    MetaItems.WETWARE_MAINFRAME_MAX.getStackForm(),
                    MetaItems.WETWARE_MAINFRAME_MAX.getStackForm(),
                    MetaItems.WETWARE_MAINFRAME_MAX.getStackForm(),
                    MetaItems.WETWARE_MAINFRAME_MAX.getStackForm(),
                    MetaItems.ENERGY_LAPOTRONIC_ORB2.getStackForm(8),
                    MetaItems.FIELD_GENERATOR_UV.getStackForm(2),
                    MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(64),
                    MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(64),
                    MetaItems.SMD_DIODE.getStackForm(16),
                    OreDictUnifier.get(OrePrefix.wireGtSingle, Tier.Superconductor, 32)).fluidInputs(
                    Materials.SolderingAlloy.getFluid(2880), Materials.Water.getFluid(16000))
                    .outputs(last_bat).duration(2000).EUt(300000)
                    .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.FUSION_COIL),
                OreDictUnifier.get(OrePrefix.plate, Materials.Plutonium241),
                OreDictUnifier.get(OrePrefix.plate, Materials.NetherStar),
                MetaItems.FIELD_GENERATOR_IV.getStackForm(2),
                MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(32),
                OreDictUnifier.get(OrePrefix.wireGtSingle, NPUMaterials.ZPMSuperconductor, 32))
                .input(OrePrefix.circuit, Tier.Ultimate)
                .input(OrePrefix.circuit, Tier.Ultimate)
                .input(OrePrefix.circuit, Tier.Ultimate)
                .input(OrePrefix.circuit, Tier.Ultimate).fluidInputs(
                Materials.SolderingAlloy.getFluid(2880))
                .outputs(NPUTileEntities.FUSION_REACTOR[0].getStackForm()).duration(1000).EUt(30000)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.FUSION_COIL),
                OreDictUnifier.get(OrePrefix.plate, Materials.Europium, 4),
                MetaItems.FIELD_GENERATOR_LUV.getStackForm(2),
                MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(48),
                OreDictUnifier.get(OrePrefix.wireGtDouble, NPUMaterials.ZPMSuperconductor, 32))
                .input(OrePrefix.circuit, Tier.Superconductor)
                .input(OrePrefix.circuit, Tier.Superconductor)
                .input(OrePrefix.circuit, Tier.Superconductor)
                .input(OrePrefix.circuit, Tier.Superconductor).fluidInputs(
                Materials.SolderingAlloy.getFluid(2880))
                .outputs(NPUTileEntities.FUSION_REACTOR[1].getStackForm()).duration(1000).EUt(60000)
                .buildAndRegister();
		
		NPURecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder().inputs(
                MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.FUSION_COIL),
                MetaItems.WETWARE_MAINFRAME_MAX.getStackForm(),
                MetaItems.WETWARE_MAINFRAME_MAX.getStackForm(),
                MetaItems.WETWARE_MAINFRAME_MAX.getStackForm(),
                MetaItems.WETWARE_MAINFRAME_MAX.getStackForm(),
                OreDictUnifier.get(OrePrefix.plate, Materials.Americium, 4),
                MetaItems.FIELD_GENERATOR_ZPM.getStackForm(2),
                MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT.getStackForm(64),
                OreDictUnifier.get(OrePrefix.wireGtDouble, Tier.Superconductor, 32)).fluidInputs(
                Materials.SolderingAlloy.getFluid(2880))
                .outputs(NPUTileEntities.FUSION_REACTOR[2].getStackForm()).duration(1000).EUt(90000)
                .buildAndRegister();
		
		//Star Recipes
		RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(12000).EUt(8).input(OrePrefix.ingot, Materials.Plutonium, 8).input(OrePrefix.dustTiny, Materials.Uranium).fluidInputs(Materials.Air.getFluid(1000)).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Plutonium, 8)).fluidOutputs(Materials.Radon.getFluid(100)).buildAndRegister();
        RecipeMaps.AUTOCLAVE_RECIPES.recipeBuilder().duration(480).EUt(7680).inputs(new ItemStack(Items.NETHER_STAR)).fluidInputs(NPUMaterials.Neutronium.getFluid(288)).outputs(MetaItems.GRAVI_STAR.getStackForm()).buildAndRegister();
		
      //Fusion Recipes
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Deuterium.getFluid(125), Materials.Tritium.getFluid(125)).fluidOutputs(Materials.Helium.getPlasma(125)).duration(16).EUt(4096).EUToStart(400000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Deuterium.getFluid(125), Materials.Helium3.getFluid(125)).fluidOutputs(Materials.Helium.getPlasma(125)).duration(16).EUt(2048).EUToStart(600000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Carbon.getFluid(125), Materials.Helium3.getFluid(125)).fluidOutputs(Materials.Oxygen.getPlasma(125)).duration(32).EUt(4096).EUToStart(800000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Beryllium.getFluid(16), Materials.Deuterium.getFluid(375)).fluidOutputs(Materials.Nitrogen.getPlasma(175)).duration(16).EUt(16384).EUToStart(1800000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Silicon.getFluid(16), Materials.Magnesium.getFluid(16)).fluidOutputs(Materials.Iron.getPlasma(125)).duration(32).EUt(8192).EUToStart(3600000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Potassium.getFluid(16), Materials.Fluorine.getFluid(125)).fluidOutputs(Materials.Nickel.getPlasma(125)).duration(16).EUt(32768).EUToStart(4800000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Beryllium.getFluid(16), Materials.Tungsten.getFluid(16)).fluidOutputs(Materials.Platinum.getFluid(16)).duration(32).EUt(32768).EUToStart(1500000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Neodymium.getFluid(16), Materials.Hydrogen.getFluid(48)).fluidOutputs(Materials.Europium.getFluid(16)).duration(64).EUt(24576).EUToStart(1500000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Lutetium.getFluid(16), Materials.Chrome.getFluid(16)).fluidOutputs(Materials.Americium.getFluid(16)).duration(96).EUt(49152).EUToStart(2000000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Plutonium.getFluid(16), Materials.Thorium.getFluid(16)).fluidOutputs(Materials.Naquadah.getFluid(16)).duration(64).EUt(32768).EUToStart(3000000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Americium.getFluid(16), Materials.Naquadria.getFluid(16)).fluidOutputs(NPUMaterials.Neutronium.getFluid(2)).duration(200).EUt(98304).EUToStart(6000000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Tungsten.getFluid(16), Materials.Helium.getFluid(16)).fluidOutputs(Materials.Osmium.getFluid(16)).duration(64).EUt(24578).EUToStart(1500000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Manganese.getFluid(16), Materials.Hydrogen.getFluid(16)).fluidOutputs(Materials.Iron.getFluid(16)).duration(64).EUt(8192).EUToStart(1200000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Mercury.getFluid(16), Materials.Magnesium.getFluid(16)).fluidOutputs(Materials.Uranium.getFluid(16)).duration(64).EUt(49152).EUToStart(2400000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Gold.getFluid(16), Materials.Aluminium.getFluid(16)).fluidOutputs(Materials.Uranium.getFluid(16)).duration(64).EUt(49152).EUToStart(2400000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Uranium.getFluid(16), Materials.Helium.getFluid(16)).fluidOutputs(Materials.Plutonium.getFluid(16)).duration(128).EUt(49152).EUToStart(4800000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Vanadium.getFluid(16), Materials.Hydrogen.getFluid(125)).fluidOutputs(Materials.Chrome.getFluid(16)).duration(64).EUt(24576).EUToStart(1400000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Gallium.getFluid(16), Materials.Radon.getFluid(125)).fluidOutputs(Materials.Duranium.getFluid(16)).duration(64).EUt(16384).EUToStart(1400000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Titanium.getFluid(48), Materials.Duranium.getFluid(32)).fluidOutputs(Materials.Tritanium.getFluid(16)).duration(64).EUt(32768).EUToStart(2000000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Gold.getFluid(16), Materials.Mercury.getFluid(16)).fluidOutputs(Materials.Radon.getFluid(125)).duration(64).EUt(32768).EUToStart(2000000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Tantalum.getFluid(16), Materials.Tritium.getFluid(16)).fluidOutputs(Materials.Tungsten.getFluid(16)).duration(16).EUt(24576).EUToStart(2000000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Silver.getFluid(16), Materials.Lithium.getFluid(16)).fluidOutputs(Materials.Indium.getFluid(16)).duration(32).EUt(24576).EUToStart(3800000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.NaquadahEnriched.getFluid(15), Materials.Radon.getFluid(125)).fluidOutputs(Materials.Naquadria.getFluid(3)).duration(64).EUt(49152).EUToStart(4000000).buildAndRegister();
        RecipeMaps.FUSION_RECIPES.recipeBuilder().fluidInputs(Materials.Lithium.getFluid(16), Materials.Tungsten.getFluid(16)).fluidOutputs(Materials.Iridium.getFluid(16)).duration(32).EUt(32768).EUToStart(3000000).buildAndRegister();
        
        //Fusion Casing Recipes
        ModHandler.addShapedRecipe("fusion_casing_1", MetaBlocks.MUTLIBLOCK_CASING.getItemVariant(MultiblockCasingType.FUSION_CASING), "PhP", "PHP", "PwP", 'P', "plateTungstenSteel", 'H', MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.LuV));
        ModHandler.addShapedRecipe("fusion_casing_2", MetaBlocks.MUTLIBLOCK_CASING.getItemVariant(MultiblockCasingType.FUSION_CASING_MK2), "PhP", "PHP", "PwP", 'P', "plateAmericium", 'H', MetaBlocks.MUTLIBLOCK_CASING.getItemVariant(MultiblockCasingType.FUSION_CASING));
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().EUt(16).inputs(MetaBlocks.MUTLIBLOCK_CASING.getItemVariant(MultiblockCasingType.FUSION_CASING)).input(OrePrefix.plate, Materials.Americium, 6).outputs(MetaBlocks.MUTLIBLOCK_CASING.getItemVariant(MultiblockCasingType.FUSION_CASING_MK2)).duration(50).buildAndRegister();
        
        ModHandler.addShapedRecipe("nputils:fusion_coil", MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.FUSION_COIL), "CRC", "FSF", "CRC", 'C', "circuitMaster", 'R', MetaItems.NEUTRON_REFLECTOR.getStackForm(), 'F', MetaItems.FIELD_GENERATOR_MV.getStackForm(), 'S', MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.SUPERCONDUCTOR));
        
        //Explosive Recipes
        ModHandler.removeRecipes(new ItemStack(Blocks.TNT));
        ModHandler.removeRecipes(MetaItems.DYNAMITE.getStackForm());
        RecipeMaps.CHEMICAL_RECIPES.recipeBuilder().duration(160).EUt(4).inputs(new ItemStack(Items.PAPER), new ItemStack(Items.STRING)).fluidInputs(Materials.Glyceryl.getFluid(500)).outputs(MetaItems.DYNAMITE.getStackForm()).buildAndRegister();
        
        //Electromagnetic Separator Recipes
        RecipeMaps.ELECTROMAGNETIC_SEPARATOR_RECIPES.recipeBuilder().duration(400).EUt(24).input(OrePrefix.dustPure, Materials.BrownLimonite).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.BrownLimonite)).chancedOutput(OreDictUnifier.get(OrePrefix.dustSmall, Materials.Iron), 4000, 750).chancedOutput(OreDictUnifier.get(OrePrefix.nugget, Materials.Iron), 2000, 750).buildAndRegister();
        RecipeMaps.ELECTROMAGNETIC_SEPARATOR_RECIPES.recipeBuilder().duration(400).EUt(24).input(OrePrefix.dustPure, Materials.YellowLimonite).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.YellowLimonite)).chancedOutput(OreDictUnifier.get(OrePrefix.dustSmall, Materials.Iron), 4000, 750).chancedOutput(OreDictUnifier.get(OrePrefix.nugget, Materials.Iron), 2000, 750).buildAndRegister();
        RecipeMaps.ELECTROMAGNETIC_SEPARATOR_RECIPES.recipeBuilder().duration(400).EUt(24).input(OrePrefix.dustPure, Materials.Nickel).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Nickel)).chancedOutput(OreDictUnifier.get(OrePrefix.dustSmall, Materials.Iron), 4000, 750).chancedOutput(OreDictUnifier.get(OrePrefix.nugget, Materials.Iron), 2000, 750).buildAndRegister();
        RecipeMaps.ELECTROMAGNETIC_SEPARATOR_RECIPES.recipeBuilder().duration(400).EUt(24).input(OrePrefix.dustPure, Materials.Pentlandite).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Pentlandite)).chancedOutput(OreDictUnifier.get(OrePrefix.dustSmall, Materials.Iron), 4000, 750).chancedOutput(OreDictUnifier.get(OrePrefix.nugget, Materials.Iron), 2000, 750).buildAndRegister();
        RecipeMaps.ELECTROMAGNETIC_SEPARATOR_RECIPES.recipeBuilder().duration(400).EUt(24).input(OrePrefix.dustPure, Materials.BandedIron).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.BandedIron)).chancedOutput(OreDictUnifier.get(OrePrefix.dustSmall, Materials.Iron), 4000, 750).chancedOutput(OreDictUnifier.get(OrePrefix.nugget, Materials.Iron), 2000, 750).buildAndRegister();
        RecipeMaps.ELECTROMAGNETIC_SEPARATOR_RECIPES.recipeBuilder().duration(400).EUt(24).input(OrePrefix.dustPure, Materials.Ilmenite).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Ilmenite)).chancedOutput(OreDictUnifier.get(OrePrefix.dustSmall, Materials.Iron), 4000, 750).chancedOutput(OreDictUnifier.get(OrePrefix.nugget, Materials.Iron), 2000, 750).buildAndRegister();
        RecipeMaps.ELECTROMAGNETIC_SEPARATOR_RECIPES.recipeBuilder().duration(400).EUt(24).input(OrePrefix.dustPure, Materials.Pyrite).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Pyrite)).chancedOutput(OreDictUnifier.get(OrePrefix.dustSmall, Materials.Iron), 4000, 750).chancedOutput(OreDictUnifier.get(OrePrefix.nugget, Materials.Iron), 2000, 750).buildAndRegister();
        RecipeMaps.ELECTROMAGNETIC_SEPARATOR_RECIPES.recipeBuilder().duration(400).EUt(24).input(OrePrefix.dustPure, Materials.Tin).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Tin)).chancedOutput(OreDictUnifier.get(OrePrefix.dustSmall, Materials.Iron), 4000, 750).chancedOutput(OreDictUnifier.get(OrePrefix.nugget, Materials.Iron), 2000, 750).buildAndRegister();
        RecipeMaps.ELECTROMAGNETIC_SEPARATOR_RECIPES.recipeBuilder().duration(400).EUt(24).input(OrePrefix.dustPure, Materials.Chromite).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Chromite)).chancedOutput(OreDictUnifier.get(OrePrefix.dustSmall, Materials.Iron), 4000, 750).chancedOutput(OreDictUnifier.get(OrePrefix.nugget, Materials.Iron), 2000, 750).buildAndRegister();
        RecipeMaps.ELECTROMAGNETIC_SEPARATOR_RECIPES.recipeBuilder().duration(400).EUt(24).input(OrePrefix.dustPure, Materials.Monazite).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Monazite)).chancedOutput(OreDictUnifier.get(OrePrefix.dustSmall, Materials.Neodymium), 4000, 750).chancedOutput(OreDictUnifier.get(OrePrefix.nugget, Materials.Neodymium), 2000, 750).buildAndRegister();
        RecipeMaps.ELECTROMAGNETIC_SEPARATOR_RECIPES.recipeBuilder().duration(400).EUt(24).input(OrePrefix.dustPure, Materials.Bastnasite).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Bastnasite)).chancedOutput(OreDictUnifier.get(OrePrefix.dustSmall, Materials.Neodymium), 4000, 750).chancedOutput(OreDictUnifier.get(OrePrefix.nugget, Materials.Neodymium), 2000, 750).buildAndRegister();
        RecipeMaps.ELECTROMAGNETIC_SEPARATOR_RECIPES.recipeBuilder().duration(400).EUt(24).input(OrePrefix.dustPure, Materials.VanadiumMagnetite).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.VanadiumMagnetite)).chancedOutput(OreDictUnifier.get(OrePrefix.dustSmall, Materials.Gold), 4000, 750).chancedOutput(OreDictUnifier.get(OrePrefix.nugget, Materials.Gold), 2000, 750).buildAndRegister();
        RecipeMaps.ELECTROMAGNETIC_SEPARATOR_RECIPES.recipeBuilder().duration(400).EUt(24).input(OrePrefix.dustPure, Materials.Magnetite).outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Magnetite)).chancedOutput(OreDictUnifier.get(OrePrefix.dustSmall, Materials.Gold), 4000, 750).chancedOutput(OreDictUnifier.get(OrePrefix.nugget, Materials.Gold), 2000, 750).buildAndRegister();
        
        //Lapotron Crystal Recipes
        ModHandler.removeRecipes(MetaItems.LAPOTRON_CRYSTAL.getStackForm());
        for (MaterialStack m : lapisLike) {
            GemMaterial gem = (GemMaterial) m.material;
            ModHandler.addShapedRecipe(String.format("nputils:lapotron_crystal_%s", gem.toString()), MetaItems.LAPOTRON_CRYSTAL.getStackForm(), "PCP", "RFR", "PCP", 'P', new UnificationEntry(OrePrefix.plate, gem), 'C', "circuitAdvanced", 'R', OreDictUnifier.get(OrePrefix.stick, gem), 'F', OreDictUnifier.get(OrePrefix.gemFlawless, Materials.Sapphire));
            ModHandler.addShapedRecipe(String.format("nputils:lapotron_crystal_with_crystal_%s", gem.toString()), MetaItems.LAPOTRON_CRYSTAL.getStackForm(), "PCP", "RFR", "PCP", 'P', new UnificationEntry(OrePrefix.plate, gem), 'C', "circuitExtreme", 'R', OreDictUnifier.get(OrePrefix.stick, gem), 'F', MetaItems.ENERGY_CRYSTAL.getStackForm());
            ModHandler.addShapelessRecipe(String.format("nputils:lapotron_crystal_shapeless_%s", gem.toString()), MetaItems.LAPOTRON_CRYSTAL.getStackForm(), OreDictUnifier.get(OrePrefix.gemExquisite, Materials.Sapphire), OreDictUnifier.get(OrePrefix.stick, gem), MetaItems.SMD_CAPACITOR.getStackForm());
        }
        
        //Configuration Circuit
        ModHandler.removeRecipes(MetaItems.BASIC_ELECTRONIC_CIRCUIT_LV.getStackForm());
        ModHandler.removeRecipes(MetaItems.INTEGRATED_CIRCUIT.getStackForm());
        ModHandler.addShapelessRecipe("nputils:basic_to_configurable_circuit", MetaItems.INTEGRATED_CIRCUIT.getStackForm(), "circuitBasic");
        
        //MAX Machine Hull
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:casing_max"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:hull_max"));
        ModHandler.addShapedRecipe("npu_casing_max", MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.MAX), "PPP", "PwP", "PPP", 'P', new UnificationEntry(OrePrefix.plate, NPUMaterials.Neutronium));
        ModHandler.addShapedRecipe("npu_hull_max", MetaTileEntities.HULL[GTValues.MAX].getStackForm(), "PHP", "CMC", 'M', MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.MAX), 'C', new UnificationEntry(OrePrefix.wireGtSingle, Tier.Superconductor), 'H', new UnificationEntry(OrePrefix.plate, NPUMaterials.Neutronium), 'P', new UnificationEntry(OrePrefix.plate, Materials.Polytetrafluoroethylene));
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(50).EUt(16).input(OrePrefix.plate, NPUMaterials.Neutronium, 8).outputs(MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.MAX)).circuitMeta(8).duration(50).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(50).EUt(16).inputs(MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.MAX)).input(OrePrefix.wireGtSingle, Tier.Superconductor, 2).fluidInputs(Materials.Polytetrafluoroethylene.getFluid(288)).outputs(MetaTileEntities.HULL[9].getStackForm()).buildAndRegister();
        
        //Redstone and glowstone melting
        RecipeMaps.FLUID_EXTRACTION_RECIPES.recipeBuilder().duration(80).EUt(32).input(OrePrefix.dust, Materials.Redstone).fluidOutputs(Materials.Redstone.getFluid(144)).buildAndRegister();
        RecipeMaps.FLUID_EXTRACTION_RECIPES.recipeBuilder().duration(80).EUt(32).input(OrePrefix.dust, Materials.Glowstone).fluidOutputs(Materials.Glowstone.getFluid(144)).buildAndRegister();
        
        //Gem Tool Part Fixes
        for (Material material : GemMaterial.MATERIAL_REGISTRY) {
            if (!OreDictUnifier.get(OrePrefix.gem, material).isEmpty() && !OreDictUnifier.get(OrePrefix.toolHeadHammer, material).isEmpty() && material != Materials.Flint) {
                ModHandler.removeRecipes(OreDictUnifier.get(OrePrefix.toolHeadAxe, material));
                ModHandler.addShapedRecipe(String.format("nputils:axe_head_%s", material.toString()), OreDictUnifier.get(OrePrefix.toolHeadAxe, material), "GG", "Gf", 'G', new UnificationEntry(OrePrefix.gem, material));
                ModHandler.removeRecipes(OreDictUnifier.get(OrePrefix.toolHeadFile, material));
                ModHandler.addShapedRecipe(String.format("nputils:file_head_%s", material.toString()), OreDictUnifier.get(OrePrefix.toolHeadFile, material), "G", "G", "f", 'G', new UnificationEntry(OrePrefix.gem, material));
                ModHandler.removeRecipes(OreDictUnifier.get(OrePrefix.toolHeadHammer, material));
                ModHandler.addShapedRecipe(String.format("nputils:hammer_head_%s", material.toString()), OreDictUnifier.get(OrePrefix.toolHeadHammer, material), "GG ", "GGf", "GG ", 'G', new UnificationEntry(OrePrefix.gem, material));
                ModHandler.removeRecipes(OreDictUnifier.get(OrePrefix.toolHeadHoe, material));
                ModHandler.addShapedRecipe(String.format("nputils:hoe_head_%s", material.toString()), OreDictUnifier.get(OrePrefix.toolHeadHoe, material), "GGf", 'G', new UnificationEntry(OrePrefix.gem, material));
                ModHandler.removeRecipes(OreDictUnifier.get(OrePrefix.toolHeadPickaxe, material));
                ModHandler.addShapedRecipe(String.format("nputils:pickaxe_head_%s", material.toString()), OreDictUnifier.get(OrePrefix.toolHeadPickaxe, material), "GGG", "f  ", 'G', new UnificationEntry(OrePrefix.gem, material));
                ModHandler.removeRecipes(OreDictUnifier.get(OrePrefix.toolHeadSaw, material));
                ModHandler.addShapedRecipe(String.format("nputils:saw_head_%s", material.toString()), OreDictUnifier.get(OrePrefix.toolHeadSaw, material), "GG", "f ", 'G', new UnificationEntry(OrePrefix.gem, material));
                ModHandler.removeRecipes(OreDictUnifier.get(OrePrefix.toolHeadSense, material));
                ModHandler.addShapedRecipe(String.format("nputils:sense_head_%s", material.toString()), OreDictUnifier.get(OrePrefix.toolHeadSense, material), "GGG", " f ", 'G', new UnificationEntry(OrePrefix.gem, material));
                ModHandler.removeRecipes(OreDictUnifier.get(OrePrefix.toolHeadShovel, material));
                ModHandler.addShapedRecipe(String.format("nputils:shovel_head_%s", material.toString()), OreDictUnifier.get(OrePrefix.toolHeadShovel, material), "fG", 'G', new UnificationEntry(OrePrefix.gem, material));
                ModHandler.removeRecipes(OreDictUnifier.get(OrePrefix.toolHeadSword, material));
                ModHandler.addShapedRecipe(String.format("nputils:sword_head_%s", material.toString()), OreDictUnifier.get(OrePrefix.toolHeadSword, material), " G", "fG", 'G', new UnificationEntry(OrePrefix.gem, material));
                ModHandler.removeRecipes(OreDictUnifier.get(OrePrefix.toolHeadUniversalSpade, material));
                ModHandler.addShapedRecipe(String.format("nputils:universal_spade_head_%s", material.toString()), OreDictUnifier.get(OrePrefix.toolHeadUniversalSpade, material), "GGG", "GfG", " G ", 'G', new UnificationEntry(OrePrefix.gem, material));
            }
        }
        
        //Misc Recipe Patches
        RecipeMaps.COMPRESSOR_RECIPES.recipeBuilder().duration(400).EUt(2).input(OrePrefix.dust, Materials.NetherQuartz).outputs(OreDictUnifier.get(OrePrefix.plate, Materials.NetherQuartz)).buildAndRegister();
        RecipeMaps.COMPRESSOR_RECIPES.recipeBuilder().duration(400).EUt(2).input(OrePrefix.dust, Materials.CertusQuartz).outputs(OreDictUnifier.get(OrePrefix.plate, Materials.CertusQuartz)).buildAndRegister();
        
        //Dust Uncrafting Fixes
        for (Material m : DustMaterial.MATERIAL_REGISTRY) {
            if (!OreDictUnifier.get(OrePrefix.dustSmall, m).isEmpty()) {
                ModHandler.removeRecipes(OreDictUnifier.get(OrePrefix.dustSmall, m));
                ModHandler.addShapedRecipe(String.format("dust_small_%s", m.toString()), OreDictUnifier.get(OrePrefix.dustSmall, m, 4), " D", "  ", 'D', new UnificationEntry(OrePrefix.dust, m));
            }
        }
        
        //Change Solar Panel recipes
        ModHandler.removeRecipes(MetaItems.COVER_SOLAR_PANEL.getStackForm());
        ModHandler.addShapedRecipe("nputils:basic_solar_panel", MetaItems.COVER_SOLAR_PANEL.getStackForm(), "PGP", "WCW", "AAA", 'P', "plateSilicon", 'C', "circuitBasic", 'G', "paneGlass", 'W', "cableGtSingleCopper", 'A', "plateAluminium");
        ModHandler.removeRecipes(MetaItems.COVER_SOLAR_PANEL_ULV.getStackForm());
        ModHandler.addShapedRecipe("nputils:ulv_solar_panel", MetaItems.COVER_SOLAR_PANEL_ULV.getStackForm(), "SSS", "SCS", "SSS", 'C', "circuitBasic", 'S', MetaItems.COVER_SOLAR_PANEL);
        ModHandler.removeRecipes(MetaItems.COVER_SOLAR_PANEL_LV.getStackForm());
        ModHandler.addShapedRecipe("nputils:lv_solar_panel", MetaItems.COVER_SOLAR_PANEL_LV.getStackForm(), "SSS", "SCS", "SSS", 'C', "circuitGood", 'S', MetaItems.COVER_SOLAR_PANEL_ULV);
        
        //Improved Superconductor recipes
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(1200).EUt(120).input(OrePrefix.dust, Materials.Cadmium, 5).input(OrePrefix.dust, Materials.Magnesium).fluidInputs(Materials.Oxygen.getFluid(6000)).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.MVSuperconductorBase, 12)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(2400).EUt(120).input(OrePrefix.dust, Materials.Titanium).input(OrePrefix.dust, Materials.Barium, 9).input(OrePrefix.dust, Materials.Copper, 10).fluidInputs(Materials.Oxygen.getFluid(20000)).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.HVSuperconductorBase, 40)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(400).EUt(480).input(OrePrefix.dust, Materials.Uranium).input(OrePrefix.dust, Materials.Platinum, 3).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.EVSuperconductorBase, 4)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(400).EUt(480).input(OrePrefix.dust, Materials.Vanadium).input(OrePrefix.dust, Materials.Indium, 3).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.IVSuperconductorBase, 4)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(2400).EUt(1920).input(OrePrefix.dust, Materials.Indium, 4).input(OrePrefix.dust, Materials.Bronze, 8).input(OrePrefix.dust, Materials.Barium, 2).input(OrePrefix.dust, Materials.Titanium).fluidInputs(Materials.Oxygen.getFluid(14000)).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.LuVSuperconductorBase, 29)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(1200).EUt(1920).input(OrePrefix.dust, Materials.Naquadah, 4).input(OrePrefix.dust, Materials.Indium, 2).input(OrePrefix.dust, Materials.Palladium, 6).input(OrePrefix.dust, Materials.Osmium).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.ZPMSuperconductorBase, 13)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(400).EUt(8).input(OrePrefix.dust, Materials.Lead, 3).input(OrePrefix.dust, Materials.Platinum).input(OrePrefix.dust, Materials.EnderPearl, 4).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.Enderium, 4)).buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(300).EUt(120).inputs(OreDictUnifier.get(OrePrefix.wireGtSingle, NPUMaterials.MVSuperconductorBase, 3), OreDictUnifier.get(OrePrefix.pipeTiny, Materials.StainlessSteel, 2), MetaItems.ELECTRIC_PUMP_MV.getStackForm(2)).fluidInputs(Materials.Nitrogen.getFluid(2000)).outputs(OreDictUnifier.get(OrePrefix.wireGtSingle, NPUMaterials.MVSuperconductor, 3)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(256).inputs(OreDictUnifier.get(OrePrefix.wireGtSingle, NPUMaterials.HVSuperconductorBase, 3), OreDictUnifier.get(OrePrefix.pipeTiny, Materials.Titanium, 2), MetaItems.ELECTRIC_PUMP_HV.getStackForm()).fluidInputs(Materials.Nitrogen.getFluid(2000)).outputs(OreDictUnifier.get(OrePrefix.wireGtSingle, NPUMaterials.HVSuperconductor, 3)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(500).EUt(480).inputs(OreDictUnifier.get(OrePrefix.wireGtSingle, NPUMaterials.EVSuperconductorBase, 9), OreDictUnifier.get(OrePrefix.pipeTiny, Materials.TungstenSteel, 6), MetaItems.ELECTRIC_PUMP_EV.getStackForm(2)).fluidInputs(Materials.Nitrogen.getFluid(6000)).outputs(OreDictUnifier.get(OrePrefix.wireGtSingle, NPUMaterials.EVSuperconductor, 9)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(300).EUt(1920).inputs(OreDictUnifier.get(OrePrefix.wireGtSingle, NPUMaterials.IVSuperconductorBase, 6), OreDictUnifier.get(OrePrefix.pipeTiny, Materials.NiobiumTitanium, 4), MetaItems.ELECTRIC_PUMP_IV.getStackForm()).fluidInputs(Materials.Nitrogen.getFluid(4000)).outputs(OreDictUnifier.get(OrePrefix.wireGtSingle, NPUMaterials.IVSuperconductor, 6)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(350).EUt(7680).inputs(OreDictUnifier.get(OrePrefix.wireGtSingle, NPUMaterials.LuVSuperconductorBase, 8), OreDictUnifier.get(OrePrefix.pipeTiny, NPUMaterials.Enderium, 5), MetaItems.ELECTRIC_PUMP_LUV.getStackForm()).fluidInputs(Materials.Nitrogen.getFluid(6000)).outputs(OreDictUnifier.get(OrePrefix.wireGtSingle, NPUMaterials.LuVSuperconductor, 8)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(30720).inputs(OreDictUnifier.get(OrePrefix.wireGtSingle, NPUMaterials.ZPMSuperconductorBase, 16), OreDictUnifier.get(OrePrefix.pipeTiny, Materials.Naquadah, 6), MetaItems.ELECTRIC_PUMP_ZPM.getStackForm()).fluidInputs(Materials.Nitrogen.getFluid(8000)).outputs(OreDictUnifier.get(OrePrefix.wireGtSingle, NPUMaterials.ZPMSuperconductor, 16)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(500).EUt(122880).inputs(OreDictUnifier.get(OrePrefix.wireGtSingle, NPUMaterials.ZPMSuperconductorBase, 32), OreDictUnifier.get(OrePrefix.pipeTiny, NPUMaterials.Neutronium, 7), MetaItems.ELECTRIC_PUMP_ZPM.getStackForm()).fluidInputs(Materials.Nitrogen.getFluid(10000)).outputs(OreDictUnifier.get(OrePrefix.wireGtSingle, Tier.Superconductor, 32)).buildAndRegister();
        
        //GTNH Coils
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(400).EUt(8).input(OrePrefix.dust, Materials.Mica, 3).input(OrePrefix.dust, Materials.RawRubber, 2).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.MicaPulp, 4)).buildAndRegister();
        RecipeMaps.MIXER_RECIPES.recipeBuilder().duration(400).EUt(8).input(OrePrefix.dust, Materials.Mica, 3).inputs(MetaItems.RUBBER_DROP.getStackForm()).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.MicaPulp, 4)).buildAndRegister();

        RecipeMaps.ALLOY_SMELTER_RECIPES.recipeBuilder().duration(1200).EUt(30).input(OrePrefix.dust, Materials.Sapphire).input(OrePrefix.dust, Materials.SiliconDioxide).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.AluminoSilicateWool, 2)).buildAndRegister();
        RecipeMaps.ALLOY_SMELTER_RECIPES.recipeBuilder().duration(1200).EUt(30).input(OrePrefix.dust, Materials.GreenSapphire).input(OrePrefix.dust, Materials.SiliconDioxide).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.AluminoSilicateWool, 2)).buildAndRegister();
        RecipeMaps.ALLOY_SMELTER_RECIPES.recipeBuilder().duration(1200).EUt(30).input(OrePrefix.dust, Materials.Ruby).input(OrePrefix.dust, Materials.SiliconDioxide).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.AluminoSilicateWool, 2)).buildAndRegister();

        RecipeMaps.FORMING_PRESS_RECIPES.recipeBuilder().duration(400).EUt(28).input(OrePrefix.dust, NPUMaterials.MicaPulp, 4).input(OrePrefix.dust, Materials.Asbestos).outputs(NPUMetaItems.MICA_SHEET.getStackForm(4)).buildAndRegister();

        RecipeMaps.ALLOY_SMELTER_RECIPES.recipeBuilder().duration(400).EUt(30).inputs(NPUMetaItems.MICA_SHEET.getStackForm(4)).input(OrePrefix.dust, Materials.SiliconDioxide).outputs(NPUMetaItems.MICA_INSULATOR_SHEET.getStackForm(4)).buildAndRegister();
        if (NPUConfig.GT6.BendingFoilsAutomatic && NPUConfig.GT6.BendingCylinders)
            NPURecipeMaps.CLUSTER_MILL_RECIPES.recipeBuilder().duration(100).EUt(30).inputs(NPUMetaItems.MICA_INSULATOR_SHEET.getStackForm()).outputs(NPUMetaItems.MICA_INSULATOR_FOIL.getStackForm(4)).buildAndRegister();
        else if (!NPUConfig.GT6.BendingFoilsAutomatic || !NPUConfig.GT6.BendingCylinders)
            RecipeMaps.BENDER_RECIPES.recipeBuilder().duration(100).EUt(30).inputs(NPUMetaItems.MICA_INSULATOR_SHEET.getStackForm()).circuitMeta(1).outputs(NPUMetaItems.MICA_INSULATOR_FOIL.getStackForm(4)).buildAndRegister();
        
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:heating_coil_cupronickel"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:heating_coil_kanthal"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:heating_coil_nichrome"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:heating_coil_tungstensteel"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:heating_coil_hss_g"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:heating_coil_naquadah"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:heating_coil_naquadah_alloy"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:wire_coil_cupronickel"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:wire_coil_kanthal"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:wire_coil_nichrome"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:wire_coil_tungstensteel"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:wire_coil_hss_g"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:wire_coil_naquadah"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:wire_coil_naquadah_alloy"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:wire_coil_superconductor"));
        
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(100).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtDouble, Materials.Cupronickel, 8), OreDictUnifier.get(OrePrefix.dust, NPUMaterials.AluminoSilicateWool, 12)).fluidInputs(Materials.Tin.getFluid(144)).outputs(MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.CUPRONICKEL)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(200).EUt(8).inputs(OreDictUnifier.get(OrePrefix.wireGtDouble, Materials.Cupronickel, 8), NPUMetaItems.MICA_INSULATOR_FOIL.getStackForm(8)).fluidInputs(Materials.Tin.getFluid(144)).outputs(MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.CUPRONICKEL)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(300).EUt(30).inputs(OreDictUnifier.get(OrePrefix.wireGtDouble, Materials.Kanthal, 8), NPUMetaItems.MICA_INSULATOR_FOIL.getStackForm(8)).fluidInputs(Materials.Copper.getFluid(144)).outputs(MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.KANTHAL)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(400).EUt(120).inputs(OreDictUnifier.get(OrePrefix.wireGtDouble, Materials.Nichrome, 8), NPUMetaItems.MICA_INSULATOR_FOIL.getStackForm(8)).fluidInputs(Materials.Aluminium.getFluid(144)).outputs(MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.NICHROME)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(500).EUt(480).inputs(OreDictUnifier.get(OrePrefix.wireGtDouble, Materials.TungstenSteel, 8), NPUMetaItems.MICA_INSULATOR_FOIL.getStackForm(8)).fluidInputs(Materials.Nichrome.getFluid(144)).outputs(MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.TUNGSTENSTEEL)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(600).EUt(1920).inputs(OreDictUnifier.get(OrePrefix.wireGtDouble, Materials.HSSG, 8), NPUMetaItems.MICA_INSULATOR_FOIL.getStackForm(8)).fluidInputs(Materials.Tungsten.getFluid(144)).outputs(MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.HSS_G)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(700).EUt(4096).inputs(OreDictUnifier.get(OrePrefix.wireGtDouble, Materials.Naquadah, 8), NPUMetaItems.MICA_INSULATOR_FOIL.getStackForm(8)).fluidInputs(Materials.HSSG.getFluid(144)).outputs(MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.NAQUADAH)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(800).EUt(7680).inputs(OreDictUnifier.get(OrePrefix.wireGtDouble, Materials.NaquadahAlloy, 8), NPUMetaItems.MICA_INSULATOR_FOIL.getStackForm(8)).fluidInputs(Materials.Naquadah.getFluid(144)).outputs(MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.NAQUADAH_ALLOY)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(1000).EUt(9001).inputs(OreDictUnifier.get(OrePrefix.wireGtDouble, Tier.Superconductor, 8), NPUMetaItems.MICA_INSULATOR_FOIL.getStackForm(8)).fluidInputs(Materials.NaquadahAlloy.getFluid(144)).outputs(MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.SUPERCONDUCTOR)).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder().duration(1000).EUt(9001).inputs(OreDictUnifier.get(OrePrefix.wireGtDouble, NPUMaterials.LuVSuperconductor, 32), NPUMetaItems.MICA_INSULATOR_FOIL.getStackForm(16)).fluidInputs(Materials.NaquadahAlloy.getFluid(144)).outputs(MetaBlocks.WIRE_COIL.getItemVariant(BlockWireCoil.CoilType.SUPERCONDUCTOR)).buildAndRegister();
        
        NPULib.printEventFinish("Advanced recipes was registered for  %.3f seconds", time, System.currentTimeMillis());
	}
	
	public static void generatedRecipes() {
		long time = System.currentTimeMillis();
		List<ResourceLocation> recipesToRemove = new ArrayList<>();
		
		for (IRecipe recipe : CraftingManager.REGISTRY) {
			if (recipe.getIngredients().size() == 9) {
				if (recipe.getIngredients().get(0).getMatchingStacks().length > 0 && Block.getBlockFromItem(recipe.getRecipeOutput().getItem()) != Blocks.AIR) {
					boolean match = true;
					for (int i = 1; i < recipe.getIngredients().size(); i++) {
						if (recipe.getIngredients().get(i).getMatchingStacks().length == 0 || !recipe.getIngredients().get(0).getMatchingStacks()[0].isItemEqual(recipe.getIngredients().get(i).getMatchingStacks()[0])) {
							match = false;
							break;
						}
					}
					if (match) {
						if (NPUConfig.gameplay.Remove3x3BlockRecipes) 
							recipesToRemove.add(recipe.getRegistryName());
						if (NPUConfig.gameplay.GenerateCompressorRecipes) 
							RecipeMaps.COMPRESSOR_RECIPES.recipeBuilder().duration(400).EUt(2).inputs(CountableIngredient.from(recipe.getIngredients().get(0).getMatchingStacks()[0], recipe.getIngredients().size())).outputs(recipe.getRecipeOutput()).buildAndRegister();
					}
				}
			}
			if (recipe.getIngredients().size() == 9) {
				if (recipe.getIngredients().get(0).getMatchingStacks().length > 0 && Block.getBlockFromItem(recipe.getRecipeOutput().getItem()) == Blocks.AIR) {
                    boolean match = true;
                    for (int i = 1; i < recipe.getIngredients().size(); i++) {
                        if (recipe.getIngredients().get(i).getMatchingStacks().length == 0 || !recipe.getIngredients().get(0).getMatchingStacks()[0].isItemEqual(recipe.getIngredients().get(i).getMatchingStacks()[0])) {
                            match = false;
                            break;
                        }
                    }
                    if (match && !recipesToRemove.contains(recipe.getRegistryName()) && !NPUMetaItems.hasPrefix(recipe.getRecipeOutput(), "dust", "dustTiny") && recipe.getRecipeOutput().getCount() == 1 && NPUConfig.gameplay.Packager3x3Recipes) {
                        RecipeMaps.PACKER_RECIPES.recipeBuilder().duration(100).EUt(4).inputs(CountableIngredient.from(recipe.getIngredients().get(0).getMatchingStacks()[0], recipe.getIngredients().size())).notConsumable(new IntCircuitIngredient(3)).outputs(recipe.getRecipeOutput()).buildAndRegister();
                    }
                }
			}
			if (recipe.getIngredients().size() == 4) {
                if (recipe.getIngredients().get(0).getMatchingStacks().length > 0 && Block.getBlockFromItem(recipe.getRecipeOutput().getItem()) != Blocks.QUARTZ_BLOCK) {
                    boolean match = true;
                    for (int i = 1; i < recipe.getIngredients().size(); i++) {
                        if (recipe.getIngredients().get(i).getMatchingStacks().length == 0 || !recipe.getIngredients().get(0).getMatchingStacks()[0].isItemEqual(recipe.getIngredients().get(i).getMatchingStacks()[0])) {
                            match = false;
                            break;
                        }
                    }
                    if (match && !recipesToRemove.contains(recipe.getRegistryName()) && !NPUMetaItems.hasPrefix(recipe.getRecipeOutput(), "dust", "dustSmall") && recipe.getRecipeOutput().getCount() == 1 && NPUConfig.gameplay.Packager2x2Recipes) {
                        RecipeMaps.PACKER_RECIPES.recipeBuilder().duration(100).EUt(4).inputs(CountableIngredient.from(recipe.getIngredients().get(0).getMatchingStacks()[0], recipe.getIngredients().size())).notConsumable(new IntCircuitIngredient(2)).outputs(recipe.getRecipeOutput()).buildAndRegister();
                    }
                }
            }
            if (recipe.getIngredients().size() == 1 && recipe.getIngredients().get(0).getMatchingStacks().length > 0 && recipe.getRecipeOutput().getCount() == 9 && Block.getBlockFromItem(recipe.getIngredients().get(0).getMatchingStacks()[0].getItem()) != Blocks.AIR && Block.getBlockFromItem(recipe.getIngredients().get(0).getMatchingStacks()[0].getItem()) != Blocks.SLIME_BLOCK) {
                boolean isIngot = false;
                for (int i : OreDictionary.getOreIDs(recipe.getRecipeOutput())) {
                    if (OreDictionary.getOreName(i).startsWith("ingot")) {
                        isIngot = true;
                        break;
                    }
                }
                if (NPUConfig.gameplay.RemoveBlockUncraftingRecipes)
                    recipesToRemove.add(recipe.getRegistryName());
                if (!isIngot) {
                    RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder().duration(100).EUt(24).inputs(recipe.getIngredients().get(0).getMatchingStacks()[0]).outputs(recipe.getRecipeOutput()).buildAndRegister();
                }
            }
            if (recipe.getIngredients().size() == 1 && recipe.getIngredients().get(0).getMatchingStacks().length > 0 && recipe.getRecipeOutput().getCount() == 9) {
                if (!recipesToRemove.contains(recipe.getRegistryName()) && NPUConfig.gameplay.Unpackager3x3Recipes) {
                    RecipeMaps.UNPACKER_RECIPES.recipeBuilder().duration(100).EUt(8).inputs(recipe.getIngredients().get(0).getMatchingStacks()[0]).notConsumable(new IntCircuitIngredient(1)).outputs(recipe.getRecipeOutput()).buildAndRegister();
                }
            }
		}
		
		for (ResourceLocation r : recipesToRemove)
			ModHandler.removeRecipeByName(r);
		recipesToRemove.clear();
		
		if (NPUConfig.gameplay.GenerateCompressorRecipes) {
			ModHandler.removeRecipeByName(new ResourceLocation("minecraft:glowstone"));
            ModHandler.removeRecipeByName(new ResourceLocation("gregtech:block_compress_glowstone"));
            ModHandler.removeRecipeByName(new ResourceLocation("minecraft:quartz_block"));
            ModHandler.removeRecipeByName(new ResourceLocation("gregtech:block_compress_nether_quartz"));
            ModHandler.removeRecipeByName(new ResourceLocation("gregtech:block_decompress_nether_quartz"));
            RecipeMaps.FORGE_HAMMER_RECIPES.recipeBuilder().duration(100).EUt(24).inputs(OreDictUnifier.get(OrePrefix.block, Materials.NetherQuartz)).outputs(OreDictUnifier.get(OrePrefix.gem, Materials.NetherQuartz, 4)).buildAndRegister();
            RecipeMaps.COMPRESSOR_RECIPES.recipeBuilder().duration(400).EUt(2).input(OrePrefix.gem, Materials.NetherQuartz, 4).outputs(new ItemStack(Blocks.QUARTZ_BLOCK)).buildAndRegister();
		}
		
		//Generate Plank Recipes
		for (IRecipe recipe : CraftingManager.REGISTRY) {
			if (recipe.getRecipeOutput().isEmpty())
				continue;
			for (int i : OreDictionary.getOreIDs(recipe.getRecipeOutput())) {
				if (OreDictionary.getOreName(i).equals("plankWood") && recipe.getIngredients().size() == 1 && recipe.getRecipeOutput().getCount() == 4) {
                    if (NPUConfig.gameplay.GeneratedSawingRecipes) {
                        ModHandler.removeRecipeByName(recipe.getRegistryName());
                        ModHandler.addShapelessRecipe(String.format("nputils:log_to_4_%s", recipe.getRecipeOutput().toString()), GTUtility.copyAmount(4, recipe.getRecipeOutput()), recipe.getIngredients().get(0).getMatchingStacks()[0], ToolDictNames.craftingToolSaw);
                        ModHandler.addShapelessRecipe(String.format("nputils:log_to_2_%s", recipe.getRecipeOutput().toString()), GTUtility.copyAmount(2, recipe.getRecipeOutput()), recipe.getIngredients().get(0).getMatchingStacks()[0]);
                    }
                    RecipeMaps.CUTTER_RECIPES.recipeBuilder().duration(200).EUt(8).inputs(recipe.getIngredients().get(0).getMatchingStacks()[0]).fluidInputs(Materials.Lubricant.getFluid(1)).outputs(GTUtility.copyAmount(6, recipe.getRecipeOutput()), OreDictUnifier.get(OrePrefix.dust, Materials.Wood, 2)).buildAndRegister();
                }
                if (OreDictionary.getOreName(i).equals("slabWood") && recipe.getRecipeOutput().getCount() == 6) {
                    RecipeMaps.CUTTER_RECIPES.recipeBuilder().duration(50).EUt(4).inputs(recipe.getIngredients().get(0).getMatchingStacks()[0]).outputs(GTUtility.copyAmount(2, recipe.getRecipeOutput())).buildAndRegister();
                }
			}
		}		
		
		//Disable wood to charcoal recipe
		NonNullList<ItemStack> allWood = OreDictionary.getOres("logWood");
		
		for (ItemStack wood : allWood) {
			ItemStack resultOfSmelt = ModHandler.getSmeltingOutput(wood);
			if (!resultOfSmelt.isEmpty() && resultOfSmelt.getItem() == Items.COAL && resultOfSmelt.getMetadata() == 1 && NPUConfig.gameplay.DisableLogToCharcoalSmelting) {
				ItemStack woodStack = wood.copy();
				ModHandler.removeFurnaceSmelting(woodStack);
			}
		}
		
		 NPULib.printEventFinish("End of generated recipes. It is took %.3f seconds", time, System.currentTimeMillis());
	}
}
