package info.nukepowered.nputils.machines;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.WorkableTieredMetaTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.multiblock.BlockPattern;
import gregtech.api.multiblock.FactoryBlockPattern;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.render.ICubeRenderer;
import gregtech.api.render.Textures;
import gregtech.api.util.GTUtility;
import gregtech.common.blocks.BlockMetalCasing.MetalCasingType;
import gregtech.common.blocks.MetaBlocks;
import info.nukepowered.nputils.api.NPULib;
import info.nukepowered.nputils.recipes.NPURecipeMaps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import static gregtech.api.multiblock.BlockPattern.RelativeDirection.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import gregtech.api.GTValues;
import gregtech.api.block.machines.MachineItemBlock;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.capability.impl.RecipeLogicEnergy;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.AdvancedTextWidget;

public class TileEntityProcessingArray extends RecipeMapMultiblockController {
	
	private InventoryMachinesHolder machinesInventory;
	private static final MultiblockAbility<?>[] ALLOWED_ABILITIES = {
	        MultiblockAbility.IMPORT_ITEMS, MultiblockAbility.EXPORT_ITEMS, MultiblockAbility.INPUT_ENERGY, MultiblockAbility.IMPORT_FLUIDS, MultiblockAbility.EXPORT_FLUIDS
	};
	
	public TileEntityProcessingArray(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, NPURecipeMaps.PROCESSING_ARRAY);
		this.machinesInventory = new InventoryMachinesHolder();
		this.recipeMapWorkable = new ProcessingArrayWorkable(this);
	}
	
	@Override
	protected ModularUI.Builder createUITemplate(EntityPlayer entityPlayer) {
		ModularUI.Builder builder = ModularUI.extendedBuilder();
		builder.image(4, 4, 150, 124, GuiTextures.DISPLAY);
		builder.label(10, 11, getMetaFullName(), 0xFFFFFF);
		builder.widget(new AdvancedTextWidget(10, 22, this::addDisplayText, 0xFFFFFF).setMaxWidthLimit(146));
		builder.slot(this.machinesInventory, 0, 154, 4, GuiTextures.SLOT);
		builder.bindPlayerInventory(entityPlayer.inventory, 134);
		builder.build(getHolder(), entityPlayer);
		return builder;
	}
	
	protected void addDisplayText(List<ITextComponent> textList) {
		if (!isStructureFormed()) {
			ITextComponent tooltip = new TextComponentTranslation("gregtech.multiblock.invalid_structure.tooltip")
					.setStyle(new Style().setColor(TextFormatting.GRAY));
			textList.add(new TextComponentTranslation("gregtech.multiblock.invalid_structure")
	                .setStyle(new Style().setColor(TextFormatting.RED)
					.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, tooltip))));
		} else {
			IEnergyContainer cont = recipeMapWorkable.getEnergyContainer();
			long maxVoltage = 0;
			if (cont != null) {
				if (cont.getEnergyCapacity() > 0) {
					maxVoltage = cont.getInputVoltage();
					int tier = GTUtility.getTierByVoltage(maxVoltage);
	                String voltageName = GTValues.VN[tier > (GTValues.VN.length - 1) ? (GTValues.VN.length - 1) : tier];
	                textList.add(new TextComponentTranslation("gregtech.multiblock.max_energy_per_tick", NPULib.format(maxVoltage), voltageName));
				}
			}
			
			Pair<MetaTileEntity, Integer> machines = this.getMachines(0);
			if (machines.getKey() != null) {
				if (machines.getKey() instanceof WorkableTieredMetaTileEntity) {
					WorkableTieredMetaTileEntity mte = (WorkableTieredMetaTileEntity) machines.getKey();
					long result = GTValues.V[mte.getTier()] * machines.getValue();
					ITextComponent comp = new TextComponentTranslation("nputils.multiblock.processing_array.eu_required", NPULib.format(result), GTValues.VN[GTUtility.getTierByVoltage(result)]);
					Style style = new Style().setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new TextComponentTranslation("nputils.multiblock.processing_array.eu_required.tooltip").setStyle(new Style().setColor(TextFormatting.GRAY))));
					if (result > maxVoltage) style.setColor(TextFormatting.RED);
					textList.add(comp.setStyle(style));
				}
			} else {
				ITextComponent noMachines = new TextComponentTranslation("nputils.multiblock.processing_array.nomachines")
						.setStyle(new Style().setColor(TextFormatting.RED));
				textList.add(noMachines);
			}
			
			if (!recipeMapWorkable.isWorkingEnabled()) {
                textList.add(new TextComponentTranslation("gregtech.multiblock.work_paused"));

            } else if (recipeMapWorkable.isActive()) {
                textList.add(new TextComponentTranslation("gregtech.multiblock.running"));
                int currentProgress = (int) (recipeMapWorkable.getProgressPercent() * 100);
                textList.add(new TextComponentTranslation("gregtech.multiblock.progress", currentProgress));
            } else {
                textList.add(new TextComponentTranslation("gregtech.multiblock.idling"));
            }
			
			if (recipeMapWorkable.isHasNotEnoughEnergy()) {
				 textList.add(new TextComponentTranslation("gregtech.multiblock.not_enough_energy").setStyle(new Style().setColor(TextFormatting.RED)));
			}
		}
	}
	
	/*
	 * Get the MTE in machines slot with stack amount
	 */
	protected Pair<MetaTileEntity, Integer> getMachines(int slot) {
		MetaTileEntity mte = null;
		int amount = 0;
		if (!this.machinesInventory.getStackInSlot(slot).isEmpty()) {
			ItemStack machines = this.machinesInventory.getStackInSlot(slot);
			amount = machines.getCount();
			if (machines.getItem() instanceof MachineItemBlock) {
				mte = MachineItemBlock.getMetaTileEntity(machines);
				return Pair.of(mte, amount);
			}
		}
		return Pair.of(null, 0);
	}
	
	@Override
	protected BlockPattern createStructurePattern() {
		FactoryBlockPattern.start();
		return FactoryBlockPattern.start(LEFT, DOWN, BACK)
				.aisle("XXX", "XXX", "XXX")
				.aisle("XXX", "XAX", "XXX")
				.aisle("XXX", "XSX", "XXX")
				.where('X', statePredicate(getCasingState()).or(abilityPartPredicate(ALLOWED_ABILITIES)))
				.where('S', selfPredicate())
				.where('A', isAirPredicate())
				.build();
	}
	
	private IBlockState getCasingState() {
		return MetaBlocks.METAL_CASING.getState(MetalCasingType.TUNGSTENSTEEL_ROBUST);
	}
	
	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
		return Textures.ROBUST_TUNGSTENSTEEL_CASING;
	}
	
	@Override
	public void clearMachineInventory(NonNullList<ItemStack> itemBuffer) {
		super.clearMachineInventory(itemBuffer);
		clearInventory(itemBuffer, machinesInventory);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setTag("MachinesInventory", machinesInventory.serializeNBT());
		return data;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.machinesInventory.deserializeNBT(data.getCompoundTag("MachinesInventory"));
	}

	@Override
	public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
		return new TileEntityProcessingArray(metaTileEntityId);
	}
	
	protected class ProcessingArrayWorkable extends MultiblockRecipeLogic  {
		
		private RecipeMap<?> recipes = null;
		private int multiplier = 0;
		private long recipeVoltage = 0;
		private int successfulOperations = 0;
		
		public ProcessingArrayWorkable(RecipeMapMultiblockController tileEntity) {
			super(tileEntity);
		}
		
		private void updateStats() {
			Pair<MetaTileEntity, Integer> machines = getMachines(0);
			
			if (machines.getKey() == null || machines.getValue() <= 0) {
				this.recipes = null;
				this.multiplier = 0;
				return;
			}
			
			// TODO fix recipes for EVERY machine
			RecipeLogicEnergy rle = ObfuscationReflectionHelper.getPrivateValue(WorkableTieredMetaTileEntity.class, (WorkableTieredMetaTileEntity) machines.getKey(), "workable");
			long recipeVoltage = GTValues.V[((WorkableTieredMetaTileEntity) machines.getKey()).getTier()];
			
			if (!rle.recipeMap.equals(this.recipes)) {
				this.recipes = rle.recipeMap;
			}
			
			if (this.multiplier != machines.getValue()) {
				this.multiplier = machines.getValue();
			}
			
			if (this.recipeVoltage != recipeVoltage) {
				this.recipeVoltage = recipeVoltage;
			}
		}
		
		@Nullable
		@Override
		protected Recipe findRecipe(long maxVoltage, IItemHandlerModifiable inputs, IMultipleTankHandler fluidInputs) {
			Recipe result = null;
			if (this.recipes != null && this.multiplier > 0 && this.recipeVoltage > 0) {
				int outTankCap = this.getMinTankCapacity(this.getOutputTank());
				
				// try check recipe
				result = this.recipes.findRecipe(this.recipeVoltage, inputs, fluidInputs, outTankCap);
				
				// if recipe not found, check recipe for every slot (for one slots machines like furnance)
				if (result == null) {
					for (int i = 0; i < inputs.getSlots(); i++) {
						ItemStack stack = inputs.getStackInSlot(i);
						if (stack.isEmpty()) continue;
						result = this.recipes.findRecipe(this.recipeVoltage, Collections.singletonList(stack), GTUtility.fluidHandlerToList(fluidInputs), outTankCap);
						if (result != null) break;
					}
				}
				
			}
			
			return result;
		}
		
		@Override
		protected boolean setupAndConsumeRecipeInputs(Recipe recipe) {
			int[] resultOverclock = calculateOverclock(recipe.getEUt(), this.recipeVoltage, recipe.getDuration());
			long totalEnergy = resultOverclock[0] * resultOverclock[1] * this.multiplier;
			boolean enoughPower = totalEnergy >= 0
					? getEnergyStored() >= (totalEnergy > getEnergyCapacity() / 2 ? resultOverclock[0] : totalEnergy)
					: (getEnergyStored() - resultOverclock[0] <= getEnergyCapacity());
			
			if (!enoughPower) {
				return false;
			}
			
			
			int usableMachinesAmount = (int) (getMaxVoltage() / this.recipeVoltage);
			List<ItemStack> outItems = recipe.getAllItemOutputs(getOutputInventory().getSlots());
			List<FluidStack> outFluids = recipe.getFluidOutputs();
			
			for (int i = 1; i <= Math.min(this.multiplier, usableMachinesAmount); i++) {
				List<ItemStack> currentOutItems = NPULib.copyStackList(outItems);
				List<FluidStack> currentOutFluids = GTUtility.copyFluidList(outFluids);
				
				for (int y = 0; y < outItems.size(); y++) {
					ItemStack current = currentOutItems.get(y);
					int newAmount = outItems.get(y).getCount() * i;
					if (newAmount > current.getMaxStackSize()) {
						currentOutItems.remove(y);
						currentOutItems.addAll(y, NPULib.getStackedList(current, newAmount));
					} else {
						current.setCount(newAmount);
					}
				}
				
				for (int y = 0; y < currentOutFluids.size(); y++) {
					FluidStack current = currentOutFluids.get(y);
					current.amount = outFluids.get(y).amount * i;
				}
				
				List<IFluidTank> tanks = getOutputFluidInventory().getFluidTanks();
				boolean canInsert = true;
				
				if (tanks.size() <= currentOutFluids.size()) {
					for (int y = 0; y < tanks.size(); y++) {
						IFluidTank tank = tanks.get(y);
						FluidStack stack = currentOutFluids.get(y);
						if ((tank.getCapacity() - tank.getFluidAmount()) < stack.amount) {
							canInsert = false;
						}
					}
				}
				
				boolean canPutItems = MetaTileEntity.addItemsToItemHandler(getOutputInventory(), true, currentOutItems) &&
						GTUtility.itemHandlerToList(getOutputInventory()).size() >= currentOutItems.size();
				boolean canPutFluids = MetaTileEntity.addFluidsToFluidHandler(getOutputFluidInventory(), true, currentOutFluids) &&
						canInsert && (tanks.size() <= currentOutFluids.size());

				
				if (canPutItems && canPutFluids) {
					if (recipe.matches(true, getInputInventory(), getInputTank())) {
						this.successfulOperations++;
					} else break;
				} else {
					break;
				}
			}
			
			return this.successfulOperations > 0;
		}
		
		@Override
		protected void setupRecipe(Recipe recipe) {
			int[] resultOverclock = calculateOverclock(recipe.getEUt(), this.recipeVoltage, recipe.getDuration());
			this.progressTime = 1;
			setMaxProgress(resultOverclock[1]);
			this.recipeEUt = resultOverclock[0] * this.successfulOperations;
			
			NonNullList<ItemStack> itemOutputs = NonNullList.create();
			List<FluidStack> fluidOutputs = new ArrayList<>();
			
			// Run random for every machine inside
			for (int i = 0; i < this.successfulOperations; i++) {
				List<ItemStack> stacks = recipe.getResultItemOutputs(getOutputInventory().getSlots(), random, GTUtility.getTierByVoltage(this.recipeVoltage));
				List<FluidStack> fluidStacks = recipe.getFluidOutputs();
				for (ItemStack stack : stacks) itemOutputs.add(stack);
				for (FluidStack stack : fluidStacks) fluidOutputs.add(stack);
			}
			
			this.fluidOutputs = fluidOutputs;
			this.itemOutputs = itemOutputs;
			if (this.wasActiveAndNeedsUpdate) {
	            this.wasActiveAndNeedsUpdate = false;
	        } else {
	            this.setActive(true);
	        }
		}
		
		@Override
		protected void completeRecipe() {
			super.completeRecipe();
		    this.successfulOperations = 0;
		}
		
		@Override
		protected void trySearchNewRecipe() {
			long maxVoltage = this.getMaxVoltage();
			Recipe currentRecipe = null;
			IItemHandlerModifiable importInventory = getInputInventory();
			IMultipleTankHandler importFluids = getInputTank();
			
			if (this.recipes != null) {
				boolean dirty = checkRecipeInputsDirty(importInventory, importFluids);
				if (dirty || this.forceRecipeRecheck || getWorld().getWorldTime() % 40 == 0) {
					this.forceRecipeRecheck = false;
					currentRecipe = findRecipe(maxVoltage, importInventory, importFluids);
					if (currentRecipe != null) {
						this.previousRecipe = currentRecipe;
					}
				}
				
				if (currentRecipe != null) {
					if (setupAndConsumeRecipeInputs(currentRecipe)) {
						setupRecipe(currentRecipe);
					}
				}
			} else {
				this.updateStats();
			}
		}
		
		@Override
		public void receiveInitialData(PacketBuffer buf) {
			super.receiveInitialData(buf);
			this.updateStats();
			this.forceRecipeRecheck();
		}
	}
	
	protected class InventoryMachinesHolder extends ItemStackHandler {
		public InventoryMachinesHolder() {
			super(16);
		}
		
		 @Override
	     public int getSlotLimit(int slot) {
	     	return 16;
	     }
		 
		 @Override
		 protected void onContentsChanged(int slot) {
			 super.onContentsChanged(slot);
			 ProcessingArrayWorkable workable = ((ProcessingArrayWorkable) recipeMapWorkable);
			 workable.updateStats();
			 workable.forceRecipeRecheck();
		 }
		 
		 @Override
		 public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
			 if (stack.getItem() instanceof MachineItemBlock) {
				 MetaTileEntity mte = MachineItemBlock.getMetaTileEntity(stack);
				 if (mte instanceof WorkableTieredMetaTileEntity) {
					 return true;
				 }
			 }
			 return false;
		 }
	}
	
}
