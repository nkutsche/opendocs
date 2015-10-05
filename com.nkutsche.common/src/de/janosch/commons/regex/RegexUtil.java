package de.janosch.commons.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Janos
 * @version 30.08.2011 | 01:05:38
 * 
 */
public class RegexUtil {
	
	public static final boolean startsWith(final String text, final String regex) {
		if (text == null || regex == null) {
			return false;
		}
		final Pattern p = Pattern.compile(regex);
		final Matcher m = p.matcher(text);
		return m.find() && m.start() == 0;
	}
	
	public static void main(String[] args) {
		boolean startsWith = startsWith("UFC 123 - blablabla", "UFC \\d\\d\\d");
		System.err.println(startsWith);
	}

}
