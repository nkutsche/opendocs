package de.janosch.commons.swing.listener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * 
 * @author Janos
 * @version 20.02.2011 | 21:21:51
 * 
 */
public abstract class DocumentListenerAdapter implements DocumentListener {

	@Override
	public void insertUpdate(DocumentEvent e) {
		documentChanged(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		documentChanged(e);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		documentChanged(e);
	}
	
	abstract public void documentChanged(DocumentEvent e);

}
