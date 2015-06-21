package net.sqf.openDocs.buttons;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.customizer.EditorPanel;

public class CloseButton extends AbstractDropDownButton {

	private static final long serialVersionUID = 8744572509495753135L;
//	private JMenuItem selectedMItem = new GroupItem("No Grouping", EditorPanel.GROUPING_NONE);

	private class GroupItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public GroupItem(String title, Icon icon, ActionListener al) {
			super(title, icon);
			this.addActionListener(al);
		}
	}

	public CloseButton(EditorPanel editorPanel) {
		super(editorPanel);
		setIcon(OpenDocsExtension.ICONS.getIcon(0, 10));
		setToolTipText("Close");
		JMenuItem closeDef = new GroupItem("Close (ask for save)",
											OpenDocsExtension.ICONS.getIcon(0, 10), 
											editorPanel.getCloseAL());
		JMenuItem closeDisc = new GroupItem("Close (discard changes)", 
											OpenDocsExtension.ICONS.getIcon(8, 22), 
											editorPanel.getCloseAL(EditorPanel.CLOSE_WO_SAVE));
		JMenuItem closeSave = new GroupItem("Save + Close",
											OpenDocsExtension.ICONS.getIcon(10, 22), 
											editorPanel.getCloseAL(EditorPanel.CLOSE_WITH_SAVE));
		addMenuItem(closeDef);
		addMenuItem(closeDisc);
		addMenuItem(closeSave);
	}
	
	@Override
	protected void setSelectedItem(JMenuItem item) {
	}

	@Override
	public void action(MouseEvent ae) {
		editorPanel.getCloseAL().actionPerformed(null);
	}

}
