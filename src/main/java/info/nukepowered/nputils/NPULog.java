package info.nukepowered.nputils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NPULog {
	private static Logger logger;
	
	public static void init() {
		logger = LogManager.getLogger(NPUtils.NAME);
		logger.info("################## NukePowered UTILS #####################");
		logger.info("#       Thank you for intsalling our GTCE addon!         #");
		logger.info(String.format("#           Current version is: %-24s #", NPUtils.VERSION));
		logger.info("#               Our offical website is                   #");
		logger.info("#        https://nukepowered.info/nputils.html           #");
		logger.info("##########################################################");
	}
	
	public static <T> void info(T str) {
		logger.info(str);
	}
	
	public static <T> void warn(T str) {
		logger.warn(str);
	}
	
	public static <T> void warn(String str, T e) {
		logger.warn(str, e);
	}
	
	public static <T> void error(T str) {
		logger.error(str);
	}
}
