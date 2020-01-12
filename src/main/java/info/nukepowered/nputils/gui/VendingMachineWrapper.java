package info.nukepowered.nputils.gui;

import java.lang.ref.WeakReference;

import javax.annotation.Nullable;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.AbstractWidgetGroup;
import gregtech.api.gui.widgets.LabelWidget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.util.Position;
import gregtech.api.util.Size;
import info.nukepowered.nputils.machines.TileEntityVendingMachine;
import info.nukepowered.nputils.machines.TileEntityVendingMachine.MODE;
import net.minecraft.network.PacketBuffer;

public class VendingMachineWrapper extends AbstractWidgetGroup {
	
	private WeakReference<TileEntityVendingMachine> machine;
	private TileEntityVendingMachine.MODE machineMode;
	private final boolean isOwner;
	
	
	public VendingMachineWrapper(Position position, Size size, TileEntityVendingMachine machine, boolean isOwner) {
		super(position, size);
		this.machine = new WeakReference<>(machine);
		this.machineMode = machine.getMode();
		this.isOwner = isOwner;
	}
	
	public void initUI() {
    	TileEntityVendingMachine machine = this.getMachine();
    	if (machine == null) return;
    	
    	// TODO remove oredict button when changed mod and move prices' buttons
    	// TODO 2 add redeal button if coinsInside > price
    	// TODO add separated inserted money counter
    	
		boolean isSaling = machineMode == MODE.SALE;
		LabelWidget str = new LabelWidget(106, isSaling ? 28 : 74, "credits", 0x404040).setXCentered(true);
		CenterDynamicLabel money = new CenterDynamicLabel(106 , isSaling ? 20 : 66, () -> Integer.toString(machine.getPrice() - (isSaling ? machine.getCoinsInserted() : 0)));
		SlotWidget coinSlot = new SlotWidget(machine.getCoinSlot(), 0, 136, isSaling ? 18 : 65, true, true)
				.setBackgroundTexture(GuiTextures.SLOT, GuiTextures.LENS_OVERLAY);
		SlotWidget workSlot = new SlotWidget(machine.getWorkingSlot(), 0, 136, isSaling ? 65 : 18, isSaling, !isSaling)
				.setBackgroundTexture(GuiTextures.SLOT);
		SlotWidget sampleSlot = new SlotWidget(machine.getSample(), 0, 96, isSaling ? 65 : 18, isOwner, isOwner);
		

		if (isOwner) sampleSlot.setBackgroundTexture(GuiTextures.SLOT); 
		
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
			writeUpdateInfo(100, buf -> buf.writeBoolean(this.machineMode == MODE.SALE));
		}
	}
	
	 @Override
	 public void readUpdateInfo(int id, PacketBuffer buffer) {
		 super.readUpdateInfo(id, buffer);
		 if (id == 100) {
			 this.machineMode = buffer.readBoolean() ? MODE.SALE : MODE.PURCHASE;
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
