package de.janosch.commons.swing.components.text;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;

import de.janosch.commons.swing.listener.DocumentListenerAdapter;

/**
 * 
 * @author Janos
 * @version 20.02.2011 | 16:57:34
 * 
 */
public class JValidationTextField extends GhostValuedTextfield {

	private static final long serialVersionUID = 4453113748285783654L;
	private final JLabel iconPanel;
	private final TextInputValidator inputValidator;
	
	private Insets originalMargin = getMargin();
	private final ComponentAdapter iconPanelPositionListener;

	public JValidationTextField(final String text, final String ghostText, final TextInputValidator inputValidator) {
		super(ghostText, text);
		
		this.inputValidator = inputValidator;

		setLayout(null);
		
		iconPanel = new JLabel();
		iconPanel.setVisible(false);
		add(iconPanel);
		
		iconPanelPositionListener = new ComponentAdapter() {
			@Override
			public void componentMoved(final ComponentEvent e) {
				updateIconPosition();
			}
			@Override
			public void componentResized(final ComponentEvent e) {
				updateIconPosition();
			}
		};
		
		this.getDocument().addDocumentListener(new DocumentListenerAdapter() {
			@Override
			public void documentChanged(final DocumentEvent e) {
				validateInput();
			}
		});
		this.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent e) {
				validateInput();
			}
			@Override
			public void focusLost(final FocusEvent e) {
				validateInput();
			}
		});
		validateInput();
	}

	@Override
	public void setText(final String t) {
		super.setText(t);
		validateInput();
	}
	
	@Override
	public void setMargin(final Insets m) {
		super.setMargin(m);
		originalMargin = m;
	}

	public boolean hasValidInput() {
		if (inputValidator == null) {
			return true;
		}
		if (isGhostMode) {
			return inputValidator.validate(null).isValid;
		}
		return inputValidator.validate(getText()).isValid;
	}
	
	protected void validateInput() {
		if (inputValidator == null) {
			return;
		}
		
		final Validation result;

		if (isGhostMode) {
			result = inputValidator.validate(null);
		}
		else {
			result = inputValidator.validate(getText());
		}
		
		if (result == null) {
			return;
		}
		if (!result.isValid) {
			iconPanel.setVisible(true);
			iconPanel.setToolTipText(result.message);
			iconPanel.setIcon(result.getIcon());
			
			final Insets errorMargin = (Insets) originalMargin.clone();
			errorMargin.right = originalMargin.right + iconPanel.getBounds().width;
			super.setMargin(errorMargin);
			
			updateIconPosition();
			addComponentListener(iconPanelPositionListener);
			
		} else {
			removeComponentListener(iconPanelPositionListener);
			iconPanel.setVisible(false);
			super.setMargin(originalMargin);
		}
		invalidate();
	}
	
	protected void updateIconPosition() {
		
		final Icon icon = iconPanel.getIcon();
		if (icon == null) {
			return;
		}
		iconPanel.setBounds(new Rectangle(0, 0, icon.getIconWidth(), icon.getIconHeight()));
		
		final Rectangle bounds = getBounds();
		final int x = (bounds.width) - iconPanel.getWidth() - originalMargin.right;
		final int y = (bounds.height - iconPanel.getHeight() + 1) / 2;
		iconPanel.getBounds().x = x;
		iconPanel.getBounds().y = y;
		
		iconPanel.setBounds(new Rectangle(x, y, iconPanel.getBounds().width, iconPanel.getBounds().height));
		invalidate();
	}

	public static interface TextInputValidator {
		abstract public Validation validate(final String textInput);
	}

	public static class Validation {
		public final String message;
		public final boolean isValid;
		public final Icon errorIcon;

		public Validation(final boolean isValid, final String message, final Icon errorIcon) {
			this.isValid = isValid;
			this.message = message;
			this.errorIcon = errorIcon;
		}
		public Validation(final boolean isValid, final String message) {
			this(isValid, message, null);
		}
		
		public Validation(final boolean isValid) {
			this(isValid, null);
		}

		public Icon getIcon() {
			return errorIcon;
		}
	}

}
