package net.sqf.openDocs.customizer;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.buttons.EditorMenu;
import net.sqf.openDocs.listNodes.EditorNode;
import net.sqf.openDocs.listNodes.NodeFactory;
import net.sqf.openDocs.options.AboutOpenDocsMenuItem;
import net.sqf.openDocs.options.Config;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ViewComponentCustomizer;
import ro.sync.exml.workspace.api.standalone.ViewInfo;
import de.janosch.commons.swing.util.SwingUtil;

public class DocViewer extends WSEditorChangeListener implements
		ViewComponentCustomizer, KeyListener, MenuBarCustomizer {

	private final StandalonePluginWorkspace spw;

	private final ArrayList<URL> editors = new ArrayList<URL>();

	private final HashMap<URL, Integer> openOrderMap = new HashMap<URL, Integer>();

	private final JComponent panel = new JPanel();

	private final GridBagLayout gbl;

	private EditorPanel ePanel;
	
	private final HashMap<URL, WSEditor> editorByUrl = new HashMap<URL, WSEditor>();
	private final ArrayList<WSEditor> closedEditor = new ArrayList<WSEditor>();


	public DocViewer(StandalonePluginWorkspace spw) {
		this.spw = spw;
		spw.addEditorChangeListener(this,
				StandalonePluginWorkspace.MAIN_EDITING_AREA);
		gbl = new GridBagLayout();
		panel.setLayout(gbl);
		panel.setBackground(Color.WHITE);
		ePanel = new EditorPanel(spw);
		SwingUtil.addComponent(panel, gbl, ePanel, 0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);

	}

	@Override
	public void customizeView(ViewInfo vi) {
		if (vi.getViewID().equals("OpenDocs")) {
			vi.setTitle("OpenDocs");
			vi.setIcon(OpenDocsExtension.ICONS.getIcon(13, 20));
			vi.setComponent(panel);
			updateEditors();
			
		}
	}

	private static int openCounter = 0;
	
	@Override
	public void editorPageChanged(URL path) {
		super.editorPageChanged(path);
	}
	
//	 && path.getProtocol().equals("file")
	@Override
	public void editorOpened(URL path) {
		super.editorOpened(path);
		if (path != null) {
			if(this.editorByUrl.containsKey(path)){
				this.closedEditor.remove(this.editorByUrl.get(path));
			}
			WSEditor editor = spw.getEditorAccess(path, StandalonePluginWorkspace.MAIN_EDITING_AREA);
			this.openOrderMap.put(path, openCounter++);
			this.editorByUrl.put(path, editor);
		}
		// editors.add(path);
		// updateEditors(path);
	}

	@Override
	public void editorSelected(URL path) {
		super.editorSelected(path);
		if (path != null) {
			if(this.editorByUrl.containsKey(path)){
				this.closedEditor.remove(this.editorByUrl.get(path));
			}
			editors.remove(path);
			editors.add(path);
			updateEditors();
		}
	}

	@Override
	public void editorClosed(URL path) {
		// TODO Auto-generated method stub
		super.editorClosed(path);
		if (path != null) {
			editors.remove(path);
			openOrderMap.remove(path);
			if(editorByUrl.containsKey(path)){
				WSEditor editor = editorByUrl.get(path);
				this.closedEditor.add(editor);
				if(closedEditor.size() > 50){
					closedEditor.remove(0);
				}
			}
			updateEditors();
		}
	}

	private void updateEditors() {
		ePanel.removeAllItems();
		ArrayList<EditorItem> items = new ArrayList<EditorItem>();
		int i = 0;
		for (URL url : editors) {
			if (url != null) {
				WSEditor editor = spw.getEditorAccess(url, StandalonePluginWorkspace.MAIN_EDITING_AREA);
				boolean isBold =  editor == spw.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
				if (editor != null) {
					int openCounter = openOrderMap.containsKey(url) ? this.openOrderMap
							.get(url) : DocViewer.openCounter;
					EditorNode en = NodeFactory.createEditorNode(editor, openCounter, i++);
//					EditorItem ei = EditorItem.createEditorItem(en, ePanel, spw, editors.size() < i + 1);
					EditorItem ei = EditorItem.createEditorItem(en, ePanel, spw, isBold);
//					EditorItem ei = new EditorItem(en, editors.size() < i + 1);
//					ei.implementPopupMenu(new EditorMenu[]{
//							new EditorMenu(spw, ei, ePanel),
//							new EditorMenu(spw, ei, ePanel, EditorItem.SHIFT_DOWN),
//							new EditorMenu(spw, ei, ePanel, EditorItem.CTRL_DOWN)});
					items.add(ei);
				}
			}
		}
		ArrayList<EditorItem> closedItems = new ArrayList<EditorItem>();
		int j = 0;
		for (WSEditor editor : this.closedEditor) {
			if (editor != null) {
				EditorNode en = NodeFactory.createEditorNode(editor, j++, j++);
				EditorItem ei = new EditorItem(en);
				ei.implementPopupMenu(new EditorMenu(spw, ei, ePanel));
				closedItems.add(ei);
			}
		}
		ePanel.updateListItems(items, closedItems);
		ePanel.implementShortCut(panel);
		ePanel.repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		updateEditors();
	}
	
	
	public void saveOptions(){
		if(ePanel != null){
			Config.saveConfig(this.spw, ePanel.getConfig());
			Config.saveConfig(this.spw, Config.STORAGE_OPTION_KEY_WORKING_SETS, ePanel.getWorkingSetConfig());
		}
	}

	@Override
	public void customizeMainMenu(JMenuBar menuBar) {
		Object frameObj = this.spw.getParentFrame();
		JFrame frame =  frameObj instanceof JFrame ? (JFrame) frameObj : null;
		JMenuItem item = new AboutOpenDocsMenuItem(frame);
		
		menuBar.getMenu(menuBar.getMenuCount() - 1).add(item);
//		menuBar.getHelpMenu().add(new JMenuItem("About DocViewer"));
	}
	
}
