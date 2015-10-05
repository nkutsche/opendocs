package de.janosch.commons.swing.components;

import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JToggleButton;

/**
 * 
 * @author Janos
 * @version 05.03.2012 | 15:41:44
 * 
 */
public class ImageToggleButton extends JToggleButton {

	private static final long serialVersionUID = -8130474695778071104L;

	public ImageToggleButton(final Icon icon) {
		this(icon, icon);
	}

	private boolean isMouseOver = false;

	public ImageToggleButton(final Icon icon, final Icon iconSelected) {
		super(icon);

		this.setMargin(new Insets(0, 0, 0, 0));
		this.setIconTextGap(0);
		this.setText(null);
		this.setContentAreaFilled(false);
		this.setFocusable(false);

		this.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (model.isSelected()) {
					setContentAreaFilled(true);
				} else {
					/*
					 * Only if the mouse is not over the button, the content area filling has to be
					 * removed. This is the case, when the button gets de-selected other than by the
					 * mouse.
					 */
					if (!isMouseOver) {
						setContentAreaFilled(false);
					}
				}
				setIcon(model.isSelected() ? iconSelected : icon);
			}
		});
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(final MouseEvent e) {
				setContentAreaFilled(true);
				isMouseOver = true;
			}

			@Override
			public void mouseExited(final MouseEvent e) {
				setContentAreaFilled(getModel().isSelected());
				isMouseOver = false;
			}
		});
	}

}
