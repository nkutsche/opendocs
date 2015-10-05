package de.janosch.commons.collections.tree;

import java.io.File;
import java.util.ArrayList;

public class FileNode implements _TreeNode<FileNode> {
	
	private final File f;
	
	public static ArrayList<FileNode> getFileNodes(ArrayList<File> files){
		ArrayList<FileNode> nodes = new ArrayList<FileNode>();
		for (File file : files) {
			nodes.add(new FileNode(file));
		}
		return nodes;
	}
	
	public FileNode(File f){
		this.f = f;
	}
	
	@Override
	public FileNode getParent() {
		if(f.getParentFile() == null){
			return null;
		}
		return new FileNode(f.getParentFile());
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return !f.isDirectory();
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return f.getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof FileNode){
			FileNode fn = (FileNode) obj;
			return fn.f.toString().equals(f.toString());
		}
		return super.equals(obj);
	}
	
	@Override
	public String getId() {
		return f.getAbsolutePath();
	}

	public File getFile() {
		return f;
	}
}
