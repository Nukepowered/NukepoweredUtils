package info.nukepowered.nputils.jei.info;

import java.util.ArrayList;
import java.util.List;

import gregtech.api.GTValues;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.DustMaterial;
import gregtech.common.blocks.BlockCompressed;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.integration.jei.multiblock.MultiblockInfoPage;
import gregtech.integration.jei.multiblock.MultiblockShapeInfo;

import info.nukepowered.nputils.blocks.BlockInductionCoil.InductionCoilType;
import info.nukepowered.nputils.blocks.NPUMetaBlocks;
import info.nukepowered.nputils.crafttweaker.NPUMultiblockCasing.CasingType;
import info.nukepowered.nputils.machines.NPUTileEntities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

/**
 * @author TheDarkDnKTv
 *
 * Copyright (C) 2021 TheDarkDnKTv
 */
public class InductionBlastFurnanceInfo extends MultiblockInfoPage {

	@Override
	public float getDefaultZoom() {
        return 0.8F;
    }
	
	@Override
	public MultiblockControllerBase getController() {
		return NPUTileEntities.INDUCTION_BLAST_FURNANCE;
	}

	@Override
	public List<MultiblockShapeInfo> getMatchingShapes() {
		final DustMaterial material = Materials.Graphite;
		final BlockCompressed block = MetaBlocks.COMPRESSED.get(material);
		IBlockState graphiteBlock = block.getDefaultState().withProperty(block.variantProperty, material);
		List<MultiblockShapeInfo> result = new ArrayList<>();
		
		
		for (InductionCoilType type : InductionCoilType.values()) {
			result.add(MultiblockShapeInfo.builder()
				.aisle("#HHH#", "#CCC#", "#PPP#", "#CCC#", "#HHH#")
	            .aisle("HHHHH", "CCGCC", "PPGPP", "CCGCC", "HHHHH")
	            .aisle("SHOHE", "CG#GC", "PG#GF", "CG#GC", "HHIHH")
	            .aisle("HHHHH", "CCGCC", "PPGPP", "CCGCC", "HHHHH")
	            .aisle("#HHH#", "#CCC#", "#PPP#", "#CCC#", "#HHH#")
	            .where('S', NPUTileEntities.INDUCTION_BLAST_FURNANCE, EnumFacing.WEST)
	            .where('H', NPUMetaBlocks.MULTIBLOCK_CASING.getState(CasingType.MAGNETIC_PROOF_MACHINE_CASING))
	            .where('P', NPUMetaBlocks.MULTIBLOCK_CASING.getState(CasingType.COOLANT_TRUNK_LINE))
	            .where('C', NPUMetaBlocks.INDUCTION_COIL.getState(type))
	            .where('G', graphiteBlock)
	            .where('I', MetaTileEntities.ITEM_IMPORT_BUS[GTValues.MV], EnumFacing.UP)
	            .where('O', MetaTileEntities.ITEM_EXPORT_BUS[GTValues.MV], EnumFacing.DOWN)
	            .where('F', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.MV], EnumFacing.EAST)
	            .where('E', MetaTileEntities.ENERGY_INPUT_HATCH[GTValues.MV], EnumFacing.EAST)
	            .where('#', Blocks.AIR.getDefaultState())
	            .build());
		}

        return result;
	}

	@Override
	public String[] getDescription() {
		return new String[]{
			I18n.format("gregtech.multiblock.induction_blast_furnance.description.general"),
			I18n.format("gregtech.multiblock.induction_blast_furnance.description.efficiency"),
			I18n.format("gregtech.multiblock.induction_blast_furnance.description.coolant")
		};
	}

}
