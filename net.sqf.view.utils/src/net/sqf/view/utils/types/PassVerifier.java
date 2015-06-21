package net.sqf.view.utils.types;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.ParseException;
import java.util.regex.Pattern;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.janosch.commons.swing.util.SwingUtil;

class PassVerifier extends InputVerifier {
	public boolean verify(JComponent input) {
		JTextField tf = (JTextField) input;
		return Pattern.matches("\\d*", tf.getText());
	}

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException, ParseException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		JFrame frame = new JFrame("number");
		JFormattedTextField stringField =  new JFormattedTextField();
		JFormattedTextField intField = new JFormattedTextField();
		JFormattedTextField dateField =  new JFormattedTextField();
		VerifierFactory.addVerifier("abc", stringField, frame);
		VerifierFactory.addVerifier("int", intField, frame);
		VerifierFactory.addVerifier("date", dateField, frame);
		
		
		
		GridBagLayout gbl = new GridBagLayout();
		frame.setLayout(gbl);
		SwingUtil.addComponent(frame, gbl, stringField, 0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
		SwingUtil.addComponent(frame, gbl, intField, 0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
		SwingUtil.addComponent(frame, gbl, dateField, 0, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
		intField.getParent().setBackground(Color.WHITE);
		intField.setInputVerifier(new PassVerifier());
		frame.setSize(300, 400);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
