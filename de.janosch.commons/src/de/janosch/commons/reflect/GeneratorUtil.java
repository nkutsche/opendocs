package de.janosch.commons.reflect;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Map;

/**
 * 
 * @author Janos
 * @version 21.09.2011 | 21:48:27
 * 
 */
public class GeneratorUtil {

	public static String toTitleCase(final String text) {
		if (text == null) {
			return null;
		}
		if ("".equals(text)) {
			return "";
		}
		return text.substring(0, 1).toUpperCase() + text.substring(1);
	}

	/**
	 * Determine whether the supplied string represents a well-formed fully-qualified Java
	 * classname. This utility method enforces no conventions (e.g., packages are all lowercase) nor
	 * checks whether the class is available on the classpath.
	 * 
	 * @param classname
	 * @return true if the string is a fully-qualified class name
	 */
	public static boolean isFullyQualifiedClassname(final String classname) {
		if (classname == null) {
			return false;
		}
		final String[] parts = classname.split("[\\.]");
		if (parts.length == 0) {
			return false;
		}
		for (final String part : parts) {
			final CharacterIterator iter = new StringCharacterIterator(part);
			// Check first character (there should at least be one character for each part) ...
			char c = iter.first();
			if (c == CharacterIterator.DONE) {
				return false;
			}
			if (!Character.isJavaIdentifierStart(c) && !Character.isIdentifierIgnorable(c)) {
				return false;
			}
			c = iter.next();
			// Check the remaining characters, if there are any ...
			while (c != CharacterIterator.DONE) {
				if (!Character.isJavaIdentifierPart(c) && !Character.isIdentifierIgnorable(c)) {
					return false;
				}
				c = iter.next();
			}
		}
		return true;
	}

	public static String convertToJavaClassname(final String classname, final Map<Character, String> replacement) {
		if (classname == null) {
			return null;
		}

		final StringBuilder sb = new StringBuilder();

		final CharacterIterator iter = new StringCharacterIterator(classname);
		char c = iter.first();
		if (c == CharacterIterator.DONE) {
			return null;
		}
		if (!Character.isJavaIdentifierStart(c) && !Character.isIdentifierIgnorable(c)) {
			String repl = replacement.get(c);
			if (repl == null) {
				repl = "_";
				System.err.println("no replacement for illegal character found: " + c);
			}
			sb.append(repl);
		} else {
			sb.append(c);
		}
		c = iter.next();
		while (c != CharacterIterator.DONE) {
			if (!Character.isJavaIdentifierPart(c) && !Character.isIdentifierIgnorable(c)) {
				String repl = replacement.get(c);
				if (repl == null) {
					repl = "_";
					System.err.println("no replacement for illegal character found: " + c);
				}
				sb.append(repl);
			} else {
				sb.append(c);
			}
			c = iter.next();
		}
		return sb.toString();
	}

}
