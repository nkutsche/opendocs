package de.janosch.commons.collections.tree;

public interface _TreeNode<TreeNode> {
	TreeNode getParent();
	boolean hasChildren();
	String getId();
}
