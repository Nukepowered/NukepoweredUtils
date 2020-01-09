package info.nukepowered.nputils.machines;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.ColourMultiplier;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.impl.EnergyContainerHandler;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.MetaTileEntityUIFactory;
import gregtech.api.render.Textures;
import gregtech.api.util.GTUtility;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityVendingMachine extends MetaTileEntity {
	
	private EnergyContainerHandler energyContainer;
	private EnumFacing outputFacing;
	private UUID owner;
	
	public TileEntityVendingMachine(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId);
		reinitializeEnergyContainer();
		initializeInventory();
	}
	
	@Override
	public void addDebugInfo(List<String> list) {
		if (this.owner != null) list.add("Owner: " + this.owner.toString());
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
	@SideOnly(Side.CLIENT)
	public Pair<TextureAtlasSprite, Integer> getParticleTexture() {
		return Pair.of(Textures.VOLTAGE_CASINGS[1].getParticleSprite(), 0xFFFFFF);
	}
	
	@Override
    protected void initializeInventory() {
		super.initializeInventory();
	}
	
    protected void reinitializeEnergyContainer() {
		this.energyContainer = EnergyContainerHandler.receiverContainer(this, 16384, GTValues.LV, 1L);
	}
	
    @Override
    public boolean onRightClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
    	if (this.owner == null) {
    		if (playerIn.isSneaking()) {
    			this.owner = EntityPlayer.getUUID(playerIn.getGameProfile());
    			if (!getWorld().isRemote) playerIn.sendMessage(new TextComponentString("Owner set to: " + playerIn.getName()).setStyle(new Style().setColor(TextFormatting.YELLOW)));
    			return true;
    		} else {
    			if (!getWorld().isRemote) {
	    			playerIn.sendMessage(new TextComponentString("Vending machine have not owner.").setStyle(new Style().setColor(TextFormatting.RED)));
	    			playerIn.sendMessage(new TextComponentString("Press shift-right click to set you as owner"));
    			}
    			return false;
    		}
    	} else {
    		if (getWorld() != null && !getWorld().isRemote) {
    			MetaTileEntityUIFactory.INSTANCE.openUI(getHolder(), (EntityPlayerMP) playerIn);
    			return true;
    		}
    		
    		return false;
    	}
    }
    
	@Override
	protected ModularUI createUI(EntityPlayer entityPlayer) {
		ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 176, 180);
		
		// Test
		if (this.owner.equals(EntityPlayer.getUUID(entityPlayer.getGameProfile()))) {
			builder.widget(new ClickButtonWidget(20, 20, 20, 20, "T", t ->  {}));
		}
		
		builder.label(5, 5, "Vending Machine")
		// Add slots to import & export items
//		.slot(getImportItems(), 0, 20, 20, GuiTextures.SLOT)
		.bindPlayerInventory(entityPlayer.inventory, 96);
		
		return builder.build(getHolder(), entityPlayer);
	}
	
	@Override
	public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
		if (playerIn.canUseCommand(2, "") && playerIn.isSneaking()) {
			playerIn.sendMessage(new TextComponentString("Owner: " + (this.owner == null ? "null" : this.owner.toString())));
			return true;
		}
		
		if (!playerIn.isSneaking()) {
			this.owner = UUID.fromString("fw");
		}
		
		return false;
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
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		
		if (this.owner != null) data.setString("Owner", this.owner.toString());
		
		return data;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		if (data.hasKey("Owner")) {
			this.owner = UUID.fromString(data.getString("Owner"));
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
