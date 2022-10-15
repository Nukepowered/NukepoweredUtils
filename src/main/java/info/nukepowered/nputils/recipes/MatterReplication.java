package info.nukepowered.nputils.recipes;

import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.DustMaterial;
import gregtech.api.unification.material.type.FluidMaterial;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.MaterialStack;
import info.nukepowered.nputils.NPUMaterials;
import info.nukepowered.nputils.api.NPULib;

import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;

public class MatterReplication {

    private static final HashSet<Material> DUST_BLACKLIST;

    static {
        DUST_BLACKLIST = new HashSet<>(Arrays.asList(
            Materials.Sphalerite,
            Materials.Naquadria, Materials.Ash,
            Materials.DarkAsh,
            NPUMaterials.Neutronium,
            Materials.Monazite,
            Materials.Bentonite,
            NPUMaterials.Enderium
        ));
    }

    public static void init() {
        long time = System.currentTimeMillis();
        //Mass fabricator
        NPURecipeMaps.MASS_FAB_RECIPES.recipeBuilder()
            .duration((int) (Materials.Hydrogen.getMass() * 100)).EUt(32)
            .fluidInputs(Materials.Hydrogen.getFluid(1000))
            .fluidOutputs(NPUMaterials.PositiveMatter.getFluid(1))
            .buildAndRegister();
        NPURecipeMaps.MASS_FAB_RECIPES.recipeBuilder()
            .duration((int) (NPUMaterials.Neutronium.getMass() * 100)).EUt(32)
            .inputs((OreDictUnifier.get(OrePrefix.dust, NPUMaterials.Neutronium)))
            .fluidOutputs(NPUMaterials.NeutralMatter.getFluid((5000)))
            .buildAndRegister();

        for (Material m : Material.MATERIAL_REGISTRY) {
            if (m.getProtons() > 0 && m.getNeutrons() > 0 && m.getMass() != 98) {
                if (m instanceof FluidMaterial && OreDictUnifier.get(OrePrefix.dust, m).isEmpty()) {
                    NPURecipeMaps.MASS_FAB_RECIPES.recipeBuilder()
                        .duration(getDurationForMaterial(m)).EUt(32)
                        .fluidInputs(((FluidMaterial) m).getFluid(1000))
                        .fluidOutputs(NPUMaterials.PositiveMatter.getFluid((int) m.getProtons()), NPUMaterials.NeutralMatter.getFluid((int) m.getNeutrons()))
                        .buildAndRegister();
                } else if (m instanceof DustMaterial && !DUST_BLACKLIST.contains(m)) {
                    NPURecipeMaps.MASS_FAB_RECIPES.recipeBuilder()
                        .duration(getDurationForMaterial(m)).EUt(32)
                        .inputs((OreDictUnifier.get(OrePrefix.dust, m)))
                        .fluidOutputs(NPUMaterials.PositiveMatter.getFluid((int) m.getProtons()), NPUMaterials.NeutralMatter.getFluid((int) m.getNeutrons()))
                        .buildAndRegister();
                }

            }
        }

        //Replication
        NPURecipeMaps.REPLICATOR_RECIPES.recipeBuilder()
            .duration((int) (Materials.Hydrogen.getMass() * 100)).EUt(32)
            .inputs(FluidCellIngredient.getIngredient(Materials.Hydrogen, 0))
            .fluidOutputs((Materials.Hydrogen.getFluid(1000)))
            .fluidInputs(NPUMaterials.PositiveMatter.getFluid(1))
            .buildAndRegister();
        NPURecipeMaps.REPLICATOR_RECIPES.recipeBuilder()
            .duration((int) (NPUMaterials.Neutronium.getMass() * 100)).EUt(32)
            .notConsumable(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.Neutronium))
            .outputs(OreDictUnifier.get(OrePrefix.dust, NPUMaterials.Neutronium))
            .fluidInputs(NPUMaterials.NeutralMatter.getFluid(5000))
            .buildAndRegister();

        for (Material m : Material.MATERIAL_REGISTRY) {
            if (m.getProtons() > 0 && m.getNeutrons() > 0 && m.getMass() != 98) {
                if (m instanceof FluidMaterial && OreDictUnifier.get(OrePrefix.dust, m).isEmpty() && m != Materials.Air && m != Materials.LiquidAir) {
                    NPURecipeMaps.REPLICATOR_RECIPES.recipeBuilder()
                        .duration(getDurationForMaterial(m)).EUt(32)
                        .inputs(FluidCellIngredient.getIngredient((FluidMaterial) m, 0)).fluidOutputs(((FluidMaterial) m).getFluid(1000))
                        .fluidInputs(NPUMaterials.PositiveMatter.getFluid((int) m.getProtons()), NPUMaterials.NeutralMatter.getFluid((int) m.getNeutrons()))
                        .buildAndRegister();
                } else if (m instanceof DustMaterial && !DUST_BLACKLIST.contains(m)) {
                    NPURecipeMaps.REPLICATOR_RECIPES.recipeBuilder()
                        .duration(getDurationForMaterial(m)).EUt(32)
                        .notConsumable(OreDictUnifier.get(OrePrefix.dust, m))
                        .outputs((OreDictUnifier.get(OrePrefix.dust, m)))
                        .fluidInputs(NPUMaterials.PositiveMatter.getFluid((int) m.getProtons()), NPUMaterials.NeutralMatter.getFluid((int) m.getNeutrons()))
                        .buildAndRegister();
                }
            }
        }

        NPULib.printEventFinish("Replication was registered for %.3f seconds", time, System.currentTimeMillis());
    }

    private static int getDurationForMaterial(Material material) {
        Deque<MaterialStack> l1 = new LinkedList<>(material.materialComponents);

        int materials = 0;
        while (!l1.isEmpty()) {
            MaterialStack ms = l1.pollFirst();
            l1.addAll(ms.material.materialComponents);
            materials++;
        }

        if (materials == 0) {
            materials = 1;
        }

        int mass = (int) material.getMass();
        int result = (int) (Math.pow(mass, 2) / materials) + 100;
        return Math.min(result, 15000 + ((result - 15000) / 10));
    }
}
