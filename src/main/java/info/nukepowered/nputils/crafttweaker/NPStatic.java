package info.nukepowered.nputils.crafttweaker;

import crafttweaker.annotations.ZenRegister;

import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author TheDarkDnKTv
 *
 */
@ZenClass("mods.nputils.NPStatic")
@ZenRegister
public class NPStatic {
	
	@ZenMethod
	public static String objectToStr(Object obj) {
		return obj == null ? "null" : obj.toString();
	}
	
	@ZenMethod
	public static int parseStrToInt(String obj) {
		return Integer.parseInt(obj);
	}
}
