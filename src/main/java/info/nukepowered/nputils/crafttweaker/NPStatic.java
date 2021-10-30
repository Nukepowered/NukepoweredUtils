package info.nukepowered.nputils.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.zenscript.GlobalRegistry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author TheDarkDnKTv
 *
 */
@ZenClass("mods.nputils.NPStatic")
@ZenRegister
public class NPStatic {
	
	public static void init() {
		GlobalRegistry.registerGlobal("objectToStr",
				GlobalRegistry.getStaticFunction(NPStatic.class, "objectToStr", Object.class));
		GlobalRegistry.registerGlobal("parseStrToInt",
				GlobalRegistry.getStaticFunction(NPStatic.class, "parseStrToInt", String.class));
	}
	
	@ZenMethod
	public static String objectToStr(Object obj) {
		return obj == null ? "null" : obj.toString();
	}
	
	@ZenMethod
	public static int parseStrToInt(String obj) {
		return Integer.parseInt(obj);
	}
}
