package net.sqf.openDocs.buttons;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.customizer.EditorItem;
import net.sqf.openDocs.customizer.EditorPanel;
import net.sqf.openDocs.workingSet.WorkingSet;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.util.editorvars.EditorVariables;

public class EditorMenu extends JPopupMenu {
	private static final long serialVersionUID = -3079839709428698837L;
	private final EditorPanel ePanel;

	private final JMenuItem closeDefault;
	private final JMenuItem closeForMod;

	private final JMenuItem save;

	private JMenuItem select;
	private JMenuItem showInExpl;
	private JMenuItem exec = null;
	private JMenuItem cmd = null;
	private final StandalonePluginWorkspace spw;
	private JMenuItem addWorkingSet;
	private JMenu addToWs;

	public EditorMenu(final StandalonePluginWorkspace spw,
			final EditorItem item, final EditorPanel ePanel) {
		this(spw, item, ePanel, EditorItem.NO_MODIFIER);
	}

	public EditorMenu(final StandalonePluginWorkspace spw,
			final EditorItem item, final EditorPanel ePanel, int modifier) {
		this.spw = spw;
		this.ePanel = ePanel;

		closeDefault = new JMenuItem("Close", OpenDocsExtension.ICONS.getIcon(
				0, 10));
		closeDefault.addActionListener(ePanel.getCloseAL());

		if (modifier == EditorItem.CTRL_DOWN) {
			closeForMod = new JMenuItem("Close without saving",
					OpenDocsExtension.ICONS.getIcon(8, 22));
			closeForMod.addActionListener(ePanel
					.getCloseAL(EditorPanel.CLOSE_WO_SAVE));
		} else if (modifier == EditorItem.SHIFT_DOWN) {
			closeForMod = new JMenuItem("Save + close",
					OpenDocsExtension.ICONS.getIcon(10, 22));
			closeForMod.addActionListener(ePanel
					.getCloseAL(EditorPanel.CLOSE_WITH_SAVE));
		} else {
			closeForMod = closeDefault;
		}

		save = new JMenuItem("Save", OpenDocsExtension.ICONS.getIcon(10, 9));
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (EditorItem item : ePanel.getSelectedItem()) {
					URL url = item.getEditorNode().getUrl();
					WSEditor editor = spw.getEditorAccess(url,
							StandalonePluginWorkspace.MAIN_EDITING_AREA);
					if (!item.isClosed()) {
						editor.save();
					}
				}
			}
		});

		select = new JMenuItem("Show in oXygen",
				OpenDocsExtension.ICONS.getIcon(12, 6));
		select.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (EditorItem item : ePanel.getSelectedItem()) {
					URL url = item.getEditorNode().getUrl();
					spw.open(url);
				}
			}
		});
		

		showInExpl = new JMenuItem("Show in explorer",
				UIManager.getIcon("Tree.openIcon"));
		
		File file = null;
		try {
			file = item.getEditorNode().getFile();
		} catch (URISyntaxException e2) {
		} 
		final File ffile = file;
		
		showInExpl.setEnabled(file != null);

		showInExpl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Runtime.getRuntime().exec("explorer.exe /select," + ffile.getAbsolutePath());
				} catch (IOException e1) {
					spw.showErrorMessage(e1.getLocalizedMessage());
				}
			}
		});
		showInExpl.setEnabled(item.getEditorNode().exists());
		if(ffile != null){
			cmd = new JMenuItem("Open cmd in its directory");
			cmd.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					File parent = ffile.getParentFile();
					try {
						Runtime.getRuntime().exec("cmd.exe /c start ", null, parent);
					} catch (IOException e1) {
						spw.showErrorMessage(e1.getLocalizedMessage());
					}
				}
			});
		}
		if(item.getEditorNode().getExtension().equals("bat") && ffile != null){
			exec = new JMenuItem("Run script");
			exec.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						File parent = ffile.getParentFile();
						Runtime.getRuntime().exec("cmd.exe /c start " + ffile.getAbsolutePath(), null, parent);
					} catch (IOException e1) {
						spw.showErrorMessage(e1.getLocalizedMessage());
					}
				}
			});
		}
		
		this.addWorkingSet = new JMenuItem("Save as Working set",
				OpenDocsExtension.ICONS.getIcon(18, 16));
		addWorkingSet.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ePanel.saveWorkingSet();
			}
		});
		this.addToWs = new JMenu("Add to Working set");
		addToWs.setIcon(OpenDocsExtension.ICONS.getIcon(18, 16));
	}

	public void show(Component c, int x, int y) {
		this.removeAll();

		final ArrayList<EditorItem> items = ePanel.getSelectedItem();

		boolean isOneModified = false;
		boolean isOneOpen = false;
		for (EditorItem item : items) {
			isOneModified = item.getEditorNode().isSaved() ? isOneModified
					: true;
			isOneOpen = item.isClosed() ? isOneOpen : true;
		}

		save.setEnabled(isOneModified && isOneOpen);
		if (isOneOpen) {
			this.add(isOneModified ? closeForMod : closeDefault);
		}
		this.add(save);
		this.add(select);
		this.add(showInExpl);
		
		ArrayList<WorkingSet> workingSets = ePanel.getWorkingSets();
		if(workingSets.size() > 0){
			addToWs.removeAll();
			addWorkingSet.setText("New Working set");
			addWorkingSet.setIcon(OpenDocsExtension.ICONS.getIcon(4, 10));
			addToWs.add(addWorkingSet);
			for (final WorkingSet ws : workingSets) {
				JMenuItem wsItem = new JMenuItem(ws.toString(), ws.getIcon());
				wsItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						ws.addToWorkingSet(items);
					}
				});
				addToWs.add(wsItem);
			}
			
			this.add(addToWs);
		} else {
			this.add(addWorkingSet);
		}
		
		addCompareMenuItem(items);
		if(exec != null){
			this.add(exec);
		}
		if(cmd != null){
			this.add(cmd);
		}
		super.show(c, x, y);
	}

	private EditorItem checkForCompare(EditorItem item) {
		if (item == null)
			return item;
		if (!item.getEditorNode().exists())
			return null;
		return item;

	}

	private void addCompareMenuItem(ArrayList<EditorItem> items) {
		JMenuItem menu = new JMenuItem(items.size() == 2 ? "Compare files"
				: "Open in XML Diff");

		if (items.size() <= 2) {

			EditorItem[] itemsArr = items.toArray(new EditorItem[2]);

			EditorItem item1 = checkForCompare(itemsArr[0]);
			EditorItem item2 = checkForCompare(itemsArr[1]);

			final EditorItem itemA = item1;
			final EditorItem itemB = item2;

			if (itemA == null && itemB == null) {
				return;
			}

			URL baseUrl = (itemA == null ? itemB : itemA).getEditorNode()
					.getUrl();
			String installDir = spw.getUtilAccess().expandEditorVariables(
					EditorVariables.OXYGEN_INSTALL_DIR, baseUrl);
			File exe = new File(installDir + File.separator + "diffFiles.exe");
			File bat = new File(installDir + File.separator + "diffFiles.bat");

			final File call = exe.exists() ? exe : bat;

			Icon ico = null;

			if (exe.exists()) {
				ico = FileSystemView.getFileSystemView().getSystemIcon(exe);
			} else {
				ico = OpenDocsExtension.ICONS.getIcon(10, 6);
			}

			menu.setIcon(ico);

			menu.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						String path = itemA == null ? "" : itemA
								.getEditorNode().getUrl().toURI().toString();
						String path2 = itemB == null ? "" : itemB
								.getEditorNode().getUrl().toURI().toString();

						Runtime.getRuntime().exec(
								call.getAbsolutePath() + " " + path + " "
										+ path2);
					} catch (URISyntaxException e) {
						spw.showErrorMessage(e.getLocalizedMessage());
					} catch (IOException e) {
						spw.showErrorMessage(e.getLocalizedMessage());
					}
				}
			});
			if(call.exists()){
				this.add(menu);
			}
		}
	}
}
