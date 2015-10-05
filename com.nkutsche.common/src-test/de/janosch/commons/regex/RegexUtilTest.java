package de.janosch.commons.regex;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * @author Janos
 * @version 30.08.2011 | 01:11:05
 * 
 */
public class RegexUtilTest {

	@Test
	public void testStartsWith() {
		
		assertFalse(RegexUtil.startsWith(null, null));
		assertFalse(RegexUtil.startsWith("text", null));
		assertFalse(RegexUtil.startsWith(null, "."));
		
		assertTrue(RegexUtil.startsWith("any Text", "."));
		assertFalse(RegexUtil.startsWith("", "."));
		assertTrue(RegexUtil.startsWith("", ""));
		assertTrue(RegexUtil.startsWith("text", "text"));
		
		assertTrue(RegexUtil.startsWith("start with", "start"));
		assertFalse(RegexUtil.startsWith("start with", "tart"));
		
		assertTrue(RegexUtil.startsWith("123abc", "\\d*"));
		assertTrue(RegexUtil.startsWith("123abc", "\\d{3}"));
		assertFalse(RegexUtil.startsWith("123abc", "\\d{4}"));
	}

}
