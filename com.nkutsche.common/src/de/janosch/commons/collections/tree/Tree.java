package de.janosch.commons.collections.tree;

import java.util.ArrayList;
import java.util.HashMap;

import de.janosch.commons.collections.MultiValueHashMap;

public class Tree<NodeType extends _TreeNode<NodeType>> {
	
	private final NodeType node;
	private final ArrayList<Tree<NodeType>> subTrees = new ArrayList<Tree<NodeType>>();
	private final ArrayList<NodeType> children = new ArrayList<NodeType>();
	private int level;
	
	public Tree(ArrayList<NodeType> nodes){
		this(null, nodes, 0);
	}
	private Tree(NodeType self, ArrayList<NodeType> nodes, int level){
		this.node = self;
		this.level = level;
		MultiValueHashMap<String, NodeType> levelNodeByChildNode = new MultiValueHashMap<String, NodeType>(true);
		HashMap<String, NodeType> nodeTypeById = new HashMap<String, NodeType>();
		for (NodeType node : nodes) {
			ArrayList<NodeType> anc = getAncestors(node);
			if(anc.size() > level + 1){
				NodeType levelNode = anc.get(level);
				nodeTypeById.put(levelNode.getId(), levelNode);
				levelNodeByChildNode.put(levelNode.getId(), node);
			} else if(anc.size() == level + 1){
				getChildren().add(node);
			}
		}
		for (String nodeId : levelNodeByChildNode.keySet()) {
			getSubTrees().add(new Tree<NodeType>(nodeTypeById.get(nodeId), levelNodeByChildNode.getAll(nodeId), level+1));
		}
	}
	
	private static <NodeType extends _TreeNode<NodeType>> ArrayList<NodeType> getAncestors(NodeType node){
		if(node == null){
			return new ArrayList<NodeType>();
		} else {
			ArrayList<NodeType> anc = getAncestors(node.getParent());
			anc.add(node);
			return anc;
		}
	}
	public ArrayList<Tree<NodeType>> getSubTrees() {
		return subTrees;
	}
	public ArrayList<NodeType> getChildren() {
		return children;
	}
	public NodeType getNode() {
		return node;
	}
	public int getLevel() {
		return level;
	}
	
	private void setLevel(int level){
		this.level = level;
		for (Tree<NodeType> subTree : this.subTrees) {
			subTree.setLevel(level + 1);
		}
	}
	public void setToRoot(){
		this.setLevel(0);
	}
	
	@Override
	public String toString() {
		
		String name = node == null ? "root" : node.toString();
		
		String indent = "";
		for (int i = 0; i < level; i++) {
			indent += "  ";
		}
		name = indent + name;
		
		for (Tree<NodeType> tree : this.subTrees) {
			name += "\n" + tree.toString(); 
		}
		for (NodeType child : this.children) {
			name += "\n" + indent + "  " + child.toString();
		}
		return name;
	}
	
}
