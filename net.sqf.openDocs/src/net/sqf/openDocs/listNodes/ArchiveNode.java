package net.sqf.openDocs.listNodes;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.Icon;

import net.sqf.view.utils.images.IconMap;
import ro.sync.exml.workspace.api.editor.WSEditor;

public class ArchiveNode extends EditorNode {
	Double[] filterFactors = new Double[]{1.0, 0.3, 0.3, 1.0};
	
	private static HashMap<String, Double[]> filtersByProtocol = new HashMap<String, Double[]>(); 
	
	static {
//		filtersByProtocol.put("jar", new Double[]{1.5, 1.5, 0.1, 1.0});
		filtersByProtocol.put("zip", new Double[]{0.3, 1.0, 1.0, 1.0});
		
	}

	public ArchiveNode(WSEditor editor, int openIndex, int selectIndex, URL fallbackUrl) {
		super(editor, openIndex, selectIndex, fallbackUrl);
		Icon ico;
		try {
			ico = IconMap.getSystemIconByExt(getExtension());
			ico = IconMap.filter(ico, getFilter());
		} catch (IOException e) {
			ico = null;
		}
		this.setIcon(ico);
	}
	
	private Double[] getFilter(){
		if(filtersByProtocol.containsKey(getProtocol())){
			return filtersByProtocol.get(getProtocol());
		}
		return filterFactors;
	}
	
	private String getProtocol(){
		return getUrl().getProtocol();
	}
	
	@Override
	public boolean isArchive() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public File getFile() throws URISyntaxException {
		URL url = getUrl();
		String path = url.toURI().toString().split("!")[0].replaceFirst(url.getProtocol() + ":", "");
		File file = new File(new URI(path));
		return file;
	}
	
	@Override
	public String toString() {
		File file;
		try {
			file = new File(new URI("file:" + getUrl().toURI().toString().split("!")[1]));
		} catch (URISyntaxException e) {
			file = new File(getUrl().toString());
		}
		String saveMarker = isSaved() ? "" : "*";
		return file.getName() + saveMarker;
	}
	
	@Override
	public Color getColor() {
		return new Color(0, 155, 155, 255);
	}
	
//	@Override
//	public Color getColor() {
//		return getColor(Color.BLACK);
//	}
//	public Color getColor(Color base) {
//		Double[] filter = getFilter();
//		int[] colorValues = new int[filter.length];
//		for (int i = 0; i < filter.length; i++) {
//			double dif = filter[i] - 1.0;
//			int baseVal = 0;
//			switch (i) {
//			case 0:
//				baseVal = base.getRed();
//				break;
//			case 1:
//				baseVal = base.getGreen();
//				break;
//			case 2:
//				baseVal = base.getBlue();
//				break;
//			case 3:
//				baseVal = base.getAlpha();
//				break;
//			default:
//				break;
//			}
//			
//			double newVal = baseVal + (255 * dif);
//			newVal = newVal < 0 ? 0 : newVal > 255 ? 255 : newVal;
//			
//			colorValues[i] = (int) newVal;
//		}
//		
//		return new Color(colorValues[0], colorValues[1], colorValues[2], colorValues[3]);
//	}
}
