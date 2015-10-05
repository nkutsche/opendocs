package net.sqf.view.utils.images;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

public final class IconMap {

	private final BufferedImage image;

	public static IconMap ICONS;
	public static File BASE_DIR = new File(".");

	public IconMap(String resource) throws IOException {
		this(new Object().getClass().getResourceAsStream(resource));
	}

	public IconMap(File imageMapFile) throws FileNotFoundException, IOException {
		this(new FileInputStream(imageMapFile.isAbsolute() ? imageMapFile
				: new File(BASE_DIR, imageMapFile.getPath())));
	}

	private IconMap(InputStream in) throws IOException {
		if (in != null) {
			GifDecoder decoder = new GifDecoder();
			decoder.read(in);
			this.image = decoder.getImage();
			in.close();
		} else {
			this.image = null;
		}
	}

	public IconMap() throws IOException {
		this(resMapPath);
	}

	private static String resMapPath = "/icons/diagona_my.gif";

	private static final int intervallX = 23;
	private static final int intervallY = 26;

	private static final int bigIconWidth = 17;
	private static final int bigIconHeight = 18;
	private static final int iconOffsetX = 2;
	private static final int iconOffsetY = 3;
	private static final int smallIconWidth = 16;
	private static final int smallIconHeight = 18;
	private static final int smallIconOffsetX = 3;
	private static final int smallIconOffsetY = 6;

	// ICONS
	public static final Point NEUTRAL = new Point(12, 2);
	public static final Point DIAGNOSTIC = new Point(14, 2);

	public static final Point MESSAGE_GROUP_FAILED = new Point(0, 10);
	public static final Point MESSAGE_GROUP_SUCCEDED = new Point(2, 10);

	public static final Point MESSAGE_WARNING = new Point(6, 15);
	public static final Point MESSAGE_ERROR = new Point(0, 15);
	public static final Point MESSAGE_FATAL_ERROR = new Point(0, 20);
	public static final Point MESSAGE_INFO = new Point(16, 15);
	public static final Point QUICKFIX_ADD = new Point(4, 10);
	public static final Point QUICKFIX_DELETE = new Point(0, 10);
	public static final Point QUICKFIX_REPLACE = new Point(2, 12);
	public static final Point QUICKFIX_STRING_REPLACE = new Point(14, 1);

	public static final Point USER_ENTRY = new Point(16, 1);

	public static final Point NOICON = new Point(14, 21);
	public static final Point QUICKFIX_HAS_PARAM = new Point(18, 18);
	public static final Point USER_ENTRY_QUEST = new Point(11, 14);
	public static final Point QUICKFIX_HAS_LINK = new Point(8, 16);

	public static ImageIcon getIcon(File file) throws IOException {
		BufferedImage image = null;
		if (!file.isAbsolute()) {
			file = new File(BASE_DIR, file.getPath());
		}
		image = ImageIO.read(file);

		if (image == null)
			throw new IOException("Format wird nicht unterstützt");
		return new ImageIcon(image);

	}

	public static ImageIcon getIcon(URI path) throws IOException {
		return getIcon(new File(path));
	}

	public ImageIcon getIcon(Point p) {
		return getIcon(p.x, p.y);
	}

	public ImageIcon getIcon(int x, int y) {
		return getIcon(x, y, x % 2 == 0);
	}

	public ImageIcon getIcon(int x, int y, boolean small) {

		int xOffset = small ? smallIconOffsetX : iconOffsetX;
		int yOffset = small ? smallIconOffsetY : iconOffsetY;
		int w = small ? smallIconWidth : bigIconWidth;
		int h = small ? smallIconHeight : bigIconHeight;
		Image img = ImageUtils.subimage(image, x * intervallX + xOffset, y
				* intervallY + yOffset, w, h);

		return new ImageIcon(img);
	}

	public static Icon getSystemIconByExt(String ext) throws IOException {
		File temp = File.createTempFile("openDocs", "." + ext);
		temp.createNewFile();
		Icon ico = FileSystemView.getFileSystemView().getSystemIcon(temp);
		temp.delete();
		return ico;
	}

	public static Icon filter(Icon icon) {
		return filter(icon, 0.5);
	}

	public static Icon filter(Icon icon, double fac) {
		return filter(icon, fac, fac, fac, fac);
	}

	public static Icon filter(Icon icon, Double[] facs) {
		return filter(icon, facs[0], facs[1], facs[2], facs[3]);
	}

	public static Icon filter(Icon icon, double rFac, double gFac, double bFac,
			double aFac) {

		BufferedImage bi = new BufferedImage(icon.getIconWidth(),
				icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		// paint the Icon to the BufferedImage.
		icon.paintIcon(null, g, 0, 0);
		g.dispose();

		for (int i = 0; i < bi.getHeight(); i++) {
			for (int j = 0; j < bi.getWidth(); j++) {
				int rgb = bi.getRGB(j, i);
				Color c = new Color(rgb, true);
				Color nc = new Color(filter(c.getRed(), rFac), filter(
						c.getGreen(), gFac), filter(c.getBlue(), bFac), filter(
						c.getAlpha(), aFac));
				bi.setRGB(j, i, nc.getRGB());
			}
		}
		return new ImageIcon(bi);
	}

	private static int filter(int colorValue, double factor) {
		double newDouble = colorValue * factor;

		if (newDouble >= 255.0) {
			return 255;
		}

		return (int) Math.round(newDouble);
	}

	private static Icon resize(Icon icon, int width, int height) {
		int oWidth = icon.getIconWidth();
		int oHeight = icon.getIconHeight();
		int nWidth = oWidth > width ? oWidth : width;
		int nHeight = oHeight > height ? oHeight : height; 
		
		int x = (nWidth - width) / 2;
		int y = (nHeight - height) / 2;
		
		BufferedImage bi = new BufferedImage(nWidth,
				nHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		// paint the Icon to the BufferedImage.
		icon.paintIcon(null, g, x, y);
		g.dispose();
		return new ImageIcon(bi);
	}
	
	public static Icon[] adjustIcons(Icon[] icons){
		int mWidth = 0;
		int mHeight = 0;
		for (Icon icon : icons) {
			mWidth = mWidth > icon.getIconWidth() ? mWidth : icon.getIconWidth();
			mHeight = mHeight > icon.getIconHeight() ? mHeight : icon.getIconHeight();
		}
		Icon[] newIcons = new Icon[icons.length];
		for (int i = 0; i < icons.length; i++) {
			newIcons[i] = resize(icons[i], mWidth, mHeight);
		}
		return newIcons;
	}

}
