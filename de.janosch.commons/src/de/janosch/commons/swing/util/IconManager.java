package de.janosch.commons.swing.util;

import java.net.URL;

import javax.swing.ImageIcon;

/**
 * 
 * @author Janos
 * @version 15.08.2010 | 21:42:28
 * 
 */
public class IconManager {

	private String iconsPath;

	/* THREAD-SAFE SINGLETON */
	private static class SINGLETON {
		static final IconManager SINGLETON = new IconManager();
	}

	public static IconManager getInstance() {
		return SINGLETON.SINGLETON;
	}

	private IconManager() {
	}

	/* ********************* */

	public void initialize(final String iconsPath) {
		this.iconsPath = iconsPath;
		if (!this.iconsPath.startsWith("/")) {
			this.iconsPath = "/" + this.iconsPath;
		}
		if (!this.iconsPath.endsWith("/")) {
			this.iconsPath = this.iconsPath + "/";
		}
	}

	public ImageIcon getIcon(final String iconFileName) {
		if (this.iconsPath == null) {
			throw new IllegalStateException("Icon manager is not initialized."); //$NON-NLS-1$
		}
		final String iconPath = this.iconsPath + iconFileName + ".gif";
		final URL iconUrl = this.getClass().getClass().getResource(iconPath);
		if (iconUrl == null) {
			return null;
		}
		return new ImageIcon(iconUrl);
	}

}
