package info.nukepowered.nputils.armor;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.util.GTUtility;
import info.nukepowered.nputils.NPUConfig;
import info.nukepowered.nputils.api.NPULib;
import info.nukepowered.nputils.input.EnumKey;
import info.nukepowered.nputils.item.NPUMetaItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;

public class QuarkTechSuite extends ArmorLogicSuite {
	protected static final Map<Potion, Integer> potionRemovalCost = new IdentityHashMap<>();
	private float charge = 0.0F;
	
	public QuarkTechSuite(EntityEquipmentSlot slot, int energyPerUse, int capacity, int tier) {
		super(energyPerUse, capacity, tier, slot);
		potionRemovalCost.put(MobEffects.POISON, 10000);
	    potionRemovalCost.put(MobEffects.WITHER, 25000);
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		IElectricItem item = itemStack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
		NBTTagCompound data = GTUtility.getOrCreateNbtCompound(itemStack);
		byte toggleTimer = data.getByte("toggleTimer");
		boolean ret = false;
		switch(SLOT) {
		case HEAD:
			int air = player.getAir();
			if (item.canUse(energyPerUse / 100) && air < 100) {
				player.setAir(air + 200);
				item.discharge(energyPerUse / 100, item.getTier(), true, false, false);
				ret = true;
			}
			
			if (item.canUse(energyPerUse / 10) && player.getFoodStats().needFood()) {
				int slotId = -1;
				for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
					ItemStack current = player.inventory.mainInventory.get(i);
					if (!current.isEmpty() && !current.isItemEqual(ItemStack.EMPTY) && current.getItem() instanceof ItemFood) {
						slotId = i;
						break;
					}
				}
				
				if (slotId > -1) {
					ItemStack stack = player.inventory.mainInventory.get(slotId);
					ActionResult<ItemStack> result = NPULib.canEat(player, stack);
					stack = result.getResult();
					if (stack.isEmpty()) {
						player.inventory.mainInventory.set(slotId, ItemStack.EMPTY);
					}
					
					if (result.getType() == EnumActionResult.SUCCESS) {
						item.discharge(energyPerUse / 10, item.getTier(), true, false, false);
					}
					
					ret = true;
				}
			}
			
			Iterator<PotionEffect> iter = (new LinkedList<>(player.getActivePotionEffects())).iterator();
			while (iter.hasNext()) {
				PotionEffect effect = iter.next();
				Potion potion = effect.getPotion();
				Integer cost = potionRemovalCost.get(potion);
				if (cost != null) {
					cost = cost * (effect.getAmplifier() + 1);
					if (item.canUse(cost)) {
						item.discharge(cost, item.getTier(), true, false, false);
						player.removePotionEffect(potion);
					}
				}
			}
			
			boolean nightvision = data.getBoolean("Nightvision");
			if (NPULib.isKeyDown(player, EnumKey.MENU) && NPULib.isKeyDown(player, EnumKey.MODE_SWITCH) && toggleTimer == 0) {
				toggleTimer = 10;
				nightvision = !nightvision;
				data.setBoolean("Nightvision", nightvision);
				if (!world.isRemote) {
					String result = nightvision ? "metaarmor.nightvision.enabled" : "metaarmor.nightvision.disabled";
					player.sendStatusMessage(new TextComponentTranslation(result), true);
				}
			}
			
			if (!world.isRemote && toggleTimer > 0) {
				--toggleTimer;
				data.setByte("toggleTimer", toggleTimer);
			}
			
			if (nightvision && !world.isRemote && item.canUse(energyPerUse / 100)) {
				BlockPos pos = new BlockPos(MathHelper.floor(player.posX), MathHelper.floor(player.posY), MathHelper.floor(player.posZ));
				int skylight = player.getEntityWorld().getLightFromNeighbors(pos);
				if (skylight > 8) {
					player.removePotionEffect(MobEffects.NIGHT_VISION);
					player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100, 0, true, true));
				} else {
					player.removePotionEffect(MobEffects.BLINDNESS);
					player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, true, true));
				}
				item.discharge(energyPerUse / 100, item.getTier(), true, false, false);
				ret = true;
			}
			break;
		case CHEST:
			player.extinguish();
			break;
		case LEGS:
			if (item.canUse(energyPerUse / 10) && (player.onGround || player.isInWater()) && NPULib.isKeyDown(player, EnumKey.FORWARD) && (NPUConfig.client.enableBoostWithSprint && player.isSprinting() || !NPUConfig.client.enableBoostWithSprint && NPULib.isKeyDown(player, EnumKey.BOOST))) {
				byte consumerTicks = data.getByte("consumerTicks");
				++consumerTicks;
				if (consumerTicks >= 10) {
					consumerTicks = 0;
					item.discharge(energyPerUse / 10, item.getTier(), true, false, false);
					ret = true;
				}
				data.setByte("consumerTicks", consumerTicks);
				float speed = 0.25F;
				if (player.isInWater()) {
					speed = 0.1F;
					if (NPULib.isKeyDown(player, EnumKey.JUMP)) {
						player.motionY += 0.1D;
					}
				}
				if (speed > 0.0F) {
					player.moveRelative(0.0F, 0.0F, 1.0F, speed);
				}
			} else if (item.canUse(energyPerUse / 10) && player.isInWater() && (NPULib.isKeyDown(player, EnumKey.SHIFT) || NPULib.isKeyDown(player, EnumKey.JUMP))) {
				byte consumerTicks = data.getByte("consumerTicks");
				++consumerTicks;
				if (consumerTicks >= 10) {
					consumerTicks = 0;
					item.discharge(energyPerUse / 10, item.getTier(), true, false, false);
					ret = true;
				}
				data.setByte("consumerTicks", consumerTicks);
				double acceleration = 0.085D;
				if (NPULib.isKeyDown(player, EnumKey.SHIFT) && NPULib.isKeyDown(player, EnumKey.BOOST)) player.motionY -= acceleration;
				if (NPULib.isKeyDown(player, EnumKey.JUMP) && NPULib.isKeyDown(player, EnumKey.BOOST)) player.motionY += acceleration;
			}
			break;
		case FEET:
			if (!world.isRemote) {
				boolean onGround = data.hasKey("onGround") ? data.getBoolean("onGround") : true;
				if (onGround && !player.onGround && NPULib.isKeyDown(player, EnumKey.JUMP) && NPULib.isKeyDown(player, EnumKey.BOOST)) {
					item.discharge(energyPerUse / 10, item.getTier(), true, false, false);
					ret = true;
				}
				
				if (player.onGround != onGround) {
					data.setBoolean("onGround", player.onGround);
				}
			} else {
				if (item.canUse(energyPerUse / 10) && player.onGround) {
					this.charge = 1.0F;
				}
				
				if (player.motionY >= 0.0D && this.charge > 0.0F && !player.isInWater()) {
					if (NPULib.isKeyDown(player, EnumKey.JUMP) && NPULib.isKeyDown(player, EnumKey.BOOST)) {
						if (this.charge == 1.0F) {
							player.motionX *= 3.6D;
							player.motionZ *= 3.6D;
						}
						
						player.motionY += (double)(this.charge * 0.32F);
						this.charge = (float)(this.charge * 0.7D);
					} else if (this.charge < 1.0F) {
						this.charge = 0.0F;
					}
				}
			}
			break;
		default:
			break;
		}
		
		if (ret) {
			player.inventoryContainer.detectAndSendChanges();
		}
	}
	
	@Override
	public ArmorProperties getProperties(EntityLivingBase player, @Nonnull ItemStack armor, DamageSource source, double damage, EntityEquipmentSlot equipmentSlot) {
		int damageLimit = Integer.MAX_VALUE;
		IElectricItem item = armor.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
		if (energyPerUse > 0) {
			damageLimit = (int) Math.min(damageLimit, 25.0D * item.getCharge() / energyPerUse);
		}
		
		if (source == DamageSource.FALL) {
			if (SLOT == EntityEquipmentSlot.FEET) {
				return new ArmorProperties(10, 1.0D, damageLimit);
			}
			
			if (SLOT == EntityEquipmentSlot.LEGS) {
				return new ArmorProperties(9, 0.8D, damageLimit);
			}
		}
		return new ArmorProperties(8, getDamageAbsorption() * getAbsorption(armor), damageLimit);
	}
	
	@Override
	public boolean handleUnblockableDamage(EntityLivingBase entity, @Nonnull ItemStack armor, DamageSource source, double damage, EntityEquipmentSlot equipmentSlot) {
		
		if (source == DamageSource.FALL || source == DamageSource.DROWN || source == DamageSource.STARVE || source == DamageSource.OUT_OF_WORLD || source.getDamageType().contains("bee.")) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public boolean protectEntity(EntityLivingBase entity, ItemStack armor, @Nullable String cause, boolean doProtect) {
		IElectricItem item = armor.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
		long energyPerBite = this.energyPerUse / 100;
		if (item != null && item.canUse(energyPerBite)) {
			if (doProtect) item.discharge(energyPerBite, item.getTier(), false, false, false);
			return true;
		}
		
		return false;
	}
	
	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack itemStack, DamageSource source, int damage, EntityEquipmentSlot equipmentSlot) {
		IElectricItem item = itemStack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
		item.discharge(energyPerUse * damage, item.getTier(), true, false, false);
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		ItemStack currentChest = Minecraft.getMinecraft().player.inventory.armorItemInSlot(EntityEquipmentSlot.CHEST.getIndex());
		ItemStack advancedChest = NPUMetaItems.ADVANCED_QAURK_TECH_SUITE_CHESTPLATE.getStackForm();
		String armorTexture = "quark_tech_suite";
		if (advancedChest.isItemEqual(currentChest)) armorTexture = "advanced_quark_tech_suite";
		return SLOT != EntityEquipmentSlot.LEGS ?
				String.format("nputils:textures/armor/%s_1.png", armorTexture):
				String.format("nputils:textures/armor/%s_2.png", armorTexture);
	}
	
	@Override
	public double getDamageAbsorption() {
		return SLOT == EntityEquipmentSlot.CHEST ? 1.2D : 1.0D;
	}
}
