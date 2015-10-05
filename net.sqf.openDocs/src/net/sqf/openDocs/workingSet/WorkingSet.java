package net.sqf.openDocs.workingSet;

import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Icon;

import net.sqf.openDocs.buttons.AbstractDropDownButton;
import net.sqf.openDocs.customizer.EditorItem;
import net.sqf.openDocs.customizer.EditorPanel;

import org.apache.batik.ext.swing.GridBagConstants;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class WorkingSet  extends AbstractDropDownButton {
	private static final long serialVersionUID = 1L;

	// private final ArrayList<EditorItem> items = new ArrayList<EditorItem>();
	private final HashMap<URL, EditorItem> itemsByUrl = new HashMap<URL, EditorItem>();

	private String title;
	private String description;



	private final RenameDialog rnd;

	private final EditorPanel ePanel;

	private Icon icon = null;

	private int iconIndex;
	

	public WorkingSet(ArrayList<EditorItem> items,
			WorkingSetArea workingSetArea, EditorPanel ePanel, String name,
			String desc, int iconIdx) {
		super(ePanel, GridBagConstants.NORTH);
		this.ePanel = ePanel;
		this.addToWorkingSet(items);
		this.setTitle(name);
		this.iconIndex = iconIdx;
		this.setIcon(RenameDialog.ICONS[iconIdx].getIcon());
		this.description = desc;
		this.setItemSupplier(new WorkingSetMenu(this, workingSetArea, ePanel.getSPW()));
		updateTooltip();
		
		this.rnd = new RenameDialog(ePanel.getSPW(), this);
	}
	public WorkingSet(ArrayList<EditorItem> items,
			WorkingSetArea workingSetArea, EditorPanel ePanel, String name,
			String desc, int iconIdx, boolean showRenameDialog) {
		this(items, workingSetArea, ePanel, name, desc, iconIdx);
		rnd.setVisible(showRenameDialog);
	}

	public String getDescription(){
		return description;
	}
	public void setDescription(String desc){
		this.description = desc;
		updateTooltip();
	}
	protected void setTitle(String title){
		this.title = title;
		super.setTitle(title);
	}
	

	private void updateTooltip() {
		this.setToolTipText(description + "(" + this.itemsByUrl.size() + " files)");
	}

	public void addToWorkingSet(ArrayList<EditorItem> items) {
		for (EditorItem item : items) {
			addToWorkingSet(item);
		}
	}

	public void addToWorkingSet(EditorItem item) {
		this.itemsByUrl.put(item.getEditorNode().getUrl(), item);
		updateTooltip();
		editorPanel.refresh();
	}

	@Override
	public String toString() {
		return title.equals("") ? description : title;
	}
	
	public String forConfig(){
		String xml = "";
		xml += "<ws>";
		xml += "<name>" + this.title + "</name>";
		xml += "<desc>" + this.description + "</desc>";
		xml += "<icon>" + this.iconIndex + "</icon>";
		for (URL url : this.itemsByUrl.keySet()) {
			xml += "<url>" + url.toString() + "</url>";
		}
		xml += "</ws>";
		return xml;
	}

	protected void openInEditor() {
		for (URL url : this.itemsByUrl.keySet()) {
			ePanel.getSPW().open(url);
		}
	}
	
	protected void closeInEditor() {
		for (URL url : this.itemsByUrl.keySet()) {
			ePanel.getSPW().close(url);
		}
	}
	
	protected void closeOtherInEditor() {
		URL[] urls = ePanel.getSPW().getAllEditorLocations(StandalonePluginWorkspace.MAIN_EDITING_AREA);
		for (URL url : urls) {
			if(!itemsByUrl.containsKey(url)){
				ePanel.getSPW().close(url);
			}
		}
	}
	
	protected ArrayList<URL> getURLs(){
		return new ArrayList<URL>(this.itemsByUrl.keySet());
	}
	
	protected EditorItem itemByUrl(URL url) {
		if(this.itemsByUrl.containsKey(url)){
			return this.itemsByUrl.get(url);
		}
		return null;
	}
	
	protected ArrayList<EditorItem> getItems(){
		ArrayList<EditorItem> items = new ArrayList<EditorItem>();
		for (URL url : getURLs()) {
			items.add(itemByUrl(url));
		}
		return items;
	}

	protected void delete(URL url) {
		this.itemsByUrl.remove(url);
		this.updateTooltip();
		editorPanel.refresh();
	}

	@Override
	public void action(MouseEvent me) {
		openInEditor();
	}
	
	protected void rename(){
		this.rnd.setVisible(true);
	}
	public void setIcon(Icon icon){
		this.icon = icon;
		super.setIcon(icon);
	}
	public Icon getIcon() {
		return this.icon;
	}

	protected void setIconIndex(int selectedIndex) {
		this.iconIndex = selectedIndex;
	}

	protected String getTitle() {
		return this.title;
	}

	protected int getIconIndex() {
		return this.iconIndex;
	}

	public boolean contains(URL url) {
		return this.itemsByUrl.containsKey(url);
	}
	
	
}
