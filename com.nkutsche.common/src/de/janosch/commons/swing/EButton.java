package de.janosch.commons.swing;

import javax.swing.JButton;

/**
 * 
 * @author Janos
 * @version 18.03.2012 | 23:57:07
 * 
 */
public class EButton extends JButton implements Enableable {

	private static final long serialVersionUID = 1547334330433294957L;
	
	private EnableableDelagate delegate = new EnableableDelagate(this);
	
	public EButton(String string) {
		super(string);
	}

	@Override
	public void setEnablement(Enablement enablement) {
		delegate.setEnablement(enablement);
	}

	@Override
	public void addChangeSource(ChangeSource source) {
		delegate.addChangeSource(source);
	}

}

