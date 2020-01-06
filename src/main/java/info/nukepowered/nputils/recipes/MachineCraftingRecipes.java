package info.nukepowered.nputils.recipes;

import gregtech.api.GTValues;
import gregtech.api.items.OreDictNames;
import gregtech.api.metatileentity.ITieredMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.MarkerMaterials.Tier;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.blocks.BlockMetalCasing.MetalCasingType;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import info.nukepowered.nputils.NPUConfig;
import info.nukepowered.nputils.item.NPUMetaItems;
import info.nukepowered.nputils.machines.NPUTileEntities;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static info.nukepowered.nputils.recipes.NPUCraftingComponents.*;

import java.util.Arrays;


public class MachineCraftingRecipes {
	public static void init() {
		//Removal
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:steam_macerator_bronze"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:steam_macerator_steel"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:bronze_primitive_blast_furnace"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:diesel_engine"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:large_plasma_turbine"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:charger_ev"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:charger_zpm"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:charger_uv"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:charger_max"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:transformer_ev"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:transformer_iv"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:transformer_luv"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:transformer_zpm"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:transformer_uv"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:coke_oven"));
        ModHandler.removeRecipeByName(new ResourceLocation("gregtech:coke_oven_hatch"));
        
        //Power Manipulation Machines
        ItemStack last_bat = (NPUConfig.GT5U.replaceUVwithMAXBat ? NPUMetaItems.MAX_BATTERY : MetaItems.ZPM2).getStackForm();         ModHandler.addShapedRecipe("nputils:charger_ev", MetaTileEntities.CHARGER[GTValues.EV].getStackForm(), "WTW", "WMW", "BCB", 'M', MetaTileEntities.HULL[GTValues.EV].getStackForm(), 'W', new UnificationEntry(OrePrefix.wireGtHex, Materials.Aluminium), 'T', OreDictNames.chestWood, 'B', MetaItems.LAPOTRON_CRYSTAL, 'C', new UnificationEntry(OrePrefix.circuit, Tier.Extreme));ModHandler.addShapedRecipe("nputils:charger_zpm", MetaTileEntities.CHARGER[GTValues.ZPM].getStackForm(), "WTW", "WMW", "BCB", 'M', MetaTileEntities.HULL[GTValues.ZPM].getStackForm(), 'W', new UnificationEntry(OrePrefix.wireGtHex, Materials.Naquadah), 'T', OreDictNames.chestWood, 'B', (NPUConfig.GT5U.enableZPMandUVBats ? NPUMetaItems.ENERGY_MODULE : MetaItems.ENERGY_LAPOTRONIC_ORB2), 'C', new UnificationEntry(OrePrefix.circuit, Tier.Ultimate));
        ModHandler.addShapedRecipe("nputils:charger_uv", MetaTileEntities.CHARGER[GTValues.UV].getStackForm(), "WTW", "WMW", "BCB", 'M', MetaTileEntities.HULL[GTValues.UV].getStackForm(), 'W', new UnificationEntry(OrePrefix.wireGtHex, Materials.NaquadahAlloy), 'T', OreDictNames.chestWood, 'B', (NPUConfig.GT5U.enableZPMandUVBats ? NPUMetaItems.ENERGY_CLUSTER : last_bat), 'C', new UnificationEntry(OrePrefix.circuit, Tier.Superconductor));
        ModHandler.addShapedRecipe("nputils:charger_max", MetaTileEntities.CHARGER[GTValues.MAX].getStackForm(), "WTW", "WMW", "BCB", 'M', MetaTileEntities.HULL[GTValues.MAX].getStackForm(), 'W', new UnificationEntry(OrePrefix.wireGtHex, Tier.Superconductor), 'T', OreDictNames.chestWood, 'B', last_bat, 'C', new UnificationEntry(OrePrefix.circuit, Tier.Infinite));

        ModHandler.addShapedRecipe("nputils:transformer_ev", MetaTileEntities.TRANSFORMER[GTValues.EV - 1].getStackForm(), "KBB", "CM ", "KBB", 'M', MetaTileEntities.HULL[GTValues.HV].getStackForm(), 'C', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Aluminium), 'B', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Gold), 'K', MetaItems.SMALL_COIL);
        ModHandler.addShapedRecipe("nputils:transformer_iv", MetaTileEntities.TRANSFORMER[GTValues.IV - 1].getStackForm(), "KBB", "CM ", "KBB", 'M', MetaTileEntities.HULL[GTValues.EV].getStackForm(), 'C', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Tungsten), 'B', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Aluminium), 'K', MetaItems.SMALL_COIL);
        ModHandler.addShapedRecipe("nputils:transformer_luv", MetaTileEntities.TRANSFORMER[GTValues.LuV - 1].getStackForm(), "KBB", "CM ", "KBB", 'M', MetaTileEntities.HULL[GTValues.IV].getStackForm(), 'C', new UnificationEntry(OrePrefix.cableGtSingle, Materials.VanadiumGallium), 'B', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Tungsten), 'K', MetaItems.POWER_INTEGRATED_CIRCUIT);
        ModHandler.addShapedRecipe("nputils:transformer_zpm", MetaTileEntities.TRANSFORMER[GTValues.ZPM - 1].getStackForm(), "KBB", "CM ", "KBB", 'M', MetaTileEntities.HULL[GTValues.LuV].getStackForm(), 'C', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Naquadah), 'B', new UnificationEntry(OrePrefix.cableGtSingle, Materials.VanadiumGallium), 'K', MetaItems.POWER_INTEGRATED_CIRCUIT);
        ModHandler.addShapedRecipe("nputils:transformer_uv", MetaTileEntities.TRANSFORMER[GTValues.UV - 1].getStackForm(), "KBB", "CM ", "KBB", 'M', MetaTileEntities.HULL[GTValues.ZPM].getStackForm(), 'C', new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.NaquadahAlloy), 'B', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Naquadah), 'K', MetaItems.HIGH_POWER_INTEGRATED_CIRCUIT);

        //Steam Machines
        ModHandler.addShapedRecipe("nputils:steam_macerator_bronze", MetaTileEntities.STEAM_MACERATOR_BRONZE.getStackForm(), "DXD", "XMX", "PXP", 'M', MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.BRONZE_HULL), 'X', new UnificationEntry(OrePrefix.pipeSmall, Materials.Bronze), 'P', OreDictNames.craftingPiston, 'D', new ItemStack(Items.FLINT));
        ModHandler.addShapedRecipe("nputils:steam_macerator_steel", MetaTileEntities.STEAM_MACERATOR_STEEL.getStackForm(), "DXD", "XMX", "PXP", 'M', MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.STEEL_HULL), 'X', new UnificationEntry(OrePrefix.pipeSmall, Materials.Steel), 'P', OreDictNames.craftingPiston, 'D', new ItemStack(Items.FLINT));
        ModHandler.addShapedRecipe("nputils:steam_mixer", NPUTileEntities.STEAM_MIXER.getStackForm(), "GRG", "GPG", "PMP", 'M', MetaBlocks.MACHINE_CASING.getItemVariant(BlockMachineCasing.MachineCasingType.BRONZE_HULL), 'P', new UnificationEntry(OrePrefix.pipeSmall, Materials.Bronze), 'R', "rotorBronze", 'G', "blockGlass");

        //MultiBlocks
        ModHandler.addShapedRecipe("nputils:primitive_blast_furnace", MetaTileEntities.PRIMITIVE_BLAST_FURNACE.getStackForm(), "hRS", "PBR", "dRS", 'R', OreDictUnifier.get(OrePrefix.stick, Materials.Iron), 'S', OreDictUnifier.get(OrePrefix.screw, Materials.Iron), 'P', "plateIron", 'B', MetaBlocks.METAL_CASING.getItemVariant(BlockMetalCasing.MetalCasingType.PRIMITIVE_BRICKS));
        ModHandler.addShapedRecipe("nputils:coke_oven", MetaTileEntities.COKE_OVEN.getStackForm(), "hRS", "PBR", "dRS", 'R', "stickIron", 'S', "screwIron", 'P', "plateIron", 'B', MetaBlocks.METAL_CASING.getItemVariant(MetalCasingType.COKE_BRICKS));
        ModHandler.addShapedRecipe("nputils:coke_oven_hatch", MetaTileEntities.COKE_OVEN_HATCH.getStackForm(), "B", "C", 'B', MetaTileEntities.COKE_OVEN.getStackForm(), 'C', new ItemStack(Blocks.GLASS));
        ModHandler.addShapedRecipe("nputils:diesel_engine", MetaTileEntities.DIESEL_ENGINE.getStackForm(), "PCP", "EME", "GWG", 'M', MetaTileEntities.HULL[GTValues.EV].getStackForm(), 'P', MetaItems.ELECTRIC_PISTON_EV, 'E', MetaItems.ELECTRIC_MOTOR_EV, 'C', new UnificationEntry(OrePrefix.circuit, Tier.Elite), 'W', new UnificationEntry(OrePrefix.wireGtSingle, Materials.TungstenSteel), 'G', new UnificationEntry(OrePrefix.gear, Materials.Titanium));
        ModHandler.addShapedRecipe("nputils:large_plasma_turbine", MetaTileEntities.LARGE_PLASMA_TURBINE.getStackForm(), "PSP", "SAS", "CSC", 'S', new UnificationEntry(OrePrefix.gear, Materials.TungstenSteel), 'P', new UnificationEntry(OrePrefix.circuit, Tier.Master), 'A', MetaTileEntities.HULL[GTValues.UV].getStackForm(), 'C', OreDictUnifier.get(OrePrefix.pipeLarge, Materials.TungstenSteel));
        ModHandler.addShapedRecipe("nputils:assembly_line", NPUTileEntities.ASSEMBLY_LINE.getStackForm(), "CRC", "SAS", "CRC", 'A', MetaTileEntities.HULL[GTValues.IV].getStackForm(), 'R', MetaItems.ROBOT_ARM_IV, 'C', MetaBlocks.MUTLIBLOCK_CASING.getItemVariant(BlockMultiblockCasing.MultiblockCasingType.ASSEMBLER_CASING), 'S', new UnificationEntry(OrePrefix.circuit, Tier.Elite));
        ModHandler.addShapedRecipe("nputils:processing_array", NPUTileEntities.PROCESSING_ARRAY.getStackForm(), "CBC","RHR","CDC",'H',MetaTileEntities.HULL[GTValues.IV].getStackForm(), 'R',MetaItems.ROBOT_ARM_IV, 'C', new UnificationEntry(OrePrefix.valueOf("circuit"), Tier.Elite), 'B', MetaItems.ENERGY_LAPOTRONIC_ORB, 'D', new UnificationEntry(OrePrefix.pipeLarge, Materials.TungstenSteel));
        
        //Generators
        registerMachineRecipe(NPUTileEntities.NAQUADAH_REACTOR, "RCR", "FMF", "QCQ", 'M', HULL, 'Q', CABLE_QUAD, 'C', BETTER_CIRCUIT, 'F', FIELD_GENERATOR, 'R', STICK_RADIOACTIVE);

        //Machines
        registerMachineRecipe(NPUTileEntities.CLUSTERMILL, "MMM", "CHC", "MMM", 'M', MOTOR, 'C', CIRCUIT, 'H', HULL);
        registerMachineRecipe(NPUTileEntities.CIRCUITASSEMBLER, "ACE", "VMV", "WCW", 'M', HULL, 'V', CONVEYOR, 'A', ROBOT_ARM, 'C', BETTER_CIRCUIT, 'W', CABLE, 'E', EMITTER);
        registerMachineRecipe(NPUTileEntities.DISASSEMBLER, "SCS", "WHW", "RCR", 'S', SENSOR, 'C', CIRCUIT, 'H', HULL, 'W', CABLE, 'R', ROBOT_ARM);
        if (NPUConfig.GT5U.highTierPumps)
            registerMachineRecipe(NPUTileEntities.PUMP, "WGW", "GMG", "TGT", 'M', HULL, 'W', CIRCUIT, 'G', PUMP, 'T', PIPE);
        if (NPUConfig.GT5U.highTierAlloySmelter)
            registerMachineRecipe(NPUTileEntities.ALLOY_SMELTER, "ECE", "CMC", "WCW", 'M', HULL, 'E', CIRCUIT, 'W', CABLE, 'C', COIL_HEATING_DOUBLE);
        if (NPUConfig.GT5U.highTierAssemblers)
            registerMachineRecipe(NPUTileEntities.ASSEMBLER, "ACA", "VMV", "WCW", 'M', HULL, 'V', CONVEYOR, 'A', ROBOT_ARM, 'C', CIRCUIT, 'W', CABLE);
        if (NPUConfig.GT5U.highTierBenders)
            registerMachineRecipe(NPUTileEntities.BENDER, "PWP", "CMC", "EWE", 'M', HULL, 'E', MOTOR, 'P', PISTON, 'C', CIRCUIT, 'W', CABLE);
        if (NPUConfig.GT5U.highTierCanners)
            registerMachineRecipe(NPUTileEntities.CANNER, "WPW", "CMC", "GGG", 'M', HULL, 'P', PUMP, 'C', CIRCUIT, 'W', CABLE, 'G', GLASS);
        if (NPUConfig.GT5U.highTierCompressors)
            registerMachineRecipe(NPUTileEntities.COMPRESSOR, " C ", "PMP", "WCW", 'M', HULL, 'P', PISTON, 'C', CIRCUIT, 'W', CABLE);
        if (NPUConfig.GT5U.highTierCutters)
            registerMachineRecipe(NPUTileEntities.CUTTER, "WCG", "VMB", "CWE", 'M', HULL, 'E', MOTOR, 'V', CONVEYOR, 'C', CIRCUIT, 'W', CABLE, 'G', GLASS, 'B', OreDictNames.craftingDiamondBlade);
        if (NPUConfig.GT5U.highTierElectricFurnace)
            registerMachineRecipe(NPUTileEntities.ELECTRIC_FURNACE, "ECE", "CMC", "WCW", 'M', HULL, 'E', CIRCUIT, 'W', CABLE, 'C', COIL_HEATING);
        if (NPUConfig.GT5U.highTierExtractors)
            registerMachineRecipe(NPUTileEntities.EXTRACTOR, "GCG", "EMP", "WCW", 'M', HULL, 'E', PISTON, 'P', PUMP, 'C', CIRCUIT, 'W', CABLE, 'G', GLASS);
        if (NPUConfig.GT5U.highTierExtruders)
            registerMachineRecipe(NPUTileEntities.EXTRUDER, "CCE", "XMP", "CCE", 'M', HULL, 'X', PISTON, 'E', CIRCUIT, 'P', PIPE, 'C', COIL_HEATING_DOUBLE);
        if (NPUConfig.GT5U.highTierLathes)
            registerMachineRecipe(NPUTileEntities.LATHE, "WCW", "EMD", "CWP", 'M', HULL, 'E', MOTOR, 'P', PISTON, 'C', CIRCUIT, 'W', CABLE, 'D', DIAMOND);
        if (NPUConfig.GT5U.highTierMacerators)
            registerMachineRecipe(NPUTileEntities.MACERATOR, "PEG", "WWM", "CCW", 'M', HULL, 'E', MOTOR, 'P', PISTON, 'C', CIRCUIT, 'W', CABLE, 'G', GRINDER);
        if (NPUConfig.GT5U.highTierMicrowaves)
            registerMachineRecipe(NPUTileEntities.MICROWAVE, "LWC", "LMR", "LEC", 'M', HULL, 'E', MOTOR, 'R', EMITTER, 'C', CIRCUIT, 'W', CABLE, 'L', new UnificationEntry(OrePrefix.plate, Materials.Lead));
        if (NPUConfig.GT5U.highTierWiremills)
            registerMachineRecipe(NPUTileEntities.WIREMILL, "EWE", "CMC", "EWE", 'M', HULL, 'E', MOTOR, 'C', CIRCUIT, 'W', CABLE);
        if (NPUConfig.GT5U.highTierCentrifuges)
            registerMachineRecipe(NPUTileEntities.CENTRIFUGE, "CEC", "WMW", "CEC", 'M', HULL, 'E', MOTOR, 'C', CIRCUIT, 'W', CABLE);
        if (NPUConfig.GT5U.highTierElectrolyzers)
            registerMachineRecipe(NPUTileEntities.ELECTROLYZER, "IGI", "IMI", "CWC", 'M', HULL, 'C', CIRCUIT, 'W', CABLE, 'I', WIRE, 'G', GLASS);
        if (NPUConfig.GT5U.highTierThermalCentrifuges)
            registerMachineRecipe(NPUTileEntities.THERMAL_CENTRIFUGE, "CEC", "OMO", "WEW", 'M', HULL, 'E', MOTOR, 'C', CIRCUIT, 'W', CABLE, 'O', COIL_HEATING_DOUBLE);
        if (NPUConfig.GT5U.highTierOreWashers)
            registerMachineRecipe(NPUTileEntities.ORE_WASHER, "RGR", "CEC", "WMW", 'M', HULL, 'R', ROTOR, 'E', MOTOR, 'C', CIRCUIT, 'W', CABLE, 'G', GLASS);
        if (NPUConfig.GT5U.highTierPackers)
            registerMachineRecipe(NPUTileEntities.PACKER, "BCB", "RMV", "WCW", 'M', HULL, 'R', ROBOT_ARM, 'V', CONVEYOR, 'C', CIRCUIT, 'W', CABLE, 'B', OreDictNames.chestWood);
        if (NPUConfig.GT5U.highTierUnpackers)
            registerMachineRecipe(NPUTileEntities.UNPACKER, "BCB", "VMR", "WCW", 'M', HULL, 'R', ROBOT_ARM, 'V', CONVEYOR, 'C', CIRCUIT, 'W', CABLE, 'B', OreDictNames.chestWood);
        if (NPUConfig.GT5U.highTierChemicalReactors)
            registerMachineRecipe(NPUTileEntities.CHEMICAL_REACTOR, "GRG", "WEW", "CMC", 'M', HULL, 'R', ROTOR, 'E', MOTOR, 'C', CIRCUIT, 'W', CABLE, 'G', GLASS);
        if (NPUConfig.GT5U.highTierFluidCanners)
            registerMachineRecipe(NPUTileEntities.FLUID_CANNER, "GCG", "GMG", "WPW", 'M', HULL, 'P', PUMP, 'C', CIRCUIT, 'W', CABLE, 'G', GLASS);
        if (NPUConfig.GT5U.highTierBreweries)
            registerMachineRecipe(NPUTileEntities.BREWERY, "GPG", "WMW", "CBC", 'M', HULL, 'P', PUMP, 'B', STICK_DISTILLATION, 'C', CIRCUIT, 'W', CABLE, 'G', GLASS);
        if (NPUConfig.GT5U.highTierFermenters)
            registerMachineRecipe(NPUTileEntities.FERMENTER, "WPW", "GMG", "WCW", 'M', HULL, 'P', PUMP, 'C', CIRCUIT, 'W', CABLE, 'G', GLASS);
        if (NPUConfig.GT5U.highTierFluidExtractors)
            registerMachineRecipe(NPUTileEntities.FLUID_EXTRACTOR, "GCG", "PME", "WCW", 'M', HULL, 'E', PISTON, 'P', PUMP, 'C', CIRCUIT, 'W', CABLE, 'G', GLASS);
        if (NPUConfig.GT5U.highTierFluidSolidifiers)
            registerMachineRecipe(NPUTileEntities.FLUID_SOLIDIFIER, "PGP", "WMW", "CBC", 'M', HULL, 'P', PUMP, 'C', CIRCUIT, 'W', CABLE, 'G', GLASS, 'B', OreDictNames.chestWood);
        if (NPUConfig.GT5U.highTierDistilleries)
            registerMachineRecipe(NPUTileEntities.DISTILLERY, "GBG", "CMC", "WPW", 'M', HULL, 'P', PUMP, 'B', STICK_DISTILLATION, 'C', CIRCUIT, 'W', CABLE, 'G', GLASS);
        if (NPUConfig.GT5U.highTierChemicalBaths)
            registerMachineRecipe(NPUTileEntities.CHEMICAL_BATH, "VGW", "PGV", "CMC", 'M', HULL, 'P', PUMP, 'V', CONVEYOR, 'C', CIRCUIT, 'W', CABLE, 'G', GLASS);
        if (NPUConfig.GT5U.highTierPolarizers)
            registerMachineRecipe(NPUTileEntities.POLARIZER, "ZSZ", "WMW", "ZSZ", 'M', HULL, 'S', STICK_ELECTROMAGNETIC, 'Z', COIL_ELECTRIC, 'W', CABLE);
        if (NPUConfig.GT5U.highTierElectromagneticSeparators)
            registerMachineRecipe(NPUTileEntities.ELECTROMAGNETIC_SEPARATOR, "VWZ", "WMS", "CWZ", 'M', HULL, 'S', STICK_ELECTROMAGNETIC, 'Z', COIL_ELECTRIC, 'V', CONVEYOR, 'C', CIRCUIT, 'W', CABLE);
        if (NPUConfig.GT5U.highTierAutoclaves)
            registerMachineRecipe(NPUTileEntities.AUTOCLAVE, "IGI", "IMI", "CPC", 'M', HULL, 'P', PUMP, 'C', CIRCUIT, 'I', PLATE, 'G', GLASS);
        if (NPUConfig.GT5U.highTierMixers)
            registerMachineRecipe(NPUTileEntities.MIXER, "GRG", "GEG", "CMC", 'M', HULL, 'E', MOTOR, 'R', ROTOR, 'C', CIRCUIT, 'G', GLASS);
        if (NPUConfig.GT5U.highTierLaserEngravers)
            registerMachineRecipe(NPUTileEntities.LASER_ENGRAVER, "PEP", "CMC", "WCW", 'M', HULL, 'E', EMITTER, 'P', PISTON, 'C', CIRCUIT, 'W', CABLE);
        if (NPUConfig.GT5U.highTierFormingPresses)
            registerMachineRecipe(NPUTileEntities.FORMING_PRESS, "WPW", "CMC", "WPW", 'M', HULL, 'P', PISTON, 'C', CIRCUIT, 'W', CABLE);
        if (NPUConfig.GT5U.highTierForgeHammers)
            registerMachineRecipe(NPUTileEntities.FORGE_HAMMER, "WPW", "CMC", "WAW", 'M', HULL, 'P', PISTON, 'C', CIRCUIT, 'W', CABLE, 'A', OreDictNames.craftingAnvil);
        if (NPUConfig.GT5U.highTierFluidHeaters)
            registerMachineRecipe(NPUTileEntities.FLUID_HEATER, "OGO", "PMP", "WCW", 'M', HULL, 'P', PUMP, 'O', COIL_HEATING_DOUBLE, 'C', CIRCUIT, 'W', CABLE, 'G', GLASS);
        if (NPUConfig.GT5U.highTierSifters)
            registerMachineRecipe(NPUTileEntities.SIFTER, "WFW", "PMP", "CFC", 'M', HULL, 'P', PISTON, 'F', MetaItems.ITEM_FILTER, 'C', CIRCUIT, 'W', CABLE);
        if (NPUConfig.GT5U.highTierArcFurnaces)
            registerMachineRecipe(NPUTileEntities.ARC_FURNACE, "WGW", "CMC", "PPP", 'M', HULL, 'P', PLATE, 'C', CIRCUIT, 'W', CABLE_QUAD, 'G', new UnificationEntry(OrePrefix.ingot, Materials.Graphite));
        if (NPUConfig.GT5U.highTierPlasmaArcFurnaces)
            registerMachineRecipe(NPUTileEntities.PLASMA_ARC_FURNACE, "WGW", "CMC", "TPT", 'M', HULL, 'P', PLATE, 'C', BETTER_CIRCUIT, 'W', CABLE_QUAD, 'T', PUMP, 'G', new UnificationEntry(OrePrefix.ingot, Materials.Graphite));
        registerMachineRecipe(NPUTileEntities.MASS_FAB, "CFC", "QMQ", "CFC", 'M', HULL, 'Q', CABLE_QUAD, 'C', BETTER_CIRCUIT, 'F', FIELD_GENERATOR);
        registerMachineRecipe(NPUTileEntities.REPLICATOR, "EFE", "CMC", "EQE", 'M', HULL, 'Q', CABLE_QUAD, 'C', BETTER_CIRCUIT, 'F', FIELD_GENERATOR, 'E', EMITTER);
        if (NPUConfig.Misc.highTierCollector)
            registerMachineRecipe(NPUTileEntities.AIR_COLLECTOR, "WFW", "PHP", "WCW", 'W', Blocks.IRON_BARS, 'F', MetaItems.ITEM_FILTER, 'P', PUMP, 'H', HULL, 'C', CIRCUIT);
        registerMachineRecipe(NPUTileEntities.FISHER, "QCQ", "RMR", "FCF", 'M', HULL, 'Q', CABLE, 'C', CIRCUIT, 'R', ROBOT_ARM, 'F', Items.FISHING_ROD);
	}
	
	public static <T extends MetaTileEntity & ITieredMetaTileEntity> void registerMachineRecipe(T[] metaTileEntities, Object... recipe) {
		for (T te : metaTileEntities) {
			if (te != null) {
				ModHandler.addShapedRecipe(String.format("nputils:%s", te.getMetaName()), te.getStackForm(), prepareRecipe(te.getTier(), Arrays.copyOf(recipe, recipe.length)));
			}
		}
	}
	
	private static Object[] prepareRecipe(int tier, Object... recipe) {
		for (int i = 3; i < recipe.length; i++) {
			if (recipe[i] instanceof NPUCraftingComponents) {
				recipe[i] =  ((NPUCraftingComponents) recipe[i]).getIngredient(tier);
			}
		}
		return recipe;
	}
	
	
	
	
}
