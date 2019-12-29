package info.nukepowered.nputils;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = NPUtils.MODID, name = NPUtils.NAME, version = NPUtils.VERSION, dependencies = "required-after:gregtech;after:tconstruct")
public class NPUtils {
	public static final String MODID = "nputils";
	public static final String NAME = "Nukepowered Utils";
	public static final String VERSION = "@VERSION@";
	@SidedProxy(modId = MODID, clientSide = "nputils.ClientProxy", serverSide = "nputils.CommonProxy")
	private static CommonProxy proxy;

	
	public NPUtils() {
		NPUEnums.preInit();
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		proxy.preInit();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit();
	}	
}