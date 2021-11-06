package info.nukepowered.nputils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NPULog {
	public static Logger logger;
	
	public static void init() {
		logger = LogManager.getLogger(NPUtils.NAME);
		logger.info("################## NukePowered UTILS #####################");
		logger.info("#       Thank you for intsalling our GTCE addon!         #");
		logger.info(String.format("#           Current version is: %-24s #", NPUtils.VERSION));
		logger.info("#               Our offical website is                   #");
		logger.info("#        https://nukepowered.info/nputils.html           #");
		logger.info("##########################################################");
	}
	
	public static <T> void info(T obj) {
		logger.info(obj);
	}
	
	public static <T> void warn(T obj) {
		logger.warn(obj);
	}
	
	public static <T> void error(T obj) {
		logger.error(obj);
	}

	public static <T> void fatal(T obj) {
		logger.fatal(obj);
	}
	
	public static <T> void warn(String str, T e) {
		logger.warn(str, e);
	}
	
	public static <T> void error(String str, T e) {
		logger.error(str, e);
	}
	
	public static <T> void fatal(String str, T e) {
		logger.fatal(str, e);
	}
}
