package net.sqf.openDocs.buttons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import com.jidesoft.plaf.basic.ThemePainter;

import ro.sync.exml.workspace.api.standalone.ui.ToolbarButton;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.OpenDocsPlugin;
import net.sqf.openDocs.customizer.EditorPanel;
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
	
	private abstract class Button extends ToolbarButton implements MouseListener{
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
				menuLabel.setIcon(OpenDocsExtension.ICONS.getIcon(arrowIcon));
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
		
		public abstract void action(MouseEvent e);
		
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
		
	}

	
	public AbstractDropDownButton(EditorPanel ePanel) {
		this.setLayout(gbl);
		this.editorPanel = ePanel;
		this.menuBar  = new JPopupMenu();
		this.button = new Button() {
			@Override
			public void action(MouseEvent e) {
				AbstractDropDownButton.this.action(e);
			}
			@Override
			public void popupMenu(MouseEvent e) {
				int x = this.getX();
				int y = this.getY() + this.getHeight();
				updateItems();
				menuBar.show(AbstractDropDownButton.this, x, y);
			}
		};
		
		this.button.addMouseMotionListener(this);
		
		

		SwingUtil.addComponent(this, gbl, button, 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	}
	
	private void updateItems(){
		for (JMenuItem item : items) {
			item.setEnabled(item.isEnabled());
		}
	}
	
	protected void addMenuItem(JMenuItem item){
		this.items.add(item);
		this.menuBar.add(item);
	}
	
	protected void setIcon(ImageIcon icon) {
		button.setIcon(icon);
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
