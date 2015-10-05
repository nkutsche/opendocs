package net.sqf.openDocs.buttons;

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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.customizer.EditorPanel;
import net.sqf.view.utils.images.IconMap;

import org.apache.batik.ext.swing.GridBagConstants;

import ro.sync.exml.workspace.api.standalone.ui.ToolbarButton;
import de.janosch.commons.swing.util.SwingUtil;

public abstract class AbstractDropDownButton extends JPanel implements MouseMotionListener {
	
	private static final long serialVersionUID = 6383094467766824925L;
	
	private final GridBagLayout gbl = new GridBagLayout();
	protected final EditorPanel editorPanel;
	private final Button button;
	private final JPopupMenu menuBar;
	private final ArrayList<JMenuItem> items = new ArrayList<JMenuItem>();
	private JMenuItem selectedMItem;
	
	
	private final Icon selectIcon = OpenDocsExtension.ICONS.getIcon(14, 22);

	private int mouseX = -1;

	private int mouseY = -1;

	private MenuItemSupplier itemSupplier;
	
	private abstract class Button extends ToolbarButton implements MouseListener{
		/**
		 * 
		 */
		private static final long serialVersionUID = -2423536804100988378L;
		private final GridBagLayout btnGbl = new GridBagLayout();
		private final JLabel btnLabel = new JLabel();
		private JSeparator separator = new JSeparator(JSeparator.VERTICAL);
		private Component invSeparator = Box.createHorizontalStrut(2);
		private JLabel menuLabel;
		public Button() {
			super(null, true);
			this.setLayout(btnGbl);
			
			File arrowIcon = new File("icons/arrow.gif");
			menuLabel = new JLabel();
			try {
				menuLabel.setIcon(IconMap.getIcon(arrowIcon));
			} catch (IOException e) {
				menuLabel.setIcon(OpenDocsExtension.ICONS.getIcon(12, 22));
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
		
		@Override
		public void setText(String text) {
			if(btnLabel != null){
				btnLabel.setText(text);
			}
		}
		
		public abstract void action(MouseEvent e);
		
		public abstract void popupMenu(MouseEvent e);
		
		@Override
		public void mouseClicked(MouseEvent e) {
			Component comp = e.getComponent().getComponentAt(mouseX, mouseY);
			
			if(e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)){
				button.popupMenu(e);
			} else if(comp == this.btnLabel){
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
		public void mousePressed(MouseEvent e) {
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {}
		
	}

	public AbstractDropDownButton(EditorPanel ePanel) {
		this(ePanel, GridBagConstants.SOUTH);
	}
	public AbstractDropDownButton(EditorPanel ePanel, final int orientation) {
		this.setLayout(gbl);
		this.editorPanel = ePanel;
		this.menuBar  = new JPopupMenu();
		this.button = new Button() {
			private static final long serialVersionUID = 9177109530368473042L;
			@Override
			public void action(MouseEvent e) {
				AbstractDropDownButton.this.action(e);
			}
			@Override
			public void popupMenu(MouseEvent e) {
				updateItems();
				int x = this.getX();
				int y = this.getY() + this.getHeight();
				switch (orientation) {
				case GridBagConstants.NORTH:
					x = this.getX();
					y = this.getY() - menuBar.getPreferredSize().height;
					break;

				default:
					break;
				}
				menuBar.show(AbstractDropDownButton.this, x, y);
			}
		};
		
		this.button.addMouseMotionListener(this);
		
		

		SwingUtil.addComponent(this, gbl, button, 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	}
	
	
	
	private void updateItems(){
		if(itemSupplier != null){
			this.menuBar.removeAll();
			this.items.removeAll(items);
			for (JComponent item : itemSupplier.getMenuItems()) {
				if (item instanceof JSeparator) {
					this.menuBar.addSeparator();
				} else if (item instanceof JMenuItem) {
					JMenuItem mitem = (JMenuItem) item;
					this.addMenuItem(mitem);
				} else {
					this.menuBar.add(item);
				}
			}
		}
		for (JMenuItem item : items) {
			item.setEnabled(item.isEnabled());
		}
	}
	
	@Override
	public void setToolTipText(String text) {
		this.button.setToolTipText(text);
	}
	
	protected void setItemSupplier(MenuItemSupplier mis){
		this.itemSupplier = mis;
	}
	
	protected void addMenuItem(JMenuItem item){
		this.items.add(item);
		this.menuBar.add(item);
	}
	
	protected void setIcon(Icon icon) {
		button.setIcon(icon);
	}
	
	protected void setTitle(String title) {
		button.setText(title);
	}
	
	protected void setSelectedItem(JMenuItem item){
		if(selectedMItem != null){
			selectedMItem.setIcon(null);
		}
		selectedMItem = item;
		item.setIcon(selectIcon);
		AbstractDropDownButton.this.repaint();
	}
	
	public abstract void action(MouseEvent me);
	
	@Override
	public void mouseMoved(MouseEvent me) {
		this.mouseX = me.getX();
		this.mouseY = me.getY();

	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {}
	
	
}
