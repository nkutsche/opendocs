package net.sqf.openDocs.listNodes;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import net.sqf.view.utils.images.IconMap;
import net.sqf.view.utils.lists.items._ListNode;

import org.apache.commons.io.FilenameUtils;

import ro.sync.exml.workspace.api.editor.WSEditor;

public class EditorNode implements _ListNode {


	private final WSEditor editor;
	private final int openIndex;
	private int selectIndex = 0;

	private Icon icon = null;
	private final URL fallbackUrl;

	protected EditorNode(WSEditor editor, int openIndex, int selectIndex, URL fallbackUrl) {
		this.editor = editor;
		this.openIndex = openIndex;
		this.selectIndex = selectIndex;
		this.fallbackUrl = fallbackUrl;

		// set icon
		try {
			URL url = getUrl();
			Icon ico = null;
			
			Double[] filterFactors = {1.0, 1.0, 1.0, 1.0}; 

			if(isArchive()){
				ico = IconMap.getSystemIconByExt(getExtension());
				filterFactors = new Double[]{0.8, 0.8, 0.8, 1.0};
			} else if(getFile() == null){
				ico = IconMap.getSystemIconByExt(getExtension());
			} else if(!getFile().exists()){
				ico = IconMap.getSystemIconByExt(getExtension());
				filterFactors[3] = 0.5;
			} else if(url.getProtocol().equals("file")){
				File file = new File(getUrl().toURI());
				ico = FileSystemView.getFileSystemView().getSystemIcon(file);
			}
			if (ico != null) {
				ico = IconMap.filter(ico, filterFactors);
				this.icon = ico;
			}
		} catch (URISyntaxException e) {
		} catch (IOException e) {
		}
	}
	
	

	@Override
	public boolean hasIcon() {
		// TODO Auto-generated method stub
		return icon != null;
	}
	
	public boolean isArchive(){
		try {
			return this.getUrl().toURI().toString().contains("!");
		} catch (URISyntaxException e) {
			return false;
		}
	}
	
	public boolean exists(){
		File file;
		try {
			file = getFile();
		} catch (URISyntaxException e1) {
			return false;
		}
		if(file == null){
			return false;
		}
		return file.exists();
	}
	
	@Override
	public Icon getIcon() {
		return icon;
	}

	public String getExtension() {
		return FilenameUtils.getExtension(getUrl().toString());
	}

	public boolean isSaved() {
		if(editor == null)
			return true;
		return !editor.isModified();
	}

	public URL getUrl() {
		if(editor == null)
			return fallbackUrl;
		return editor.getEditorLocation();
	}
	
	
	public File getFile() throws URISyntaxException{
		URL url = getUrl();
		String prot = url.getProtocol();
		if(prot.equals("file")){
			File file = new File(url.toURI());
			return file;
		} else if (isArchive()){
			String path = url.toURI().toString().split("!")[0].replaceFirst(url.getProtocol() + ":", "");
			File file = new File(new URI(path));
			return file;
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		File file;
		String saveMarker = isSaved() ? "" : "*";
		
		try {
			if(isArchive()){
				file = new File(getUrl().toURI().toString());
			} else {
				file = new File(getUrl().toURI());
			}
		} catch (URISyntaxException e) {
			file = new File(getUrl().toString());
		} catch (Exception e){
			String[] splits = getUrl().toString().split("/");
			return splits[splits.length - 1] + saveMarker;
		}
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
	
	
	public Color getColor(){
		if(!this.exists()){
			return Color.GRAY;
		}
		return Color.BLACK;
		
	}
}
