package info.nukepowered.nputils.jei.info;

import java.util.Collections;
import java.util.List;

import gregtech.api.GTValues;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.common.blocks.BlockMetalCasing.MetalCasingType;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.integration.jei.multiblock.MultiblockInfoPage;
import gregtech.integration.jei.multiblock.MultiblockShapeInfo;
import info.nukepowered.nputils.machines.NPUTileEntities;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

public class ProcessingArrayInfo extends MultiblockInfoPage {

	@Override
	public MultiblockControllerBase getController() {
		return NPUTileEntities.PROCESSING_ARRAY;
	}

	@Override
	public List<MultiblockShapeInfo> getMatchingShapes() {
		MultiblockShapeInfo shapeInfo = MultiblockShapeInfo.builder()
				.aisle("XXX", "XXX", "XXX")
				.aisle("OXX", "SAE", "IXX")
				.aisle("XXX", "XXX", "XXX")
				.where('X', MetaBlocks.METAL_CASING.getState(MetalCasingType.TUNGSTENSTEEL_ROBUST))
				.where('S', NPUTileEntities.PROCESSING_ARRAY, EnumFacing.WEST)
				.where('A', Blocks.AIR.getDefaultState())
				.where('O', MetaTileEntities.ITEM_EXPORT_BUS[GTValues.LV], EnumFacing.WEST)
				.where('I', MetaTileEntities.ITEM_IMPORT_BUS[GTValues.LV], EnumFacing.WEST)
				.where('E', MetaTileEntities.ENERGY_INPUT_HATCH[GTValues.HV], EnumFacing.EAST)
				.build();
		return Collections.singletonList(shapeInfo);
	}

	@Override
	public String[] getDescription() {
		return  new String[]{I18n.format("gregtech.multiblock.processing_array.description")};
	}
	
}
