package net.sqf.view.utils.buttons;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public abstract class AbstractDropDownButton extends JPanel{

	private static final long serialVersionUID = -8884841472288234500L;

	public abstract void addMenuItem(JMenuItem item);
	public abstract void removeAllMenuItems();

	public abstract void setIcon(Icon icon);
	
	public abstract void addSeparator(String title);
	public abstract void addSeparator();

}
