package net.sqf.view.utils.types;

import java.awt.Container;

import javax.swing.JFormattedTextField;

import net.sqf.view.utils.types.panels.CalendarTimePanel2;

public class CalendarTimeVerifier implements _Verifier {


	@Override
	public void setVerifier(JFormattedTextField field, Container owner) {
		field.addMouseListener(new CalendarTimePanel2(field, 11, owner, true));
	}

	@Override
	public _Verifier getNewInstance() {
		// TODO Auto-generated method stub
		return new CalendarTimeVerifier();
	}

	@Override
	public void setVerifier(JFormattedTextField field, Container owner, boolean entryHelp) {
		setVerifier(field, owner);
	}

}
