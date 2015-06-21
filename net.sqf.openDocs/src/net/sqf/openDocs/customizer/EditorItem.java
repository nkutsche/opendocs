package net.sqf.openDocs.customizer;

import java.awt.Font;

import javax.swing.Icon;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.listNodes.EditorNode;
import net.sqf.utils.process.queues.WatchFactory;
import net.sqf.utils.process.queues.WatchTask;
import net.sqf.view.utils.lists.items.AbstractListItem;

public class EditorItem extends AbstractListItem<EditorNode> implements Comparable<EditorItem> {

	private static final long serialVersionUID = 7660252715616109765L;
	private final EditorNode editorNode;
	private boolean isClosed = false;
	

	private static Icon DEFAULT_ICON = OpenDocsExtension.ICONS.getIcon(0, 2);
	
//	private final GridBagLayout gbl;
	public EditorItem(EditorNode editorNode, boolean isBold) {
		super(editorNode, DEFAULT_ICON);
		this.editorNode = editorNode;
		String path = editorNode.getUrl().toString();
		this.setToolTipText(path);
		
		if(isBold){
			this.setLabelStyle(Font.BOLD);
		}
		
		WatchFactory.addWatchTask(new WatchTask() {
			@Override
			public void watch() {
				updateText();
			}
		});
	}
	
	public EditorItem(EditorNode editorNode) {
		this(editorNode, false);
	}
	
	
	@Override
	public int compareTo(EditorItem coEi) {
		return 0;
//		return toString().compareTo(coEi.toString());
	}
	
	@Override
	public String toString() {
		return editorNode.toString();
	}
	
	public EditorNode getEditorNode(){
		return this.editorNode;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}
	
	

}
