package net.sqf.openDocs.listNodes;

import java.net.URL;

import ro.sync.exml.workspace.api.editor.WSEditor;

public class NodeFactory {
	public static EditorNode createEditorNode(WSEditor editor, int openIndex, int selectIndex){
		URL url = editor.getEditorLocation();
		return createEditorNode(editor, openIndex, selectIndex, url);
		
	}
	public static EditorNode createEditorNode(WSEditor editor, int openIndex, int selectIndex, URL url){
		if(url.toString().contains("!")){
			return new ArchiveNode(editor, openIndex, selectIndex, url);
		} else if (url.getProtocol().equals("http")) {
			return new HttpNode(editor, openIndex, selectIndex, url);
		} else {
			return new EditorNode(editor, openIndex, selectIndex, url);
		}
	}
	
}
