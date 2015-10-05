package net.sqf.openDocs.customizer;

import java.awt.Color;
import java.awt.Font;
import java.net.URL;

import javax.swing.Icon;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.buttons.EditorMenu;
import net.sqf.openDocs.listNodes.EditorNode;
import net.sqf.openDocs.listNodes.NodeFactory;
import net.sqf.utils.process.queues.WatchFactory;
import net.sqf.utils.process.queues.WatchTask;
import net.sqf.view.utils.lists.items.AbstractListItem;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class EditorItem extends AbstractListItem<EditorNode> implements Comparable<EditorItem> {

	private static final long serialVersionUID = 7660252715616109765L;
	private final EditorNode editorNode;
	private boolean isClosed = false;
	

	private static Icon DEFAULT_ICON = OpenDocsExtension.ICONS.getIcon(0, 2);
	private final boolean isBold;
	
	public static EditorItem createDummyEditorItem(URL url){
		return new EditorItem(NodeFactory.createEditorNode(null, -1, -1, url));
	}
	
	public static EditorItem createEditorItem(EditorNode en, EditorPanel ePanel, StandalonePluginWorkspace spw, boolean isBold){
		EditorItem ei = new EditorItem(en, isBold);
		ei.implementPopupMenu(new EditorMenu[]{
				new EditorMenu(spw, ei, ePanel),
				new EditorMenu(spw, ei, ePanel, EditorItem.SHIFT_DOWN),
				new EditorMenu(spw, ei, ePanel, EditorItem.CTRL_DOWN)});
		ePanel.register(ei);
		return ei;
	}
	private static EditorItem createEditorItem(EditorNode en,
			EditorPanel ePanel, StandalonePluginWorkspace spw, Color color) {
		EditorItem ei = createEditorItem(en, ePanel, spw, false);
		ei.setLabelStyle(color);
		return ei;
	}
	
//	private final GridBagLayout gbl;
	private EditorItem(EditorNode editorNode, boolean isBold) {
		super(editorNode, DEFAULT_ICON);
		this.editorNode = editorNode;
		this.isBold = isBold;
		String path = editorNode.getUrl().toString();
		this.setToolTipText(path);
		
		
		int style = Font.PLAIN;
		
		style += isBold ? Font.BOLD : 0;
		style += editorNode.isArchive() ? Font.ITALIC : 0;
		
		this.setLabelStyle(style);
		
		this.setLabelStyle(editorNode.getColor());
		
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
	
	public EditorItem copy(EditorPanel ePanel, StandalonePluginWorkspace spw, Color color){
		if(this.isBold && color != null){
			return EditorItem.createEditorItem(this.node, ePanel, spw, color);
		} else {
			return EditorItem.createEditorItem(this.node, ePanel, spw, this.isBold);
		}
	}

}
