package info.nukepowered.nputils.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.util.GTUtility;
import info.nukepowered.nputils.NPUConfig;
import info.nukepowered.nputils.api.NPULib;
import info.nukepowered.nputils.input.EnumKey;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ImpellerJetpack extends ArmorLogicSuite {
	
	public ImpellerJetpack(int energyPerUse, int capacity, int tier) {
		super(energyPerUse, capacity, tier, EntityEquipmentSlot.CHEST);
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		IElectricItem container = stack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
		if (container.canUse(energyPerUse) && !player.isInWater() && !player.isInLava()) {
			NBTTagCompound data = GTUtility.getOrCreateNbtCompound(stack);
			byte toggleTimer = 0;
			boolean hover = false;
			boolean res = false;
			if (data.hasKey("toggleTimer")) toggleTimer = data.getByte("toggleTimer");
			if (data.hasKey("hover")) hover = data.getBoolean("hover");
			
			if (NPULib.isKeyDown(player, EnumKey.MODE_SWITCH) && NPULib.isKeyDown(player, EnumKey.JUMP) && toggleTimer == 0) {
				hover = !hover;
				toggleTimer = 10;
				if (!world.isRemote) {
					data.setBoolean("hover", hover);
					if (hover) {
						player.sendMessage(new TextComponentTranslation("metaarmor.jetpack.hover.enable"));
					} else {
						player.sendMessage(new TextComponentTranslation("metaarmor.jetpack.hover.disable"));
					}
				}
			}
			
			if (!hover) {
				if (NPULib.isKeyDown(player, EnumKey.JUMP)) {
					if (player.motionY < 0.6D) player.motionY += 0.2D;
					if (NPULib.isKeyDown(player, EnumKey.FORWARD)) {
						player.moveRelative(0.0F, 0.0F, 1.0F, 0.1F);
					}
					NPULib.spawnParticle(world, player, EnumParticleTypes.CLOUD, -0.6D);
					NPULib.playJetpackSound(player);
					res = true;
				}
			} else {
					if (!player.onGround) {
						NPULib.spawnParticle(world, player, EnumParticleTypes.CLOUD, -0.3D);
						NPULib.playJetpackSound(player);
					}
					if (NPULib.isKeyDown(player, EnumKey.FORWARD) && player.motionX < 0.5D && player.motionZ < 0.5D) {
						player.moveRelative(0.0F, 0.0F, 1.0F, 0.025F);
					}
					
					if (NPULib.isKeyDown(player, EnumKey.JUMP)) {
						if (player.motionY < 0.5D) {
							player.motionY += 0.125D;
						}
					} else if (NPULib.isKeyDown(player, EnumKey.SHIFT)) {
						if (player.motionY < -0.5D) player.motionY += 0.1D;
					} else if (!NPULib.isKeyDown(player, EnumKey.JUMP) && !NPULib.isKeyDown(player, EnumKey.SHIFT) && !player.onGround) {
						if (player.motionY < 0 && player.motionY >= -0.03D) player.motionY = -0.025D;
						if (player.motionY < -0.025D) {
							if (player.motionY + 0.2D > -0.025D) {
								player.motionY = -0.025D;
							} else {
								player.motionY += 0.2D;
							}
						}
					}
					player.fallDistance = 0.0F;
					res = true;
			}
			
			if (res && !player.onGround) {
				container.discharge(energyPerUse, container.getTier(), false, false, false);
			}
			
			if (world.getWorldTime() % 40 == 0 && !player.onGround) {
				NPULib.resetPlayerFloatingTime(player);
			}
			
			if (toggleTimer > 0) toggleTimer--;
			
			data.setByte("toggleTimer", toggleTimer);
			player.inventoryContainer.detectAndSendChanges();
		}
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack itemStack, DamageSource source, int damage, EntityEquipmentSlot equipmentSlot) {}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		return ImmutableMultimap.of();
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return "nputils:textures/armor/jetpack.png";
	}
	
	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, EntityEquipmentSlot equipmentSlot) {
		return new ArmorProperties(0, 0, 0);
	}

	@Override
	public double getDamageAbsorption() {
		return 0;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean isNeedDrawHUD() {
		return NPUConfig.Misc.enableHUD && true;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void drawHUD(ItemStack item) {
		super.addCapacityHUD(item);
		NBTTagCompound data = item.getTagCompound();
		if (data != null) { 
			if (data.hasKey("hover")) {
				String status = (data.getBoolean("hover") ? I18n.format("metaarmor.hud.status.enabled") : I18n.format("metaarmor.hud.status.disabled"));
				String result = I18n.format("metaarmor.hud.hover_mode", status);
				this.HUD.newString(result);
			}
		}
		this.HUD.draw();
		this.HUD.reset();
		
	}
}
