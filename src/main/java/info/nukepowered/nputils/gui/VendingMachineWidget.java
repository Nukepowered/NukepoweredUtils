package info.nukepowered.nputils.gui;

import java.lang.ref.WeakReference;

import gregtech.api.gui.widgets.AbstractWidgetGroup;
import gregtech.api.util.Position;
import gregtech.api.util.Size;
import net.minecraft.network.PacketBuffer;

public class VendingMachineWidget extends AbstractWidgetGroup {
	
	private WeakReference<VendingMachineWrapper> wrapper;
	// Что-то, что будет возвращать, что можно сравнить, и понять, нужно ли обновить UI
	
	public VendingMachineWidget(Position position, Size size, VendingMachineWrapper wrapper) {
		super(position, size);
		this.wrapper = new WeakReference<>(wrapper);
	}
	
	@Override
    public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
	}
	
	 @Override
	 public void readUpdateInfo(int id, PacketBuffer buffer) {
		 super.readUpdateInfo(id, buffer);
		 
	 }
	
}
