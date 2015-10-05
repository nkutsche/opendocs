package de.janosch.commons.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 * @author Janos
 * @version 19.03.2012 | 00:07:44
 * 
 */
public class ChangeSourceDelegate {
	
	private List<ChangeListener> listeners = new ArrayList<ChangeListener>();

	public void fireChange(ChangeEvent e) {
		for (ChangeListener listener : listeners) {
			listener.stateChanged(e);
		}
	}

	public void addChangeListener(ChangeListener changeListener) {
		if (!listeners.contains(changeListener)) {
			listeners.add(changeListener);
		}
	}
	
	public void removeChangeListener(ChangeListener changeListener) {
		while (listeners.contains(changeListener)) {
			listeners.remove(changeListener);
		}
	}

}
