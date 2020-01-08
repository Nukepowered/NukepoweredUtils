package info.nukepowered.nputils.machines;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import appeng.core.localization.GuiText;
import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.ColourMultiplier;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.impl.EnergyContainerHandler;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.render.Textures;
import gregtech.api.util.GTUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

public class TileEntityVendingMachine extends MetaTileEntity {
	
	private EnergyContainerHandler energyContainer;
	private EnumFacing outputFacing;
	
	public TileEntityVendingMachine(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId);
		reinitializeEnergyContainer();
		initializeInventory();
	}

	@Override
	public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
		return new TileEntityVendingMachine(metaTileEntityId);
	}
	
	@Override
	public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
		IVertexOperation[] colouredPipeline = ArrayUtils.add(pipeline, new ColourMultiplier(GTUtility.convertRGBtoOpaqueRGBA_CL(getPaintingColorForRendering())));
		Textures.VOLTAGE_CASINGS[1].render(renderState, translation, colouredPipeline);
		// TODO face overlay
		Textures.AMPLIFAB_OVERLAY.render(renderState, translation, colouredPipeline, getFrontFacing(), true);
		Textures.PIPE_OUT_OVERLAY.renderSided(getOutputFacing(), renderState, translation, colouredPipeline);
	}
	
	@Override
    protected void initializeInventory() {
		super.initializeInventory();
	}
	
    protected void reinitializeEnergyContainer() {
		this.energyContainer = EnergyContainerHandler.receiverContainer(this, 16384, GTValues.LV, 1L);
	}
	
	@Override
	protected ModularUI createUI(EntityPlayer entityPlayer) {
		ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 176, 180);
		
		builder.label(5, 5, "Vending Machine")
		// Add slots to import & export items
		.slot(getImportItems(), 0, 20, 20, GuiTextures.SLOT)
		.bindPlayerInventory(entityPlayer.inventory, 96);
		
		return builder.build(getHolder(), entityPlayer);
	}
	
	@Override
    public boolean onWrenchClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
        if (!playerIn.isSneaking()) {
            EnumFacing currentOutputSide = getOutputFacing();
            if (currentOutputSide == facing || getFrontFacing() == facing) {
            	return false;
            }
            if(!getWorld().isRemote) {
               	setOutputFacing(facing);
            }
            return true;
        }
        return super.onWrenchClick(playerIn, hand, facing, hitResult);
    }
	
	@Override
    public boolean isValidFrontFacing(EnumFacing facing) {
		return Arrays.asList(EnumFacing.HORIZONTALS).contains(facing) && facing != getOutputFacing();
    }

	public EnumFacing getOutputFacing() {
        return this.outputFacing == null ? EnumFacing.UP : this.outputFacing;
    }
	
	public void setOutputFacing(EnumFacing outputFacing) {
        this.outputFacing = outputFacing;
        if (!getWorld().isRemote) {
            getHolder().notifyBlockUpdate();
            writeCustomData(100, buf -> buf.writeByte(outputFacing.getIndex()));
            markDirty();
        }
    }
	
	@Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == 100) {
            this.outputFacing = EnumFacing.VALUES[buf.readByte()];
            getHolder().scheduleChunkForRenderUpdate();
        }
    }
}
