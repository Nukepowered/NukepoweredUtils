package info.nukepowered.nputils.armor;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import forestry.api.apiculture.IArmorApiarist;
import gregtech.api.GTValues;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.api.items.armor.ISpecialArmorLogic;
import gregtech.api.items.metaitem.ElectricStats;
import info.nukepowered.nputils.NPUConfig;
import info.nukepowered.nputils.api.NPULib;
import gregtech.api.items.armor.ArmorMetaItem.ArmorMetaValueItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "forestry.api.apiculture.IArmorApiarist", modid = GTValues.MODID_FR)
public abstract class ArmorLogicSuite implements ISpecialArmorLogic, IArmorApiarist {
	
	protected final int energyPerUse;
	protected final int tier;
	protected final int maxCapacity;
	protected final EntityEquipmentSlot SLOT;
	@SideOnly(Side.CLIENT)
	protected NPULib.ModularHUD HUD;
	
	protected ArmorLogicSuite(int energyPerUse, int maxCapacity, int tier, EntityEquipmentSlot slot) {
		this.energyPerUse = energyPerUse;
		this.maxCapacity = maxCapacity;
		this.tier = tier;
		this.SLOT = slot;
		if (NPULib.SIDE.isClient() && this.isNeedDrawHUD()) {
			HUD = new NPULib.ModularHUD();
		}
	}
	
	@Override
	public abstract void onArmorTick(World world, EntityPlayer player, ItemStack itemStack);
	
	@Override
	public int getArmorLayersAmount(ItemStack itemStack) {
        return 1;
    }
	
	@Override
	public ArmorProperties getProperties(EntityLivingBase player, @Nonnull ItemStack armor, DamageSource source, double damage, EntityEquipmentSlot equipmentSlot) {
		IElectricItem item = armor.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
		int damageLimit = Integer.MAX_VALUE;
		if (source.isUnblockable()) return new ArmorProperties(0, 0.0, 0);
		if (energyPerUse > 0) damageLimit = (int) Math.min(damageLimit, item.getCharge() / energyPerUse * 25.0);
		return new ArmorProperties(0, getAbsorption(armor) * getDamageAbsorption(), damageLimit);
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		IElectricItem item = armor.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
		if (item == null) return 0;
		if (item.getCharge() >= energyPerUse) {
			return (int) Math.round(20.0F * this.getAbsorption(armor) * this.getDamageAbsorption());
		} else {
			return (int) Math.round(4.0F * this.getAbsorption(armor) * this.getDamageAbsorption());
		}
	}
	
	@Override
	public void addToolComponents(@SuppressWarnings("rawtypes") ArmorMetaValueItem mvi) {
		mvi.addStats(new ElectricStats(maxCapacity, tier, true, false) {
			@Override
			public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
				return onRightClick(world, player, hand);
			}
			
			@Override
			public void addInformation(ItemStack itemStack, List<String> lines) {
				addInfo(itemStack, lines);
			}
		});
		if (Loader.isModLoaded(GTValues.MODID_FR)) {
			mvi.addStats(new ApiaristProvider());
		}
	}
	
	public void addInfo(ItemStack itemStack, List<String> lines) {
	}
	
	/**
	 * Forestry's anti-bee method
	 */
	public boolean protectEntity(EntityLivingBase entity, ItemStack armor, @Nullable String cause, boolean doProtect) {
		return false;
	}
	
	public ActionResult<ItemStack> onRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (player.getHeldItem(hand).getItem() instanceof ArmorMetaItem) {
			ItemStack armor = player.getHeldItem(hand);
			if (armor.getItem() instanceof ArmorMetaItem && player.inventory.armorInventory.get(SLOT.getIndex()).isEmpty() && !player.isSneaking()) {
				player.inventory.armorInventory.set(SLOT.getIndex(), armor.copy());
				player.setHeldItem(hand, ItemStack.EMPTY);
				player.playSound(new SoundEvent(new ResourceLocation("item.armor.equip_generic")), 1.0F, 1.0F);
				return ActionResult.newResult(EnumActionResult.SUCCESS, armor);
			}
		}
		
		return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
	}
	
	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack itemStack, DamageSource source, int damage, EntityEquipmentSlot equipmentSlot) {
	}

	@Override
	public EntityEquipmentSlot getEquipmentSlot(ItemStack itemStack) {
		return SLOT;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		return ImmutableMultimap.of();
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return "";
	}
	
	public abstract double getDamageAbsorption();
	
	@SideOnly(Side.CLIENT)
	public boolean isNeedDrawHUD() {
		return NPUConfig.client.enableHUD && false;
	}
	
	@SideOnly(Side.CLIENT)
	public void drawHUD(ItemStack stack) {
		this.addCapacityHUD(stack);
		this.HUD.draw();
		this.HUD.reset();
	}
	
	@SideOnly(Side.CLIENT)
	protected void addCapacityHUD(ItemStack stack) {
		IElectricItem cont = stack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
		if (cont == null) return;
		if (cont.getCharge() == 0) return;
		float energyMultiplier = cont.getCharge() * 100.0F / cont.getMaxCharge();
		this.HUD.newString(I18n.format("metaarmor.hud.energy_lvl", String.format("%.1f", energyMultiplier) + "%"));
	}
	
	public int getEnergyPerUse() {
		return this.energyPerUse;
	}
	
	protected float getAbsorption(ItemStack itemStack) {
		switch(this.getEquipmentSlot(itemStack)) {
			case HEAD: return 0.15F;
			case CHEST: return 0.4F;
			case LEGS: return 0.3F;
			case FEET: return 0.15F;
			default: return 0.0F;
		}
	}
}
