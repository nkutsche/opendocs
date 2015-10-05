package de.janosch.commons.swing.components.text;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;

import de.janosch.commons.swing.components.ImageButton;

/**
 * 
 * @author Janos
 * @version 22.02.2011 | 19:23:11
 * 
 */
public class ClearButton extends ImageButton {

	private static final long serialVersionUID = 6947088154616099777L;

	public ClearButton(final JTextComponent textComponent, final Icon icon) {
		super(icon);

		super.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				textComponent.setText(null);
			}
		});
		this.setToolTipText("Clear");
	}

}
