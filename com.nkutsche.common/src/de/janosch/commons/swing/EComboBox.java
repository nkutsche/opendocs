package de.janosch.commons.swing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 * @author Janos
 * @version 19.03.2012 | 00:00:31
 * 
 */
public class EComboBox extends JComboBox implements ChangeSource {

	private static final long serialVersionUID = -5676339771385846245L;

	private ChangeSourceDelegate delegate = new ChangeSourceDelegate();

	{
		addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					delegate.fireChange(new ChangeEvent(e.getSource()));
				}
			}
		});
	}

	@Override
	public void addChangeListener(ChangeListener changeListener) {
		delegate.addChangeListener(changeListener);
	}

}
