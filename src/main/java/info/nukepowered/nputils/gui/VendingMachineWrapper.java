package info.nukepowered.nputils.gui;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import gregtech.api.gui.Widget;
import info.nukepowered.nputils.machines.TileEntityVendingMachine;

public class VendingMachineWrapper {
	
	private WeakReference<TileEntityVendingMachine> machine;
	
	public VendingMachineWrapper(TileEntityVendingMachine machine) {
		this.machine = new WeakReference<>(machine);
		
	}
	
	public void initUI(int y, Consumer<Widget> widgetGroup, boolean isOwner) {
		
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
