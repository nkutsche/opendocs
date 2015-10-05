package net.sqf.openDocs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.sqf.openDocs.customizer.DocViewer;
import net.sqf.view.utils.images.IconMap;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class OpenDocsExtension implements WorkspaceAccessPluginExtension {

	public static IconMap ICONS;
	private DocViewer dv;

	@Override
	public boolean applicationClosing() {
		dv.saveOptions();
		return true;
	}

	@Override
	public void applicationStarted(
			StandalonePluginWorkspace pluginWorkspaceAccess) {
		// TODO Auto-generated method stub

		// WSOptionsStorage options = pluginWorkspaceAccess.getOptionsStorage();

		try {
			IconMap.BASE_DIR = OpenDocsPlugin.descriptor.getBaseDir();
			ICONS = new IconMap(new File("icons/diagona_my.gif"));
			
			dv = new DocViewer(pluginWorkspaceAccess);
			pluginWorkspaceAccess.addViewComponentCustomizer(dv);
			pluginWorkspaceAccess.addMenuBarCustomizer(dv);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
