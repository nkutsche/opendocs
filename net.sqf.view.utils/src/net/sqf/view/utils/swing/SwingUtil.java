package net.sqf.view.utils.swing;

import java.awt.Point;
import java.awt.Window;


public class SwingUtil {
	public static void centerFrame(final Window  frame, Window owner){
		if(owner != null){
			Point ownerLoc	= owner.getLocation();
			frame.setLocation(ownerLoc.x + (owner.getWidth() / 2) - (frame.getWidth() / 2),
					ownerLoc.y + (owner.getHeight() / 2) - (frame.getHeight() / 2));
		} else {
			de.janosch.commons.swing.util.SwingUtil.centerFrame(frame);
		}
	}
}
