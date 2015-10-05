package de.janosch.commons.math;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * 
 * @author Janos
 * @version 28.09.2011 | 23:14:46
 * 
 */
public class MathUtil {

	public static double round(final Number number, final int precision) {
		final BigDecimal dec = new BigDecimal(number.doubleValue());
		return dec.round(new MathContext(length(number.longValue()) + precision)).doubleValue();
	}

	private static int length(long value) {
		int i = 1;
		while ((value = value / 10) > 0) {
			i++;
		}
		return i;
	}

}
