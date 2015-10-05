package de.janosch.commons.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 * @author Janos
 * @version 19.03.2012 | 00:14:19
 * 
 */
public class EnableableDelagate implements Enableable, ChangeListener {

	private List<ChangeSource> changeSources = new ArrayList<ChangeSource>();
	private Enablement enablement;
	private final JComponent component;
	
	public EnableableDelagate(JComponent component) {
		this.component = component;
	}
	
	@Override
	public void setEnablement(Enablement enablement) {
		this.enablement = enablement;
	}

	@Override
	public void addChangeSource(ChangeSource source) {
		if (!changeSources.contains(source)) {
			changeSources.add(source);
			source.addChangeListener(this);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (enablement != null) {
			component.setEnabled(enablement.isEnabled());
		}
	}

}
