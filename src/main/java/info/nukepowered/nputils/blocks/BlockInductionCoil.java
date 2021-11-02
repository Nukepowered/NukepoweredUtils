package info.nukepowered.nputils.blocks;

import java.util.List;

import javax.annotation.Nullable;

import gregtech.common.blocks.VariantBlock;
import gregtech.common.blocks.VariantItemBlock;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author TheDarkDnKTv
 *
 * Copyright (C) 2021 TheDarkDnKTv
 */
public class BlockInductionCoil extends VariantBlock<BlockInductionCoil.InductionCoilType> {

	
	public BlockInductionCoil() {
		super(Material.IRON);
		setTranslationKey("induction_coil_block");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(InductionCoilType.COPPER));
	}

	@Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, @Nullable World worldIn, List<String> lines, ITooltipFlag tooltipFlag) {
        super.addInformation(itemStack, worldIn, lines, tooltipFlag);
        IBlockState stackState = getBlockState(itemStack);
        InductionCoilType coilType = getState(stackState);
        lines.add(I18n.format("tile.induction_coil_block.tooltip.temp", coilType.heatingTemp));
    }
	
	@Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type) {
        return false;
    }
	
	@SuppressWarnings({"deprecation", "unchecked"})
    public IBlockState getBlockState(ItemStack stack) {
		VariantItemBlock<InductionCoilType, BlockInductionCoil> itemBlock = (VariantItemBlock<InductionCoilType, BlockInductionCoil>) stack.getItem();
        return itemBlock.getBlock().getStateFromMeta(itemBlock.getMetadata(stack.getItemDamage()));
    }
	
	public static enum InductionCoilType implements IStringSerializable {
		COPPER("copper_coil", 4700),
		SILVER_PLATED_COPPER("silver_plated_copper_coil", 5800),
		FINE_GOLD_WIRE("fine_gold_coil", 7500),
		SUPERCONDUCTING("superconducting_coil", 10000);

		public final String name;
		public final int heatingTemp;
		
		InductionCoilType(String name, int heatingTemp) {
			this.name = name;
			this.heatingTemp = heatingTemp;
		}
		
		@Override
		public String getName() {
			return this.name;
		}
		
	}
}
