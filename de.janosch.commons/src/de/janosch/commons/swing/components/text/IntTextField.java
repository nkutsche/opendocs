package de.janosch.commons.swing.components.text;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * 
 * 
 * @author Janos
 * @version 18.02.2011 | 02:23:42
 * 
 */
public class IntTextField extends JTextField {

	private static final long serialVersionUID = -735434445112951794L;

	public IntTextField(final int defval, final int size) {
		super("" + defval, size);
	}

	@Override
	protected Document createDefaultModel() {
		return new IntTextDocument();
	}

//	@Override
//	public boolean isValid() {
//		return false;
////		if (getText() == null) {
////			return false;
////		}
////		try {
////			Integer.parseInt(getText());
////			return true;
////		} catch (final NumberFormatException e) {
////			return false;
////		}
//	}

	public int getValue() {
		try {
			return Integer.parseInt(getText());
		} catch (final NumberFormatException e) {
			return 0;
		}
	}

	class IntTextDocument extends PlainDocument {
		private static final long serialVersionUID = -117098722590077683L;

		@Override
		public void insertString(final int offs, final String str, final AttributeSet a) throws BadLocationException {
			if (str == null)
				return;
			final String oldString = getText(0, getLength());
			final String newString = oldString.substring(0, offs) + str + oldString.substring(offs);
			try {
				Integer.parseInt(newString + "0");
				super.insertString(offs, str, a);
			} catch (final NumberFormatException e) {
			}
		}
	}

}