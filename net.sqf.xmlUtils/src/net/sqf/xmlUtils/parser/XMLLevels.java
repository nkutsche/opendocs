package net.sqf.xmlUtils.parser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;

import net.sqf.stringUtils.TextSource;
import net.sqf.xmlUtils.staxParser.NodeInfo;
import net.sqf.xmlUtils.staxParser.StringNode;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.ctc.wstx.io.CharsetNames;

public class XMLLevels {
	
	private StringNode doc;

	public XMLLevels(URI uri) throws IOException, SAXException, XMLStreamException{
		TextSource ts = TextSource.readTextFile(new File(uri));
		doc = new StringNode(ts);

	}
	
	
	public XMLLevels(String xmlString, String baseUri) throws IOException, SAXException, XMLStreamException{
		InputStream is = new ByteArrayInputStream(xmlString.getBytes());
		TextSource ts = TextSource.readTextFile(is, CharsetNames.CS_UTF8, new File(baseUri));
		doc = new StringNode(ts);
//		doc = new PositionalXMLReader().readXML(xmlString, baseUri);
	}
	
	private HashMap<Integer, Integer> levelMap = new HashMap<Integer, Integer>();
	public String[] getLevels() {
		int maxLine = 0;
		try {
			ArrayList<NodeInfo> nodes = doc.getNodeSetInfo("//*");
			for (NodeInfo nodeI : nodes) {
				int level = getLevel(nodeI.getNode());
				int line = nodeI.getStart().getLineNumber();
				int lastLine = nodeI.getEnd().getLineNumber();
				
				addLine(line, level);
				addLine(lastLine, level);

				maxLine = line > maxLine ? line : maxLine;
				maxLine = lastLine > maxLine ? lastLine : maxLine;
			}

			String[] levelArr = new String[maxLine];
			int levBefore = 0;
			for (int j = 0; j < levelArr.length; j++) {
				int level = levelMap.containsKey(j + 1) ? levelMap.get(j + 1) : levBefore;
				levelArr[j] = "" + level;
				levBefore = level;
			}
			return levelArr;
		} catch (XPathExpressionException e) {
		}
		return new String[]{};
	}
	
	private void addLine(int line, int level){
		int levelBevor = levelMap.containsKey(line) ? levelMap.get(line) : -1;
		if(!(levelBevor > 0 && levelBevor < level)){
			levelMap.put(line, level);
		}
	}
	
	private int getLevel(Node node){
		Node parent = node.getParentNode();
		if(parent.getNodeType() == Node.DOCUMENT_NODE)
			return 0;
		return getLevel(parent) + 1;
	}
	
	public static boolean isExisting(String uri) throws URISyntaxException{
		File f = new File(new URI(uri).getPath());
		return f.isDirectory() && f.exists();
	}
	
	public static int getLastLine(Node node){
		Object lastLine = node.getUserData(PositionalXMLReader.LINE_NUMBER_LAST_KEY_NAME);
		return (Integer) (lastLine == null ? -1 : lastLine);
	}
	
	public static String[] getLevels(String uriString) throws IOException, SAXException, URISyntaxException, XMLStreamException{
		URI uri = new URI(uriString);
		XMLLevels xmlLevels = new XMLLevels(uri);
		return xmlLevels.getLevels();
	}
	public static String[] getLevels(String xml, String uri) throws IOException, SAXException, XMLStreamException{
		XMLLevels xmlLevels = new XMLLevels(xml, uri);
		return xmlLevels.getLevels();
	}
	
	public static void main(String[] args) throws URISyntaxException, IOException, SAXException, XMLStreamException{
		String uri = "/D:/nico/Work/Java/net.sqf.website/in/files/userGuide/people.xml";
//		
		String xml = TextSource.readTextFile(new File(uri)).toString();
//		
		String[] levels = getLevels(xml, uri);
		
		for (int i = 0; i < levels.length; i++) {
			System.out.println(levels[i]);
		}
//		
//		String[] lines = xml.split("\\r?\\n");
//		
//		for (int i = 0; i < levels.length; i++) {
//			System.out.println(levels[i] + " | " + lines[i]);
//		}
//		try {
//			String[] levels = getLevels("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
//			"<p lang=\"it\" xml:lang=\"en\">Può la disciplina nella guerra più che il furore.</p>");
//			for (int i = 0; i < levels.length; i++) {
//				System.out.println(levels[i]);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
}
