package net.sqf.openDocs.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.customizer.EditorPanel;

public class SortByButton extends AbstractDropDownButton {

	private static final long serialVersionUID = 8744572509495753135L;
	
	private static final ImageIcon DOWN_TO_UP = OpenDocsExtension.ICONS.getIcon(13, 10);
	private static final ImageIcon UP_TO_DOWN = OpenDocsExtension.ICONS.getIcon(15, 10);
	

	

	
	private boolean revert = false;

	private String sorting;


	private class GroupItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		
		
		public GroupItem(String title, final String sorting) {
			super(title);
			if(editorPanel.getConfig().getSortBy().equals(sorting)){
				setSelectedItem(this, sorting);
			}
			this.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (SortByButton.this.editorPanel != null){
						SortByButton.this.editorPanel.setSortBy(sorting, revert);
					}
					setSelectedItem(GroupItem.this, sorting);
					SortByButton.this.repaint();
				}
			});
		}
	}
	
	private void setSelectedItem(JMenuItem selectedItem, String sorting){
		setSelectedItem(selectedItem);
		this.sorting = sorting;
	}
	

	public SortByButton(EditorPanel editorPanel) {
		super(editorPanel);
		
		setToolTipText("Sorting");
		this.revert = editorPanel.getConfig().isReverseSort();
		updateIcon();
		
		GroupItem[] menuItems = new GroupItem[] {
				new GroupItem("Name", EditorPanel.SORTING_ALPH),
				new GroupItem("Opened", EditorPanel.SORTING_OPEN),
				new GroupItem("Selected", EditorPanel.SORTING_SELECT)
		};
		
		for (GroupItem item : menuItems) {
			this.addMenuItem(item);
		}
		
		
		
	}
	
	private void updateIcon(){
		setIcon(revert ? UP_TO_DOWN : DOWN_TO_UP);
	}

	@Override
	public void action(MouseEvent me) {
		revert = !revert;
		updateIcon();
		repaint();
		SortByButton.this.editorPanel.setSortBy(SortByButton.this.sorting, revert);
		SortByButton.this.editorPanel.updateUI();
	}

}
