package net.sqf.openDocs.customizer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.sqf.openDocs.buttons.CloseButton;
import net.sqf.openDocs.buttons.GroupByButton;
import net.sqf.openDocs.buttons.OptionButton;
import net.sqf.openDocs.buttons.SelectButton;
import net.sqf.openDocs.buttons.SortByButton;
import net.sqf.openDocs.listNodes.EditorNode;
import net.sqf.openDocs.listNodes.GroupNode;
import net.sqf.openDocs.options.Config;
import net.sqf.view.utils.lists.AbstractList;
import net.sqf.view.utils.lists.items.AbstractItemMouseListener;
import net.sqf.view.utils.swing.FileDragSource;
import net.sqf.view.utils.swing.FileDropTarget;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import de.janosch.commons.collections.MultiValueHashMap;
import de.janosch.commons.collections.tree.FileNode;
import de.janosch.commons.collections.tree.Tree;

public class EditorPanel extends AbstractList<EditorNode, EditorItem>{
	
	public static final String GROUPING_NONE = "NONE";
	public static final String GROUPING_EXT = "EXT";
	public static final String GROUPING_FOLDER = "FOLDER";
	public static final String GROUPING_FOLDER_TREE = "FOLDER_TREE";
//	public static final String GROUPING_SAVE = "SAVE";
	
	public static final String SORTING_ALPH = "ALPH";
	public static final String SORTING_OPEN = "OPEN";
	public static final String SORTING_SELECT = "SELECT";
	
	

	private static final long serialVersionUID = 4156960074154004679L;
	
	int counter = 0;
	

	private final StandalonePluginWorkspace spw;

//	private String groupBy = GROUPING_NONE;
//	private String sortBy = SORTING_ALPH;
//	private int showClosedFiles = 0;
	
	private Config conf = Config.readConfig();
//	private String aboutHtm = Config.readAbout();
	
//	private boolean revert = false;
	private ArrayList<EditorItem> closedItems;
	
	
	private class EditorItemListener extends AbstractItemMouseListener<EditorNode, EditorItem> {
		
		public EditorItemListener(EditorItem item) {
			super(item, EditorPanel.this);
		}
		
		@Override
		public void oneClick(MouseEvent e, boolean isSelected) {
		}
		@Override
		public void doubleClick(MouseEvent e) {
			spw.open(item.getEditorNode().getUrl());
		}
	}
	
	private final static int UNDEFINED = -1;
	private final static int YES = 0;
	private final static int NO = 1;
	
	public EditorPanel(final StandalonePluginWorkspace spw) {
		this.spw = spw;
		
		new FileDropTarget(this.endPanel) {
			
			@Override
			public void receiveFiles(List<File> files) {
				receiveFiles(files, UNDEFINED);
			}
			public void receiveFiles(List<File> files, int answer) {
				for (File file : files) {
					if(file.isDirectory()){
						if(answer == UNDEFINED){
							answer = spw.showConfirmDialog("Open directories", "Open all files in the directory?", new String[]{"Yes", "No"}, new int[]{0, 1});
						}
						if(answer == YES){
							receiveFiles(Arrays.asList(file.listFiles()), answer);
						}
					} else {
						try {
							spw.open(file.toURI().toURL());
						} catch (MalformedURLException e) {
							spw.showErrorMessage(e.getLocalizedMessage());
						}
					}
				}
			}
		};
		
		Object objFram = spw.getParentFrame();
		if(objFram instanceof JFrame){
			this.implementShortCut((JFrame) objFram);
		}

		
		this.setOpaque(true);
		

		JFrame frame =  spw.getParentFrame() instanceof JFrame ? (JFrame) spw.getParentFrame() : null;
		
		this.addComponentToToolbar(new CloseButton(this), 		0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);
		this.addComponentToToolbar(new GroupByButton(this), 	1, 0, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);
		this.addComponentToToolbar(new SortByButton(this), 		2, 0, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);
		this.addComponentToToolbar(new SelectButton(this), 		3, 0, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);
		this.addComponentToToolbar(new JPanel(), 				4, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);
		this.addComponentToToolbar(new OptionButton(this, frame), 		5, 0, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE);
		
		this.setMinimumSize(new Dimension(250, getMinimumSize().height));
		
		this.setFocusable(true);
		this.requestFocus();
	}
	
	private class EditorItemComparator implements Comparator<EditorItem> {
		@Override
		public int compare(EditorItem o1, EditorItem o2) {
			EditorNode en1 = o1.getEditorNode();
			EditorNode en2 = o2.getEditorNode();
			
			int revert = conf.isReverseSort() ? -1 : 1;
			
			int result = 0;
			
			if(conf.getSortBy().equals(SORTING_ALPH)){
				result = en1.toString().toUpperCase().compareTo(en2.toString().toUpperCase()) > 0 ? 1 : -1;
			} else if (conf.getSortBy().equals(SORTING_OPEN)){
				result = en1.getOpenIndex() > en2.getOpenIndex() ? 1 : -1;
			} else {
				result = en1.getSelectIndex() > en2.getSelectIndex() ? 1 : -1;
			}
			
			return result * revert;
		}
	}
	
	private class GroupComparator implements Comparator<Object> {
		
		private final MultiValueHashMap<Object, EditorItem> groupMap;
		private final EditorItemComparator itemComp = new EditorItemComparator();

		public GroupComparator(MultiValueHashMap<Object, EditorItem> groupMap) {
			this.groupMap = groupMap;
		}

		@Override
		public int compare(Object o1, Object o2) {
			
			if(o1 == lastClosedItems)
				return 1;
			if(o2 == lastClosedItems)
				return -1;
			
			if(!conf.getSortBy().equals(SORTING_ALPH)){
				return itemComp.compare(groupMap.getAll(o1).get(0), groupMap.getAll(o2).get(0));
			} else {
				int revert = conf.isReverseSort() ? -1 : 1;
				
				int result = 0;
				
				if (o1 instanceof File && o2 instanceof File){
					File f1 = (File) o1;
					File f2 = (File) o2;
					result = f1.getName().compareTo(f2.getName());
				} else {
					result = o1.toString().compareTo(o2.toString());
				}
				
				return result * revert;
			}

		}
		
	}
	
	public EditorItem getListItemByURL(URL url){
		for (EditorItem item : this.itemList) {
			if(item.getEditorNode().getUrl().equals(url)){
				return item;
			}
		}
		return null;
	}
	
	
	public void updateListItems(ArrayList<EditorItem> items, ArrayList<EditorItem> closedItems){
		this.closedItems = closedItems;
		updateListItems(items);
	}
	
	public final static Object lastClosedItems = new Object(){
		public String toString() {
			return "Last closed items";
		};
	};
	
	private class DragListener extends FileDragSource {

		private final EditorItem ei;
		public DragListener(EditorItem ei) {
			super(ei);
			this.ei = ei;
		}
		@Override
		public File[] getDragedFileList() {
			ArrayList<EditorItem> si = EditorPanel.this.getSelectedItem();
			si.remove(ei);
			si.add(ei);
			ArrayList<File> filesArr = new ArrayList<File>();
			
			for (EditorItem ei : si) {
				try {
					File f = ei.getEditorNode().getFile();
					if(f.exists()){
						filesArr.add(f);
					}
				} catch (URISyntaxException e) {
				}
			}
			return filesArr.toArray(new File[filesArr.size()]);
		}
		
	}
	
	
	

	
	private void updateListItemsAsTree(List<EditorItem> items){
		HashMap<File, EditorItem> itemByFile = new HashMap<File, EditorItem>();
		for (EditorItem item : items) {
			try {
				itemByFile.put(item.getEditorNode().getFile(), item);
			} catch (URISyntaxException e) {
				continue;
			}
		}
		ArrayList<File> fileList = new ArrayList<File>(itemByFile.keySet());
		Tree<FileNode> tree = new Tree<FileNode>(FileNode.getFileNodes(fileList));
		for (Tree<FileNode> topLevelTree : tree.getSubTrees()) {
			topLevelTree.setToRoot();
			this.addListItem(EditorGroupItem.convertFromTree(topLevelTree, itemByFile, this));
		}
		
	}
	
	
	
	private void updateListItems(ArrayList<EditorItem> items){
		items.removeAll(closedItems);
		this.removeAllItems();
		MultiValueHashMap<Object, EditorItem> groupMap = new MultiValueHashMap<Object, EditorItem>(true);
		List<EditorItem> itemList = items;
		Collections.sort(itemList, new EditorItemComparator());
		
		
		if(conf.getGroupBy().equals(GROUPING_FOLDER_TREE)){
			updateListItemsAsTree(items);
			this.updateUI();
			return;
		}
		
		for (EditorItem item : itemList) {
			new DragListener(item);
			Object key = "All files";
			if(conf.getGroupBy().equals(GROUPING_EXT)) {
				key = item.getEditorNode().getExtension();
				groupMap.put(key, item);
			} else if (conf.getGroupBy().equals(GROUPING_FOLDER)) {
				try {
					key = item.getEditorNode().getFile().getParentFile();
					groupMap.put(key, item);
				} catch (URISyntaxException e) {
					groupMap.put("", item);
				}
			} else {
				groupMap.put(key, item);
			}
		}
		for (int i = closedItems.size() - 1; i >= closedItems.size() - this.conf.getShowClosedFiles() && i >= 0; i--) {
			closedItems.get(i).setClosed(true);
			groupMap.put(lastClosedItems, closedItems.get(i));
		}

		removeAllItems();
		List<Object> keys = new ArrayList<Object>(groupMap.keySet());
		
		Collections.sort(keys, new GroupComparator(groupMap));
		
		for (Object key : keys) {
			ArrayList<EditorItem> group = groupMap.getAll(key);
			GroupNode groupNode = new GroupNode(key);
			EditorGroupItem groupItem = new EditorGroupItem(groupNode, group, 0, this);
			this.addListItem(groupItem);
		}
		this.updateUI();
	}
	
	private ArrayList<EditorGroupItem> parseFileHierarchy(MultiValueHashMap<Object, EditorItem> groupMap){
		ArrayList<EditorGroupItem> groups = new ArrayList<EditorGroupItem>();
		MultiValueHashMap<Object, EditorItem> parentGroupMap = new MultiValueHashMap<Object, EditorItem>(true);
		
		for (Object key : groupMap.keySet()) {
			if(key instanceof File){
				File fileKey = (File) key;
				
			} else {
				
			}
		}
		return groups;
	}
	
	@Override
	public void addListItem(EditorItem item) {
		super.addListItem(item);
		item.addSelectionListener(new EditorItemListener(item));
	}
	public final static int CLOSE_ASK_SAVE = 0;
	public final static int CLOSE_WITH_SAVE = 1;
	public final static int CLOSE_WO_SAVE = 2; 
	
	public ActionListener getCloseAL(){
		return getCloseAL(CLOSE_ASK_SAVE);
	}
	public ActionListener getCloseAL(final int askForSave){
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (EditorItem item : EditorPanel.this.getSelectedItem()) {
					if(!item.isClosed()){
						WSEditor wse = EditorPanel.this.spw.getEditorAccess(item.getEditorNode().getUrl(), StandalonePluginWorkspace.MAIN_EDITING_AREA);
						switch (askForSave) {
						case CLOSE_WITH_SAVE:
							wse.save();
							wse.close(true);
							break;
						case CLOSE_WO_SAVE:
							wse.close(false);
							break;
						default:
							wse.close(true);
							break;
						}
					}
//					EditorPanel.this.spw.getEditorAccess(item.getEditorNode().getUrl(), 0).cl
				}
			}
		};
	}
	
	public void setGroupBy(String groupBy){
		this.conf.setGroupBy(groupBy);
		updateListItems(this.itemList);
	}
	
	public void setSortBy(String sortBy, boolean revert){
		this.conf.setSortBy(sortBy);
		this.conf.setReverseSort(revert);
		updateListItems(this.itemList);
		this.repaint();
	}
	
	public void keyTyped(KeyEvent ke) {
		if(ke.getKeyChar() == KeyEvent.VK_ESCAPE){
			unselectItems();
		} else if(ke.getKeyChar() == KeyEvent.VK_DELETE){
			int closeMod = CLOSE_ASK_SAVE;
			if(ke.isShiftDown()){
				closeMod = CLOSE_WITH_SAVE;
			} else if(ke.isControlDown()){
				closeMod = CLOSE_WO_SAVE;
			}
			getCloseAL(closeMod).actionPerformed(null);
		} else if(ke.getKeyChar() == KeyEvent.VK_ENTER){
			for (EditorItem item : getSelectedItem()) {
				if(ke.isShiftDown()){
					try {
						String path = item.getEditorNode().getFile().getAbsolutePath();  
						Runtime.getRuntime().exec("explorer.exe /select," + path);
					} catch (IOException e) {
						this.spw.showErrorMessage(e.getLocalizedMessage());
					} catch (URISyntaxException e) {
						this.spw.showErrorMessage(e.getLocalizedMessage());
					}
				} else {
					this.spw.open(item.getEditorNode().getUrl());
				}
			}
		} else {
			super.keyTyped(ke);
		}
	}
//	public void addEditor(WSEditor editor){
//		EditorItem item = new EditorItem(new EditorNode(editor));
//		SwingUtil.addComponent(this, gbl, item, 0, counter++, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
//	}org.mozilla.universalchardet.UniversalDetector


	public void setShowClosedFiles(int showClosedFiles) {
		this.conf.setShowClosedFiles(showClosedFiles);
		updateListItems(this.itemList);
	}

	public Config getConfig(){
		return this.conf;
	}
	

}
