package info.nukepowered.nputils.armor;

import forestry.api.apiculture.ApicultureCapabilities;
import forestry.api.apiculture.IArmorApiarist;
import gregtech.api.items.armor.IArmorLogic;
import gregtech.api.items.metaitem.stats.IItemCapabilityProvider;
import info.nukepowered.nputils.api.NPULib;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ApiaristProvider implements IItemCapabilityProvider {

	@Override
	public ICapabilityProvider createProvider(ItemStack item) {
		return new ICapabilityProvider() {
			
			@Override
			public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
				return capability == ApicultureCapabilities.ARMOR_APIARIST;
			}
			
			@Override
			public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
				if (capability == ApicultureCapabilities.ARMOR_APIARIST) {
					IArmorLogic logic = NPULib.getArmorLogic(item);
					if (logic instanceof IArmorApiarist) {
						return ApicultureCapabilities.ARMOR_APIARIST.cast((IArmorApiarist) logic);
					}
				}
				
				return null;
			}
		};
	}
}
