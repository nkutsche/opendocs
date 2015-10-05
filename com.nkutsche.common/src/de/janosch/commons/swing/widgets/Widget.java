package de.janosch.commons.swing.widgets;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.janosch.commons.swing.util.SwingUtil;

/**
 * 
 * @author Janos
 * @version 22.02.2011 | 17:50:45
 * 
 */
public abstract class Widget extends JPanel {

	private static final long serialVersionUID = -4914189787141822103L;

	public void showStandalone() {

		final JFrame frame = new JFrame(this.getClass().getSimpleName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setOpaque(true);
		frame.setContentPane(this);
		frame.pack();

		SwingUtil.centerFrame(frame);

		frame.setVisible(true);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		SwingUtil.setEnabledForChildren(this, enabled);
	}

}
