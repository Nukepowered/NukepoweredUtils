package info.nukepowered.nputils.item;

import gregtech.api.items.toolitem.ToolMetaItem;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.DustMaterial;
import gregtech.api.unification.material.type.IngotMaterial;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.material.type.SolidMaterial;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;
import info.nukepowered.nputils.NPUConfig;
import info.nukepowered.nputils.tools.BendingCylinder;
import info.nukepowered.nputils.tools.SmallBendingCylinder;

public class NPUMetaTool extends ToolMetaItem<ToolMetaItem<?>.MetaToolValueItem> {
	public void registerSubItems() {
		NPUMetaItems.BENDING_CYLINDER = addItem(0, "tool.bending_cylinder").setToolStats(new BendingCylinder()).addOreDict("craftingToolBendingCylinder");
		NPUMetaItems.SMALL_BENDING_CYLINDER = addItem(1, "tool.bending_cylinder_small").setToolStats(new SmallBendingCylinder()).addOreDict("craftingToolBendingCylinderSmall");
	}
	
	public void registerRecipes() {
		for (Material material : IngotMaterial.MATERIAL_REGISTRY) {
			if (material instanceof IngotMaterial && !material.hasFlag(DustMaterial.MatFlags.NO_SMASHING) && ((IngotMaterial)material).toolDurability != 0) {
				IngotMaterial toolMaterial = (IngotMaterial) material;
				
				if (NPUConfig.GT6.BendingCylinders) {
					ModHandler.addShapedRecipe(String.format("cylinder_%s", material.toString()),
							((ToolMetaItem<?>.MetaToolValueItem) NPUMetaItems.BENDING_CYLINDER).getStackForm(toolMaterial),
							"sfh", "XXX", "XXX", 'X', new UnificationEntry(OrePrefix.ingot, toolMaterial));
					ModHandler.addShapedRecipe(String.format("small_cylinder_%s", material.toString()),
                            ((ToolMetaItem<?>.MetaToolValueItem) NPUMetaItems.SMALL_BENDING_CYLINDER).getStackForm(toolMaterial),
                            "sfh", "XXX", 'X', new UnificationEntry(OrePrefix.ingot, toolMaterial));
				}
				
				//GT6 Wrench Recipe
				if (NPUConfig.gameplay.ExpensiveWrenches && !OreDictUnifier.get(OrePrefix.plate, material).isEmpty()) {
					ModHandler.addShapedRecipe(String.format("ga_wrench_%s", material.toString()),
                            MetaItems.WRENCH.getStackForm(toolMaterial),
                            "XhX", "XXX", " X ", 'X', new UnificationEntry(OrePrefix.plate, toolMaterial));
				}
			}
		}
		for (Material material : Material.MATERIAL_REGISTRY) {
			if (!OreDictUnifier.get(OrePrefix.gem, material).isEmpty() && !OreDictUnifier.get(OrePrefix.toolHeadHammer, material).isEmpty() && material != Materials.Flint) {
				SolidMaterial toolMaterial = (SolidMaterial) material;
				ModHandler.addMirroredShapedRecipe(String.format("gem_hammer_%s", material.toString()),
						(MetaItems.HARD_HAMMER).getStackForm(toolMaterial), "GG ", "GGS", "GG ",
						'G', new UnificationEntry(OrePrefix.gem, toolMaterial),
						'S', new UnificationEntry(OrePrefix.stick, Materials.Wood));
			}
		}
	}
}
