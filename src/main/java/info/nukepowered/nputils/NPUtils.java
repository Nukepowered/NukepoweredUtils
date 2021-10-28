package info.nukepowered.nputils;

import crafttweaker.zenscript.GlobalRegistry;
import info.nukepowered.nputils.crafttweaker.NPStatic;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = NPUtils.MODID, name = NPUtils.NAME, version = NPUtils.VERSION, dependencies = "required-after:gregtech@[1.8.13.470,);after:tconstruct")
public class NPUtils {
	public static final String MODID = "nputils";
	public static final String NAME = "Nukepowered Utils";
	public static final String VERSION = "@VERSION@";
	@SidedProxy(modId = MODID, clientSide = "info.nukepowered.nputils.ClientProxy", serverSide = "info.nukepowered.nputils.CommonProxy")
	private static CommonProxy proxy;

	
	public NPUtils() {
		NPUEnums.preInit();
	}
	
	@EventHandler
	public void onConstruct(FMLConstructionEvent e) {
		GlobalRegistry.registerGlobal("objectToStr",
				GlobalRegistry.getStaticFunction(NPStatic.class, "objectToStr", Object.class));
		GlobalRegistry.registerGlobal("parseStrToInt",
				GlobalRegistry.getStaticFunction(NPStatic.class, "parseStrToInt", String.class));
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