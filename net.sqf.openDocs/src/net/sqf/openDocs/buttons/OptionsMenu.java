package net.sqf.openDocs.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.sqf.openDocs.OpenDocsExtension;

public class OptionsMenu extends JButton implements ActionListener {
	private static final long serialVersionUID = 1L;
	JPopupMenu menu = new JPopupMenu();
	
	public OptionsMenu() {
		this.setIcon(OpenDocsExtension.ICONS.getIcon(0, 4));
		this.setToolTipText("Options");
		
		menu.add(new JMenuItem("Show closed docs"));
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		int x = this.getX() + this.getWidth();
		x -= menu.getWidth();
		
		menu.show(this, x, this.getY() + this.getWidth());
	}
}
