package de.janosch.commons.swing.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * 
 * @author Janos
 * @version 15.08.2010 | 21:39:17
 * 
 */
public class SwingUtil {

	public static void addComponent(final Container cont, final GridBagLayout gbl, final Component c, final int x, final int y,
			final int width, final int height, final double weightx, final double weighty) {
		addComponent(cont, gbl, c, x, y, width, height, weightx, weighty, GridBagConstraints.NORTH, GridBagConstraints.BOTH);
	}

	public static void addComponent(final Container cont, final GridBagLayout gbl, final Component c, final int x, final int y,
			final int width, final int height, final double weightx, final double weighty, final int anchor, final int fill) {
		addComponent(cont, gbl, c, x, y, width, height, weightx, weighty, anchor, fill, new Insets(0, 0, 0, 0));
	}

	public static void addComponent(final Container container, final GridBagLayout gbl, final Component c, final int x, final int y,
			final int width, final int height, final double weightx, final double weighty, final int anchor, final int fill,
			final Insets insets) {
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = fill;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.anchor = anchor;
		gbc.insets = insets;
		gbl.setConstraints(c, gbc);
		container.add(c);
	}

	public static void setEnabledForChildren(final Container container, final boolean bEnabled) {
		for (int i = 0; i < container.getComponentCount(); i++) {
			final Component child = container.getComponent(i);
			child.setEnabled(bEnabled);
		}
	}

	public static void setEnabledRecursively(final Container container, final boolean bEnabled) {
		for (int i = 0; i < container.getComponentCount(); i++) {
			final Component child = container.getComponent(i);
			if (child instanceof Container) {
				setEnabledRecursively((Container) child, bEnabled);
			}
			child.setEnabled(bEnabled);
		}
		container.setEnabled(bEnabled);
	}

	public static void addKeyAdapterRecursively(final Container container, final KeyAdapter keyAdapter) {
		container.addKeyListener(keyAdapter);
		for (int i = 0; i < container.getComponentCount(); i++) {
			final Component child = container.getComponent(i);
			if (child instanceof Container) {
				addKeyAdapterRecursively((Container) child, keyAdapter);
			}
			child.addKeyListener(keyAdapter);
		}
	}

	public static final int getComponentIndex(final Component component) {
		if (component != null && component.getParent() != null) {
			final Container c = component.getParent();
			for (int i = 0; i < c.getComponentCount(); i++) {
				if (c.getComponent(i) == component) {
					return i;
				}
			}
		}
		return -1;
	}

	public static void centerFrame(final Window frame) {
		final Rectangle bounds = frame.getBounds();
		final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		bounds.x = (screen.width / 2) - (bounds.width / 2);
		bounds.y = (screen.height / 2) - (bounds.height / 2);
		frame.setBounds(bounds);
	}
	

	public static void setSystemLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public static void addEscapeExitListeners(final JFrame window) {
		addKeyAdapterRecursively(window, new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					System.exit(0);
				}
			}
		});
	}
	public static void addEscapeExitListeners(final JDialog window) {
		addKeyAdapterRecursively(window, new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					System.exit(0);
				}
			}
		});
	}

}
