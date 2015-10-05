package net.sqf.xmlUtils.staxParser;

// PositionalXMLReader.java

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

import net.sqf.stringUtils.TextSource;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class PositionalXMLReader {
	public final static String NODE_INNER_LOCATION_START = "nils";
	public final static String NODE_INNER_LOCATION_END = "nile";

	
	public final static String NODE_LOCATION_START = "nls";
	public final static String NODE_LOCATION_END = "nle";


	public final static String ATTRIBUTE_REGION_LOCATION_START = "arls";
	public final static String ATTRIBUTE_REGION_LOCATION_END = "arle";
	
	public final static String DTD_LOCATION_START = "dls";
	public final static String DTD_LOCATION_END = "dle";
	
	public static final String XML_DECL_LOCATION_START = "xdls";
	public static final String XML_DECL_LOCATION_END = "xdle";
	
	public static final String ATTRIBUT_REGION = "ar";
	
	public final static int STANDARD_TYPE = 0;
	public final static int ELEMENT_START_TYPE = 1;
	public final static int ELEMENT_END_TYPE = 2;
	public final static int ATTRIBUTE_TYPE = 3;
	public final static int TEXT_TYPE = 4;
	public final static int DTD_TYPE = 5;
	public static final int DTD_TYPE_END = 6;
	
	protected static final String ELEMENT_ID = "elementId";
	public static final String HAS_DTD = "hasDTD";
	
	public static final String NAMESPACE_CONTEXT = "nsc";
//	public static final String PREFIX_MAPPING = "prefixMapping";
//	public static final String NAMESPACE_MAPPING = "namespaceMapping";
	
//	private final HashMap<Integer, Integer> linePositionMap = new HashMap<Integer, Integer>();
//	private int lineCounter = 0;
	

	private final static PositionalStaxParser woodStoxParser = initialParser();


	
	
	private static PositionalStaxParser initialParser() {
		return new PositionalStaxParser();
	}

	public static int getPosition(Node node, String userDataKey){
		return getPosition(getNodeInfo(node), userDataKey);
	}
	
	private static int getPosition(NodeInfo info, String userDataKey) {
		if (userDataKey.equals(NODE_LOCATION_START)) {
			return info.getStartOffset();
		}
		if (userDataKey.equals(NODE_LOCATION_END)) {
			return info.getEndOffset();
		}
		if (info instanceof ElementInfo) {
			if (userDataKey.equals(NODE_INNER_LOCATION_END)) {
				return ((ElementInfo) info).getInnerEndOffset();
			}
			if (userDataKey.equals(NODE_INNER_LOCATION_START)) {
				return ((ElementInfo) info).getInnerStartOffset();
			}
			if (userDataKey.equals(ATTRIBUTE_REGION_LOCATION_START)) {
				return ((ElementInfo) info).getAttributRegionStartOffset();
			}
			if (userDataKey.equals(ATTRIBUTE_REGION_LOCATION_END)) {
				return ((ElementInfo) info).getAttributRegionEndOffset();
			}
		}
		if(info instanceof DocumentInfo){
			if(userDataKey.equals(XML_DECL_LOCATION_START)){
				return ((DocumentInfo) info).getXmlDeclStart().getCharacterOffset();
			}
			if(userDataKey.equals(XML_DECL_LOCATION_END)){
				return ((DocumentInfo) info).getXmlDeclEnd().getCharacterOffset();
			}
			if(userDataKey.equals(DTD_LOCATION_START)){
				return ((DocumentInfo) info).getDtdStart().getCharacterOffset();
			}
			if(userDataKey.equals(DTD_LOCATION_END)){
				return ((DocumentInfo) info).getDtdEnd().getCharacterOffset();
			}
		}
		return -1;
	}
	
	
	public static int getLine(Node node, String userDataKey){
		Location loc = (Location) node.getUserData(userDataKey);
		return loc.getLineNumber();
	}
	
	public PositionalXMLReader(){
		
	}
	
	
//	private void positionMap(String doc){
//		String lines[] = StringUtil.getLinesArr(doc);
//		int position = 0;
//		int lineCount = 0;
//		for (; lineCount < lines.length; lineCount++) {
//			linePositionMap.put(lineCount + 1, position);
//			String line = lines[lineCount];
//			position += line.length();
//		}
//		this.lineCounter = lineCount;
//	}
	
	
//	public int getLineForPos(int pos){
//		
//		int line = 0;
//		for (; line < this.lineCounter; line++) {
//			int linePos = this.linePositionMap.get(line + 1);
//			if(!this.linePositionMap.containsKey(line + 2))
//				break;
//			int lineNextPos = this.linePositionMap.get(line + 2);
//			
//			if(linePos <= pos && lineNextPos > pos)
//				break;
//		}
//		
//		return line;
//	}
	private boolean isWellformed = true;
	public boolean isWellformed(final TextSource source){
		return isWellformed(source.toString());
	}
	public boolean isWellformed(final String source){
		final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		this.isWellformed = true;
		try {
			docBuilder = docBuilderFactory
					.newDocumentBuilder();
			docBuilder.setErrorHandler(new ErrorHandler() {
				@Override
				public void warning(SAXParseException arg0) throws SAXException {
					
				}
				@Override
				public void fatalError(SAXParseException arg0) throws SAXException {
					isWellformed = false;
				}
				@Override
				public void error(SAXParseException arg0) throws SAXException {
					isWellformed = false;
				}
			});
			docBuilder.parse(new InputSource(new StringReader(source)));
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			return false;
		} catch (SAXException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return isWellformed;
	}
	
	public Document readXML(final File doc) throws IOException, SAXException, XMLStreamException{
		TextSource ts = TextSource.readTextFile(doc);
		return this.readXML(ts);
	}
	
	public Document readXML(final TextSource source) throws IOException,
			SAXException, XMLStreamException {

//		positionMap(source.toString());
		
		final Document docNode;
		
		
		final ArrayList<Node> allNodes = new ArrayList<Node>();
		
		final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory
					.newDocumentBuilder();
			docNode = docBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(
			"Can't create SAX parser / DOM builder.", e);
		}
		

		
		// encoding
        woodStoxParser.parse(docNode, source);
		
		docNode.setUserData("allNodes", allNodes, null);
//		docNode.setUserData("positionNodeMap", posHandler.positionNodeMap(), null);
		
		return docNode;
	}
	
	public static Location getLocation(Node node, String key){
		return (Location) node.getUserData(key);
	}
	
	public static ElementInfo getNodeInfo(Element node){
		return new ElementInfo(node);
	}
	public static NodeInfo getNodeInfo(Node node){
		if(node instanceof Element){
			return getNodeInfo((Element) node);
		}
		if(node instanceof Attr){
			return getNodeInfo((Attr) node);
		}
		if(node instanceof Document){
			return getNodeInfo((Document) node);
		}
		return new NodeInfo(node);
	}
	private static DocumentInfo getNodeInfo(Document node) {
		return new DocumentInfo(node);
	}
	
	private static NodeInfo getNodeInfo(Attr node) {
		return new AttributeInfo(node);
	}

	public static ArrayList<NodeInfo> getNodeInfo(NodeList nodes){
		ArrayList<NodeInfo> nodeInfos = new ArrayList<NodeInfo>();
		for (int i = 0; i < nodes.getLength(); i++) {
			nodeInfos.add(getNodeInfo(nodes.item(i)));
		}
		return nodeInfos;
	}

}