package info.nukepowered.nputils.recipes;

import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.common.items.MetaItems;
import info.nukepowered.nputils.NPULog;
import info.nukepowered.nputils.item.NPUMetaItems;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class TinkersIntegration {
	public static void init() {
		NPULog.info("Tinkers' Construct integarion enabled");
		ModHandler.removeRecipes(MetaItems.SHAPE_EMPTY.getStackForm());
		TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EMPTY.getStackForm(), Materials.Steel.getMaterialFluid(), 576, 160));
		for (ItemStack mold : NPURecipeAddition.molds)
			ModHandler.removeRecipes(mold);
		
		ModHandler.addShapedRecipe("nputils:anvil_mold_form", NPUMetaItems.MOLD_FORM_ANVIL.getStackForm(), "fx ", " M ", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:ball_mold_form", NPUMetaItems.MOLD_FORM_BALL.getStackForm(), "f x", " M ", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:block_mold_form", NPUMetaItems.MOLD_FORM_BLOCK.getStackForm(), "f  ", "xM ", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:bottle_mold_form", NPUMetaItems.MOLD_FORM_BOTTLE.getStackForm(), "f  ", " Mx", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:coinage_mold_form", NPUMetaItems.MOLD_FORM_COINAGE.getStackForm(), "f  ", " M ", "x  ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:cylinder_mold_form", NPUMetaItems.MOLD_FORM_CYLINDER.getStackForm(), "f  ", " M ", " x ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:gear_mold_form", NPUMetaItems.MOLD_FORM_GEAR.getStackForm(), "f  ", " M ", "  x", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:ingot_mold_form", NPUMetaItems.MOLD_FORM_INGOT.getStackForm(), "xf ", " M ", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:name_mold_form", NPUMetaItems.MOLD_FORM_NAME.getStackForm(), " fx", " M ", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:nugget_mold_form", NPUMetaItems.MOLD_FORM_NUGGETS.getStackForm(), " f ", "xM ", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:plate_mold_form", NPUMetaItems.MOLD_FORM_PLATE.getStackForm(), " f ", " Mx", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:small_gear_mold_form", NPUMetaItems.MOLD_FORM_SMALL_GEAR.getStackForm(), " f ", " M ", "x  ", 'M', new ItemStack(TinkerSmeltery.cast));

        ModHandler.addShapedRecipe("nputils:axe_shape", NPUMetaItems.SHAPE_AXE_HEAD.getStackForm(), " f ", " M ", " x ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:block_shape", NPUMetaItems.SHAPE_BLOCK.getStackForm(), " f ", " M ", "  x", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:bolt_shape", NPUMetaItems.SHAPE_BOLT.getStackForm(), "x f", " M ", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:bottle_shape", NPUMetaItems.SHAPE_BOTTLE.getStackForm(), " xf", " M ", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:cell_shape", NPUMetaItems.SHAPE_CELL.getStackForm(), "  f", "xM ", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:file_shape", NPUMetaItems.SHAPE_FILE_HEAD.getStackForm(), "  f", " Mx", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:gear_shape", NPUMetaItems.SHAPE_GEAR.getStackForm(), "  f", " M ", "x  ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:hammer_shape", NPUMetaItems.SHAPE_HAMMER_HEAD.getStackForm(), "  f", " M ", " x ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:hoe_shape", NPUMetaItems.SHAPE_HOE_HEAD.getStackForm(), "  f", " M ", "  x", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:ingot_shape", NPUMetaItems.SHAPE_INGOT.getStackForm(), "x  ", "fM ", "  x", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:large_pipe_shape", NPUMetaItems.SHAPE_LARGE_PIPE.getStackForm(), " x ", "fM ", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:medium_pipe_shape", NPUMetaItems.SHAPE_NORMAL_PIPE.getStackForm(), "  x", "fM ", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:pickaxe_shape", NPUMetaItems.SHAPE_PICKAXE_HEAD.getStackForm(), "   ", "fMx", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:plate_shape", NPUMetaItems.SHAPE_PLATE.getStackForm(), "   ", "fM ", "x  ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:ring_shape", NPUMetaItems.SHAPE_RING.getStackForm(), "   ", "fM ", " x ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:rod_shape", NPUMetaItems.SHAPE_ROD.getStackForm(), "   ", "fM ", "  x", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:saw_shape", NPUMetaItems.SHAPE_SAW_BLADE.getStackForm(), "x  ", " Mf", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:shovel_shape", NPUMetaItems.SHAPE_SHOVEL_HEAD.getStackForm(), " x ", " Mf", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:small_pipe_shape", NPUMetaItems.SHAPE_SMALL_PIPE.getStackForm(), "  x", " Mf", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:sword_shape", NPUMetaItems.SHAPE_SWORD_BLADE.getStackForm(), "   ", "xMf", "   ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:tiny_pipe_shape", NPUMetaItems.SHAPE_TINY_PIPE.getStackForm(), "   ", " Mf", "x  ", 'M', new ItemStack(TinkerSmeltery.cast));
        ModHandler.addShapedRecipe("nputils:wire_shape", NPUMetaItems.SHAPE_WIRE.getStackForm(), "   ", " Mf", " x ", 'M', new ItemStack(TinkerSmeltery.cast));
		
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_MOLD_ANVIL.getStackForm(), RecipeMatch.of(NPUMetaItems.MOLD_FORM_ANVIL.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_MOLD_BALL.getStackForm(), RecipeMatch.of(NPUMetaItems.MOLD_FORM_BALL.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_MOLD_BLOCK.getStackForm(), RecipeMatch.of(NPUMetaItems.MOLD_FORM_BLOCK.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_MOLD_BOTTLE.getStackForm(), RecipeMatch.of(NPUMetaItems.MOLD_FORM_BOTTLE.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_MOLD_CREDIT.getStackForm(), RecipeMatch.of(NPUMetaItems.MOLD_FORM_COINAGE.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_MOLD_CYLINDER.getStackForm(), RecipeMatch.of(NPUMetaItems.MOLD_FORM_CYLINDER.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_MOLD_GEAR.getStackForm(), RecipeMatch.of(NPUMetaItems.MOLD_FORM_GEAR.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_MOLD_INGOT.getStackForm(), RecipeMatch.of(NPUMetaItems.MOLD_FORM_INGOT.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_MOLD_NAME.getStackForm(), RecipeMatch.of(NPUMetaItems.MOLD_FORM_NAME.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_MOLD_NUGGET.getStackForm(), RecipeMatch.of(NPUMetaItems.MOLD_FORM_NUGGETS.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_MOLD_PLATE.getStackForm(), RecipeMatch.of(NPUMetaItems.MOLD_FORM_PLATE.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_MOLD_GEAR_SMALL.getStackForm(), RecipeMatch.of(NPUMetaItems.MOLD_FORM_SMALL_GEAR.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_AXE.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_AXE_HEAD.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_BLOCK.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_BLOCK.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_BOLT.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_BOLT.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_BOTTLE.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_BOTTLE.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_CELL.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_CELL.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_FILE.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_FILE_HEAD.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_GEAR.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_GEAR.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_HAMMER.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_HAMMER_HEAD.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_HOE.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_HOE_HEAD.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_INGOT.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_INGOT.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_PIPE_LARGE.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_LARGE_PIPE.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_PIPE_MEDIUM.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_NORMAL_PIPE.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_PICKAXE.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_PICKAXE_HEAD.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_PLATE.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_PLATE.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_RING.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_RING.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_ROD.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_ROD.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_SAW.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_SAW_BLADE.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_SHOVEL.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_SHOVEL_HEAD.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_PIPE_SMALL.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_SMALL_PIPE.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_SWORD.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_SWORD_BLADE.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_PIPE_TINY.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_TINY_PIPE.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
        TinkerRegistry.registerTableCasting(new CastingRecipe(MetaItems.SHAPE_EXTRUDER_WIRE.getStackForm(), RecipeMatch.of(NPUMetaItems.SHAPE_WIRE.getStackForm()), Materials.Steel.getMaterialFluid(), 576, true, false));
	}
	
	public static void preInit() {
		ModHandler.removeFurnaceSmelting(TinkerCommons.grout);
		ModHandler.addShapelessRecipe("nputils:seared_brick", NPUMetaItems.COMPRESSED_GROUT.getStackForm(), TinkerCommons.grout, MetaItems.WOODEN_FORM_BRICK);
		ModHandler.addShapedRecipe("eight_seared_brick", NPUMetaItems.COMPRESSED_GROUT.getStackForm(8), "BBB", "BFB", "BBB", 'B', TinkerCommons.grout, 'F', MetaItems.WOODEN_FORM_BRICK);
        ModHandler.addSmeltingRecipe(NPUMetaItems.COMPRESSED_GROUT.getStackForm(), TinkerCommons.searedBrick);
        RecipeMaps.ALLOY_SMELTER_RECIPES.recipeBuilder().duration(200).EUt(2).inputs(TinkerCommons.grout).notConsumable(MetaItems.SHAPE_MOLD_INGOT.getStackForm()).outputs(TinkerCommons.searedBrick).buildAndRegister();
	}
}
