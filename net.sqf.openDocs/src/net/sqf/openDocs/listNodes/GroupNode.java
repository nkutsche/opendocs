package net.sqf.openDocs.listNodes;

import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.UIManager;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.customizer.EditorPanel;
import net.sqf.view.utils.lists.items._ListGroupNode;

public class GroupNode implements _ListGroupNode {

	private static Icon DIR_ICON;

	static {
		DIR_ICON = UIManager.getIcon("Tree.openIcon");
	}

	private final String title;
	private final String tooltip;
	private Icon icon = DIR_ICON;

	public GroupNode(Object label) {
		if (label instanceof File) {
			File f = (File) label;
			if (f.getName().equals("")) {
				this.title = f.getAbsolutePath();
			} else {
				this.title = f.getName();
			}
			this.tooltip = f.toURI().toString();
		} else if (label instanceof File[]) {
			File[] fs = (File[]) label;
			String title = "";
			for (File file : fs) {
				String name = file.getName().equals("") ? file.getAbsolutePath() : file.getName();  
				title += name + File.separator;
			}
			this.title = title;
			this.tooltip = fs.length > 0 ? fs[fs.length - 1].getAbsolutePath() : "";
//			this.tooltip = "";
		} else {
			this.title = label.toString();
			this.tooltip = label.toString();
		}
		if (label == EditorPanel.lastClosedItems) {
			icon = OpenDocsExtension.ICONS.getIcon(0, 10);
		}
	}

	@Override
	public boolean hasIcon() {
		// TODO Auto-generated method stub
		return icon != null;
	}

	@Override
	public Icon getIcon() throws IOException {
		// TODO Auto-generated method stub
		return icon;
	}

	@Override
	public String toString() {
		return title;
	}

	public String getToolTip() {
		return tooltip;
	}

	@Override
	public void setIcon(Icon icon) {
		this.icon = icon;
	}

}
