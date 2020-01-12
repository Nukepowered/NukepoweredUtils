package info.nukepowered.nputils.machines;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Predicate;

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
import gregtech.api.gui.widgets.ClickButtonWidget.ClickData;
import gregtech.api.gui.widgets.CycleButtonWidget;
import gregtech.api.gui.widgets.DynamicLabelWidget;
import gregtech.api.gui.widgets.ImageWidget;
import gregtech.api.gui.widgets.LabelWidget;
import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.MetaTileEntityUIFactory;
import gregtech.api.render.Textures;
import gregtech.api.util.GTUtility;
import gregtech.api.util.Position;
import gregtech.api.util.Size;
import info.nukepowered.nputils.NPULog;
import info.nukepowered.nputils.NPUTextures;
import info.nukepowered.nputils.api.NPULib;
import info.nukepowered.nputils.gui.VendingMachineWrapper;
import info.nukepowered.nputils.item.CoinBehaviour;
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
	private IItemHandlerModifiable coins;
	private IItemHandlerModifiable workingWith;
	private IItemHandlerModifiable sample;
	private UUID owner;
	private int price = 0;
	private int dealsAmount = 0;
	private int coinsInserted = 0;
	private boolean oreDictMode = false;
	private boolean unlimitedStock = false;
	private MODE workingMode = MODE.SALE;
	private EnumFacing outputFacing = EnumFacing.UP;

	
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
		list.add("OreDictMode: " + this.oreDictMode);
		list.add("CoinsInside: " + this.coinsInserted);
		list.add("UnlimitedStock: " + this.unlimitedStock);
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
	
	public int getPrice() {
		return this.price;
	}
	
	public int getCoinsInserted() {
		return this.coinsInserted;
	}
	
	public MODE getMode() {
		return this.workingMode;
	}
	
	public void toggleStock() {
		this.unlimitedStock = !this.unlimitedStock;
		writeCustomData(105, buf -> buf.writeBoolean(this.unlimitedStock));
	}
	
	public void toggleOreDict() {
		this.oreDictMode = !this.oreDictMode;
		writeCustomData(104, buf -> buf.writeBoolean(this.oreDictMode));
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
	
	public boolean tryDeal() {
		ItemStack sample = this.sample.getStackInSlot(0);
		if (sample.isItemEqual(ItemStack.EMPTY)) return false; 
		Predicate<ItemStack> canInsert = stack -> this.workingWith.insertItem(0, stack, true).isEmpty();
		if (this.unlimitedStock) {
			if (this.workingMode == MODE.SALE) {
				while (canInsert.test(sample) && this.coinsInserted >= this.price) {
					this.coinsInserted -= this.price;
					writeCustomData(106, buf -> buf.writeInt(this.coinsInserted));
					this.workingWith.insertItem(0, sample.copy(), false);
				}
			} else {
				// TODO
			}
		} else {
			// TODO
		}

		
		return false;
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
				if (workingMode == MODE.SALE) {
					return NPULib.getCoinBehaviour(stack) != null;
				} else {
					return NPULib.getWalletBehaviour(stack) != null;
				}
			}
			
			@Override
			protected void onContentsChanged(int slot) {
				if (workingMode == MODE.SALE) {
					ItemStack coin = this.getStackInSlot(slot);
					int value = 0;
					if (NPULib.getCoinBehaviour(coin) != null) {
						CoinBehaviour beh = NPULib.getCoinBehaviour(coin);
						value = beh.getValue() * coin.getCount();
					}
					if (value <= 0) return;
					coinsInserted += value;
					this.setStackInSlot(slot, ItemStack.EMPTY);
					tryDeal();
				} else {
					
				}
				
				
		    }
		};
		this.workingWith = new ItemStackHandler(1) {
			@Override
			protected void onContentsChanged(int slot) {
				if (workingMode == MODE.PURCHASE) {
					tryDeal();
				}
		    }
			
			@Override
		    @Nonnull
		    public ItemStack extractItem(int slot, int amount, boolean simulate) {
				return super.extractItem(slot, amount, simulate);
			}
		};
		this.sample = new ItemStackHandler(1);
	}
	
    protected void reinitializeEnergyContainer() {
		this.energyContainer = EnergyContainerHandler.receiverContainer(this, 16384, GTValues.LV, 1L);
	}
	
    @Override
	protected ModularUI createUI(EntityPlayer entityPlayer) {
    	VendingMachineWrapper ui = new VendingMachineWrapper(new Position(0, 0), new Size(176, 120), this, this.isOwner(entityPlayer));
		WidgetGroup group  = new WidgetGroup();
		Function<ClickData, Integer> getValue = data -> {
			return data.isCtrlClick && data.isShiftClick ? 1000 : 
				data.isShiftClick ? 100 :
				data.isCtrlClick ? 1 : 10;
		};
		
		ui.initUI();
		group.addWidget(new LabelWidget(5, 5, "Vending Machine"));
		group.addWidget(new DynamicLabelWidget(10, 19, () -> "Deals: " + this.dealsAmount));
		group.addWidget(new ImageWidget(144, 39, 3, 24, NPUTextures.VENDING_MACHINE_LINE));
		group.addWidget(new ImageWidget(104, 39, 3, 24, NPUTextures.VENDING_MACHINE_LINE));
		if (this.isOwner(entityPlayer)) {
			group.addWidget(new CycleButtonWidget(10, 42, 38, 13, new String[] {"Exact", "OreDict"}, () -> this.oreDictMode ? 1 : 0, val -> this.toggleOreDict()));
			group.addWidget(new ClickButtonWidget(10, 29, 30, 11, "Mode", data -> this.changeWorkingMode()));
			group.addWidget(new ClickButtonWidget(23, 57, 12, 11, "+", data -> this.incPrice(getValue.apply(data))));
			group.addWidget(new ClickButtonWidget(10, 57, 12, 11, "-", data -> this.decrPrice(getValue.apply(data))));
			if (entityPlayer.canUseCommand(2, "")) {
				group.addWidget(new CycleButtonWidget(41, 29, 12, 11, new String[] {"✖", "∞"}, () -> this.unlimitedStock ? 1 : 0, val -> this.toggleStock()));
			}
		}

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
		buf.writeInt(this.coinsInserted);
		buf.writeInt(this.dealsAmount);
		buf.writeEnumValue(this.workingMode);
		buf.writeBoolean(this.unlimitedStock);
		buf.writeBoolean(this.oreDictMode);
	}
	
	@Override
    public void receiveInitialSyncData(PacketBuffer buf) {
		super.receiveInitialSyncData(buf);
		this.outputFacing = EnumFacing.VALUES[buf.readByte()];
		String own = buf.readString(36);
		if (own.length() == 36) this.owner = UUID.fromString(own);
		this.price = buf.readInt();
		this.coinsInserted = buf.readInt();
		this.dealsAmount = buf.readInt();
		this.workingMode = buf.readEnumValue(MODE.class);
		this.unlimitedStock = buf.readBoolean();
		this.oreDictMode = buf.readBoolean();
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		if (this.owner != null) data.setString("Owner", this.owner.toString());
		if (this.outputFacing != null) data.setByte("Output", (byte) this.outputFacing.getIndex());
		data.setInteger("Price", this.price);
		data.setInteger("CoinsInside", this.coinsInserted);
		data.setInteger("Deals", this.dealsAmount);
		data.setBoolean("Mode", this.workingMode == MODE.SALE);
		data.setBoolean("Unlimited", this.unlimitedStock);
		data.setBoolean("OreDictMode", this.oreDictMode);
		GTUtility.writeItems(this.sample, "Sample", data);
		
		return data;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		if (data.hasKey("Owner")) this.owner = UUID.fromString(data.getString("Owner"));
		if (data.hasKey("Output")) this.outputFacing = EnumFacing.VALUES[data.getByte("Output")];
		this.price = data.getInteger("Price");
		this.coinsInserted = data.getInteger("CoinsInside");
		this.dealsAmount = data.getInteger("Deals");
		this.workingMode = data.getBoolean("Mode") ? MODE.SALE : MODE.PURCHASE;
		this.unlimitedStock = data.getBoolean("Unlimited");
		this.oreDictMode = data.getBoolean("OreDictMode");
		GTUtility.readItems(this.sample, "Sample", data);
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
        } else if (dataId == 104) {
        	this.oreDictMode = buf.readBoolean();
        } else if (dataId == 105) {
        	this.unlimitedStock = buf.readBoolean();
        } else if (dataId == 106) {
        	this.coinsInserted = buf.readInt();
        }
    }
	
	public static enum MODE {
		SALE,
		PURCHASE;
	}
}
