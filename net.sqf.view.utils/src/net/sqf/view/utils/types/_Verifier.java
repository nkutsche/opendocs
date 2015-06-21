package net.sqf.view.utils.types;

import java.awt.Container;

import javax.swing.JFormattedTextField;

public interface _Verifier {
	public void setVerifier(JFormattedTextField field, Container owner);
	public _Verifier getNewInstance();
	void setVerifier(JFormattedTextField field, Container owner, boolean entryHelp);
}
