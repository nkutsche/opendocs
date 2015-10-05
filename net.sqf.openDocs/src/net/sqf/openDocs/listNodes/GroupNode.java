package net.sqf.openDocs.listNodes;

import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.UIManager;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.customizer.EditorPanel;
import net.sqf.openDocs.workingSet.WorkingSet;
import net.sqf.view.utils.images.IconMap;
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
		String title = label.toString();
		String tooltip = label.toString();
		if (label instanceof File) {
			File f = (File) label;
			if (f.getName().equals("")) {
				title = f.getAbsolutePath();
			} else {
				title = f.getName();
			}
			tooltip = f.toURI().toString();
		} else if (label instanceof File[]) {
			File[] fs = (File[]) label;
			String titleText = "";
			for (File file : fs) {
				String name = file.getName().equals("") ? file.getAbsolutePath() : file.getName();  
				titleText += name + File.separator;
			}
			title = titleText;
			tooltip = fs.length > 0 ? fs[fs.length - 1].getAbsolutePath() : "";
//			this.tooltip = "";
		} else if (label instanceof WorkingSet) {
			WorkingSet ws = (WorkingSet) label;
			this.icon = ws.getIcon() == null ? OpenDocsExtension.ICONS.getIcon(18, 16) : ws.getIcon();
			title = label.toString();
			tooltip = label.toString();
		} else if (label instanceof EditorPanel.Extension) {
			Icon ico;
			try {
				ico = IconMap.getSystemIconByExt(label.toString());
			} catch (IOException e) {
				ico = null;
			}
			this.icon = ico != null ? ico : this.icon;
		} else if (label instanceof JLabel) {
			JLabel jLabel = (JLabel) label;
			title = jLabel.getText();
			tooltip = jLabel.getToolTipText() == null ? title : jLabel.getToolTipText();
			if(jLabel.getIcon() != null){
				this.icon = jLabel.getIcon();
			}
		}
		this.title = title;
		this.tooltip = tooltip;
		
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
