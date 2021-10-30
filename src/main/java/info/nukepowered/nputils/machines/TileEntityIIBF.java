package info.nukepowered.nputils.machines;

import java.util.List;
import java.util.Map.Entry;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.multiblock.BlockPattern;
import gregtech.api.multiblock.BlockWorldState;
import gregtech.api.multiblock.FactoryBlockPattern;
import gregtech.api.multiblock.PatternMatchContext;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.render.ICubeRenderer;
import gregtech.api.render.OrientedOverlayRenderer;
import gregtech.api.render.Textures;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.DustMaterial;
import gregtech.api.util.GTUtility;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.blocks.BlockCompressed;

import info.nukepowered.nputils.blocks.BlockInductionCoil;
import info.nukepowered.nputils.blocks.NPUMetaBlocks;
import info.nukepowered.nputils.crafttweaker.NPUMultiblockCasing.CasingType;
import info.nukepowered.nputils.recipes.NPURecipeMaps;
import info.nukepowered.nputils.blocks.BlockInductionCoil.InductionCoilType;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import static net.minecraft.util.text.TextFormatting.*;

/**
 * Meta TileEntity Industrial Induction Blast Furnance
 * @author TheDarkDnKTv
 *
 * Copyright (C) 2021 TheDarkDnKTv - All Rights Reserved.
 */
public class TileEntityIIBF extends RecipeMapMultiblockController {

	private static final MultiblockAbility<?>[] ALLOWED_ABILITIES = {
	    MultiblockAbility.IMPORT_FLUIDS, MultiblockAbility.EXPORT_FLUIDS, MultiblockAbility.INPUT_ENERGY
	};
	
	private final int MAX_HEAT = 100000;
	private final int WORKING_LEVEL = (int)(MAX_HEAT * 0.4D);
	private final int STOP_LEVEL = (int)(MAX_HEAT * 0.9D);
	
	private InductionCoilType coilType;
	private int heat;
	// UI blink boolean
	private boolean blink = false;
	
	public TileEntityIIBF(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, RecipeMaps.BLAST_RECIPES);
		this.recipeMapWorkable = new IIBFRecipeLogic(this);
	}
	
	@Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
        return new TileEntityIIBF(metaTileEntityId);
    }
	
    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        if (isStructureFormed()) {
        	int heatAdj = heat - WORKING_LEVEL;
        	int heatMaxAdj = MAX_HEAT - WORKING_LEVEL - (MAX_HEAT - STOP_LEVEL);
        	int overheat = (int)Math.min(100.0D, Math.max(0, (heatAdj * 1.0D / heatMaxAdj * 100.0D)));
        	
            textList.add(new TextComponentTranslation("gregtech.multiblock.blast_furnace.max_temperature", coilType.heatingTemp));
            IEnergyContainer energyContainer = recipeMapWorkable.getEnergyContainer();
            if (energyContainer != null && energyContainer.getEnergyCapacity() > 0) {
                long maxVoltage = energyContainer.getInputVoltage();
                String voltageName = GTValues.VN[GTUtility.getTierByVoltage(maxVoltage)];
                textList.add(new TextComponentTranslation("gregtech.multiblock.max_energy_per_tick", maxVoltage, voltageName));
            }
            
            if (overheat > 0) {
	            Style heatStyle = new Style();
	        	if (overheat >= 5) {
	        		TextFormatting color = overheat > 60 ? RED : YELLOW;
	        		heatStyle.setColor(color);
	        	}
            	
	            if (recipeMapWorkable.isActive()) {
	            	if (overheat > 5 && overheat <= 99)
		        		textList.add(new TextComponentTranslation("nputils.machine.induction_blast_furnance.gui.throttle").setStyle(new Style().setColor(YELLOW)));
		            else if (overheat > 99)
		            	textList.add(new TextComponentTranslation("nputils.machine.induction_blast_furnance.gui.stopped").setStyle(new Style().setColor(DARK_RED).setBold(true)));
	            }
	            
	            textList.add(new TextComponentTranslation("nputils.machine.induction_blast_furnance.gui.overheat", overheat).setStyle(heatStyle));
            }
            
            if (recipeMapWorkable.isActive()) {
            	double currentProgress = recipeMapWorkable.getProgressPercent();
                StringBuilder builder = new StringBuilder();
                builder.append("<");
                for (int i = 0; i < 24; i++)
                	builder.append(currentProgress > 0.01D && i <=  currentProgress * 24 ? '=' : "-");
                builder.append('>');
                textList.add(new TextComponentTranslation("gregtech.multiblock.progress", (int)(currentProgress * 100)));
                textList.add(new TextComponentString(builder.toString()).setStyle(new Style().setColor(blink ? GRAY : DARK_GRAY)));
            }
            
            if (recipeMapWorkable.isWorkingEnabled()) {
            	if (recipeMapWorkable.isActive()) {
                	if (overheat == 0)
                		textList.add(new TextComponentTranslation("gregtech.multiblock.running"));
                } else {
                	textList.add(new TextComponentTranslation("gregtech.multiblock.idling"));
                }
            } else {
            	textList.add(new TextComponentTranslation("gregtech.multiblock.work_paused"));
            }

            if (recipeMapWorkable.isHasNotEnoughEnergy()) {
                textList.add(new TextComponentTranslation("gregtech.multiblock.not_enough_energy").setStyle(new Style().setColor(TextFormatting.RED)));
            }
        }
    }
    
    @Override
    public void addDebugInfo(List<String> list) {
    	list.add("Coil type: " + coilType);
    	list.add("Heat Stored: " + heat + " / " + MAX_HEAT);
    	list.add("Blnk: " + blink);
    	list.add("Working state: " + (heat >= MAX_HEAT ? "OVERHEATED" : heat > WORKING_LEVEL ? "THROTTLE" : "NORMAL"));
    }
    
    protected void applyLabel(List<ITextComponent> list) {
    	list.add(new TextComponentTranslation(getMetaFullName()));
    }
	
    @Override
    protected void updateFormedValid() {
        this.recipeMapWorkable.updateWorkable();
    }
    
    @Override
    protected ModularUI.Builder createUITemplate(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.extendedBuilder();
        builder.image(7, 4, 162, 121, GuiTextures.DISPLAY);
        builder.label(0, -10, getMetaFullName(), 0xFFFFFF);
        builder.widget(new AdvancedTextWidget(11, 9, this::addDisplayText, 0xFFFFFF)
            .setMaxWidthLimit(156)
            .setClickHandler(this::handleDisplayClick));
        builder.bindPlayerInventory(entityPlayer.inventory, 134);
        return builder;
    }
    
    
    
	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
	            .aisle("#HHH#", "#CCC#", "#PFP#", "#CCC#", "#HHH#")
	            .aisle("HHHHH", "CCGCC", "PKGKP", "CCGCC", "HHHHH")
	            .aisle("HHOHH", "CG#GC", "PG#GP", "CG#GC", "HHIHH")
	            .aisle("HHHHH", "CCGCC", "PKGKP", "CCGCC", "HHHHH")
	            .aisle("#HSH#", "#CCC#", "#PPP#", "#CCC#", "#HHH#")
	            .setAmountAtLeast('L', 30)
	            .setAmountAtLeast('K', 12)
	            .where('S', selfPredicate())
	            .where('L', statePredicate(getCasingState()))
	            .where('G', statePredicate(getGraphiteState()))
	            .where('K', statePredicate(getTrunkState()))
	            .where('P', statePredicate(getTrunkState()).or(abilityPartPredicate(MultiblockAbility.IMPORT_FLUIDS)))
	            .where('H', statePredicate(getCasingState()).or(abilityPartPredicate(ALLOWED_ABILITIES)))
	            .where('O', abilityPartPredicate(MultiblockAbility.EXPORT_ITEMS))
	            .where('I', abilityPartPredicate(MultiblockAbility.IMPORT_ITEMS))
	            .where('F', abilityPartPredicate(MultiblockAbility.IMPORT_FLUIDS))
	            .where('C', heatingCoilPredicate())
	            .where('#', isAirPredicate())
	            .build();
	}
	
	@Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.coilType = context.getOrDefault("CoilType", InductionCoilType.COPPER);
    }
	
	public InductionCoilType getCoilType() {
		return this.coilType;
	}
	
	public static IBlockState getGraphiteState() {
		final DustMaterial material = Materials.Graphite;
		final BlockCompressed block = MetaBlocks.COMPRESSED.get(material);
		return block.getDefaultState().withProperty(block.variantProperty, material);
    }
	
	public static IBlockState getCasingState() {
        return NPUMetaBlocks.MULTIBLOCK_CASING.getState(CasingType.MAGNETIC_PROOF_MACHINE_CASING);
    }
	
	public static IBlockState getTrunkState() {
		return NPUMetaBlocks.MULTIBLOCK_CASING.getState(CasingType.COOLANT_TRUNK_LINE);
	}
	
	public static Predicate<BlockWorldState> heatingCoilPredicate() {
		return blockWorldState -> {
			IBlockState blockState = blockWorldState.getBlockState();
			if (!(blockState.getBlock() instanceof BlockInductionCoil))
				return false;
			BlockInductionCoil blockWireCoil = (BlockInductionCoil) blockState.getBlock();
			InductionCoilType coilType = blockWireCoil.getState(blockState);
			InductionCoilType currentCoilType = blockWorldState.getMatchContext().getOrPut("CoilType", coilType);
			return currentCoilType.getName().equals(coilType.getName());
		};
	}
	
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.HEAT_PROOF_CASING; // TODO TEXTURES
    }
    
    @Nonnull
    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return Textures.AMPLIFAB_OVERLAY;
    }
	
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
    	super.writeToNBT(data);
    	data.setInteger("heat", heat);
    	return data;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound data) {
    	super.readFromNBT(data);
    	if (data.hasKey("heat"))
    		this.heat = data.getInteger("heat");
    }
    
	protected class IIBFRecipeLogic extends MultiblockRecipeLogic {
		
		private Fluid cachedFluid = null;
		
		public IIBFRecipeLogic(TileEntityIIBF tileEntity) {
			super(tileEntity);
		}
		
		@Override
		public void updateWorkable() {
			super.updateWorkable();
			TileEntityIIBF machine = TileEntityIIBF.this;
			if (!machine.getWorld().isRemote) {
				int maxVoltage = recipeEUt <= 0 ? (int) machine.getEnergyContainer().getInputVoltage() : recipeEUt;
				double voltageMult = Math.pow(maxVoltage * 0.00002D, 0.66D);
				// Air coolant
				if (getMetaTileEntity().getOffsetTimer() % 15 == 0 && machine.heat > 0)
					machine.heat--;
				
				int amount = drainCoolant((int)(machine.heat * 1.0D / MAX_HEAT * 200));
				if (amount > 0) {
					int coolantMult = NPURecipeMaps.COOLANTS.get(cachedFluid);
					int newHeat = (int)Math.max(0, machine.heat - (Math.pow(coolantMult, 1.7D) * amount * voltageMult));
					machine.heat = newHeat;
					if (!workingEnabled)
						drawEnergy((int)(maxVoltage * 0.95D));
				}
			}
		}
		
		@Override
		protected void updateRecipeProgress() {
			TileEntityIIBF machine = TileEntityIIBF.this;
			int coilTier = machine.coilType.ordinal();
			double overheat = ((double) machine.heat) / MAX_HEAT;
			long time = machine.getOffsetTimer();
			
			if (machine.heat <= STOP_LEVEL) {
				if (machine.blink)
					machine.blink = false;
				
				int throttle = 1;
				if (machine.heat > WORKING_LEVEL)
					throttle = (int)((overheat - 0.4D) * 40.0D) + 1;
				if (throttle < 21) {
					machine.heat += (int)(recipeEUt * 0.02D * Math.pow(0.9D, coilTier) *  (1 - (throttle - 1) * 0.025D));
					if (time % throttle == 0)
						super.updateRecipeProgress();
				}
			} else {
				drawEnergy((int)(recipeEUt * 0.9D));
				if (time % 15 == 0) {
					machine.blink = !machine.blink;
					if (progressTime > 1)
						--progressTime;
				}
			}
		}
		
		protected int drainCoolant(int amount) {
			IMultipleTankHandler fluidHanlder = TileEntityIIBF.this.getInputFluidInventory();
			FluidStack drained = null;
			if (cachedFluid == null) {
				for (Entry<Fluid, Integer> entry : NPURecipeMaps.COOLANTS.entrySet()) {
					drained = fluidHanlder.drain(new FluidStack(entry.getKey(), amount), true);
					if (drained != null) {
						cachedFluid = drained.getFluid();
						return drained.amount;
					}
				}
			} else {
				drained = fluidHanlder.drain(new FluidStack(cachedFluid, amount), true);
				if (drained != null)
					return drained.amount;
			}
			
			cachedFluid = null;
			return 0;
		}
		
		@Override
		protected int[] calculateOverclock(int EUt, int duration) {
			int[] result = super.calculateOverclock(EUt, duration);
			final int coilTier = TileEntityIIBF.this.coilType.ordinal();
			result[1] = Math.max(1, (int)(result[1] * 0.5D * Math.pow(0.85D, coilTier)));
			return result;
		}
	}
}
