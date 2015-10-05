package net.sqf.openDocs;

import ro.sync.exml.plugin.Plugin;
import ro.sync.exml.plugin.PluginDescriptor;

public class OpenDocsPlugin extends Plugin {

	private static OpenDocsPlugin instance = null;
	protected static PluginDescriptor descriptor;

	public OpenDocsPlugin(PluginDescriptor descriptor) {
		super(descriptor);
		OpenDocsPlugin.descriptor = descriptor;
		if (instance != null) {
			throw new IllegalStateException("Already instantiated!");
		}
		instance = this;
	}
	
	public static OpenDocsPlugin getInstance() {
		return instance;
	}
	
}
