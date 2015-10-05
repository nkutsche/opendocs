package net.sqf.view.utils.types;

import java.awt.Container;

import javax.swing.JFormattedTextField;

import net.sqf.view.utils.types.panels.StringPanel;

public class StringVerifier extends ValidCharVerifier {
	public StringVerifier() {
		this("");
	}
	public StringVerifier(String invalidStrings){
		this(invalidStrings, "");
	}
	public StringVerifier(String invalidStrings, String invalidStarts){
		super(invalidStrings, invalidStarts, false);
	}
	
	@Override
	public void setVerifier(JFormattedTextField field, Container owner) {
		StringPanel sp = new StringPanel(field, owner);
		field.addMouseListener(sp);
		field.addFocusListener(sp);
		super.setVerifier(field, owner);
	}
	@Override
	public _Verifier getNewInstance() {
		return new StringVerifier();
	}
}
