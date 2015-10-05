package net.sqf.xmlUtils.parser;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sqf.stringUtils.TextSource;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

public class PositionalXMLHandler extends DefaultHandler2 {
	private final Document doc;
	private String systemId = "";
	private final HashMap<Integer, Integer> linePositionMap;
	private final HashMap<String, String> prefixNamespaceMap = new HashMap<String, String>();
	private final HashMap<String, String> namespacePrefixMap = new HashMap<String, String>();

	private Locator locator;
	private final Stack<Element> elementStack = new Stack<Element>();

	private ArrayList<Node> textBuffer = new ArrayList<Node>();

	private final ArrayList<Node> allNodes = new ArrayList<Node>();
	private final HashMap<Integer, Node> positionNodeMap = new HashMap<Integer, Node>();
	private final byte[] docBytes;

	public PositionalXMLHandler(Document docNode, String docString,
			HashMap<Integer, Integer> linePositionMap) {
		this.doc = docNode;
		this.docBytes = docString.getBytes();
		docNode.setUserData(PositionalXMLReader.HAS_DTD, false, null);
		this.linePositionMap = linePositionMap;
		
		this.prefixNamespaceMap.put("xml", "http://www.w3.org/XML/1998/namespace");
		this.namespacePrefixMap.put("http://www.w3.org/XML/1998/namespace", "xml");
	}

	protected HashMap<Integer, Node> positionNodeMap() {
		return this.positionNodeMap;
	}

	private int getPosition(int line, int col) {
		if(!this.linePositionMap.containsKey(line)){
			return -1;
		}
		int linePos = this.linePositionMap.get(line);
		return linePos + col;
	}
	private int getLineNumber(int pos){
		int i = 1;
		while(getPosition(i, 1) <= pos && getPosition(i + 1, 1) > 0){
			i++;
		}
		return i;
	}
	private int getColumn(int pos){
		int line = getLineNumber(pos);
		return pos - this.linePositionMap.get(line);
	}

	@Override
	public InputSource resolveEntity(String name, String publicId,
			String baseURI, String systemId) {
		try {
			URI dtdUri;
			if (baseURI != null) {
				URI base = new URI(baseURI);
				dtdUri = base.resolve(systemId);
			} else {
				dtdUri = new URI(systemId);
			}
			String dtdString = TextSource.readTextFile(dtdUri.toURL()).toString();
			StringReader strReader = new StringReader(dtdString);
			InputSource is = new InputSource(strReader);
			is.setPublicId(publicId);
			is.setSystemId(systemId);
			return is;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public void endDTD() throws SAXException {
		super.endDTD();
		if(locator.getSystemId().equals(this.systemId)){
			this.setUserData(doc, PositionalXMLReader.DTD_TYPE_END);
		}
		this.parseLocalDTD = false;
	};
	@Override
	public void startDTD(String name, String publicId, String systemId) throws SAXException {
		this.parseLocalDTD = true;
		this.setUserData(doc, PositionalXMLReader.DTD_TYPE);
		doc.setUserData(PositionalXMLReader.HAS_DTD, true, null);
		super.startDTD(name, publicId, systemId);
	};
	@Override
	public void attributeDecl(String eName, String aName, String type, String mode, String value) throws SAXException {
		super.attributeDecl(eName, aName, type, mode, value);
	};
	@Override
	public void elementDecl(String name, String model) throws SAXException {
		super.elementDecl(name, model);
	};
	@Override
	public void endEntity(String name) throws SAXException {
		super.endEntity(name);
	};
	@Override
	public void endCDATA() throws SAXException {
		super.endCDATA();
	};
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	};
	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		super.endPrefixMapping(prefix);
	};
	@Override
	public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
		super.externalEntityDecl(name, publicId, systemId);
	};
	@Override
	public InputSource getExternalSubset(String name, String baseURI) throws SAXException ,IOException {
		return super.getExternalSubset(name, baseURI);
	};
	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		String appendText = new String(ch, start, length);
		final Node textNode = doc.createTextNode(appendText);

		// el.appendChild(textNode);
		this.textBuffer.add(textNode);

		// get line/column number
		setUserData(textNode, PositionalXMLReader.TEXT_TYPE);
	};
	@Override
	public void internalEntityDecl(String name, String value) throws SAXException {
		super.internalEntityDecl(name, value);
	};
	@Override
	public void notationDecl(String name, String publicId, String systemId) throws SAXException {
		super.notationDecl(name, publicId, systemId);
	};
	@Override
	public void skippedEntity(String name) throws SAXException {
		super.skippedEntity(name);
	};
	@Override
	public void startCDATA() throws SAXException {
		super.startCDATA();
	};
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	};
	@Override
	public void startEntity(String name) throws SAXException {
		super.startEntity(name);
	};
	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		this.prefixNamespaceMap.put(prefix, uri);
		this.namespacePrefixMap.put(uri, prefix);
		super.startPrefixMapping(prefix, uri);
	};
	@Override
	public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
		super.unparsedEntityDecl(name, publicId, systemId, notationName);
	};
	

	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		return new InputSource(new ByteArrayInputStream(new byte[0]));
	}

	@Override
	public void setDocumentLocator(final Locator locator) {
		this.locator = locator;
		if ("".equals(this.systemId)) {
			this.systemId = locator.getSystemId();
		}
		// Save the locator, so that it can be
		// used later for line tracking when
		// traversing nodes.
	}

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes attributes)
			throws SAXException {
		addTextIfNeeded();
		// final Element el = doc.createElement(qName);
		final Element el = doc.createElementNS(uri, localName);
		el.setPrefix(this.namespacePrefixMap.get(uri));
		el.setUserData(PositionalXMLReader.PREFIX_MAPPING, this.prefixNamespaceMap.clone(), null);
		el.setUserData(PositionalXMLReader.NAMESPACE_MAPPING, this.namespacePrefixMap.clone(), null);
		setUserData(el, PositionalXMLReader.ELEMENT_START_TYPE);

		for (int i = 0; i < attributes.getLength(); i++) {
			String attrName = attributes.getQName(i);
			String ns = attributes.getURI(i);
			if(ns.equals("")){
				el.setAttribute(attrName, attributes.getValue(i));
			} else {
				el.setAttributeNS(ns, attrName, attributes.getValue(i));
			}
			Attr attrNode = el.getAttributeNode(attrName);
			setUserData(el, attrNode);
		}
		this.setAttributeRegion(el);

		elementStack.push(el);
	}

	@Override
	public void endElement(final String uri, final String localName,
			final String qName) {
		addTextIfNeeded();
		final Element closedEl = elementStack.pop();

		setUserData(closedEl, PositionalXMLReader.ELEMENT_END_TYPE);

		if (elementStack.isEmpty()) { // Is this the root element?
			doc.appendChild(closedEl);
		} else {
			final Element parentEl = elementStack.peek();
			parentEl.appendChild(closedEl);
		}
	}

	@Override
	public void characters(final char ch[], final int start, final int length)
			throws SAXException {
		String appendText = new String(ch, start, length);
		final Node textNode = doc.createTextNode(appendText);

		// el.appendChild(textNode);
		this.textBuffer.add(textNode);

		// get line/column number
		setUserData(textNode, PositionalXMLReader.TEXT_TYPE);

		// textBuffer.append(ch, start, length);
	}

	@Override
	public void processingInstruction(String target, String data) {
		addTextIfNeeded();
		if (this.systemId.equals(this.locator.getSystemId()) && !this.parseLocalDTD) {
			final Node el = elementStack.isEmpty() ? doc : elementStack.peek();
			final Node pi = doc.createProcessingInstruction(target, data);

			el.appendChild(pi);

			setUserData(pi);
		}
	}

	@Override
	public void comment(char[] ch, int start, int length) {
		addTextIfNeeded();
		if (this.systemId.equals(this.locator.getSystemId()) && !this.parseLocalDTD) {
			final Node el = elementStack.isEmpty() ? doc : elementStack.peek();
			final String comment = new String(ch, start, length);
			Node commentNode = doc.createComment(comment);

			el.appendChild(commentNode);

			setUserData(commentNode);
		}
	}

	private void addTextIfNeeded() {
		if (this.textBuffer.size() > 0) {
			String textBuffer = "";
			for (Iterator<Node> iterator = this.textBuffer.iterator(); iterator
					.hasNext();) {
				Node textNode = iterator.next();
				textBuffer += textNode.getNodeValue();
			}
			final Node el = elementStack.peek();
			Node newTextNode = doc.createTextNode(textBuffer);
			Node first = this.textBuffer.get(0);
			Node last = this.textBuffer.get(this.textBuffer.size() - 1);
			newTextNode.setUserData(
							PositionalXMLReader.LINE_NUMBER_KEY_NAME,
							first.getUserData(PositionalXMLReader.LINE_NUMBER_KEY_NAME),
							null);
			newTextNode.setUserData(
							PositionalXMLReader.COLUMN_NUMBER_KEY_NAME,
							first.getUserData(PositionalXMLReader.COLUMN_NUMBER_KEY_NAME),
							null);
			newTextNode.setUserData(PositionalXMLReader.POSITION_KEY_NAME,
					first.getUserData(PositionalXMLReader.POSITION_KEY_NAME),
					null);
			newTextNode.setUserData(
							PositionalXMLReader.LINE_NUMBER_LAST_KEY_NAME,
							last.getUserData(PositionalXMLReader.LINE_NUMBER_LAST_KEY_NAME),
							null);
			newTextNode.setUserData(
							PositionalXMLReader.COLUMN_NUMBER_LAST_KEY_NAME,
							last.getUserData(PositionalXMLReader.COLUMN_NUMBER_LAST_KEY_NAME),
							null);
			newTextNode.setUserData(
							PositionalXMLReader.POSITION_LAST_KEY_NAME,
							last.getUserData(PositionalXMLReader.POSITION_LAST_KEY_NAME),
							null);
			el.appendChild(newTextNode);
			this.textBuffer = new ArrayList<Node>();
		}
	}

	private int precLineNumber = 1;
	private int precColNumber = 0;
	private boolean parseLocalDTD = false;

	private Pattern attrPattern = Pattern.compile("\\s+(\\S+)(\\s+)?=(\\s+)?(\"([^\"]*)\"|'([^']*)')");

	private void setUserData(Node el, Attr at) {
		allNodes.add(at);
		int startPos = (Integer) el.getUserData(PositionalXMLReader.POSITION_KEY_NAME);
		int endPos = (Integer) el.getUserData(PositionalXMLReader.INNER_POSITION_KEY_NAME);
		
		String startTag = new String(this.docBytes, startPos, endPos - startPos);
		Matcher matcher = attrPattern.matcher(startTag);
		while (matcher.find()) {
			String name = matcher.group(1);
			if(name.equals(at.getName())){
				
				MatchResult mresult = matcher.toMatchResult();
				startPos += mresult.start();
				endPos -= startTag.length() - mresult.end();

				at.setUserData(PositionalXMLReader.POSITION_KEY_NAME,
						startPos, null);
				at.setUserData(PositionalXMLReader.LINE_NUMBER_KEY_NAME,
						this.getLineNumber(startPos), null);
				at.setUserData(PositionalXMLReader.COLUMN_NUMBER_KEY_NAME,
						this.getColumn(startPos), null);
				at.setUserData(PositionalXMLReader.POSITION_LAST_KEY_NAME,
						endPos,
						null);
				at.setUserData(PositionalXMLReader.LINE_NUMBER_LAST_KEY_NAME,
						this.getLineNumber(endPos), null);
				at.setUserData(PositionalXMLReader.COLUMN_NUMBER_LAST_KEY_NAME,
						this.getColumn(endPos), null);
				break;
			}
		}
		
//		Pattern pattern = Pattern.compile("\\s+" + StringUtil.escapeRegex(at.getName()) + "\\s*=\\s*(\'|\")" + StringUtil.escapeRegex(at.getValue()) + "(\'|\")");
//		Matcher matcher2 = pattern.matcher(startTag);
//		if(matcher.find()){
//			MatchResult mresult = matcher.toMatchResult();
//			startPos += mresult.start();
//			endPos -= startTag.length() - mresult.end();
//
//			at.setUserData(PositionalXMLReader.POSITION_KEY_NAME,
//					startPos, null);
//			at.setUserData(PositionalXMLReader.LINE_NUMBER_KEY_NAME,
//					this.getLineNumber(startPos), null);
//			at.setUserData(PositionalXMLReader.COLUMN_NUMBER_KEY_NAME,
//					this.getColumn(startPos), null);
//			at.setUserData(PositionalXMLReader.POSITION_LAST_KEY_NAME,
//					endPos,
//					null);
//			at.setUserData(PositionalXMLReader.LINE_NUMBER_LAST_KEY_NAME,
//					this.getLineNumber(endPos), null);
//			at.setUserData(PositionalXMLReader.COLUMN_NUMBER_LAST_KEY_NAME,
//					this.getColumn(endPos), null);
//		}
	}

	private void setAttributeRegion(Node el){
		int startPos = (Integer) el.getUserData(PositionalXMLReader.POSITION_KEY_NAME);
		int endPos = (Integer) el.getUserData(PositionalXMLReader.INNER_POSITION_KEY_NAME);
		String startTag = new String(this.docBytes, startPos, endPos - startPos);
		
		Pattern patternStart = Pattern.compile("\\s");
		Matcher matcherStart = patternStart.matcher(startTag);
		
		Pattern patternEnd = Pattern.compile("(/)?>$", Pattern.DOTALL);
		Matcher matcherEnd = patternEnd.matcher(startTag);
		
		endPos -= matcherEnd.find() ? startTag.length() - matcherEnd.start() : endPos - 1;
		startPos += matcherStart.find() ? matcherStart.start() : endPos - startPos;
		el.setUserData(PositionalXMLReader.ATTRIBUTE_REGION_POSITION, startPos, null);
		el.setUserData(PositionalXMLReader.ATTRIBUTE_REGION_POSITION_LAST, endPos, null);
		
	}

	private void setUserData(Node node) {
		setUserData(node, PositionalXMLReader.STANDARD_TYPE);
	}

	private void setUserData(Node node, int type) {
		if (node.getNodeType() == Node.DOCUMENT_NODE) {
			if (!node.equals(this.doc)) {
				return;
			}
		}

		int lineNumber = this.precLineNumber;
		int colNumber = this.precColNumber;
		int pos = getPosition(lineNumber, colNumber);

		int lastLineNumber = this.locator.getLineNumber();
		int lastColNumber = this.locator.getColumnNumber() - 1;
		int lastPos = getPosition(lastLineNumber, lastColNumber);
//		String startTag = new String(this.docBytes, pos, lastPos - pos);
		if (this.elementStack.isEmpty()
				&& (type == PositionalXMLReader.STANDARD_TYPE || type == PositionalXMLReader.ELEMENT_START_TYPE)) {
			String passage = new String(docBytes, pos, lastPos - pos);
			if(pos == 0 && passage.contains("<?xml")){
				int endXMlDel = passage.indexOf("?>") + 2;
				passage = passage.substring(endXMlDel);
				pos += endXMlDel;
			}
			if(node.getNodeType() == Node.COMMENT_NODE){
				int corr = passage.indexOf("<!--");
//				if(this.parseLocalDTD){
//					if(passage.substring(0, corr).contains("]")){
//						this.parseLocalDTD = false;
//					} else {
//						this.doc.removeChild(node);
//						this.precColNumber = this.locator.getColumnNumber();
//						this.precLineNumber = this.locator.getLineNumber();
//						return;
//					}
//				}
				pos += corr;
				passage = passage.substring(corr);
			} else if (node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE){
				int corr = passage.indexOf("<?");
//				if(this.parseLocalDTD){
//					if(passage.substring(0, corr).contains("]")){
//						this.parseLocalDTD = false;
//					} else {
//						this.doc.removeChild(node);
//						this.precColNumber = this.locator.getColumnNumber();
//						this.precLineNumber = this.locator.getLineNumber();
//						return;
//					}
//				}
				pos += corr;
				passage = passage.substring(corr);
			} else if (type == PositionalXMLReader.ELEMENT_START_TYPE){
				int corr = passage.indexOf("<");
				pos += corr;
			}
		}

		String[] firstKeys;
		String[] lastKeys;
		if (type == PositionalXMLReader.STANDARD_TYPE
				|| type == PositionalXMLReader.TEXT_TYPE) {
			firstKeys = new String[] {
					PositionalXMLReader.LINE_NUMBER_KEY_NAME,
					PositionalXMLReader.COLUMN_NUMBER_KEY_NAME,
					PositionalXMLReader.POSITION_KEY_NAME };
			lastKeys = new String[] {
					PositionalXMLReader.LINE_NUMBER_LAST_KEY_NAME,
					PositionalXMLReader.COLUMN_NUMBER_LAST_KEY_NAME,
					PositionalXMLReader.POSITION_LAST_KEY_NAME };
		} else if (type == PositionalXMLReader.ELEMENT_START_TYPE) {
			firstKeys = new String[] {
					PositionalXMLReader.LINE_NUMBER_KEY_NAME,
					PositionalXMLReader.COLUMN_NUMBER_KEY_NAME,
					PositionalXMLReader.POSITION_KEY_NAME };
			lastKeys = new String[] {
					PositionalXMLReader.INNER_LINE_NUMBER_KEY_NAME,
					PositionalXMLReader.INNER_COLUMN_NUMBER_KEY_NAME,
					PositionalXMLReader.INNER_POSITION_KEY_NAME };
		} else if (type == PositionalXMLReader.ELEMENT_END_TYPE) {
			firstKeys = new String[] { 
					PositionalXMLReader.INNER_LINE_NUMBER_LAST_KEY_NAME, 
					PositionalXMLReader.INNER_COLUMN_NUMBER_LAST_KEY_NAME,
					PositionalXMLReader.INNER_POSITION_LAST_KEY_NAME };
			lastKeys = new String[] {
					PositionalXMLReader.LINE_NUMBER_LAST_KEY_NAME,
					PositionalXMLReader.COLUMN_NUMBER_LAST_KEY_NAME,
					PositionalXMLReader.POSITION_LAST_KEY_NAME };
		} else if (type == PositionalXMLReader.DTD_TYPE) {
			firstKeys = new String[] {
					PositionalXMLReader.DTD_LINE_NUMMBER_KEY_NAME,
					PositionalXMLReader.DTD_COLUMN_NUMMBER_KEY_NAME,
					PositionalXMLReader.DTD_POSITION_KEY_NAME };
			lastKeys = new String[] {
					PositionalXMLReader.DTD_LINE_NUMMBER_LAST_KEY_NAME,
					PositionalXMLReader.DTD_COLUMN_NUMMBER_LAST_KEY_NAME,
					PositionalXMLReader.DTD_POSITION_LAST_KEY_NAME };
		} else if (type == PositionalXMLReader.DTD_TYPE_END) {
			firstKeys = new String[] { "", "", "" };
			lastKeys = new String[] {
					PositionalXMLReader.DTD_LINE_NUMMBER_LAST_KEY_NAME,
					PositionalXMLReader.DTD_COLUMN_NUMMBER_LAST_KEY_NAME,
					PositionalXMLReader.DTD_POSITION_LAST_KEY_NAME };
		} else {
			firstKeys = new String[] { "", "", "" };
			lastKeys = new String[] { "", "", "" };
		}
		if (type != PositionalXMLReader.DTD_TYPE_END) {
			node.setUserData(firstKeys[0], lineNumber, null);
			node.setUserData(firstKeys[1], colNumber, null);
			node.setUserData(firstKeys[2], pos, null);
		}
		node.setUserData(lastKeys[0], lastLineNumber, null);
		node.setUserData(lastKeys[1], lastColNumber, null);
		node.setUserData(lastKeys[2], lastPos, null);

		this.precLineNumber = lastLineNumber;
		this.precColNumber = lastColNumber;

		if (type != PositionalXMLReader.DTD_TYPE && type != PositionalXMLReader.DTD_TYPE_END) {
			allNodes.add(node);
		}
		positionNodeMap.put(lastPos, node);
	}


	// private void correctionNode(String document, Node node){
	// int firstPos = (Integer)
	// firstNode.getUserData(PositionalXMLReader.POSITION_KEY_NAME);
	// int lastPos = (Integer)
	// firstNode.getUserData(PositionalXMLReader.POSITION_LAST_KEY_NAME);
	// String substring = document.substring(firstPos, lastPos);
	// switch (node.getNodeType()) {
	// case Node.PROCESSING_INSTRUCTION_NODE:
	// correctionPI(substring, node);
	// break;
	// case Node.COMMENT_NODE:
	// correctionComment(substring, node);
	// break;
	// default:
	// break;
	// }
	// }
	//
	// private void correctionPI(String substring, Node pi){
	// }
	// private void correctionComment(String substring, Node pi){
	// }
	// private void correctLastPos(Node node, int corCol) {
	// int col = (Integer) node
	// .getUserData(PositionalXMLReader.COLUMN_NUMBER_LAST_KEY_NAME);
	// int pos = (Integer) node
	// .getUserData(PositionalXMLReader.POSITION_LAST_KEY_NAME);
	// col -= corCol;
	// pos -= corCol;
	// node.setUserData(PositionalXMLReader.COLUMN_NUMBER_LAST_KEY_NAME, col,
	// null);
	// node.setUserData(PositionalXMLReader.POSITION_LAST_KEY_NAME, pos, null);
	// }
}
