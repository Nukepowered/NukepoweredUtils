package info.nukepowered.nputils.machines;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

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
import gregtech.api.gui.widgets.DynamicLabelWidget;
import gregtech.api.gui.widgets.LabelWidget;
import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.MetaTileEntityUIFactory;
import gregtech.api.render.Textures;
import gregtech.api.util.GTUtility;
import gregtech.api.util.Position;
import gregtech.api.util.Size;
import info.nukepowered.nputils.api.NPULib;
import info.nukepowered.nputils.gui.VendingMachineUI;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityVendingMachine extends MetaTileEntity {
	
	private EnergyContainerHandler energyContainer;
	private EnumFacing outputFacing = EnumFacing.UP;
	private IItemHandlerModifiable coins;
	private IItemHandlerModifiable workingWith;
	private IItemHandlerModifiable sample;
	private int price = 0;
	private int dealsAmount = 0;
	private MODE workingMode = MODE.SALE;
	private UUID owner;

	
	public TileEntityVendingMachine(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId);
		reinitializeEnergyContainer();
		initializeInventory();
	}
	
	@Override
	public void addDebugInfo(List<String> list) {
		list.add("Owner: " + (this.owner != null ? this.owner.toString() : "§cnull"));
		list.add("Output: " + (this.outputFacing != null ? this.outputFacing.toString() : "§cnull"));
		list.add("Working mode: " + this.workingMode.name());
		list.add("Price: " + this.price);
    }
	
	
	
	
	public IItemHandlerModifiable getCoinSlot() {
		return this.coins;
	}
	
	public IItemHandlerModifiable getWorkingSlot() {
		return this.workingWith;
	}
	
	public IItemHandlerModifiable getSample() {
		return this.sample;
	}
	
	public int getDeals() {
		return this.dealsAmount;
	}
	
	public int getPrice() {
		return this.price;
	}
	
	public String workingMode() {
		return "Mode: " + (this.workingMode == MODE.SALE ? "Sale" : "Buying");
	}
	
	public MODE getMode() {
		return this.workingMode;
	}
	
	public void changeWorkingMode() {
		if (this.workingMode == MODE.PURCHASE) {
			this.workingMode = MODE.SALE;
		} else {
			this.workingMode = MODE.PURCHASE;
		}
		
		writeCustomData(102, buf -> buf.writeEnumValue(this.workingMode));
	}
	
	public void decrPrice(int decr) {
		if (this.price - decr >= 0) {
			this.price -= decr;
			writeCustomData(103, buf -> buf.writeInt(this.price));
		}
	}
	
	public void incPrice(int inc) {
		this.price += inc;
		writeCustomData(103, buf -> buf.writeInt(this.price));
	}
	
	public boolean isOwner(@Nonnull EntityPlayer player) {
		return EntityPlayer.getUUID(player.getGameProfile()).equals(this.owner);
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
		if (this.owner == null) {
			Textures.AMPLIFAB_OVERLAY.render(renderState, translation, colouredPipeline, getFrontFacing(), false);
		} else {
			Textures.AMPLIFAB_OVERLAY.render(renderState, translation, colouredPipeline, getFrontFacing(), true);
		}
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
		this.coins = new ItemStackHandler(1) {
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				return NPULib.getCoinBehaviour(stack) != null;
			}
		};
		this.workingWith = new ItemStackHandler(1);
		this.sample = new ItemStackHandler(1);
	}
	
    protected void reinitializeEnergyContainer() {
		this.energyContainer = EnergyContainerHandler.receiverContainer(this, 16384, GTValues.LV, 1L);
	}
	
    @Override
	protected ModularUI createUI(EntityPlayer entityPlayer) {
    	VendingMachineUI ui = new VendingMachineUI(new Position(0, 0), new Size(176, 120), this, this.isOwner(entityPlayer));
		WidgetGroup group = new WidgetGroup();
		
		group.addWidget(new LabelWidget(5, 5, "Vending Machine"));
		group.addWidget(new DynamicLabelWidget(18, 70, () -> "Deals: " + this.dealsAmount));
		if (this.isOwner(entityPlayer)) {
			group.addWidget(new ClickButtonWidget(25, 45, 12, 11, "+", data -> this.incPrice(data.isCtrlClick ? 1 : data.isShiftClick ? 100 : 10)));
			group.addWidget(new ClickButtonWidget(12, 45, 12, 11, "-", data -> this.decrPrice(data.isCtrlClick ? 1 : data.isShiftClick ? 100 : 10)));
			group.addWidget(new ClickButtonWidget(12, 57, 30, 11, "Mode", data -> this.changeWorkingMode()));
		}
		// TODO OreDict toggle button
		ui.initUI();
		
		return ModularUI.builder(GuiTextures.BACKGROUND, 176, 180)
				.widget(group)
				.widget(ui)
				.bindPlayerInventory(entityPlayer.inventory, 96)
				.build(getHolder(), entityPlayer);
	}
    
    @Override
    public boolean onRightClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
    	if (this.owner == null) {
    		if (!getWorld().isRemote) {
    			this.owner = EntityPlayer.getUUID(playerIn.getGameProfile());
    			this.writeCustomData(101, t -> {
    				t.writeString(this.owner.toString());
    			});
    			playerIn.sendMessage(new TextComponentString("Owner successfully set to " + playerIn.getName()));
    		}
    	} else {
    		if (getWorld() != null && !getWorld().isRemote) {
    			MetaTileEntityUIFactory.INSTANCE.openUI(getHolder(), (EntityPlayerMP) playerIn);
    		}
    	}
    	
    	return true;
    }
    
	@Override
	public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, CuboidRayTraceResult hitResult) {
		// Admins features
		if (playerIn.canUseCommand(2, "") && playerIn.isSneaking()) {
			if (!getWorld().isRemote) {
				playerIn.sendMessage(new TextComponentString("Owner: " + (this.owner == null ? "null" : this.owner.toString())));
			}
		}
		
		
		// For debug only, remove before commit into master
		if (!playerIn.isSneaking() && !getWorld().isRemote) {
			this.owner = UUID.randomUUID();
			this.writeCustomData(101, t -> {
				t.writeString(this.owner.toString());
			});
		}
		
		return true;
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
    public void writeInitialSyncData(PacketBuffer buf) {
		super.writeInitialSyncData(buf);
		buf.writeByte(this.outputFacing != null ? this.outputFacing.getIndex() : 1);
		buf.writeString(this.owner != null ? this.owner.toString() : "");
		buf.writeInt(this.price);
		buf.writeEnumValue(this.workingMode);
	}
	
	@Override
    public void receiveInitialSyncData(PacketBuffer buf) {
		super.receiveInitialSyncData(buf);
		this.outputFacing = EnumFacing.VALUES[buf.readByte()];
		String own = buf.readString(36);
		if (own.length() == 36) this.owner = UUID.fromString(own);
		this.price = buf.readInt();
		this.workingMode = buf.readEnumValue(MODE.class);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		
		if (this.owner != null) data.setString("Owner", this.owner.toString());
		if (this.outputFacing != null) data.setByte("Output", (byte) this.outputFacing.getIndex());
		data.setInteger("Price", this.price);
		data.setBoolean("Mode", this.workingMode == MODE.SALE);
		
		return data;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		
		if (data.hasKey("Owner")) this.owner = UUID.fromString(data.getString("Owner"));
		if (data.hasKey("Output")) this.outputFacing = EnumFacing.VALUES[data.getByte("Output")];
		this.price = data.getInteger("Price");
		this.workingMode = data.getBoolean("Mode") ? MODE.SALE : MODE.PURCHASE;
		
	}
	
	@Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == 100) {
            this.outputFacing = EnumFacing.VALUES[buf.readByte()];
            getHolder().scheduleChunkForRenderUpdate();
        } else if (dataId == 101) {
        	this.owner = UUID.fromString(buf.readString(36));
        	getHolder().scheduleChunkForRenderUpdate();
        } else if (dataId == 102) {
        	this.workingMode = buf.readEnumValue(MODE.class);
        } else if (dataId == 103) {
        	this.price = buf.readInt();
        }
    }
	
	public static enum MODE {
		SALE,
		PURCHASE;
	}
}
