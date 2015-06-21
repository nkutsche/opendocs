package net.sqf.view.utils.types;

import java.awt.Container;

import javax.swing.JFormattedTextField;

import net.sqf.view.utils.types.panels.CalendarPanel2;

public class CalendarVerifier implements _Verifier {


	@Override
	public void setVerifier(JFormattedTextField field, Container owner) {
		field.addMouseListener(new CalendarPanel2(field, 11, owner));
	}

	@Override
	public _Verifier getNewInstance() {
		// TODO Auto-generated method stub
		return new CalendarVerifier();
	}

	@Override
	public void setVerifier(JFormattedTextField field, Container owner, boolean entryHelp) {
		setVerifier(field, owner);
	}

}
