package net.sqf.openDocs.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.customizer.EditorItem;
import net.sqf.openDocs.customizer.EditorPanel;

public class SelectButton extends AbstractDropDownButton {
	
	private static final int SELECT_MODE_ALL = 0;
	private static final int SELECT_MODE_MOD = 1;
	private static final int SELECT_MODE_NEW = 2;
	private static final int SELECT_MODE_REV = 3;

	private static final long serialVersionUID = 8744572509495753135L;
//	private JMenuItem selectedMItem = new GroupItem("No Grouping", EditorPanel.GROUPING_NONE);

	private class GroupItem extends JMenuItem  implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final int selectMode;
		public GroupItem(String title, Icon icon, int selectMode) {
			super(title, icon);
			this.selectMode = selectMode;
			this.addActionListener(this);
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			selectEditors(selectMode);
		}
		
		@Override
		public boolean isEnabled() {
			if(selectMode == SELECT_MODE_REV){
				return true;
			} else {
				return getItemsByMode(selectMode).size() > 0;
			}
		}
	}

	public SelectButton(EditorPanel editorPanel) {
		super(editorPanel);
		setIcon(OpenDocsExtension.ICONS.getIcon(2, 11));
		setToolTipText("Select");
		JMenuItem selectAll = new GroupItem("Select all",
											OpenDocsExtension.ICONS.getIcon(2, 11), 
											SELECT_MODE_ALL);
		JMenuItem selectMod = new GroupItem("Select all modified", 
											OpenDocsExtension.ICONS.getIcon(8, 21), 
											SELECT_MODE_MOD);
		JMenuItem selectNew = new GroupItem("Select all new files", 
											OpenDocsExtension.ICONS.getIcon(6, 22), 
											SELECT_MODE_NEW);
		JMenuItem selectReverse = new GroupItem("Reverse selection",
											OpenDocsExtension.ICONS.getIcon(0, 12), 
											SELECT_MODE_REV);
		addMenuItem(selectAll);
		addMenuItem(selectMod);
		addMenuItem(selectNew);
		addMenuItem(selectReverse);
	}
	
	@Override
	protected void setSelectedItem(JMenuItem item) {
	}

	@Override
	public void action(MouseEvent me) {
		selectEditors(SELECT_MODE_ALL);
	}
	
	private ArrayList<EditorItem> getItemsByMode(int selectMode){
		ArrayList<EditorItem> selItemsBefore = editorPanel.getAllItems();
		ArrayList<EditorItem> selItems = new ArrayList<EditorItem>();
		for (EditorItem editorItem : selItemsBefore) {
			boolean select = false;
			switch (selectMode) {
			case SELECT_MODE_ALL:
				select = true;
				break;
			case SELECT_MODE_MOD:
				select = !editorItem.getEditorNode().isSaved();
				break;	
			case SELECT_MODE_REV:
				select = !editorItem.isSelected();
				break;
				
			case SELECT_MODE_NEW:
				select = !editorItem.getEditorNode().exists();
				break;
			default:
				select = true;
				break;
			}
			if(select){
				selItems.add(editorItem);
			}
		}
		return selItems;
	}
	
	private void selectEditors(int selectMode){
		editorPanel.selectUnselectItem(getItemsByMode(selectMode));
	}
	
}
