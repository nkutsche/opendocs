package de.janosch.commons.swing.components.text;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;

import de.janosch.commons.swing.components.ImageButton;

/**
 * 
 * @author Janos
 * @version 20.02.2011 | 21:09:16
 * 
 */
public class CopyButton extends ImageButton implements ClipboardOwner {

	private static final long serialVersionUID = 1109893645500043522L;

	public CopyButton(final JTextComponent textComponent, final Icon icon) {
		this(textComponent, icon, "Copy");
	}
	public CopyButton(final JTextComponent textComponent, final Icon icon, final String tooltip) {
		super(icon);
		super.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				String text = textComponent.getText();
				if (text != null && !"".equals(text)) {
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), CopyButton.this);
				}
			}
		});
		this.setToolTipText(tooltip);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}

}
