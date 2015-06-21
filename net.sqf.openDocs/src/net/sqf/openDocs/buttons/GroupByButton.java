package net.sqf.openDocs.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.customizer.EditorPanel;

public class GroupByButton extends AbstractMenuButton {

	private static final long serialVersionUID = 8744572509495753135L;
	

	private class GroupItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public GroupItem(String title, final String grouping) {
			super(title);
			
			if(editorPanel.getConfig().getGroupBy().equals(grouping)){
				setSelectedItem(this);
			}
			
			this.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (GroupByButton.this.editorPanel != null){
						GroupByButton.this.editorPanel.setGroupBy(grouping);
					}
					setSelectedItem(GroupItem.this);
				}
			});
		}
	}
	


	public GroupByButton(EditorPanel editorPanel) {
		super(editorPanel);
		
		setIcon(OpenDocsExtension.ICONS.getIcon(11, 18));
		
		JMenuItem noGroup = new GroupItem(	"No Grouping", 	EditorPanel.GROUPING_NONE);
		JMenuItem ext = new GroupItem(		"Extension", 	EditorPanel.GROUPING_EXT);
		JMenuItem folder = new GroupItem(	"Folder", 		EditorPanel.GROUPING_FOLDER);
//		JMenuItem folderTree = new GroupItem(	"Folder-Tree", 		EditorPanel.GROUPING_FOLDER_TREE);
		
		this.addMenuItem(noGroup);
		this.addMenuItem(ext);
		this.addMenuItem(folder);
//		this.addMenuItem(folderTree);
		
	}
	
	


}
