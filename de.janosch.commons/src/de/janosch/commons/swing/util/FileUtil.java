package de.janosch.commons.swing.util;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

/**
 * 
 * @author Janos
 * @version 26.10.2011 | 23:51:20
 * 
 */
public class FileUtil {

	public static File promtForDirectory() {
		return promtForDirectory(null, null, null);
	}

	public static File promtForDirectory(final File currentDirectory, final Component parent, final String dialogTitle) {

		final JFileChooser fileChooser = new JFileChooser();
		if (dialogTitle != null) {
			fileChooser.setDialogTitle(dialogTitle);
		}
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);

		if (currentDirectory == null || !currentDirectory.exists()) {
			File dir = fileChooser.getFileSystemView().getHomeDirectory();
			dir = fileChooser.getFileSystemView().getFiles(dir, true)[0];
			fileChooser.setCurrentDirectory(dir);
		} else {
			fileChooser.setCurrentDirectory(currentDirectory);
		}

		final int result = fileChooser.showOpenDialog(parent);

		if (result == JFileChooser.CANCEL_OPTION) {
			return null;
		}

		final File selectedFile = fileChooser.getSelectedFile();
		return selectedFile;
	}

}
