package info.nukepowered.nputils.recipes;

import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.DustMaterial;
import gregtech.api.unification.material.type.FluidMaterial;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.ore.OrePrefix;
import info.nukepowered.nputils.NPUMaterials;
import info.nukepowered.nputils.api.NPULib;

public class MatterReplication {
	public static void init( ) {
		long time = System.currentTimeMillis();
		//Mass fabricator
		NPURecipeMaps.MASS_FAB_RECIPES.recipeBuilder().duration((int)(Materials.Hydrogen.getMass() * 100)).EUt(32).fluidInputs(Materials.Hydrogen.getFluid(1000)).fluidOutputs(NPUMaterials.PositiveMatter.getFluid(1)).buildAndRegister();
		for (Material m : FluidMaterial.MATERIAL_REGISTRY) {
			if (m.getProtons() > 0 && m.getNeutrons() > 0 && m.getMass() != 98 && m instanceof FluidMaterial && OreDictUnifier.get(OrePrefix.dust, m).isEmpty()) {
				NPURecipeMaps.MASS_FAB_RECIPES.recipeBuilder().duration((int)(m.getMass() * 100)).EUt(32).fluidInputs(((FluidMaterial) m).getFluid(1000)).fluidOutputs(NPUMaterials.PositiveMatter.getFluid((int) m.getProtons()), NPUMaterials.NeutralMatter.getFluid((int) m.getNeutrons())).buildAndRegister();
			}
		}
		
		NPURecipeMaps.MASS_FAB_RECIPES.recipeBuilder().duration((int) (NPUMaterials.Neutronium.getMass() * 100)).EUt(32).inputs((OreDictUnifier.get(OrePrefix.dust, NPUMaterials.Neutronium))).fluidOutputs(NPUMaterials.NeutralMatter.getFluid((5000))).buildAndRegister();
        for (Material m : DustMaterial.MATERIAL_REGISTRY) {
            if (m.getProtons() >= 1 && m.getNeutrons() >= 0 && m.getMass() != 98 && m instanceof DustMaterial && m != Materials.Sphalerite && m != Materials.Naquadria && m != Materials.Ash && m != Materials.DarkAsh && m != NPUMaterials.Neutronium && m != Materials.Monazite && m != Materials.Bentonite && m != NPUMaterials.Enderium) {
                NPURecipeMaps.MASS_FAB_RECIPES.recipeBuilder().duration((int) (m.getMass() * 100)).EUt(32).inputs((OreDictUnifier.get(OrePrefix.dust, m))).fluidOutputs(NPUMaterials.PositiveMatter.getFluid((int) m.getProtons()), NPUMaterials.NeutralMatter.getFluid((int) m.getNeutrons())).buildAndRegister();
            }
        }
        
        //Replication
        NPURecipeMaps.REPLICATOR_RECIPES.recipeBuilder().duration((int) (Materials.Hydrogen.getMass() * 100)).EUt(32).inputs(FluidCellIngredient.getIngredient(Materials.Hydrogen, 0)).fluidOutputs((Materials.Hydrogen.getFluid(1000))).fluidInputs(NPUMaterials.PositiveMatter.getFluid(1)).buildAndRegister();
        for (Material m : FluidMaterial.MATERIAL_REGISTRY) {
            if (m.getProtons() > 0 && m.getNeutrons() > 0 && m.getMass() != 98 && m instanceof FluidMaterial && OreDictUnifier.get(OrePrefix.dust, m).isEmpty() && m != Materials.Air && m != Materials.LiquidAir) {
                NPURecipeMaps.REPLICATOR_RECIPES.recipeBuilder().duration((int) (m.getMass() * 100)).EUt(32).inputs(FluidCellIngredient.getIngredient((FluidMaterial) m, 0)).fluidOutputs(((FluidMaterial) m).getFluid(1000)).fluidInputs(NPUMaterials.PositiveMatter.getFluid((int) m.getProtons()), NPUMaterials.NeutralMatter.getFluid((int) m.getNeutrons())).buildAndRegister();
            }
        }
        NPURecipeMaps.REPLICATOR_RECIPES.recipeBuilder().duration((int) (NPUMaterials.Neutronium.getMass() * 100)).EUt(32).notConsumable(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.Neutronium)).outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.Neutronium)).fluidInputs(NPUMaterials.NeutralMatter.getFluid(5000)).buildAndRegister();
        for (Material m : DustMaterial.MATERIAL_REGISTRY) {
            if (m.getProtons() >= 1 && m.getNeutrons() >= 0 && m.getMass() != 98 && m instanceof DustMaterial && m != Materials.Sphalerite && m != Materials.Naquadria && m != Materials.Ash && m != Materials.DarkAsh && m != NPUMaterials.Neutronium && m != Materials.Monazite && m != Materials.Bentonite && m != NPUMaterials.Enderium) {
                NPURecipeMaps.REPLICATOR_RECIPES.recipeBuilder().duration((int) (m.getMass() * 100)).EUt(32).notConsumable(OreDictUnifier.get(OrePrefix.dust, m)).outputs((OreDictUnifier.get(OrePrefix.dust, m))).fluidInputs(NPUMaterials.PositiveMatter.getFluid((int) m.getProtons()), NPUMaterials.NeutralMatter.getFluid((int) m.getNeutrons())).buildAndRegister();
            }
        }
        
        NPULib.printEventFinish("Replication was registered for %.3f seconds", time, System.currentTimeMillis());
	}
}
