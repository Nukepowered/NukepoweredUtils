package info.nukepowered.nputils.item;

import java.util.List;

import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.stats.IItemBehaviour;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;;

public class CoinBehaviour implements IItemBehaviour {
	
	private final int value;
	
	public CoinBehaviour(int value) {
		this.value = value;
	}
	
	public static int getCoinValue(MetaItem<?>.MetaValueItem metaItem) {
		int ret = -1;
		
		for (IItemBehaviour beh : metaItem.getBehaviours()) {
			if (beh instanceof CoinBehaviour) {
				ret = ((CoinBehaviour) beh).getValue();
			}
		}
		
		return ret;
	}
	
	public int getValue() {
		return this.value;
	}
	
	@Override
	public void addInformation(ItemStack itemStack, List<String> lines) {
		// Remove GT's coins tooltip
		if (lines.size() == 2) lines.remove(1);
		lines.add(I18n.format("metaitem.coin.value", this.value));
    }
}
