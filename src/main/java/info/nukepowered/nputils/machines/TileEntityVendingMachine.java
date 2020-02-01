package info.nukepowered.nputils.machines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.ColourMultiplier;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.capability.impl.EnergyContainerHandler;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.Widget.ClickData;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.gui.widgets.CycleButtonWidget;
import gregtech.api.gui.widgets.DynamicLabelWidget;
import gregtech.api.gui.widgets.ImageWidget;
import gregtech.api.gui.widgets.LabelWidget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.MetaTileEntityUIFactory;
import gregtech.api.render.Textures;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.util.GTUtility;
import gregtech.api.util.Position;
import gregtech.api.util.Size;
import info.nukepowered.nputils.NPUTextures;
import info.nukepowered.nputils.api.NPULib;
import info.nukepowered.nputils.gui.VendingMachineWrapper;
import info.nukepowered.nputils.item.CoinBehaviour;
import info.nukepowered.nputils.item.NPUMetaItems;
import info.nukepowered.nputils.item.WalletBehavior;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityVendingMachine extends MetaTileEntity {
	
	private EnergyContainerHandler energyContainer;
	private IItemHandlerModifiable coins;
	private IItemHandlerModifiable workingWith;
	private IItemHandlerModifiable sample;
	private IItemHandlerModifiable battery;
	private UUID owner;
	private int price = 1;
	private int dealsAmount = 0;
	private int coinsInserted = 0;
	private final long ENERGY_PER_DEAL;
	private boolean oreDictMode = false;
	private boolean unlimitedStock = false;
	private MODE workingMode = MODE.SALE;
	private EnumFacing outputFacing = EnumFacing.UP;

	
	public TileEntityVendingMachine(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId);
		reinitializeEnergyContainer();
		initializeInventory();
		this.ENERGY_PER_DEAL = 32L;
	}
	
	@Override
	public void update() {
		super.update();
		if (!getWorld().isRemote) this.energyContainer.dischargeOrRechargeEnergyContainers(this.battery, 0);
		if (getWorld().getWorldTime() % 10 == 0) {
			this.tryDeal();
		}
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
	
	private void updateCoins() {
		if (workingMode == MODE.SALE) {
			ItemStack coins = this.coins.getStackInSlot(0);
			if (!coins.isEmpty()) {
				CoinBehaviour beh = NPULib.getBehaviour(CoinBehaviour.class, coins);
				this.coins.setStackInSlot(0, ItemStack.EMPTY);
				this.coinsInserted += beh.getValue() * coins.getCount();
			}
		} else {
			ItemStack wallet = this.coins.getStackInSlot(0);
			if (!wallet.isEmpty() && NPULib.getBehaviour(CoinBehaviour.class, wallet) == null) {
				NBTTagCompound data = GTUtility.getOrCreateNbtCompound(wallet);
				int coinsInside = data.getInteger("MoneyAmount");
				coinsInside += this.coinsInserted;
				this.coinsInserted = 0;
				data.setInteger("MoneyAmount", coinsInside);
				wallet.setTagCompound(data);
			}
		}
	}
	
	private boolean canWork() {
		if (this.unlimitedStock) {
			return true;
		} else {
			if (this.owner != null) {
				return true;
			}
		}
		return false;
	}
	
	private ItemStack findWallet() {
		IItemHandler storage = this.getStorage();
		ItemStack wallet = ItemStack.EMPTY;
		if (storage != null) {
			for (int i = 0; i < storage.getSlots(); i++) {
				if (storage.getStackInSlot(i).isItemEqual(NPUMetaItems.COIN_WALLET.getStackForm())) {
					wallet = storage.getStackInSlot(i);
					break;
				}
			}
		}
		
		return wallet;
	}
	
	private int availableDeals() {
		int itemAvailable = 0;
		IItemHandler storage = this.getStorage();
		if (!this.unlimitedStock) {
			if (this.workingMode == MODE.SALE) {
				ItemStack sample = this.sample.getStackInSlot(0);
				if (!sample.isEmpty() && storage != null) {
					List<Integer> slots = NPULib.getItemInSlots(storage, sample);
					for (int index : slots) itemAvailable += storage.getStackInSlot(index).getCount();
					return MathHelper.floor(itemAvailable * 1.0D / sample.getCount());
				}
			} else {
				ItemStack wallet = this.findWallet();
				if (!wallet.isEmpty()) {
					NBTTagCompound data = GTUtility.getOrCreateNbtCompound(wallet);
					itemAvailable = MathHelper.floor(data.getInteger("MoneyAmount") * 1.0D / this.price);
				}
			}
		} else {
			return -1;
		}
		
		return itemAvailable;
	}
	
	public IItemHandler getCoinSlot() {
		return (IItemHandler) this.coins;
	}
	
	public IItemHandler getWorkingSlot() {
		return (IItemHandler) this.workingWith;
	}
	
	public IItemHandler getSample() {
		return (IItemHandler) this.sample;
	}
	
	public int getPrice() {
		return this.price;
	}
	
	public boolean getOreDictState() {
		return this.oreDictMode;
	}
	
	public MODE getMode() {
		return this.workingMode;
	}
	
	public void setStockMode(boolean mode) {
		this.unlimitedStock = mode;
		writeCustomData(105, buf -> buf.writeBoolean(this.unlimitedStock));
	}
	
	public void setOreDictMode(boolean mode) {
		this.oreDictMode = mode;
		writeCustomData(104, buf -> buf.writeBoolean(mode));
	}
	
	public void toggleWorkingMode() {
		if (this.workingMode == MODE.PURCHASE) {
			this.workingMode = MODE.SALE;
		} else {
			this.workingMode = MODE.PURCHASE;
		}
		
		writeCustomData(102, buf -> buf.writeEnumValue(this.workingMode));
	}
	
	public void decrPrice(int decr) {
		if (this.price - decr > 0) {
			this.price -= decr;
		} else {
			this.price = 1;
		}
		writeCustomData(103, buf -> buf.writeInt(this.price));
	}
	
	public void incPrice(int inc) {
		this.price += inc;
		writeCustomData(103, buf -> buf.writeInt(this.price));
	}
	
	public boolean isOwner(EntityPlayer player) {
		if (player == null) return false;
		return EntityPlayer.getUUID(player.getGameProfile()).equals(this.owner);
	}
	
	@Nullable
	private IItemHandler getStorage() {
		World world = getWorld();
		if (world == null) return null;
		TileEntity te = world.getTileEntity(getPos().offset(this.outputFacing));
		if (te != null) {
			if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.outputFacing.getOpposite())) ;{
				return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.outputFacing.getOpposite());
			}
		}
		
		return null;
	}
	
	// Deal-related methods
	private void tryDeal() {
		if (this.workingMode == MODE.SALE) {
			ItemStack sample = this.sample.getStackInSlot(0);
			if (sample.isEmpty() || !ItemHandlerHelper.insertItemStacked(workingWith, sample.copy(), true).isEmpty() || this.price > this.coinsInserted) return;
			this.workModeSelling(sample);
		} else {
			ItemStack workingItem = this.workingWith.getStackInSlot(0);
			ItemStack wallet = this.coins.getStackInSlot(0);
			ItemStack sample = this.sample.getStackInSlot(0);
			if (sample.isEmpty() || wallet.isEmpty() || workingItem.isEmpty()) return;
			if (this.oreDictMode && !OreDictUnifier.getOreDictionaryNames(sample).isEmpty()) {
				this.workModeBuyWithOreDict();
			} else {
				this.workModeBuy();
			}
		}
	}
	
	private void workModeSelling(ItemStack sample) {
		Predicate<ItemStack> canInsert = stack -> ItemHandlerHelper.insertItemStacked(workingWith, stack, true).isEmpty();
		if (this.unlimitedStock) {
			while (canInsert.test(sample) && this.coinsInserted >= this.price) {
				this.coinsInserted -= this.price;
				this.dealsAmount++;
				ItemHandlerHelper.insertItemStacked(workingWith, sample.copy(), false);
			}
		} else {
			IItemHandler storage = this.getStorage();
			if (storage != null && !getWorld().isRemote) {
				List<Integer> loot = NPULib.getItemInSlots(storage, sample);
				ItemStack wallet = this.findWallet();
				int availableItems = 0;
				for (int x : loot) {
					availableItems += storage.getStackInSlot(x).getCount();
				}
				
				while (canInsert.test(sample) && this.coinsInserted >= this.price && this.energyContainer.getEnergyStored() >= ENERGY_PER_DEAL && availableItems > 0 && !wallet.isEmpty()) {
					this.coinsInserted -= this.price;
					this.dealsAmount++;
					int needRemove = sample.getCount();
					ItemHandlerHelper.insertItemStacked(this.workingWith, sample.copy(), false);
					
					for (int i = 0; i < loot.size() && needRemove > 0; i++) {
						ItemStack removed = storage.getStackInSlot(loot.get(i));
						if (removed.getCount() < needRemove) {
							needRemove -= removed.getCount();
							removed.shrink(removed.getCount());
						} else {
							needRemove = 0;
							removed.shrink(sample.getCount());
						}
					}
					
					NBTTagCompound data = GTUtility.getOrCreateNbtCompound(wallet);
					data.setInteger("MoneyAmount", data.getInteger("MoneyAmount") + this.price);
					wallet.setTagCompound(data);
					availableItems -= sample.getCount();
					this.energyContainer.removeEnergy(ENERGY_PER_DEAL);
				}
			}
		}
		writeCustomData(106, buf -> {
			buf.writeInt(this.coinsInserted);
			buf.writeInt(this.dealsAmount);
		});
	}
	
	private void workModeBuy() {
		ItemStack sample = this.sample.getStackInSlot(0);
		ItemStack workingWith = this.workingWith.getStackInSlot(0);
		ItemStack walletOwn = this.findWallet();
		ItemStack wallet = this.coins.getStackInSlot(0);
		IItemHandler storage = this.getStorage();
		if (this.unlimitedStock) {
			if (sample.isItemEqual(workingWith)) {
				while (sample.getCount() <= workingWith.getCount()) {
					workingWith.shrink(sample.getCount());
					this.coinsInserted += this.price;
					this.dealsAmount++;
					this.updateCoins();
					if (workingWith.isEmpty()) break;
				}
			}
		} else {
			if (sample.isItemEqual(workingWith) && storage != null) {
				while (sample.getCount() <= workingWith.getCount() && this.energyContainer.getEnergyCapacity() >= ENERGY_PER_DEAL && !wallet.isEmpty()) {
					NBTTagCompound data = GTUtility.getOrCreateNbtCompound(walletOwn);
					if (data.getInteger("MoneyAmount") >= this.price && ItemHandlerHelper.insertItemStacked(storage, sample, true).isEmpty()) {
						workingWith.shrink(sample.getCount());
						data.setInteger("MoneyAmount", data.getInteger("MoneyAmount") - this.price);
						walletOwn.setTagCompound(data);
						this.coinsInserted += this.price;
						this.dealsAmount++;
						this.updateCoins();
						ItemHandlerHelper.insertItemStacked(storage, sample.copy(), false);
						this.energyContainer.removeEnergy(ENERGY_PER_DEAL);
					} else {
						break;
					}
				}
			}
		}
		
		writeCustomData(106, buf -> {
			buf.writeInt(this.coinsInserted);
			buf.writeInt(this.dealsAmount);
		});
	}
	
	private void workModeBuyWithOreDict() {
		ItemStack sample = this.sample.getStackInSlot(0);
		ItemStack workingWith = this.workingWith.getStackInSlot(0);
		ItemStack walletOwn = this.findWallet();
		ItemStack wallet = this.coins.getStackInSlot(0);
		IItemHandler storage = this.getStorage();
		List<String> sampleOreDict = new ArrayList<>(OreDictUnifier.getOreDictionaryNames(sample));
		List<String> workingOreDict = new ArrayList<>(OreDictUnifier.getOreDictionaryNames(workingWith));
		boolean canDeal = false;
		
		for (String str : sampleOreDict) {
			if (workingOreDict.contains(str)) {
				canDeal = true;
				break;
			}
		}
		
		if (canDeal) {
			if (this.unlimitedStock) {
				while (sample.getCount() <= workingWith.getCount()) {
					workingWith.shrink(sample.getCount());
					this.coinsInserted += this.price;
					this.dealsAmount++;
					this.updateCoins();
					if (workingWith.isEmpty()) break;
				}
			} else {
				if (!wallet.isEmpty() && !walletOwn.isEmpty() && storage != null) {
					while (sample.getCount() <= workingWith.getCount() && this.energyContainer.getEnergyCapacity() >= ENERGY_PER_DEAL) {
						NBTTagCompound data = GTUtility.getOrCreateNbtCompound(walletOwn);
						ItemStack moving = workingWith.copy();
						moving.setCount(sample.getCount());
						if (data.getInteger("MoneyAmount") >= this.price && ItemHandlerHelper.insertItemStacked(storage, moving, true).isEmpty()) {
							data.setInteger("MoneyAmount", data.getInteger("MoneyAmount") - this.price);
							walletOwn.setTagCompound(data);
							this.coinsInserted += this.price;
							this.dealsAmount++;
							this.updateCoins();
							ItemHandlerHelper.insertItemStacked(storage, moving, false);
							workingWith.shrink(sample.getCount());
							this.energyContainer.removeEnergy(ENERGY_PER_DEAL);
						} else {
							break;
						}
					}
				}
			}
			
			writeCustomData(106, buf -> {
				buf.writeInt(this.coinsInserted);
				buf.writeInt(this.dealsAmount);
			});
		}
	}
	
	@Override
    protected void initializeInventory() {
		super.initializeInventory();
		this.coins = new ItemStackHandler(1) {
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				if (workingMode == MODE.SALE) {
					return NPULib.getBehaviour(CoinBehaviour.class, stack) != null;
				} else {
					return NPULib.getBehaviour(WalletBehavior.class, stack) != null;
				}
			}
			
			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				updateCoins();
		    }
		};
		this.workingWith = new ItemStackHandler(1);
		this.sample = new ItemStackHandler(1);
		this.battery = new ItemStackHandler(1);
	}
	
    protected void reinitializeEnergyContainer() {
		this.energyContainer = EnergyContainerHandler.receiverContainer(this, 16384, GTValues.V[GTValues.LV], 1L);
	}
	
	@Override
	public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
		return new TileEntityVendingMachine(metaTileEntityId);
	}
	
	@Override
	public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
		IVertexOperation[] colouredPipeline = ArrayUtils.add(pipeline, new ColourMultiplier(GTUtility.convertRGBtoOpaqueRGBA_CL(getPaintingColorForRendering())));
		Textures.VOLTAGE_CASINGS[1].render(renderState, translation, colouredPipeline);
		NPUTextures.VENDING_MACHINE.render(renderState, translation, colouredPipeline, this.getFrontFacing(), this.canWork());
		Textures.PIPE_OUT_OVERLAY.renderSided(getOutputFacing(), renderState, translation, colouredPipeline);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Pair<TextureAtlasSprite, Integer> getParticleTexture() {
		return Pair.of(Textures.VOLTAGE_CASINGS[1].getParticleSprite(), 0xFFFFFF);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
		tooltip.add(I18n.format("gregtech.universal.tooltip.voltage_in", energyContainer.getInputVoltage(), GTValues.VN[GTValues.LV]));
        tooltip.add(I18n.format("gregtech.universal.tooltip.energy_storage_capacity", energyContainer.getEnergyCapacity()));
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> dropsList, @Nullable EntityPlayer harvester) {
		dropsList.add(this.sample.getStackInSlot(0));
		dropsList.add(this.workingWith.getStackInSlot(0));
		dropsList.add(this.coins.getStackInSlot(0));
		if (this.coinsInserted > 0 && EntityPlayer.getUUID(harvester.getGameProfile()).equals(this.owner)) {
			dropsList.addAll(NPULib.getCoinsList(this.coinsInserted));
		}
	}
	
    @Override
	protected ModularUI createUI(EntityPlayer entityPlayer) {
    	VendingMachineWrapper wrapper = new VendingMachineWrapper(new Position(0, 0), new Size(176, 120), this, this.isOwner(entityPlayer));
    	SlotWidget batterySlot = new SlotWidget((IItemHandler) this.battery, 0, 150, 65, this.isOwner(entityPlayer) || entityPlayer.canUseCommand(2, ""), this.isOwner(entityPlayer) || entityPlayer.canUseCommand(2, "")) {
    		@Override
    	    public boolean canPutStack(ItemStack stack) {
    	        IElectricItem capability = stack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
    	        return capability != null && capability.canProvideChargeExternally();
    	    }
    	};
    	batterySlot.setBackgroundTexture(GuiTextures.SLOT, GuiTextures.CHARGER_OVERLAY);
		WidgetGroup staticWidgets  = new WidgetGroup();
		Function<ClickData, Integer> getValue = data -> {
			return data.isCtrlClick && data.isShiftClick ? 1000 : 
				data.isShiftClick ? 100 :
				data.isCtrlClick ? 1 : 10;
		};
		Consumer<List<ITextComponent>> text = textList -> {
			textList.add(new TextComponentTranslation("nputils.vending_machine.ui.available_deals"));
			String deals = this.availableDeals() != -1 ? NPULib.format(this.availableDeals()) : "ထ";
			textList.add(new TextComponentString(deals));
		};
		wrapper.initUI();
		staticWidgets.addWidget(new LabelWidget(3, 3, "nputils.machine.vending_machine.label"));
		staticWidgets.addWidget(new LabelWidget(8, 15, "nputils.vending_machine.ui.credits"));
		staticWidgets.addWidget(new DynamicLabelWidget(8, 85, () -> I18n.format("nputils.vending_machine.ui.deals", NPULib.format(this.dealsAmount))));
		staticWidgets.addWidget(new DynamicLabelWidget(8, 26, () -> NPULib.format(this.coinsInserted)));
		staticWidgets.addWidget(new AdvancedTextWidget(8, 37, text, 0x404040));
		staticWidgets.addWidget(new ImageWidget(137, 39, 3, 24, NPUTextures.VENDING_MACHINE_LINE));
		staticWidgets.addWidget(new ImageWidget(104, 39, 3, 24, NPUTextures.VENDING_MACHINE_LINE));
		if (!this.unlimitedStock) staticWidgets.addWidget(batterySlot);
		if (this.isOwner(entityPlayer)) {
			staticWidgets.addWidget(new ClickButtonWidget(8, 70, 31, 12, "nputils.vending_machine.ui.mode_switch", data -> this.toggleWorkingMode()));
			staticWidgets.addWidget(new ClickButtonWidget(52, 70, 13, 12, "+", data -> this.incPrice(getValue.apply(data))));
			staticWidgets.addWidget(new ClickButtonWidget(39, 70, 13, 12, "-", data -> this.decrPrice(getValue.apply(data))));
			if (entityPlayer.canUseCommand(2, "")) {
				staticWidgets.addWidget(new CycleButtonWidget(66, 70, 13, 12, new String[] {"S", "ထ"}, () -> this.unlimitedStock ? 1 : 0, val -> this.setStockMode(val == 1)).setTooltipHoverString("nputils.vending_machine.ui.stock"));
			}
		}
		
		return ModularUI.builder(GuiTextures.BACKGROUND, 176, 180)
				.widget(staticWidgets)
				.widget(wrapper)
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
    			playerIn.sendMessage(new TextComponentTranslation("nputils.vending_machine.owner_change", playerIn.getName()));
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
    public float getBlockHardness() {
		World world = getWorld();
		if (world == null) return -1.0f;
        return this.isOwner(world.getClosestPlayer(getPos().getX(), getPos().getY(), getPos().getZ(), 2.0D, false)) || this.owner == null ? 6.0f : -1.0f;
    }

    @Override
    public float getBlockResistance() {
        return 100.0F;
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
		GTUtility.writeItems(this.workingWith, "Working", data);
		GTUtility.writeItems(this.coins, "Coins", data);
		GTUtility.writeItems(this.battery, "Battery", data);
		
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
		GTUtility.readItems(this.workingWith, "Working", data);
		GTUtility.readItems(this.coins, "Coins", data);
		GTUtility.readItems(this.battery, "Battery", data);
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
        	this.dealsAmount = buf.readInt();
        }
    }
	
	public static enum MODE {
		SALE,
		PURCHASE;
	}
}
