package info.nukepowered.nputils.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("nputils");
	
	public NetworkHandler() {
		INSTANCE.registerMessage(KeysUpdateHandler.class, KeysPacket.class, 0, Side.SERVER);
	}
}
