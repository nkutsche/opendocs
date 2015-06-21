package de.janosch.commons.swing.components.text;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

/**
 * 
 * @author Janos
 * @version 15.08.2010 | 15:01:25
 * 
 */
public class GhostValuedTextfield extends JTextField {

	private static final long serialVersionUID = -7468355518691233201L;

	private static final Color DEFAULT_GHOST_TEXT_COLOR = new Color(128, 128, 128);

	private String ghostText;

	private Color ghostTextColor;
	private Color tmpForegroundColor;

	protected boolean isGhostMode = false;

	public GhostValuedTextfield(final String ghostText) {
		this(ghostText, "");

		enterGhostMode();
	}
	
	public GhostValuedTextfield(final String ghostText, final String text) {
		super(text);
		this.ghostText = ghostText;
		this.ghostTextColor = DEFAULT_GHOST_TEXT_COLOR;
		
		this.addFocusListener(new TheFocusListener());
		
		exitGhostMode();
	}
	
	public GhostValuedTextfield(final String ghostText, final Color ghostTextColor) {
		this(ghostText);
		this.ghostTextColor = ghostTextColor;
	}

	public void setGhostText(final String ghostText) {
		this.ghostText = ghostText;
	}

	public String getGhostText() {
		return this.ghostText;
	}

	public void setGhostTextColor(final Color color) {
		this.ghostTextColor = color;
	}

	public Color getGhostTextColor() {
		return this.ghostTextColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.text.JTextComponent#setText(java.lang.String)
	 */
	@Override
	public void setText(final String t) {
		if (this.isGhostMode) {
			exitGhostMode();
		}
		super.setText(t);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.text.JTextComponent#getText()
	 */
	@Override
	public String getText() {
		if (this.isGhostMode) {
			return null;
		}
		return super.getText();
	}
	
	protected void enterGhostMode() {
		this.tmpForegroundColor = super.getForeground();
		super.setForeground(this.ghostTextColor);
		this.setText(this.ghostText);
		this.isGhostMode = true;
	}

	protected void exitGhostMode() {
		super.setForeground(this.tmpForegroundColor);
		this.isGhostMode = false;
	}

	class TheFocusListener implements FocusListener {

		@Override
		public void focusLost(final FocusEvent e) {
			final String text = GhostValuedTextfield.this.getText();
			if (text == null || "".equals(text)) { //$NON-NLS-1$
				enterGhostMode();
			} else {
				exitGhostMode();
			}
		}

		@Override
		public void focusGained(final FocusEvent e) {
			if (GhostValuedTextfield.this.isGhostMode) {
				GhostValuedTextfield.this.setText(""); //$NON-NLS-1$
			}
			exitGhostMode();
		}
	}

}
