package info.nukepowered.nputils.machines;

import static gregtech.api.multiblock.BlockPattern.RelativeDirection.BACK;
import static gregtech.api.multiblock.BlockPattern.RelativeDirection.DOWN;
import static gregtech.api.multiblock.BlockPattern.RelativeDirection.LEFT;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.multiblock.BlockPattern;
import gregtech.api.multiblock.FactoryBlockPattern;
import gregtech.api.render.ICubeRenderer;
import gregtech.api.render.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import info.nukepowered.nputils.blocks.NPUMetaBlocks;
import info.nukepowered.nputils.blocks.NPUTransparentCasing;
import info.nukepowered.nputils.crafttweaker.NPUMultiblockCasing;
import info.nukepowered.nputils.recipes.NPURecipeMaps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public class TileEntityAssemblyLine extends RecipeMapMultiblockController {
	public TileEntityAssemblyLine(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, NPURecipeMaps.ASSEMBLY_LINE_RECIPES);
	}
	
	@Override
	public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
		return new TileEntityAssemblyLine(metaTileEntityId);
	}
	
	@Override
    protected BlockPattern createStructurePattern() {
        FactoryBlockPattern.start();
		return FactoryBlockPattern.start(LEFT, DOWN, BACK)
                .aisle("#Y#", "GAG", "RTR", "COC")
                .aisle("#Y#", "GAG", "RTR", "FIF").setRepeatable(3, 14)
                .aisle("#Y#", "GSG", "RTR", "FIF")
                .where('S', selfPredicate())
                .where('C', statePredicate(getCasingState()))
                .where('F', statePredicate(getCasingState()).or(abilityPartPredicate(MultiblockAbility.IMPORT_FLUIDS)))
                .where('O', statePredicate(getCasingState()).or(abilityPartPredicate(MultiblockAbility.EXPORT_ITEMS)))
                .where('Y', statePredicate(getCasingState()).or(abilityPartPredicate(MultiblockAbility.INPUT_ENERGY)))
                .where('I', tilePredicate((state, tile) -> {
                    return tile.metaTileEntityId.equals(MetaTileEntities.ITEM_IMPORT_BUS[0].metaTileEntityId);
                }))
                .where('G', statePredicate(MetaBlocks.MUTLIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.GRATE_CASING)))
                .where('A', statePredicate(MetaBlocks.MUTLIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.ASSEMBLER_CASING)))
                .where('R', statePredicate(NPUMetaBlocks.TRANSPARENT_CASING.getState(NPUTransparentCasing.CasingType.REINFORCED_GLASS)))
                .where('T', statePredicate(NPUMetaBlocks.MULTIBLOCK_CASING.getState(NPUMultiblockCasing.CasingType.TUNGSTENSTEEL_GEARBOX_CASING)))
                .where('#', (tile) -> {
                    return true;
                })
                .build();
	}
	
	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
		return Textures.SOLID_STEEL_CASING;
	}
	
	protected IBlockState getCasingState() {
		return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
	}
}
