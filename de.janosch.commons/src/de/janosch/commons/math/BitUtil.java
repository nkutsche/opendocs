package de.janosch.commons.math;

/**
 * 
 * @author Janos
 * @version 23.09.2011 | 23:19:49
 * 
 */
public class BitUtil {
	
	public static int getWordSize() {
		final String bitness = System.getProperty("sun.arch.data.model");
		return Integer.parseInt(bitness);
	}

}
