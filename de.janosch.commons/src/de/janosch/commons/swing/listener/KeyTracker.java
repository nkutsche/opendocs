package de.janosch.commons.swing.listener;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Janos
 * @version 23.02.2011 | 03:38:38
 * 
 */
public class KeyTracker implements AWTEventListener {

	public static final KeyTracker INSTANCE = init();

	private static KeyTracker init() {
		final KeyTracker keyTracker = new KeyTracker();
		Toolkit.getDefaultToolkit().addAWTEventListener(keyTracker, AWTEvent.KEY_EVENT_MASK);
		return keyTracker;
	}
	
	private KeyTracker() {}

	protected final Set<Integer> keyState = new HashSet<Integer>();

	@Override
	public void eventDispatched(final AWTEvent event) {
		if (event.getID() == KeyEvent.KEY_PRESSED) {
			keyPressed((KeyEvent) event);
		} else if (event.getID() == KeyEvent.KEY_RELEASED) {
			keyReleased((KeyEvent) event);
		}
	}

	public void keyPressed(final KeyEvent e) {
		this.keyState.add(e.getKeyCode());
	}

	public void keyReleased(final KeyEvent e) {
		this.keyState.remove(e.getKeyCode());
	}

	public boolean isDown(final int keyCode) {
		return this.keyState.contains(keyCode);
	}

}
