package info.nukepowered.nputils.armor;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import gregtech.api.GTValues;
import gregtech.api.items.armor.IArmorLogic;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.recipes.FuelRecipe;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import info.nukepowered.nputils.NPUConfig;
import info.nukepowered.nputils.NPULog;
import info.nukepowered.nputils.api.NPULib;
import info.nukepowered.nputils.input.EnumKey;
import gregtech.api.items.armor.ArmorMetaItem.ArmorMetaValueItem;
import gregtech.api.items.metaitem.stats.IItemBehaviour;
import gregtech.api.items.metaitem.stats.IItemCapabilityProvider;
import gregtech.api.items.metaitem.stats.IItemDurabilityManager;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PowerlessJetpack implements IArmorLogic {
	public final int tankCapacity;
	public final int burnTier;
	public final static List<FuelRecipe> fuels;
	public final static List<Fluid> forbiddenFuels;
	@SideOnly(Side.CLIENT)
	private NPULib.ModularHUD HUD;
	
	public PowerlessJetpack(int capacity, int burnTier) {
		if (NPULib.SIDE.isClient()) HUD = new NPULib.ModularHUD();
		this.tankCapacity = capacity;
		this.burnTier = burnTier;
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		IFluidHandlerItem internalTank = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
		FuelRecipe currentRecipe = null;
		NBTTagCompound data = GTUtility.getOrCreateNbtCompound(stack);
		int burntime = data.hasKey("burnTimer") ? data.getShort("burnTimer") : 0;
		byte toggleTimer = data.hasKey("toggleTimer") ? data.getByte("toggleTimer") : 0;
		boolean hover = data.hasKey("hover") ? data.getBoolean("hover") : false;
		boolean suc = false;
		
		if (NPULib.isKeyDown(player, EnumKey.JUMP) && NPULib.isKeyDown(player, EnumKey.MODE_SWITCH) && toggleTimer == 0) {
			hover = !hover;
			toggleTimer = 10;
			if (world.isRemote) {
				String status = hover ? "metaarmor.jetpack.hover.enable" : "metaarmor.jetpack.hover.disable";
				player.sendStatusMessage(new TextComponentTranslation(status), true);
			}
		}
		
		if (internalTank.drain(1, false) != null && !player.isInWater() && !player.isInLava()) {
			for (FuelRecipe current : fuels) {
				if (current.getRecipeFluid().isFluidEqual(internalTank.drain(1, false))) {
					currentRecipe = current;
					break;
				}
			}
			
			if (currentRecipe == null) {
				NPULog.error("Something going wrong while finding burn recipe for jetpack. Report about this!");
				return;
			}
			
			FluidStack fuel = currentRecipe.getRecipeFluid();
			
			if (internalTank.drain(fuel, false).amount == fuel.amount || burntime >= GTValues.V[burnTier]) {
				if (!hover) {
					if (NPULib.isKeyDown(player, EnumKey.JUMP)) {
						if (player.motionY < 0.6D) player.motionY += 0.2D;
						if (NPULib.isKeyDown(player, EnumKey.FORWARD)) {
							player.moveRelative(0.0F, 0.0F, 1.0F, 0.1F);
						}
						NPULib.spawnParticle(world, player, EnumParticleTypes.SMOKE_LARGE, -0.6D);
						NPULib.spawnParticle(world, player, EnumParticleTypes.CLOUD, -0.6D);
						NPULib.playJetpackSound(player);
						suc = true;
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
							NPULib.spawnParticle(world, player, EnumParticleTypes.SMOKE_LARGE, -0.6D);
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
					suc = true;
				}
				
				if (suc) {
					if (!player.onGround) {
						if (burntime < GTValues.V[burnTier]) {
							player.fallDistance = 0.0F;
							burntime = (int) (currentRecipe.getDuration() * currentRecipe.getMinVoltage());
							internalTank.drain(fuel.amount, true);
						} else {
							burntime -= GTValues.V[burnTier];
						}
					}
				}
			}
		}
		

		if (player.onGround && !NPULib.isKeyDown(player, EnumKey.JUMP) && hover && !world.isRemote) {
			hover = !hover;
		}
		
		if (world.getWorldTime() % 40 == 0 && !player.onGround) {
			NPULib.resetPlayerFloatingTime(player);
		}
		
		if (toggleTimer > 0) toggleTimer--;
		
		data.setBoolean("hover", hover);
		data.setShort("burnTimer", (short) burntime);
		data.setByte("toggleTimer", toggleTimer);
		player.inventoryContainer.detectAndSendChanges();
	}
	
	@Override
	public EntityEquipmentSlot getEquipmentSlot(ItemStack itemStack) {
		return EntityEquipmentSlot.CHEST;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack itemStack, DamageSource source, int damage, EntityEquipmentSlot equipmentSlot) {}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		return ImmutableMultimap.of();
	}
	
	@Override
	public void addToolComponents(@SuppressWarnings("rawtypes") ArmorMetaValueItem mvi) {
		mvi.addComponents(new Behaviour(tankCapacity));
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return "nputils:textures/armor/liquid_fuel_jetpack.png";
	}
	
	@SideOnly(Side.CLIENT)
	public void drawHUD(ItemStack item) {
		IFluidHandlerItem tank = item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
		if (tank != null) {
			IFluidTankProperties[] prop = tank.getTankProperties();
			if (prop[0] != null) {
				if (prop[0].getContents() != null) {
					if (prop[0].getContents().amount == 0) return;
					String formated = String.format("%.1f", (prop[0].getContents().amount * 100.0F / prop[0].getCapacity()));
					this.HUD.newString(I18n.format("metaarmor.hud.fuel_lvl", formated + "%"));
					NBTTagCompound data = item.getTagCompound();
					if (data != null) {
						if (data.hasKey("hover")) {
							String status = (data.getBoolean("hover") ? I18n.format("metaarmor.hud.status.enabled") : I18n.format("metaarmor.hud.status.disabled"));
							String result = I18n.format("metaarmor.hud.hover_mode", status);
							this.HUD.newString(result);
						}
					}
				}
			}
		}
		this.HUD.draw();
		this.HUD.reset();
	}
	
	@SideOnly(Side.CLIENT)
	public boolean isNeedDrawHUD() {
		return NPUConfig.Misc.enableHUD && true;
	}
	
	
	public static class Behaviour implements IItemDurabilityManager, IItemCapabilityProvider, IItemBehaviour {

		public final int maxCapacity;
		
		public Behaviour(int internalCapacity) {
			this.maxCapacity = internalCapacity;
		}
		
		@Override
		public boolean showsDurabilityBar(ItemStack itemStack) {
			return true;
		}

		@Override
		public double getDurabilityForDisplay(ItemStack itemStack) {
	        IFluidHandlerItem fluidHandlerItem = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
	        IFluidTankProperties fluidTankProperties = fluidHandlerItem.getTankProperties()[0];
	        FluidStack fluidStack = fluidTankProperties.getContents();
	        return fluidStack == null ? 1.0 : (1.0 - fluidStack.amount / (fluidTankProperties.getCapacity() * 1.0));
		}

		@Override
		public int getRGBDurabilityForDisplay(ItemStack itemStack) {
			return MathHelper.hsvToRGB(0.33f, 1.0f, 1.0f);
		}
		
		@Override
		public ICapabilityProvider createProvider(ItemStack itemStack) {
			return new FluidHandlerItemStack(itemStack, maxCapacity) {
				@Override
	            public boolean canFillFluidType(FluidStack fluidStack) {
					for (FuelRecipe recipe : fuels)
						if (fluidStack.isFluidEqual(recipe.getRecipeFluid()) && !forbiddenFuels.contains(fluidStack.getFluid())) return true;
	                return false;
	            }
				
				@Override
			    public IFluidTankProperties[] getTankProperties() {
			        return new FluidTankProperties[] { new FluidTankProperties(getFluid(), capacity, true, false) };
			    }
			};
		}
		
		@Override
		public void addInformation(ItemStack itemStack, List<String> lines) {
		}
	}
	
	static {
		fuels = RecipeMaps.DIESEL_GENERATOR_FUELS.getRecipeList();
		forbiddenFuels = Arrays.asList(new Fluid[] {
				Materials.Oil.getFluid(1).getFluid(),
				Materials.SulfuricLightFuel.getFluid(1).getFluid()
		});
	}
}
