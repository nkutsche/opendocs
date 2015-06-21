package net.sqf.view.utils.images;



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
import javax.swing.ImageIcon;




public final class IconMap {
	
	private final BufferedImage image;


	public static File BASE_DIR = new File(".");
	
	public IconMap(String resource) throws IOException{
		this(new Object().getClass().getResourceAsStream(resource));
	}
	
	public IconMap(File imageMapFile) throws FileNotFoundException, IOException {
		this(new FileInputStream(imageMapFile.isAbsolute() ? imageMapFile : new File(BASE_DIR, imageMapFile.getPath())));
	}
	
	private IconMap(InputStream in) throws IOException{
		if(in != null){
			GifDecoder decoder = new GifDecoder();
			decoder.read(in);
			this.image = decoder.getImage();
			in.close();
		} else {
			this.image = null;
		}
	}
	
	public IconMap() throws IOException{
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
	public static final Point MESSAGE_GROUP_SUCCEDED = new Point(2,
			10);

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
		if(!file.isAbsolute()){
			file = new File(BASE_DIR, file.getPath());
		}
		image = ImageIO.read(file);
		
		if(image == null)
			throw new IOException("Format wird nicht unterstützt");
		return new ImageIcon(image);
		
	}

	public static ImageIcon getIcon(URI path) throws IOException {
		return getIcon(new File(path));
	}
	
	public ImageIcon getIcon(Point p){
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

}
