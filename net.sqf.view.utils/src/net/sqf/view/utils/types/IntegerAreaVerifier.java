package net.sqf.view.utils.types;


import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFormattedTextField;

import net.sqf.view.utils.types.panels.StringPanel;

public class IntegerAreaVerifier implements _Verifier, KeyListener {
	
	protected final char[] validChars = "0123456789".toCharArray();
	private final int start;
	private final int end;
	
	protected JFormattedTextField field = new JFormattedTextField();
	public IntegerAreaVerifier(int end) {
		this(0, end);
	}
	public IntegerAreaVerifier(int start, int end) {
		this.start = start;
		this.end = end;
		
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
		char insert = keyEvent.getKeyChar();
		
		boolean consume = true;
		String newValue = field.getText();
		newValue = newValue.substring(0, selStart) + keyEvent.getKeyChar() + newValue.substring(selEnd);
		
		for (char valChar : this.validChars) {
			if(valChar == insert){
				consume = false;
				break;
			}
		}
		if(consume){
			keyEvent.consume();
 		} else {
 			int newValInt = Integer.parseInt(newValue);
 			if(newValInt > end || newValInt < start){
 				keyEvent.consume();
 			}
 		}
		
		
		System.out.println(newValue);
//		System.out.println("pos: " + pos);
//		System.out.println("selStart: " + selStart);
//		System.out.println("selEnd: " + selEnd);
		
	}
	@Override
	public void keyPressed(KeyEvent arg0) {}

	@Override
	public _Verifier getNewInstance() {
//		if(this instanceof IntegerVerifier)
//			return new IntegerVerifier(String.copyValueOf(validChars), String.copyValueOf(startChars));
		return new IntegerAreaVerifier(start, end);
	}

	@Override
	public void setVerifier(JFormattedTextField field, Container owner, boolean entryHelp) {
		if(entryHelp)
			field.addMouseListener(new StringPanel(field, this, owner));
		setVerifier(field, owner);
	}

}
