package de.janosch.commons.reflect;

import java.io.IOException;
import java.io.InputStream;

/**
 * Provides acces to an {@link InputStream} of a class file.
 * 
 * The {@link InputStream} of the {@link Class} instance is determined at construction time, using
 * the local {@link ClassLoader} or the provided {@link ClassLoader}.
 * 
 * @author Janos
 * @version 27.09.2011 | 21:36:36
 * 
 */
public class ClassInputStream extends InputStream {

	protected final InputStream inputStream;

	public ClassInputStream(final Class<?> clazz) {
		this.inputStream = getInputStream(clazz, this.getClass().getClassLoader());
	}

	public ClassInputStream(final Class<?> clazz, final ClassLoader classLoader) {
		this.inputStream = getInputStream(clazz, classLoader);
	}

	public ClassInputStream(final String className) throws ClassNotFoundException {
		this.inputStream = getInputStream(className, this.getClass().getClassLoader());
		if (this.inputStream == null) {
			throw new ClassNotFoundException("InputStream not found for class: " + className);
		}
	}

	private static InputStream getInputStream(final Class<?> clazz, final ClassLoader classLoader) {
		return classLoader.getResourceAsStream(ClassLoaderUtil.getPath(clazz));
	}

	private static InputStream getInputStream(final String className, final ClassLoader classLoader) {
		return classLoader.getResourceAsStream(ClassLoaderUtil.convertToPath(className));
	}

	@Override
	public int read() throws IOException {
		return this.inputStream.read();
	}

}
