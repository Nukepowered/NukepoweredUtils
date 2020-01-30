package info.nukepowered.nputils.armor;

import forestry.api.apiculture.ApicultureCapabilities;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.items.metaitem.stats.IItemCapabilityProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ApiaristArmorBehaviour implements IItemCapabilityProvider {

	@Override
	public ICapabilityProvider createProvider(ItemStack item) {
		return new ICapabilityProvider() {
			
			@Override
			public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
				return canUse(item) ? capability == ApicultureCapabilities.ARMOR_APIARIST : false;
			}
			
			@Override
			public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
				if (capability == ApicultureCapabilities.ARMOR_APIARIST) {
					return canUse(item) ? capability.getDefaultInstance() : null;
				} else {
					return null;
				}
			}
		};
	}
	
	private boolean canUse(ItemStack item) {
		IElectricItem container = item.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
		if (container != null) {
			if (container.canUse(container.getTransferLimit())) {
				return true;
			}
		}
		
		return false;
	}
}
