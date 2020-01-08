package info.nukepowered.nputils.item;

import gregtech.api.items.metaitem.stats.IItemColorProvider;
import gregtech.api.items.metaitem.stats.IItemNameProvider;
import gregtech.api.unification.material.type.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class CoinInfoBehaviour implements IItemColorProvider, IItemNameProvider {
	
	private final Material material;
	
	public CoinInfoBehaviour(Material material) {
		this.material = material;
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack itemStack, String unlocalizedName) {
		return I18n.format("metaitem.coin.name", this.material.getLocalizedName());
	}

	@Override
	public int getItemStackColor(ItemStack itemStack, int tintIndex) {
		return this.material.materialRGB;
	}

}
