package net.sqf.openDocs.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.customizer.EditorPanel;
import net.sqf.openDocs.workingSet.WorkingSet;

public class WorkingSetButton extends AbstractDropDownButton implements MenuItemSupplier, ActionListener{
	
//	private static final int SELECT_MODE_ALL = 0;
//	private static final int SELECT_MODE_MOD = 1;
//	private static final int SELECT_MODE_NEW = 2;
//	private static final int SELECT_MODE_REV = 3;

	private static final long serialVersionUID = 8744572509495753135L;
//	private JMenuItem selectedMItem = new GroupItem("No Grouping", EditorPanel.GROUPING_NONE);

	private class GroupItem extends JMenuItem  implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final WorkingSet ws;
		public GroupItem(WorkingSet ws) {
			super(ws.toString(), ws.getIcon());
			this.ws = ws;
			this.addActionListener(this);
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			ws.addToWorkingSet(editorPanel.getSelectedItem());
		}
		
		@Override
		public boolean isEnabled() {
			return editorPanel.getSelectedItem().size() > 0;
		}
	}

	private final JMenuItem addNew;

	public WorkingSetButton(EditorPanel editorPanel) {
		super(editorPanel);
		setIcon(OpenDocsExtension.ICONS.getIcon(18, 16));
		setToolTipText("Working set");
		this.addNew = new JMenuItem("Add to new Working set", OpenDocsExtension.ICONS.getIcon(4, 10));
		this.addNew.addActionListener(this);
		
		this.setItemSupplier(this);
	}
	
	@Override
	protected void setSelectedItem(JMenuItem item) {
	}

	@Override
	public void action(MouseEvent me) {
		action();
	}
	
	private void action(){
		editorPanel.saveWorkingSet();
	}
	
	

	@Override
	public ArrayList<JComponent> getMenuItems() {
		ArrayList<JComponent> mitems = new ArrayList<JComponent>();
		mitems.add(addNew);
		for (WorkingSet ws : editorPanel.getWorkingSets()) {
			mitems.add(new GroupItem(ws));
		}
		
		return mitems;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		action();
	}
	
}
