package info.nukepowered.nputils.machines;

import gregtech.api.capability.impl.FilteredFluidHandler;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.gui.widgets.TankWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.SteamMetaTileEntity;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import info.nukepowered.nputils.NPUTextures;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class SteamMixer extends SteamMetaTileEntity {
	public SteamMixer(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, RecipeMaps.MIXER_RECIPES, NPUTextures.STEAM_MIXER_OVERLAY, false);
	}
	
	@Override
	public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
		return new SteamMixer(metaTileEntityId);
	}
	
	@Override
	protected boolean isBrickedCasing() {
		return false;
	}
	
	@Override
	public IItemHandlerModifiable createImportItemHandler() {
		return new ItemStackHandler(4);
	}
	
	@Override
	protected IItemHandlerModifiable createExportItemHandler() {
        return new ItemStackHandler(1);
    }
	
	@Override
	public FluidTankList createImportFluidHandler() {
		this.steamFluidTank = (new FilteredFluidHandler(this.getSteamCapacity())).setFillPredicate(ModHandler::isSteam);
		return new FluidTankList(false, this.steamFluidTank, new FluidTank(32000), new FluidTank(32000));
	}
	
	@Override
	protected FluidTankList createExportFluidHandler() {
		return new FluidTankList(false, new FluidTank(32000));
	}
	
	@Override
    public ModularUI createUI(EntityPlayer player) {
        return createUITemplate(player)
                .widget(new SlotWidget(this.importItems, 0, 36, 15)
                        .setBackgroundTexture(BRONZE_SLOT_BACKGROUND_TEXTURE, GuiTextures.BRONZE_DUST_OVERLAY))
                .widget(new SlotWidget(this.importItems, 1, 54, 15)
                        .setBackgroundTexture(BRONZE_SLOT_BACKGROUND_TEXTURE, GuiTextures.BRONZE_DUST_OVERLAY))
                .widget(new SlotWidget(this.importItems, 2, 36, 33)
                        .setBackgroundTexture(BRONZE_SLOT_BACKGROUND_TEXTURE, GuiTextures.BRONZE_DUST_OVERLAY))
                .widget(new SlotWidget(this.importItems, 3, 54, 33)
                        .setBackgroundTexture(BRONZE_SLOT_BACKGROUND_TEXTURE, GuiTextures.BRONZE_DUST_OVERLAY))
                .widget(new SlotWidget(this.exportItems, 0, 109, 23)
                		.setBackgroundTexture(BRONZE_SLOT_BACKGROUND_TEXTURE, GuiTextures.BRONZE_DUST_OVERLAY))
                .widget(new TankWidget(this.importFluids.getTankAt(1), 18, 15, 18, 18)
                        .setBackgroundTexture(NPUTextures.BRONZE_FLUID_SLOT).setAlwaysShowFull(true).setContainerClicking(false, true))
                .widget(new TankWidget(this.importFluids.getTankAt(2), 18, 33, 18, 18)
                        .setBackgroundTexture(NPUTextures.BRONZE_FLUID_SLOT).setAlwaysShowFull(true).setContainerClicking(false, true))
                .widget(new ProgressWidget(workableHandler::getProgressPercent, 78, 24, 20, 16)
                        .setProgressBar(getFullGuiTexture("progress_bar_%s_furnace"),
                                getFullGuiTexture("progress_bar_%s_furnace_filled"),
                                ProgressWidget.MoveType.HORIZONTAL))
                .widget(new TankWidget(this.exportFluids.getTankAt(0), 127, 23, 18, 18)
                        .setBackgroundTexture(NPUTextures.BRONZE_FLUID_SLOT).setAlwaysShowFull(true).setContainerClicking(true, false))
                .build(getHolder(), player);
    }
}
