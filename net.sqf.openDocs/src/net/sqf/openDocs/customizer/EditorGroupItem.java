package net.sqf.openDocs.customizer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Icon;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.listNodes.EditorNode;
import net.sqf.openDocs.listNodes.GroupNode;
import net.sqf.view.utils.lists.items.ItemGroup;
import de.janosch.commons.collections.tree.FileNode;
import de.janosch.commons.collections.tree.Tree;

public class EditorGroupItem extends ItemGroup<EditorNode, EditorItem> implements Comparable<EditorGroupItem>  {

	private static final long serialVersionUID = -5346436627288280009L;

	private static Icon DEFAULT_ICON = OpenDocsExtension.ICONS.getIcon(12, 4);
	
	public static EditorGroupItem convertFromTree(Tree<FileNode> fileTree, HashMap<File, EditorItem> itemByFile, EditorPanel ePanel){
		

		int level = fileTree.getLevel();
		
		ArrayList<File> singleTreeFiles = new ArrayList<File>();
		singleTreeFiles.add(fileTree.getNode().getFile());
		while(fileTree.getChildren().size() == 0 && fileTree.getSubTrees().size() == 1) {
			fileTree = fileTree.getSubTrees().get(0);
			singleTreeFiles.add(fileTree.getNode().getFile());
		}
		
		ArrayList<EditorItem> children = new ArrayList<EditorItem>();
		for (FileNode fn : fileTree.getChildren()) {
			children.add(itemByFile.get(fn.getFile()));
		}
		
		
		ArrayList<ItemGroup<EditorNode, EditorItem>> subGroups = new ArrayList<ItemGroup<EditorNode,EditorItem>>();
		for (Tree<FileNode> subTree : fileTree.getSubTrees()) {
			subGroups.add(convertFromTree(subTree, itemByFile, ePanel));
		}
		
		
		if(singleTreeFiles.size() == 1) {
			return new EditorGroupItem(new GroupNode(fileTree.getNode().getFile()), children, level, ePanel, subGroups);
		} else {
			File[] singleTreeFileArray = singleTreeFiles.toArray(new File[singleTreeFiles.size()]);
			return new EditorGroupItem(new GroupNode(singleTreeFileArray), children, level, ePanel, subGroups);
			
		}
			
		
		
		
	}
	

	public EditorGroupItem(GroupNode node, ArrayList<EditorItem> items,
			int level, EditorPanel ePanel) {
		this(node, items, level, ePanel, new ArrayList<ItemGroup<EditorNode, EditorItem>>());
		
	}
	public EditorGroupItem(GroupNode node, ArrayList<EditorItem> items,
			int level, EditorPanel ePanel, ArrayList<ItemGroup<EditorNode, EditorItem>> subGroups) {
		super(node, items, subGroups, level, ePanel, DEFAULT_ICON);
		this.setToolTipText(node.getToolTip());
	}

	@Override
	public int compareTo(EditorGroupItem arg0) {
		return 0;
	}
	
}
