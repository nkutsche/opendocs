package net.sqf.openDocs.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.customizer.EditorPanel;
import net.sqf.openDocs.options.AboutOpenDocsMenuItem;

public class OptionButton extends AbstractMenuButton {

	private static final long serialVersionUID = 8744572509495753135L;


	
	
	private class ShowClosedOption extends JMenuItem {
		private static final long serialVersionUID = 1L;
		
		public ShowClosedOption(String text, final int showClosed){
			super(text);
			if(editorPanel.getConfig().getShowClosedFiles() == showClosed){
				setSelectedItem(ShowClosedOption.this);
			}
			this.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					setSelectedItem(ShowClosedOption.this);
					editorPanel.setShowClosedFiles(showClosed);
				}
			});
		}
	}
	

	public OptionButton(EditorPanel editorPanel, JFrame parentFrame) {
		super(editorPanel);
		setIcon(OpenDocsExtension.ICONS.getIcon(0, 4));
		JMenu showClosed = new JMenu("Show closed files");
		showClosed.add(new ShowClosedOption("Hide closed files", 0));
		showClosed.add(new ShowClosedOption("Show the last 5 closed files", 5));
		showClosed.add(new ShowClosedOption("Show the last 10 closed files", 10));
		showClosed.add(new ShowClosedOption("Show the last 15 closed files", 15));
		showClosed.add(new ShowClosedOption("Show the last 20 closed files", 20));
		showClosed.add(new ShowClosedOption("Show the last 50 closed files", 50));
		addMenuItem(showClosed);
		addMenuItem(new AboutOpenDocsMenuItem(parentFrame));
//		this.add(new JLabel(DocViewerExtension.ICONS.getIcon(11, 18)));
	}

}
