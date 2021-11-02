package info.nukepowered.nputils.jei.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.util.BlockInfo;
import gregtech.common.blocks.MetaBlocks;
import gregtech.integration.jei.multiblock.MultiblockShapeInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public class NPUMultiblockShapeInfo extends MultiblockShapeInfo {
	private final BlockInfo[][][] blocks; // Z, Y, X
	
	public NPUMultiblockShapeInfo(BlockInfo[][][] blocks) {
		super(blocks);
		this.blocks = blocks;
	}
	
	public BlockInfo[][][] getBlocks() {
		return blocks;
	}
	
	public static Builder builder() {
		return new NPUMultiblockShapeInfo.Builder();
	}
	
	public static class Builder extends MultiblockShapeInfo.Builder {
		private List<String[]> shape = new ArrayList<>();
        private Map<Character, BlockInfo> symbolMap = new HashMap<>();

        public Builder aisle(String... data) {
            this.shape.add(data);
            return this;
        }

        public Builder where(char symbol, BlockInfo value) {
            this.symbolMap.put(symbol, value);
            return this;
        }

        public Builder where(char symbol, IBlockState blockState) {
            return where(symbol, new BlockInfo(blockState));
        }

        public Builder where(char symbol, MetaTileEntity tileEntity, EnumFacing frontSide) {
            MetaTileEntityHolder holder = new MetaTileEntityHolder();
            holder.setMetaTileEntity(tileEntity);
            holder.getMetaTileEntity().setFrontFacing(frontSide);
            return where(symbol, new BlockInfo(MetaBlocks.MACHINE.getDefaultState(), holder));
        }

        private BlockInfo[][][] bakeArray() {
            BlockInfo[][][] blockInfos = new BlockInfo[shape.size()][][];
            for(int i = 0; i < blockInfos.length; i++) {
                String[] aisleEntry = shape.get(i);
                BlockInfo[][] aisleData = new BlockInfo[aisleEntry.length][];
                for(int j = 0; j < aisleData.length; j++) {
                    String columnEntry = aisleEntry[j];
                    BlockInfo[] columnData = new BlockInfo[columnEntry.length()];
                    for(int k = 0; k < columnData.length; k++) {
                        columnData[k] = symbolMap.getOrDefault(columnEntry.charAt(k), BlockInfo.EMPTY);
                        if(columnData[k].getTileEntity() != null && columnData[k].getTileEntity() instanceof MetaTileEntityHolder) {

                            MetaTileEntityHolder holder = (MetaTileEntityHolder) columnData[k].getTileEntity();

                            MetaTileEntityHolder newHolder = new MetaTileEntityHolder();
                            newHolder.setMetaTileEntity(holder.getMetaTileEntity().createMetaTileEntity(newHolder));
                            newHolder.getMetaTileEntity().setFrontFacing(holder.getMetaTileEntity().getFrontFacing());

                            columnData[k] = new BlockInfo(columnData[k].getBlockState(), newHolder);
                        }
                    }
                    aisleData[j] = columnData;
                }
                blockInfos[i] = aisleData;
            }
            return blockInfos;
        }

        public NPUMultiblockShapeInfo build() {
            return new NPUMultiblockShapeInfo(bakeArray());
        }
	}
	
	
}
