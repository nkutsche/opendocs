package de.janosch.commons.reflect;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Test;

import static de.janosch.commons.reflect.ClassLoaderUtil.*;

/**
 * 
 * @author Janos
 * @version 03.10.2011 | 23:15:11
 * 
 */
public class ClassLoaderUtilTest {

	@Test
	public void testConvertToPath() throws Exception {
		assertNull(convertToPath(null));
		assertEquals("", convertToPath(""));

		assertEquals("String.class", convertToPath("String"));
		assertEquals("a/b/c/D.class", convertToPath("a.b.c.D"));
		assertEquals("java/lang/String.class", convertToPath(String.class.getName()));
	}

	@Test
	public void testResolve() throws Exception {
		assertNull(resolve(null));
		assertNull(resolve(""));

		assertNotNull(resolve(Object.class.getName()));
		assertSame(Object.class, resolve("java.lang.Object"));
		assertSame(Object.class, resolve(Object.class.getName()));

		assertNotNull(resolve(convertToPath(Object.class.getName())));
		assertNotNull(resolve(convertToPath("java.lang.Object")));
		assertNotNull(resolve("java/lang/Object.class"));
	}

	@Test
	public void testGetPath() throws Exception {
		assertNull(getPath(null));

		assertEquals("java/lang/Object.class", getPath(Object.class));
	}

	@Test
	public void testToFileUrl() throws Exception {

		assertNull(toFileUrl(null));

		URL url;

		url = new URL("file:/test");
		assertEquals(new URL("file:/test"), toFileUrl(url));

		url = new URL("jar:file:/jarfile!/content");
		assertEquals(new URL("file:/jarfile"), toFileUrl(url));

		final String s = "jar:file:/G:/Program%20Files/Java/jdk1.6.0_24/jre/lib/rt.jar!/a";
		url = new URL(s);
		assertEquals(new URL("file:/G:/Program%20Files/Java/jdk1.6.0_24/jre/lib/rt.jar"), toFileUrl(url));

	}

}
