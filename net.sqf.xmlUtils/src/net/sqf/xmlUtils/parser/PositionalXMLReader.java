package net.sqf.xmlUtils.parser;

// PositionalXMLReader.java

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sqf.stringUtils.StringUtil;
import net.sqf.stringUtils.TextSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class PositionalXMLReader {
	public final static String LINE_NUMBER_KEY_NAME = "lineNumber";
	public final static String COLUMN_NUMBER_KEY_NAME = "columnNumber";

	public static final String LINE_NUMBER_LAST_KEY_NAME = "lineNumberLast";
	public static final String COLUMN_NUMBER_LAST_KEY_NAME = "columnNumberLast";

	// just for elements
	public final static String INNER_LINE_NUMBER_KEY_NAME = "innerLineNumber";
	public final static String INNER_COLUMN_NUMBER_KEY_NAME = "innerColumnNumber";
	
	public static final String INNER_LINE_NUMBER_LAST_KEY_NAME = "innerLineNumberLast";
	public static final String INNER_COLUMN_NUMBER_LAST_KEY_NAME = "innerColumnNumberLast";
	
	public final static String POSITION_KEY_NAME = "positionStart";
	public final static String INNER_POSITION_KEY_NAME = "positionInner";
	public final static String INNER_POSITION_LAST_KEY_NAME = "positionInnerLast";
	public final static String POSITION_LAST_KEY_NAME = "positionEnd";

	public static final String ATTRIBUTE_REGION_POSITION = "attributeRegionStart";
	public static final String ATTRIBUTE_REGION_POSITION_LAST = "attributeRegionEnd";
	
	public final static String DTD_POSITION_KEY_NAME = "dtdPositionStart";
	public final static String DTD_POSITION_LAST_KEY_NAME = "dtdPositionEnd";
	public final static String DTD_LINE_NUMMBER_KEY_NAME = "dtdLineNumber";
	public final static String DTD_COLUMN_NUMMBER_KEY_NAME = "dtdColumnNumber";
	public final static String DTD_LINE_NUMMBER_LAST_KEY_NAME = "dtdLineNumberLast";
	public final static String DTD_COLUMN_NUMMBER_LAST_KEY_NAME = "dtdColumnNumberLast";
	
	public final static int STANDARD_TYPE = 0;
	public final static int ELEMENT_START_TYPE = 1;
	public final static int ELEMENT_END_TYPE = 2;
	public final static int ATTRIBUTE_TYPE = 3;
	public final static int TEXT_TYPE = 4;
	public final static int DTD_TYPE = 5;
	public static final int DTD_TYPE_END = 6;
	
	protected static final String ELEMENT_ID = "elementId";
	public static final String HAS_DTD = "hasDTD";
	public static final String PREFIX_MAPPING = "prefixMapping";
	public static final String NAMESPACE_MAPPING = "namespaceMapping";
	
	private final HashMap<Integer, Integer> linePositionMap = new HashMap<Integer, Integer>();
	private int lineCounter = 0;
	

	private final static XMLReader parser = initialParser();
	
	
	private static XMLReader initialParser() {
		try {
			return XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public PositionalXMLReader(){
		
	}
	
	
	private void positionMap(String doc){
		String lines[] = StringUtil.getLinesArr(doc);
		int position = 0;
		int lineCount = 0;
		for (; lineCount < lines.length; lineCount++) {
			linePositionMap.put(lineCount + 1, position);
			String line = lines[lineCount];
			position += line.length();
		}
		this.lineCounter = lineCount;
	}
	
	
	public int getLineForPos(int pos){
		
		int line = 0;
		for (; line < this.lineCounter; line++) {
			int linePos = this.linePositionMap.get(line + 1);
			if(!this.linePositionMap.containsKey(line + 2))
				break;
			int lineNextPos = this.linePositionMap.get(line + 2);
			
			if(linePos <= pos && lineNextPos > pos)
				break;
		}
		
		return line;
	}
	
	public Document readXML(final File doc) throws IOException, SAXException{
		TextSource ts = TextSource.readTextFile(doc);
		return this.readXML(ts.toString(), doc.getAbsolutePath());
	}
	
	public Document readXML(final String docString, final String baseUri) throws IOException,
			SAXException {

		positionMap(docString);
		
		final Document docNode;
		InputStream is = new ByteArrayInputStream(docString.getBytes());
		
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
		

		final PositionalXMLHandler posHandler = new PositionalXMLHandler(docNode, docString, this.linePositionMap);
		
		// encoding
		Reader reader = new InputStreamReader(is, "UTF-8");
		
		InputSource isource = new InputSource(reader);
		isource.setEncoding("UTF-8");
		isource.setSystemId(baseUri);
		parser.setProperty("http://xml.org/sax/properties/lexical-handler",
				posHandler);
		parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
		
		parser.setEntityResolver(posHandler);
		parser.setErrorHandler(new ErrorHandler() {
			@Override
			public void warning(SAXParseException arg0) throws SAXException {
				
			}
			@Override
			public void fatalError(SAXParseException arg0) throws SAXException {
				
			}
			@Override
			public void error(SAXParseException arg0) throws SAXException {
				
			}
		});
		parser.setContentHandler(posHandler);
		parser.parse(isource);
		
		
		docNode.setUserData("allNodes", allNodes, null);
		docNode.setUserData("positionNodeMap", posHandler.positionNodeMap(), null);
		
		return docNode;
	}

}