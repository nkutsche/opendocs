package net.sqf.view.utils.buttons;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.sqf.view.utils.images.IconMap;
import de.janosch.commons.swing.util.SwingUtil;

public class DropDownButton extends AbstractDropDownButton implements MouseMotionListener {
	
	private static final long serialVersionUID = 6383094467766824925L;
	
	private final GridBagLayout gbl = new GridBagLayout();
	private final Button button;
	private final JPopupMenu menuBar;
	private final ArrayList<JMenuItem> items = new ArrayList<JMenuItem>();
	private JMenuItem selectedMItem;
	
	
	private final Icon selectIcon = IconMap.ICONS.getIcon(14, 22);

	private int mouseX = -1;

	private int mouseY = -1;
	
	private abstract class Button extends JButton implements MouseListener{
		private static final long serialVersionUID = -3718955415293253222L;
		private final GridBagLayout btnGbl = new GridBagLayout();
		private final JLabel btnLabel = new JLabel();
		private JSeparator separator = new JSeparator(JSeparator.VERTICAL);
		private Component invSeparator = Box.createHorizontalStrut(2);
		private JLabel menuLabel;
		private _DropDownButtonAction action = null;
		public Button() {
			super();
			this.setLayout(btnGbl);
			
			File arrowIcon = new File("icons/arrow.gif");
			menuLabel = new JLabel();
			try {
				menuLabel.setIcon(IconMap.getIcon(arrowIcon));
			} catch (IOException e) {
				menuLabel.setIcon(IconMap.ICONS.getIcon(12, 22));
			}
			
			this.addMouseListener(this);
			this.removeAll();
			SwingUtil.addComponent(this, btnGbl, btnLabel, 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH);
			hideSeparator();
			SwingUtil.addComponent(this, btnGbl, menuLabel, 2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.BOTH);
			
			
		}
		
		private void hideSeparator(){
			this.remove(separator);
			SwingUtil.addComponent(this, btnGbl, invSeparator, 1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL);
			this.updateUI();
		}
		private void showSeparator(){
			this.remove(invSeparator);
			SwingUtil.addComponent(this, btnGbl, separator, 1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL);
			this.updateUI();
		}
		
		
		@Override
		public void setIcon(Icon icon) {
			if(btnLabel != null){
				btnLabel.setIcon(icon);
			}
		}
		
		public void action(MouseEvent e){
			if(this.action != null){
				action.action(e);
			}
		};
		
		public abstract void popupMenu(MouseEvent e);
		
		@Override
		public void mouseClicked(MouseEvent e) {
			Component comp = e.getComponent().getComponentAt(mouseX, mouseY);
			if(comp == this.btnLabel){
				button.action(e);
			} else if (comp == this.menuLabel){
				button.popupMenu(e);
			} else if (comp == this.separator){
				int center = comp.getWidth() / 2;
				int xOnSep = mouseX - comp.getX();
				if(xOnSep < center){
					button.action(e);
				} else {
					button.popupMenu(e);
				}
			}
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			button.showSeparator();
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			button.hideSeparator();
		}
		
		@Override
		public void mousePressed(MouseEvent e) {}
		
		@Override
		public void mouseReleased(MouseEvent e) {}
		
		public void addActionListener(_DropDownButtonAction action) {
			this.action = action;
		}
	}

	
	public DropDownButton(_DropDownButtonAction action) {
		this.setLayout(gbl);
		this.menuBar  = new JPopupMenu();
		this.button = new Button() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 8097277545011108849L;

			@Override
			public void popupMenu(MouseEvent e) {
				int x = this.getX();
				int y = this.getY() + this.getHeight();
				updateItems();
				menuBar.show(DropDownButton.this, x, y);
			}
		};
		this.button.addActionListener(action);
		
		this.button.addMouseMotionListener(this);
		
		

		SwingUtil.addComponent(this, gbl, button, 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	}
	
	private void updateItems(){
		for (JMenuItem item : items) {
			item.setEnabled(item.isEnabled());
		}
	}
	
	@Override
	public void addMenuItem(JMenuItem item){
		this.items.add(item);
		this.menuBar.add(item);
	}
	
	@Override
	public void addSeparator() {
		this.addSeparator("");
	}
	
	@Override
	public void addSeparator(String title) {
		JSeparator sep = new JSeparator();
		this.menuBar.add(sep);
	}
	
	@Override
	public void setIcon(Icon icon) {
		button.setIcon(icon);
	}
	
	protected void setSelectedItem(JMenuItem item){
		if(selectedMItem != null){
			selectedMItem.setIcon(null);
		}
		selectedMItem = item;
		item.setIcon(selectIcon);
		DropDownButton.this.repaint();
	}
	
	
	@Override
	public void mouseMoved(MouseEvent me) {
		this.mouseX = me.getX();
		this.mouseY = me.getY();

	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {}
	
	@Override
	public void removeAllMenuItems() {
//		for (JMenuItem item : this.items) {
//			this.menuBar.remove(item);
//		}
		this.menuBar.removeAll();
	}
}
