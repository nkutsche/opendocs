package net.sqf.openDocs.workingSet;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

public abstract class RenameFields extends JTextField implements KeyListener, FocusListener {

	private static final long serialVersionUID = 1L;
	private int maxLength = -1;
	public RenameFields() {
		super(10);
		this.addKeyListener(this);
		this.addFocusListener(this);
	}
	
	public RenameFields(int maxLength) {
		this();
		this.maxLength = maxLength;
	}
	
	@Override
	public void focusGained(FocusEvent arg0) {
		this.setSelectionStart(0);
		this.setSelectionEnd(this.getText().length());
	}
	@Override
	public void focusLost(FocusEvent arg0) {}
	
	
	@Override
	public void keyPressed(KeyEvent e) {
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	@Override
	public void keyTyped(KeyEvent e) {
		if(e.getKeyChar() == '\n'){
			enter();
		} else if(e.getKeyChar() == KeyEvent.VK_ESCAPE){
			escape();
		} else if(this.getText().length() + 1 > this.maxLength && this.maxLength > 0){
			e.consume();
		}
	}
	
	public abstract void enter();
	public abstract void escape();
}
