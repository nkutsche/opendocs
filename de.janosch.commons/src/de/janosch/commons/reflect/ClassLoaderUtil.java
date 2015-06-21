package de.janosch.commons.reflect;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 * @author Janos
 * @version 03.10.2011 | 23:12:03
 * 
 */
public class ClassLoaderUtil {

	public static URL getUrl(final Class<?> aClass, final ClassLoader cl) {
		return cl.getResource(getPath(aClass));
	}
	
	public static URL toFileUrl(final URL aUrl) {
		if (aUrl == null) {
			return null;
		}
		if ("file".equals(aUrl.getProtocol())) {
			return aUrl;
		}
		if ("jar".equals(aUrl.getProtocol())) {
			try {
				return toFileUrl(new URL(aUrl.getPath().substring(0, aUrl.getPath().lastIndexOf('!'))));
			} catch (MalformedURLException e) {
				return null;
			}
		}
		
		return null;
		
	}
	
	public static void main(String[] args) throws IOException {
		
//		String s = "jar:file:/G:/Program%20Files/Java/jdk1.6.0_24/jre/lib/rt.jar!/java/lang/Error.class";
		String s = "jar:file:/G:/Program%20Files/Java/jdk1.6.0_24/jre/lib/rt.jar!/a";
		URL url = new URL(s);
		
		System.err.println(toFileUrl(url));
		
	}

	/**
	 * Returns the path of a class instance.
	 * 
	 * The path is a string in the form "package/package2/.../Classname.class". The path can be used
	 * to retrieve the {@link URL} of a class.
	 */
	public static String getPath(final Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		return convertToPath(clazz.getName());
	}

	/**
	 * Converts a classname in the form "package.package2.<...>.Classname" to the path form
	 * "package/package2/.../Classname.class".
	 */
	public static String convertToPath(final String className) {
		if (className == null) {
			return null;
		}
		if ("".equals(className)) {
			return "";
		}
		return className.replace('.', '/') + ".class";
	}

	/**
	 * Resolves a {@link Class} instance for a given class name or class path.
	 * 
	 * If no class is found with the specified name, a {@link ClassNotFoundException} is thrown.
	 */
	public static Class<?> resolve(final String nameOrPath) throws ClassNotFoundException {
		if (nameOrPath == null) {
			return null;
		}
		if ("".equals(nameOrPath)) {
			return null;
		}
		if (nameOrPath.contains("/")) {
			if (nameOrPath.endsWith(".class")) {
				return Class.forName(nameOrPath.substring(0, nameOrPath.length() - 6).replace('/', '.'));
			}
			return Class.forName(nameOrPath.replace('/', '.'));
		}
		return Class.forName(nameOrPath);
	}

}
