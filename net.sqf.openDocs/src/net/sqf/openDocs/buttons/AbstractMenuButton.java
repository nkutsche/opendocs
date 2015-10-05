package net.sqf.openDocs.buttons;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.customizer.EditorPanel;
import ro.sync.exml.workspace.api.standalone.ui.ToolbarButton;
import de.janosch.commons.swing.util.SwingUtil;

public abstract class AbstractMenuButton extends JPanel {
	
	private static final long serialVersionUID = 6383094467766824925L;
	
	private final GridBagLayout gbl = new GridBagLayout();
	protected final EditorPanel editorPanel;
	private final JPopupMenu menu;
	private final ArrayList<JMenuItem> items = new ArrayList<JMenuItem>();
	private JMenuItem selectedMItem;
	
	private final Icon selectIcon = OpenDocsExtension.ICONS.getIcon(14, 22);

	private Button button;
	
	private class Button extends ToolbarButton {
		private static final long serialVersionUID = 1L;
		private LayoutManager btnGbl;

		public Button() {
			super(null, true);
			this.setLayout(btnGbl);
			this.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					int x = getX();
					int y = getY() + getHeight();
					updateItems();
					menu.show(AbstractMenuButton.this, x, y);
				}
			});
		}
		
	}
	
	public AbstractMenuButton(EditorPanel ePanel) {
		this.setLayout(gbl);
		this.editorPanel = ePanel;
		this.menu  = new JPopupMenu();
		this.button = new Button();
		
		
		SwingUtil.addComponent(this, gbl, button, 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
	}
	
	@Override
	public void setToolTipText(String text) {
		this.button.setToolTipText(text);
		super.setToolTipText(text);
	}
	
	private void updateItems(){
		for (JMenuItem item : items) {
			item.setEnabled(item.isEnabled());
		}
	}
	
	protected void addMenuItem(JMenuItem item){
		this.items.add(item);
		this.menu.add(item);
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
		AbstractMenuButton.this.repaint();
	}
}
