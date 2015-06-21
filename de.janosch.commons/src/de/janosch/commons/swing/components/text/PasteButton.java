package de.janosch.commons.swing.components.text;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
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
public class PasteButton extends ImageButton implements FlavorListener {

	private static final long serialVersionUID = 1109893645500043522L;
	private final Clipboard clipboard;

	public PasteButton(final JTextComponent textComponent, final Icon icon, final boolean replaceOnPaste) {
		super(icon);
		super.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				final Transferable transfer = clipboard.getContents(null);
				try {
					final String clipboardContent = (String) transfer.getTransferData(DataFlavor.stringFlavor);
					if (clipboardContent != null) {
						if (replaceOnPaste) {
							textComponent.setText(clipboardContent);
						}
						else {
							textComponent.setText(textComponent.getText() + clipboardContent);
						}
					}
				} catch (final Exception e) {
				}
			}
		});
		this.setToolTipText("Paste");

		this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		this.clipboard.addFlavorListener(this);
	}

	@Override
	public void flavorsChanged(final FlavorEvent e) {
		try {
			final Transferable transfer = clipboard.getContents(null);
			final DataFlavor[] transferDataFlavors = transfer.getTransferDataFlavors();
			for (final DataFlavor dataFlavor : transferDataFlavors) {
				if (dataFlavor.equals(DataFlavor.stringFlavor)) {
					this.setEnabled(true);
					return;
				}
			}
		} catch (final Exception e1) {
		}
		this.setEnabled(false);
	}

}
