package net.sqf.openDocs.listNodes;

import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;


import net.sqf.view.utils.lists.items._ListNode;

import org.apache.commons.io.FilenameUtils;

import ro.sync.exml.workspace.api.editor.WSEditor;
import de.schlichtherle.io.File;

public class EditorNode implements _ListNode {


	private final WSEditor editor;
	private final int openIndex;
	private int selectIndex = 0;

	private Icon icon = null;

	public EditorNode(WSEditor editor, int openIndex, int selectIndex) {
		this.editor = editor;
		this.openIndex = openIndex;
		this.selectIndex = selectIndex;

		// set icon
		try {
			File file = new File(getUrl().toURI());
			Icon ico = FileSystemView.getFileSystemView().getSystemIcon(file);
			if (ico != null) {
				this.icon = ico;
			}
		} catch (URISyntaxException e) {
		}
	}

	@Override
	public boolean hasIcon() {
		// TODO Auto-generated method stub
		return icon != null;
	}

	@Override
	public Icon getIcon() {
		return icon;
	}

	public String getExtension() {
		return FilenameUtils.getExtension(getUrl().toString());
	}

	public boolean isSaved() {
		return !editor.isModified();
	}

	public URL getUrl() {
		return editor.getEditorLocation();
	}
	public WSEditor getEditor(){
		return this.editor;
	}
	
	public File getFile() throws URISyntaxException{
		File file = new File(getUrl().toURI());
		return file;
	}

	@Override
	public String toString() {
		File file = new File(getUrl().toString());
		String saveMarker = isSaved() ? "" : "*";
		return file.getName() + saveMarker;
	}

	public int getOpenIndex() {
		return this.openIndex;
	}

	public int getSelectIndex() {
		return selectIndex;
	}

	public void setSelectIndex(int selectIndex) {
		this.selectIndex = selectIndex;
	}

	@Override
	public void setIcon(Icon icon) {
		this.icon = icon;
	}

}
