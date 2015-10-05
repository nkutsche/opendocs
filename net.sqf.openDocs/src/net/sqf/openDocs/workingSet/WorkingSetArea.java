package net.sqf.openDocs.workingSet;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.xml.xpath.XPathExpressionException;

import net.sqf.openDocs.customizer.EditorItem;
import net.sqf.openDocs.customizer.EditorPanel;
import net.sqf.view.utils.swing.FileDragSource;
import net.sqf.view.utils.swing.FileDropTarget;
import net.sqf.view.utils.swing.WrapLayout;
import net.sqf.xmlUtils.staxParser.StringNode;
import net.sqf.xmlUtils.xpath.XPathReader;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class WorkingSetArea extends JPanel implements ComponentListener {
	private static final long serialVersionUID = 1L;
	private final ArrayList<WorkingSet> sets = new ArrayList<WorkingSet>();
	private EditorPanel ePanel;
	private JPanel minSizePlaceHolder;
	
	public WorkingSetArea(EditorPanel panel, StringNode config) {
		this.setLayout(new WrapLayout(FlowLayout.LEFT));
		this.addComponentListener(this);
		this.ePanel = panel;
		this.addMouseListener(new WorkingSetAreaMenu(ePanel, this));

		this.minSizePlaceHolder = new JPanel();
		final Dimension minSize = minSizePlaceHolder.getMinimumSize();
		minSize.height = 20;
		minSize.width = 25;
		minSizePlaceHolder.setPreferredSize(minSize);
		minSizePlaceHolder.setMinimumSize(minSize);
		
		new FileDragSource(minSizePlaceHolder) {
			
			@Override
			public File[] getDragedFileList() {
				return new File[]{new File("build.xml")};
			}
		};
//		minSizePlaceHolder.setOpaque(true);
//		minSizePlaceHolder.setVisible(true);
		
		
		if(config != null){
			NodeList wsNl;
			try {
				wsNl = config.getNodeSet("/wsa/ws");
				for (int i = 0; i < wsNl.getLength(); i++) {
					addWorkingSet(wsNl.item(i));
				}
			} catch (XPathExpressionException e) {
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		updateWs();
		
		new FileDropTarget(this) {
			
			@Override
			public void receiveFiles(List<File> files) {
				addWorkingSet(files);
			}
		};
		
	}
	
	
//	private class DragListener extends FileDragSource {
//
//		private final WorkingSet ws;
//		public DragListener(WorkingSet ws) {
//			super(ws);
//			this.ws = ws;
//		}
//		@Override
//		public File[] getDragedFileList() {
//			ArrayList<File> filesArr = new ArrayList<File>();
//			
//			for (EditorItem ei : ws.getItems()) {
//				try {
//					File f = ei.getEditorNode().getFile();
//					if(f.exists()){
//						filesArr.add(f);
//					}
//				} catch (URISyntaxException e) {
//				}
//			}
//			return filesArr.toArray(new File[filesArr.size()]);
//		}
//		
//	}
	
	private void addWorkingSet(List<File> filesOrDirs){
		ArrayList<File> files = new ArrayList<File>();
		
		for (File fOd : filesOrDirs) {
			if(fOd.isFile()){
				files.add(fOd);
			} else if(fOd.isDirectory()){
				files.addAll(Arrays.asList(fOd.listFiles()));
			}
		}
		ArrayList<EditorItem> items = new ArrayList<EditorItem>();
		for (File file : files) {
			URL url;
			try {
				url = file.toURI().toURL();
				items.add(EditorItem.createDummyEditorItem(url));
			} catch (MalformedURLException e) {
				continue;
			}
		}
		addWorkingSet(items);
	}

	private XPathReader xpr = new XPathReader();
	
	private void addWorkingSet(Node wsNode) throws XPathExpressionException, MalformedURLException{
		ArrayList<EditorItem> items = new ArrayList<EditorItem>();
		String name = xpr.getBoolean("name/text()", wsNode) ? xpr.getNode("name/text()", wsNode).getNodeValue() : "";
		String desc = xpr.getBoolean("desc/text()", wsNode) ? xpr.getNode("desc/text()", wsNode).getNodeValue() : "";
		int iconIdx = Integer.parseInt(xpr.getBoolean("number(icon/text()) != 'NaN'", wsNode) ? xpr.getNode("icon/text()", wsNode).getNodeValue() : "");
		
		NodeList urls = xpr.getNodeSet("./url/text()", wsNode);
		for (int i = 0; i < urls.getLength(); i++) {
			URL url = new URL(urls.item(i).getNodeValue());
			items.add(EditorItem.createDummyEditorItem(url));
		}
		addWorkingSet(items, name, desc, iconIdx);
	}
	
	public void removeWorkingSet(WorkingSet ws){
		this.sets.remove(ws);
		updateWs();
	}

	private void addWorkingSet(ArrayList<EditorItem> items, String name, String desc, int icon){
		WorkingSet ws = new WorkingSet(items, this, ePanel, name, desc, icon);
//		new DragListener(ws);
		this.sets.add(ws);
		updateWs();
	}
	

	private static int counter = 1;
	
	public void addWorkingSet(ArrayList<EditorItem> items){
		
		ArrayList<EditorItem> newItems = new ArrayList<EditorItem>();
		for (EditorItem item : items) {
			if(!item.getEditorNode().exists()){
				newItems.add(item);
			}
		}
		if(newItems.size() > 0){
			String urls = "";
			for (EditorItem item : newItems) {
				urls += "- " + item.getEditorNode().getUrl().toString() +"\n";
			}
			int result = ePanel.getSPW().showConfirmDialog("Save non existing files", "These files do not exist yet: \n" +
					urls +
					"To store in Working sets, files must exist.\nDo you want to save this files now?", new String[]{"Save this files", "Use the other files", "Xancel"}, new int[]{0, 1, 2});
			switch (result) {
// save all non existing files
			case 0:
				for (EditorItem item : newItems) {
					ePanel.getSPW().getEditorAccess(item.getEditorNode().getUrl(), StandalonePluginWorkspace.MAIN_EDITING_AREA).save();
					if(!item.getEditorNode().exists()){
						return;
					}
				}
				break;
//				use the other files for Working set
			case 1:
				for (EditorItem item : newItems) {
					items.remove(item);
				}
				break;
//				cancel:
			case 2:
				return;
			default:
				break;
			}
			
		}
		
		String title = "WS " + counter;
		String desc = "Working set " + counter++;
		while (getWsByTitle(title) != null) {
			title = "WS " + counter;
			desc = "Working set " + counter++;
		}
		
		WorkingSet ws = new WorkingSet(items, this, ePanel, title, desc, 0, true);
//		new DragListener(ws);
		this.sets.add(ws);
		updateWs();
	}
	
	public WorkingSet getWsByTitle(String title){
		for (WorkingSet ws : this.sets) {
			if(ws.getTitle().equals(title)){
				return ws;
			}
		}
		return null;
	}
	
	private void updateWs(){
		this.removeAll();
		if(sets.size() > 0){
			for (WorkingSet set : sets) {
				this.add(set);
			}
		} else {
			this.add(this.minSizePlaceHolder);
		}
		this.updateUI();
		ePanel.refresh();
		this.ePanel.updateUI();
	}
	
	public String forConfig(){
		String xml = "";
		xml += "<wsa>";
		for (WorkingSet set : this.sets) {
			xml += set.forConfig();
		}
		xml += "</wsa>";
		return xml;
	}

	public ArrayList<WorkingSet> getWorkingSets() {
		return new ArrayList<WorkingSet>(this.sets);
	}

	public boolean groupMenuPerFile() {
		return true;
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {}

	@Override
	public void componentMoved(ComponentEvent arg0) {}

	@Override
	public void componentResized(ComponentEvent arg0) {
		this.updateUI();
	}

	@Override
	public void componentShown(ComponentEvent arg0) {}
	
	public ArrayList<WorkingSet> getWorkingSet(URL url){
		ArrayList<WorkingSet> sets = new ArrayList<WorkingSet>();
		for (WorkingSet set : this.sets) {
			if(set.contains(url))
				sets.add(set);
		}
		return sets;
	} 
}
