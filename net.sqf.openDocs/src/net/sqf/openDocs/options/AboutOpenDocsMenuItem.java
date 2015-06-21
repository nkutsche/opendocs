package net.sqf.openDocs.options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

public class AboutOpenDocsMenuItem extends JMenuItem {

	private static final long serialVersionUID = 1L;
	private AboutOpenDocs aop;
	
	public AboutOpenDocsMenuItem(JFrame parentFrame){
		super("About DocViewer");
		aop = new AboutOpenDocs(parentFrame);
		this.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				aop.pack();
				aop.setVisible(true);
			}
		});
	}

}
