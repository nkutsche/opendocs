package net.sqf.view.utils.types;


import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFormattedTextField;

import net.sqf.view.utils.types.panels.StringPanel;

public class ValidCharVerifier implements _Verifier, KeyListener {
	protected final char[] validChars;
	protected final char[] startChars;
	protected JFormattedTextField field = new JFormattedTextField();
	private final boolean charsAreValid;
	public ValidCharVerifier(String validStrings, String validStart) {
		this(validStrings, validStart, true);
	}
	public ValidCharVerifier(String validStrings, String validStart, boolean charsAreValid) {
		validChars = (validStrings + validStart).toCharArray();
		this.startChars = validStart.toCharArray();
		this.charsAreValid = charsAreValid;
	}
	
	
	
	@Override
	public void setVerifier(JFormattedTextField field, Container owner) {
		this.field = field;
		field.addKeyListener(this);
	}
	protected JFormattedTextField getField(){
		return this.field;
	}
	public void keyReleased(KeyEvent ke) {}
	public void keyTyped(KeyEvent keyEvent) {
		// TODO Auto-generated method stub
		int pos = field.getCaret().getMark();
		int selStart = field.getSelectionStart();
		int selEnd = field.getSelectionEnd();
		boolean consume = charsAreValid;
		for (int i = 0; i < validChars.length; i++) {
			if (validChars[i] == keyEvent.getKeyChar()) {
				consume = !charsAreValid;
				break;
			}
		}
		if (pos != 0 && selStart != 0) {
			for (int i = 0; i < startChars.length; i++) {
				if (startChars[i] == keyEvent.getKeyChar()) {
					consume = charsAreValid;
					break;
				}
			}
		}
		if (pos == 0 && selEnd ==0){
			char[] chars = field.getText().toCharArray();
			for (int i = 0; i < startChars.length; i++) {
				
				if (chars.length >0 && startChars[i] == chars[0]) {
					consume = charsAreValid;
					break;
				}
			}
		}
		if (consume)
			keyEvent.consume();
	}
	@Override
	public void keyPressed(KeyEvent arg0) {}

	@Override
	public _Verifier getNewInstance() {
//		if(this instanceof IntegerVerifier)
//			return new IntegerVerifier(String.copyValueOf(validChars), String.copyValueOf(startChars));
		return new ValidCharVerifier(String.copyValueOf(validChars),
				String.copyValueOf(startChars), charsAreValid);
	}

	@Override
	public void setVerifier(JFormattedTextField field, Container owner, boolean entryHelp) {
		if(entryHelp)
			field.addMouseListener(new StringPanel(field, this, owner));
		setVerifier(field, owner);
	}

}
