package net.sqf.openDocs.buttons;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.customizer.EditorItem;
import net.sqf.openDocs.customizer.EditorPanel;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class EditorMenu extends JPopupMenu {
	private static final long serialVersionUID = -3079839709428698837L;
	private final EditorPanel ePanel;

	private final JMenuItem closeDefault;
	private final JMenuItem closeForMod;
	
	private final JMenuItem save;
	
	
	private JMenuItem select;
	private JMenuItem showInExpl;
	
	public EditorMenu(final StandalonePluginWorkspace spw, final EditorItem item, final EditorPanel ePanel) {
		this(spw, item, ePanel, EditorItem.NO_MODIFIER);
	}
	
	public EditorMenu(final StandalonePluginWorkspace spw, final EditorItem item, final EditorPanel ePanel, int modifier) {
		this.ePanel = ePanel;
		
		closeDefault = new JMenuItem("Close", OpenDocsExtension.ICONS.getIcon(0, 10));
		closeDefault.addActionListener(ePanel.getCloseAL());
		
		if(modifier == EditorItem.CTRL_DOWN){
			closeForMod = new JMenuItem("Close without saving", OpenDocsExtension.ICONS.getIcon(8, 22));
			closeForMod.addActionListener(ePanel.getCloseAL(EditorPanel.CLOSE_WO_SAVE));
		} else if (modifier == EditorItem.SHIFT_DOWN) {
			closeForMod = new JMenuItem("Save + close", OpenDocsExtension.ICONS.getIcon(10, 22));
			closeForMod.addActionListener(ePanel.getCloseAL(EditorPanel.CLOSE_WITH_SAVE));
		} else {
			closeForMod = closeDefault;
		}
		
		
		
		
		
		save = new JMenuItem("Save", OpenDocsExtension.ICONS.getIcon(10, 9));
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for(EditorItem item: ePanel.getSelectedItem()){
					URL url = item.getEditorNode().getUrl();
					WSEditor editor = spw.getEditorAccess(url, StandalonePluginWorkspace.MAIN_EDITING_AREA);
					if(!item.isClosed()){
						editor.save();
					}
				}
			}
		});

		select = new JMenuItem("Show in oXygen", OpenDocsExtension.ICONS.getIcon(12, 6));
		select.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(EditorItem item: ePanel.getSelectedItem()){
					URL url = item.getEditorNode().getUrl();
					spw.open(url);
				}
			}
		});
		
		showInExpl = new JMenuItem("Show in explorer", UIManager.getIcon("Tree.openIcon"));
		showInExpl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String path = item.getEditorNode().getFile().getAbsolutePath();  
					Runtime.getRuntime().exec("explorer.exe /select," + path);
				} catch (IOException e1) {
					spw.showErrorMessage(e1.getLocalizedMessage());
				} catch (URISyntaxException e1) {
					spw.showErrorMessage(e1.getLocalizedMessage());
				}
			}
		});
		
		
		
		
	}
	
	
	public void show(Component c, int x, int y) {
		this.removeAll();

		ArrayList<EditorItem> items = ePanel.getSelectedItem();
		boolean isModified = false;
		boolean isOneOpen = false;
		for (EditorItem item : items) {
			isModified = item.getEditorNode().isSaved() ? isModified : true;
			isOneOpen = item.isClosed() ? isOneOpen : true; 
		}
		
		save.setEnabled(isModified && isOneOpen);
		if(isOneOpen){
			this.add(isModified ? closeForMod : closeDefault);
		}
		this.add(save);
		this.add(select);
		this.add(showInExpl);
		super.show(c, x, y);
	}
}
