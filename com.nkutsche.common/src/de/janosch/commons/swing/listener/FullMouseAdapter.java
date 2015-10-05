package de.janosch.commons.swing.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 
 * @author Janos
 * @version 21.02.2011 | 02:40:09
 * 
 */
public abstract class FullMouseAdapter extends MouseAdapter {

	@Override
	public void mouseMoved(final MouseEvent e) {
		onMouseEvent(e);
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		onMouseEvent(e);
	}
	@Override
	public void mouseEntered(final MouseEvent e) {
		onMouseEvent(e);
	}
	@Override
	public void mouseExited(final MouseEvent e) {
		onMouseEvent(e);
	}
	@Override
	public void mouseClicked(final MouseEvent e) {
//		onMouseEvent(e);
	}
	
	public abstract void onMouseEvent(final MouseEvent e);
	
}
