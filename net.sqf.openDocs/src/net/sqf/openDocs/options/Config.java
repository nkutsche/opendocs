package net.sqf.openDocs.options;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import net.sqf.openDocs.OpenDocsPlugin;
import net.sqf.openDocs.customizer.EditorPanel;
import net.sqf.stringUtils.TextSource;
import net.sqf.xmlUtils.staxParser.StringNode;

import org.xml.sax.SAXException;

import ro.sync.exml.workspace.api.options.WSOptionsStorage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class Config {
	
	private String groupBy = EditorPanel.GROUPING_NONE;
	private String sortBy = EditorPanel.SORTING_ALPH;
	private boolean reverseSort = false;
	private int showClosedFiles = 0;
	
	private static File configFile = new File(OpenDocsPlugin.getInstance().getDescriptor().getBaseDir(), "conf/config.cfg");
	
	private Config(){
	}
	
	private Config(String groupBy, String sortBy, boolean reverseSort, int showClosedFiles){
		this.groupBy = groupBy;
		this.sortBy = sortBy;
		this.setReverseSort(reverseSort);
		this.showClosedFiles = showClosedFiles;
	}

	public static Config readConfig(StandalonePluginWorkspace spw){
		WSOptionsStorage storage = spw.getOptionsStorage();
		return readConfig(storage.getOption(STORAGE_OPTION_KEY, readConfig().toString()));
	}
	public static StringNode readWorkingSetOptions(StandalonePluginWorkspace spw){
		WSOptionsStorage storage = spw.getOptionsStorage();
		String text =  storage.getOption(STORAGE_OPTION_KEY_WORKING_SETS, null);
		if(text == null)
			return null;
		File tempF;
		try {
			tempF = File.createTempFile(STORAGE_OPTION_KEY_WORKING_SETS, "xml");
			return new StringNode(TextSource.createVirtualTextSource(tempF).setData(text));
		} catch (IOException e) {
		} catch (SAXException e) {
		} catch (XMLStreamException e) {
		}
		return null;
		
	}
	public static Config readConfig(){
		if(!configFile.exists()){
			return new Config();
		}
		try {
			TextSource ts = TextSource.readTextFile(configFile);
			return readConfig(ts.toString());
		} catch (IOException e) {
			return new Config();
		}
	}
	
	private static Config readConfig(String configContent){
		String[] fields = configContent.split(";");
		if(fields.length == 4){
			return new Config(fields[0], fields[1], Boolean.parseBoolean(fields[2]), Integer.parseInt(fields[3]));
		} else {
			return new Config();
		}
	}
	
	public static File getAbout(File baseDir){
		return new File(baseDir, "conf/about.htm");
	}
	
	public static String readAbout(File descrpitor){
		TextSource ts;
		try {
			File aboutFile = getAbout(descrpitor);
			ts = TextSource.readTextFile(aboutFile);
			return ts.toString();
		} catch (IOException e) {
			return "<html></html>";
		}
	}
	
	public static void saveConfig(Config conf){
		if(!configFile.getParentFile().exists()){
			configFile.getParentFile().mkdirs();
		}
		TextSource ts = TextSource.createVirtualTextSource(configFile);
		ts.setData(conf.toString());
		try {
			TextSource.write(configFile, ts);
		} catch (IOException e) {
		}
	}
	private static final String STORAGE_OPTION_KEY = "OpenDocs.Config";
	public static final String STORAGE_OPTION_KEY_WORKING_SETS = "OpenDocs.Config.WORKING_SET";
	public static void saveConfig(StandalonePluginWorkspace spw, Config conf){
		spw.getOptionsStorage().setOption(STORAGE_OPTION_KEY, conf.toString());
	}
	public static void saveConfig(StandalonePluginWorkspace spw, String key, String content){
		spw.getOptionsStorage().setOption(key, content);
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public int getShowClosedFiles() {
		return showClosedFiles;
	}

	public void setShowClosedFiles(int showClosedFiles) {
		this.showClosedFiles = showClosedFiles;
	}
	
	@Override
	public String toString() {
		return this.getGroupBy() + ";" + this.getSortBy() + ";" +  (this.isReverseSort() ? "true" : "false") + ";" + this.getShowClosedFiles();
	}

	public boolean isReverseSort() {
		return reverseSort;
	}

	public void setReverseSort(boolean reverseSort) {
		this.reverseSort = reverseSort;
	}
}
