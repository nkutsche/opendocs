package net.sqf.view.utils.types;


import java.awt.Container;

import javax.swing.JFormattedTextField;

import net.sqf.view.utils.types.panels.StringPanel;

public class IntegerVerifier extends ValidCharVerifier {
	private final static String digits = "0123456789";
	public IntegerVerifier() {
		this("");
	}
	public IntegerVerifier(String vddValidStrings){
		this(digits + vddValidStrings, "");
	}
	public IntegerVerifier(String vddValidStrings, String startChars){
		super(digits + vddValidStrings, startChars);
	}
	
	@Override
	public void setVerifier(JFormattedTextField field, Container owner) {
		setVerifier(field, owner, true);
	}
	
	@Override
	public void setVerifier(JFormattedTextField field, Container owner, boolean entryHelp) {
		if(entryHelp){
			field.addMouseListener(new StringPanel(field, this, owner));
		}
		super.setVerifier(field, owner);
	}
	
	@Override
	public _Verifier getNewInstance() {
		return new IntegerVerifier(String.copyValueOf(validChars), String.copyValueOf(startChars));
	}
}
