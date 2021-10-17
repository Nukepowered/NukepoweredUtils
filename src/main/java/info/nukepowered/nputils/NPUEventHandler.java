package info.nukepowered.nputils;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.api.items.armor.IArmorLogic;
import info.nukepowered.nputils.api.NPULib;
import info.nukepowered.nputils.armor.ArmorLogicSuite;
import info.nukepowered.nputils.armor.PowerlessJetpack;
import info.nukepowered.nputils.input.Key;
import info.nukepowered.nputils.input.Keybinds;
import info.nukepowered.nputils.item.NPUMetaItems;
import info.nukepowered.nputils.network.KeysPacket;
import info.nukepowered.nputils.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NPUEventHandler {
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
    public void onRender(final TickEvent.RenderTickEvent event) {
		final Minecraft mc = Minecraft.getMinecraft();
		if (mc.inGameHasFocus && mc.world != null && !mc.gameSettings.showDebugInfo && Minecraft.isGuiEnabled()) {
			final ItemStack item = mc.player.inventory.armorItemInSlot(EntityEquipmentSlot.CHEST.getIndex());
			if (item.getItem() instanceof ArmorMetaItem) {
				IArmorLogic logic = NPULib.getArmorLogic(item);
				if (logic instanceof ArmorLogicSuite) {
					ArmorLogicSuite armorLogic = (ArmorLogicSuite) logic;
					if (armorLogic.isNeedDrawHUD()) {
						armorLogic.drawHUD(item);
					}
				} else if (logic instanceof PowerlessJetpack) {
					PowerlessJetpack armorLogic = (PowerlessJetpack) logic;
					if (armorLogic.isNeedDrawHUD()) {
						armorLogic.drawHUD(item);
					}
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onKeyInput(KeyInputEvent event) {
		if (NPULib.SIDE.isClient()) {
			boolean needNewPacket = false;
			for (Key key : Keybinds.REGISTERY) {
				boolean keyState = key.getBind().isKeyDown();
				if (key.state != keyState) {
					key.state = keyState;
					needNewPacket = true;
				}
			}
			if (needNewPacket) NetworkHandler.INSTANCE.sendToServer(new KeysPacket(Keybinds.REGISTERY));
		}
	}
	
	
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onEntityLivingFallEvent(LivingFallEvent event) {
		if (!event.getEntity().getEntityWorld().isRemote && event.getEntity() instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) event.getEntity();
			ItemStack armor = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
			final ItemStack NANO = NPUMetaItems.NANO_MUSCLE_SUITE_BOOTS.getStackForm();
			final ItemStack QUARK = NPUMetaItems.QUARK_TECH_SUITE_BOOTS.getStackForm();
			if (armor != null) {
				int fallDamage = 0;
				if (armor.isItemEqual(NANO)) {	
					fallDamage = MathHelper.floor(event.getDistance() - 3.0);
					if (fallDamage >= 8) return;
				} else if (armor.isItemEqual(QUARK)) {
					fallDamage = Math.max((int)event.getDistance() - 10, 0);
				} else {
					return;
				}
				ArmorMetaItem<?>.ArmorMetaValueItem armorMetaValue = ((ArmorMetaItem<?>)armor.getItem()).getItem(armor);
				ArmorLogicSuite armorLogic = (ArmorLogicSuite) armorMetaValue.getArmorLogic();
				IElectricItem item = armor.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
				if (item == null) return;
				int energyCost = armorLogic.getEnergyPerUse() * fallDamage;
				if (item.getCharge() >= energyCost) {
					item.discharge(energyCost, item.getTier(), true, false, false);
					event.setCanceled(true);
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onGUIEvent(GuiOpenEvent event) {
		boolean resync = false;
		for (Key current : Keybinds.REGISTERY) {
			if (current.state = true) {
				current.state = false;
				resync = true;
			}
		}
		if (resync) NetworkHandler.INSTANCE.sendToServer(new KeysPacket(Keybinds.REGISTERY));
	}
	
	
	
}
