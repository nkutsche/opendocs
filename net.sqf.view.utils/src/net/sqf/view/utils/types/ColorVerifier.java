package net.sqf.view.utils.types;


import java.awt.Color;
import java.awt.Container;
import java.awt.event.KeyEvent;

import javax.swing.JFormattedTextField;

import net.sqf.view.utils.types.panels.ColorPanel;

public class ColorVerifier extends ValidCharVerifier {

	public ColorVerifier() {
		super("0123456789abcdefABCDEF", "");
	}
	@Override
	public void setVerifier(JFormattedTextField field, Container owner) {
		field.addMouseListener(new ColorPanel(field, owner));
		super.setVerifier(field, owner);
	}



	public void keyReleased(KeyEvent keyEvent) {
		super.keyTyped(keyEvent);
		JFormattedTextField field = this.getField();
		String code = field.getText();
		Color c = field.getBackground();
		if (code.replaceAll("[\\dabcdefABCDEF]", "").equals("")
				&& code.length() == 6) {
			c = Color.decode("#" + code);
		}
		field.setBackground(c);
		if(c.getRed()+ c.getGreen() * 1.5  < 334)
			field.setForeground(Color.WHITE);
		else
			field.setForeground(Color.BLACK);
		field.repaint();
	}
	@Override
	public _Verifier getNewInstance() {
		return new ColorVerifier();
	}
}
