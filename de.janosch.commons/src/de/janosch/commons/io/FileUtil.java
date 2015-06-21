package de.janosch.commons.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * @author Janos
 * @version 15.09.2011 | 23:32:48
 * 
 */
public class FileUtil {

	/**
	 * Outputs the file content to standard output.
	 * 
	 * @param file
	 *            The file reference.
	 * @throws IOException
	 */
	public static void outputFile(final File file) throws IOException {
		if (file == null) {
			return;
		}
		outputFile(file, System.out);
	}

	/**
	 * Outputs the file content to the given output.
	 * 
	 * @param file
	 *            The file reference.
	 * @param os
	 *            The output which to output the file content to.
	 * @throws IOException
	 */
	public static void outputFile(final File file, final OutputStream os) throws IOException {
		if (file == null) {
			return;
		}
		final InputStream fis = new FileInputStream(file);
		outputStream(fis, os);
	}

	public static void outputStream(final InputStream inputStream, final OutputStream outputStream) throws IOException {
		final int bufLen = 1024;
		final byte[] buf = new byte[bufLen];
		int read;
		while ((read = inputStream.read(buf)) >= 0) {
			outputStream.write(buf, 0, read);
		}
		outputStream.flush();
		inputStream.close();
	}

	public static void outputStream(final InputStream inputStream, final StringBuilder sb) throws IOException {
		outputStream(inputStream, new StringBuilderWriter(sb));
	}

	public static void outputFile(final File file, final StringBuilder sb) throws IOException {
		outputFile(file, new StringBuilderWriter(sb));
	}

	static class StringBuilderWriter extends OutputStream {

		protected final StringBuilder stringBuilder;

		public StringBuilderWriter(final StringBuilder sb) {
			this.stringBuilder = sb;
		}

		@Override
		public void write(final int b) throws IOException {
			this.stringBuilder.append((char) b);
		}

	}

	/**
	 * Creates a file and writes the passed content into it.
	 * 
	 * If the file already exists, the file is overwritten only when 'overwrite' is
	 * <code>true</code>. Otherwise, an {@link IOException} is thrown.
	 */
	public static File createFile(final String content, final String path, final boolean overwrite) throws IOException {
		final File f = new File(path);
		if (f.exists()) {
			if (!overwrite) {
				throw new IOException("The file already exists: " + f.getAbsolutePath());
			}
			f.delete();
		}
		new File(f.getParent()).mkdirs();
		f.createNewFile();
		final BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write(content);
		bw.flush();
		bw.close();
		return f;
	}

	public static boolean delete(File dirOrFile) {
		boolean b = true;
		if (dirOrFile.isDirectory()) {
			for (File child : dirOrFile.listFiles()) {
				b &= delete(child);
			}
		}
		return dirOrFile.delete() && b;
	}

}
