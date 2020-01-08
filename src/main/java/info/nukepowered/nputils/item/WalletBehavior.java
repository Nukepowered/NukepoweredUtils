package info.nukepowered.nputils.item;

import java.util.List;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.gui.widgets.DynamicLabelWidget;
import gregtech.api.items.gui.ItemUIFactory;
import gregtech.api.items.gui.PlayerInventoryHolder;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.items.metaitem.stats.IItemBehaviour;
import gregtech.api.util.GTUtility;
import info.nukepowered.nputils.NPULog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class WalletBehavior implements IItemBehaviour, ItemUIFactory {
	
	private void giveCoin(EntityPlayer player, ItemStack stack, int value) {
		int money = WalletBehavior.getMoney(stack);
		NBTTagCompound data = GTUtility.getOrCreateNbtCompound(stack);
		if (money >= value) {
			money -= value;
			MetaItem<?>.MetaValueItem coin = null;
			
			for (MetaItem<?>.MetaValueItem current : NPUMetaItems.COINS) {
				if (value == CoinBehaviour.getCoinValue(current)) {
					coin = current;
				}
			}
			
			if (coin == null) {
				NPULog.fatal("Incorrect coin value for WalletBehavoior#giveCoin", new IllegalArgumentException());
				return;
			}
			
			if (player.addItemStackToInventory(coin.getStackForm())) {
				data.setInteger("MoneyAmount", money);
			}
			
			
		}
	}
	
	public static int getMoney(ItemStack stack) {
		if (stack == null) return -1;
		int money = 0;
		NBTTagCompound data = GTUtility.getOrCreateNbtCompound(stack);
		if (data.hasKey("MoneyAmount")) 
			money = data.getInteger("MoneyAmount");
		return money;
	}
	
	@Override
	public ModularUI createUI(PlayerInventoryHolder holder, EntityPlayer entityPlayer) {
		ModularUI.Builder ui = ModularUI.builder(GuiTextures.BACKGROUND, 176, 104)
				.label(6, 6, I18n.format("metaitem.coin_wallet.name"))
				.widget(new DynamicLabelWidget(62, 20, () -> I18n.format("metaitem.coin_wallet.money_amont", WalletBehavior.getMoney(holder.getCurrentItem()))));
		
		for (int i = 0; i < NPUMetaItems.COINS.size(); i++) {
			int posX = 11;
			int posY = 34;
			int width = 28;
			int height = 18;
			int coinValue = CoinBehaviour.getCoinValue(NPUMetaItems.COINS.get(i));
			
			// Adjust positions of every button
			posY = posY + (i / 5 * 21);
			posX = posX + ((i % 5) * (width + 3));
			
			// UI size cannot fit more
			if (i > 15) {
				NPULog.warn("In-game registered more than 15 types of coins, cannnot display all of it in UI");
				break;
			}
			
			ui.widget(new ClickButtonWidget(posX, posY, width, height, 
					(coinValue >= 1000 ? (coinValue / 1000) + "k" : Integer.toString(coinValue)),
					t -> giveCoin(entityPlayer, holder.getCurrentItem(), coinValue)));
		}
		
		return ui.build(holder, entityPlayer);
	}
	
	@Override
	public void addInformation(ItemStack itemStack, List<String> lines) {
		NBTTagCompound data = GTUtility.getOrCreateNbtCompound(itemStack);
		int money = data.hasKey("MoneyAmount") ? data.getInteger("MoneyAmount") : 0;
		lines.add(I18n.format("metaitem.coin_wallet.money_amont", money));
		lines.add(I18n.format("metaitem.coin_wallet.use"));
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack heldItem = player.getHeldItem(hand);
		if (!player.isSneaking()) {
			if (!world.isRemote) {
				PlayerInventoryHolder holder = new PlayerInventoryHolder(player, hand);
				holder.openUI();
			}
		} else {
			NBTTagCompound data = GTUtility.getOrCreateNbtCompound(heldItem);
			
			for (int i = 0; i < player.inventoryContainer.inventorySlots.size(); i++) {
				ItemStack current = player.inventoryContainer.inventoryItemStacks.get(i);
				if (current.getItem() instanceof MetaItem<?>) {
					List<IItemBehaviour> behaviours = ((MetaItem<?>) current.getItem()).getBehaviours(current);
					for (IItemBehaviour beh : behaviours) {
						if (beh instanceof CoinBehaviour) {
							int money = data.hasKey("MoneyAmount") ? data.getInteger("MoneyAmount") : 0;
							money += current.getCount() * ((CoinBehaviour) beh).getValue();
							data.setInteger("MoneyAmount", money);
							player.inventoryContainer.putStackInSlot(i, ItemStack.EMPTY);
						}
					}
				}
			}
			
			heldItem.setTagCompound(data);
		}
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, heldItem);
	}
}