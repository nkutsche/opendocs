package net.sqf.view.utils.process;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

import net.sqf.utils.process.exceptions.CancelException;
import net.sqf.utils.process.exceptions.ErrorViewer;

import de.janosch.commons.swing.util.SwingUtil;

public class ProgressViewer extends JFrame {

	private static final long serialVersionUID = 3337486025841559125L;
	private final JProgressBar bar;
	private final GridBagLayout gbl = new GridBagLayout();
	private JLabel procLab = new JLabel();
	private final JFrame parent;
	
	public ProgressViewer(String title, ErrorViewer errorListener) throws CancelException {
		this(title, null, errorListener);
	}

	public ProgressViewer(String title, JFrame parent, ErrorViewer errorListener) throws CancelException {

		this.parent = parent;
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			errorListener.viewException(e);
		}
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.bar = new JProgressBar(0, 100);
		this.bar.setStringPainted(true);
		this.setLayout(gbl);
		JLabel titleLab = new JLabel(title);
		Font defaultFont = titleLab.getFont();
		titleLab.setFont(new Font(defaultFont.getFontName(), Font.BOLD,
				defaultFont.getSize()));

		procLab.setFont(new Font(defaultFont.getFontName(), Font.PLAIN,
				defaultFont.getSize()));

		SwingUtil.addComponent(this, gbl, titleLab, 0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(5, 5, 5, 5));
		SwingUtil.addComponent(this, gbl, procLab, 0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(5, 5, 5, 5));
		SwingUtil.addComponent(this, gbl, bar, 0, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(5, 5, 5, 5));

		this.setAlwaysOnTop(true);
//		this.setUndecorated(true);
	}
	
	public void start(){
		this.pack();
		this.setSize(this.getWidth() * 2, this.getHeight() + 10);
		net.sqf.view.utils.swing.SwingUtil.centerFrame(this, parent);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		this.setVisible(true);	
	}
	
	public void setProcessState(int progress, String desc){
		this.procLab.setText(desc);
		this.bar.setValue(progress);
	}
	public void end(){
		this.setVisible(false);
	}
}
