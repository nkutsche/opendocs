package de.janosch.commons.swing.components;

import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * 
 * @author Janos
 * @version 15.08.2010 | 22:04:45
 * 
 */
public class ImageButton extends JButton {

	private static final long serialVersionUID = -2651677895339491248L;

	private final MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mouseEntered(final MouseEvent e) {
			if (iconMouseOver != null) {
				ImageButton.this.setIcon(iconMouseOver);
			}
			else {
				setContentAreaFilled(true);
			}
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			setContentAreaFilled(false);
			if (iconMouseOver != null) {
				ImageButton.this.setIcon(iconNormal);
			}
		}
	};

	private final Icon iconNormal;
	private Icon iconMouseOver;
	
	public ImageButton(final Icon iconNormal, final Icon iconMouseOver) {
		this(iconNormal);
		this.iconMouseOver = iconMouseOver;
	}

	public ImageButton(final Icon icon) {
		super(icon);
		
		this.iconNormal = icon;

		this.setMargin(new Insets(0, 0, 0, 0));
		this.setIconTextGap(0);
		this.setText(null);
		this.setContentAreaFilled(false);
		this.setFocusable(false);

		this.addMouseListener(this.mouseListener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.AbstractButton#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(final boolean b) {
		super.setEnabled(b);
		if (b) {
			this.removeMouseListener(this.mouseListener);
			this.addMouseListener(this.mouseListener);
		} else {
			this.removeMouseListener(this.mouseListener);
			this.setContentAreaFilled(false);
		}
	}

	@Override
	public void setText(final String text) {
		// Prevent changes!
	}

}
