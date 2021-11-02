package info.nukepowered.nputils.jei.info;

import java.util.ArrayList;
import java.util.List;

import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.integration.jei.multiblock.MultiblockInfoPage;
import gregtech.integration.jei.multiblock.MultiblockShapeInfo;

import info.nukepowered.nputils.blocks.NPUMetaBlocks;
import info.nukepowered.nputils.blocks.NPUTransparentCasing;
import info.nukepowered.nputils.crafttweaker.NPUMultiblockCasing;
import info.nukepowered.nputils.machines.NPUTileEntities;

import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

public class AsseblyLineInfo extends MultiblockInfoPage {

	@Override
	public MultiblockControllerBase getController() {
		return NPUTileEntities.ASSEMBLY_LINE;
	}

	@Override
	public List<MultiblockShapeInfo> getMatchingShapes() {
		List<MultiblockShapeInfo> shapes = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            NPUMultiblockShapeInfo.Builder builder = NPUMultiblockShapeInfo.builder();
            builder.aisle("CIC", "RTR", "GSG", "#Y#");
            for (int num = 0; num < 3 + i; num++) {
                if (num == 4 || num == 9)
                    builder.aisle("FIf", "RTR", "GAG", "#Y#");
                else
                    builder.aisle("CIC", "RTR", "GAG", "#Y#");
            }
            builder.aisle("COC", "RTR", "GAG", "#Y#")
                    .where('S', NPUTileEntities.ASSEMBLY_LINE, EnumFacing.NORTH)
                    .where('C', MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID))
                    .where('F', MetaTileEntities.FLUID_IMPORT_HATCH[4], EnumFacing.WEST)
                    .where('f', MetaTileEntities.FLUID_IMPORT_HATCH[4], EnumFacing.EAST)
                    .where('O', MetaTileEntities.ITEM_EXPORT_BUS[4], EnumFacing.DOWN)
                    .where('Y', MetaTileEntities.ENERGY_INPUT_HATCH[4], EnumFacing.UP)
                    .where('I', MetaTileEntities.ITEM_IMPORT_BUS[0], EnumFacing.DOWN)
                    .where('G', MetaBlocks.MUTLIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.GRATE_CASING))
                    .where('A', MetaBlocks.MUTLIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.ASSEMBLER_CASING))
                    .where('R', NPUMetaBlocks.TRANSPARENT_CASING.getState(NPUTransparentCasing.CasingType.REINFORCED_GLASS))
                    .where('T', NPUMetaBlocks.MULTIBLOCK_CASING.getState(NPUMultiblockCasing.CasingType.TUNGSTENSTEEL_GEARBOX_CASING))
                    .where('#', Blocks.AIR.getDefaultState());
            shapes.add(builder.build());
        }
        return shapes;
	}

	@Override
	public String[] getDescription() {
		return new String[]{I18n.format("gregtech.multiblock.assembly_line.description")};
	}

}
