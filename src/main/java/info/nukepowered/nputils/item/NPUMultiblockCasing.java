package info.nukepowered.nputils.item;

import gregtech.common.blocks.VariantBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.IStringSerializable;

public class NPUMultiblockCasing extends VariantBlock<NPUMultiblockCasing.CasingType> {
	public NPUMultiblockCasing() {
		super(Material.IRON);
		setUnlocalizedName("npu_multiblock_casing");
		setHardness(5.0f);
		setResistance(10.0f);
		setSoundType(SoundType.METAL);
		setHarvestLevel("wrench", 2);
		setDefaultState(getState(CasingType.TUNGSTENSTEEL_GEARBOX_CASING));
	}
	
	
	public enum CasingType implements IStringSerializable {

//		REACTOR_CASING("reactor_casing"),
		TUNGSTENSTEEL_GEARBOX_CASING("tungstensteel_gearbox_casing");
		
		private final String name;
		
		CasingType(String name) {
			this.name = name;
		}
		
		@Override
		public String getName() {
			return this.name;
		}
	}
}
