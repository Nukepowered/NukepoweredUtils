package info.nukepowered.nputils.gui;

import java.lang.ref.WeakReference;

import javax.annotation.Nullable;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.AbstractWidgetGroup;
import gregtech.api.gui.widgets.LabelWidget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.gui.widgets.ToggleButtonWidget;
import gregtech.api.util.Position;
import gregtech.api.util.Size;
import gregtech.api.util.function.BooleanConsumer;
import info.nukepowered.nputils.NPUTextures;
import info.nukepowered.nputils.api.NPULib;
import info.nukepowered.nputils.machines.TileEntityVendingMachine;
import info.nukepowered.nputils.machines.TileEntityVendingMachine.MODE;
import net.minecraft.network.PacketBuffer;

public class VendingMachineWrapper extends AbstractWidgetGroup {
	
	private WeakReference<TileEntityVendingMachine> machine;
	private TileEntityVendingMachine.MODE machineMode;
	private boolean oreDict;
	private final boolean isOwner;
	
	
	public VendingMachineWrapper(Position position, Size size, TileEntityVendingMachine machine, boolean isOwner) {
		super(position, size);
		this.machine = new WeakReference<>(machine);
		this.machineMode = machine.getMode();
		this.isOwner = isOwner;
		this.oreDict = machine.getOreDictState();
	}
	
	public void initUI() {
    	TileEntityVendingMachine machine = this.getMachine();
    	if (machine == null) return;
		boolean isSaling = machineMode == MODE.SALE;
		LabelWidget str = new LabelWidget(106, isSaling ? 28 : 74, "nputils.vending_machine.ui.wrapper.credits", 0x404040).setXCentered(true);
		CenterDynamicLabel money = new CenterDynamicLabel(106 , isSaling ? 20 : 66, () -> NPULib.format(machine.getPrice()));
		SlotWidget coinSlot = new SlotWidget(machine.getCoinSlot(), 0, 130, isSaling ? 18 : 65, true, true)
				.setBackgroundTexture(GuiTextures.SLOT, (isSaling ? NPUTextures.COIN_OVERLAY : NPUTextures.WALLET_OVERLAY));
		SlotWidget workSlot = new SlotWidget(machine.getWorkingSlot(), 0, 130, isSaling ? 65 : 18, true, !isSaling)
				.setBackgroundTexture(GuiTextures.SLOT, (isSaling ? NPUTextures.SELL_OVERLAY : NPUTextures.BUY_OVERLAY));
		SlotWidget sampleSlot = new SlotWidget(machine.getSample(), 0, 96, isSaling ? 65 : 18, isOwner, isOwner);
		BooleanConsumer oreDictSwitch = state -> {
			this.oreDict = state;
			machine.setOreDictMode(state);
		};
		ToggleButtonWidget oreDict = new ToggleButtonWidget(150, 18, 18, 18, NPUTextures.BUTTON_OREDICT,
				() -> this.oreDict, oreDictSwitch)
				.setTooltipText("nputils.vending_machine.ui.wrapper.oredict");
		
		if (isOwner) {
			sampleSlot.setBackgroundTexture(GuiTextures.SLOT); 
		}
		if (!isSaling && isOwner) {
			this.addWidget(oreDict);
		}
		if (this.oreDict && !isOwner) {
			this.addWidget(new LabelWidget(8, 59, "OreDict", 0xa90004));
		}
		this.addWidget(money);
		this.addWidget(str);
		this.addWidget(coinSlot);
		this.addWidget(workSlot);
		this.addWidget(sampleSlot);
	}
	
	@Override
    public void detectAndSendChanges() {
		super.detectAndSendChanges();
		TileEntityVendingMachine machine = this.getMachine();
		if (machine == null) return;
		if (this.machineMode != machine.getMode()) {
			this.machineMode = machine.getMode();
			writeUpdateInfo(100, buf -> {
				buf.writeBoolean(this.machineMode == MODE.SALE);
				buf.writeBoolean(this.oreDict);
			});
		}
	}
	
	 @Override
	 public void readUpdateInfo(int id, PacketBuffer buffer) {
		 super.readUpdateInfo(id, buffer);
		 if (id == 100) {
			 this.machineMode = buffer.readBoolean() ? MODE.SALE : MODE.PURCHASE;
			 this.oreDict = buffer.readBoolean();
			 this.clearAllWidgets();
			 this.initUI();
		 }
	 }
	 
	@Nullable
	private TileEntityVendingMachine getMachine() {
		TileEntityVendingMachine machine = this.machine.get();

		if (machine != null) {
			if (machine.isValid()) {
				return machine;
			}
		}

		this.machine.clear();
		return null;
	}
}
