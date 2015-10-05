package net.sqf.openDocs.listNodes;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.Icon;

import net.sqf.view.utils.images.IconMap;
import ro.sync.exml.workspace.api.editor.WSEditor;

public class HttpNode extends EditorNode {

	protected HttpNode(WSEditor editor, int openIndex, int selectIndex, URL fallbackUrl) {
		super(editor, openIndex, selectIndex, fallbackUrl);
		Icon ico;
		try {
			ico = IconMap.getSystemIconByExt(getExtension());
			ico = IconMap.filter(ico, new Double[]{1.0, 1.0, 0.3, 1.0});
		} catch (IOException e) {
			ico = null;
		}
		this.setIcon(ico);
	}
	
	@Override
	public boolean exists() {
		return true;
	}
	
	
	@Override
	public File getFile() throws URISyntaxException {
		return null;
	}
	
	@Override
	public String toString() {

		String saveMarker = isSaved() ? "" : "*";
		String[] splits = getUrl().toString().split("/");
		return splits[splits.length - 1] + saveMarker;
	}
	
	@Override
	public Color getColor() {
		return new Color(155, 155, 0, 255);
	}
}
