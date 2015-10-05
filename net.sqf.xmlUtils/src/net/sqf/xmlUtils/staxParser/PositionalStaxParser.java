package net.sqf.xmlUtils.staxParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sqf.stringUtils.TextSource;
import net.sqf.xmlUtils.parser.PositionalXMLReader;
import net.sqf.xmlUtils.xpath.ProcessNamespaces;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class PositionalStaxParser {
	private Document doc;
	private byte[] docBytes;
	private String initialBaseUri = null;

	private ArrayList<Node> textBuffer = new ArrayList<Node>();
	private final Stack<Element> elementStack = new Stack<Element>();
	private UserDataManager udm;
	


	private void initialDoc(Document doc) {
		this.doc = doc;
		this.doc.setUserData(PositionalXMLReader.HAS_DTD, false, null);
	}

	public synchronized void parse(Document doc, TextSource docString)
			throws XMLStreamException, SAXException, UnsupportedEncodingException, IllegalStateException {

		initialDoc(doc);
		this.docBytes = docString.toString().getBytes(docString.getEncoding());
		this.udm = new UserDataManager(doc, docString.toString());
		
		
		InputStream is = new ByteArrayInputStream(docBytes);
		
		String orgParser = System.getProperty("javax.xml.stream.XMLInputFactory");
		System.setProperty("javax.xml.stream.XMLInputFactory",
				  "com.ctc.wstx.stax.WstxInputFactory");
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		
		if(orgParser != null){
			System.setProperty("javax.xml.stream.XMLInputFactory", orgParser);
		}
		
		
//		inputFactory.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", Boolean.TRUE);
//		inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
//		inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
		
		// Setup a new eventReader
		initialBaseUri = docString.getFile().toURI().toString();
		doc.setDocumentURI(initialBaseUri);
		
		XMLStreamReader event = inputFactory.createXMLStreamReader(initialBaseUri, is);

		while (event.hasNext()) {
			switch (event.getEventType()) {
			case XMLStreamConstants.START_DOCUMENT:
				startDocument(event);
				break;
			case XMLStreamConstants.START_ELEMENT:
				startElement(event);
				break;
			case XMLStreamConstants.END_ELEMENT:
				endElement(event);
				break;
			case XMLStreamConstants.DTD:
				endDTD(event);
				break;
			case XMLStreamConstants.COMMENT:
				comment(event);
				break;
			case XMLStreamConstants.CHARACTERS:
				characters(event);
				break;
			case XMLStreamConstants.END_DOCUMENT:
//				System.out.println("end document "
//						+ event.getLocation().getCharacterOffset());
				event.next();
				break;
			case XMLStreamConstants.ATTRIBUTE:
//				System.out.println("attribut "
//						+ event.getLocation().getCharacterOffset());
				event.next();
				break;
			case XMLStreamConstants.ENTITY_DECLARATION:
//				System.out.println("entity declaration "
//						+ event.getLocation().getCharacterOffset());
				event.next();
				break;
			case XMLStreamConstants.ENTITY_REFERENCE:
//				System.out.println("entity reference "
//						+ event.getLocation().getCharacterOffset());
				event.next();
				break;
			case XMLStreamConstants.NAMESPACE:
//				System.out.println("namespace "
//						+ event.getLocation().getCharacterOffset());
				event.next();
				break;
			case XMLStreamConstants.PROCESSING_INSTRUCTION:
				processingInstruction(event);
				break;
			case XMLStreamConstants.SPACE:
				characters(event);
				break;
			case XMLStreamConstants.CDATA:
//				System.out.println("cdata "
//						+ event.getLocation().getCharacterOffset());
				event.next();
				break;
			default:
//				System.out.println("undefined xml event "
//						+ event.getEventType());
				event.next();
				break;
			}
		}
	}

	private void startDocument(XMLStreamReader event) throws XMLStreamException {
		this.elementStack.removeAllElements();
		this.textBuffer = new ArrayList<Node>();
		this.udm.setXmlDeclaration(event);
	}
	
	private void endDTD(XMLStreamReader event) throws XMLStreamException{
		this.udm.setDTD(event);
	}

	private void startElement(XMLStreamReader event) throws XMLStreamException {
		addTextIfNeeded();
		
		QName name = event.getName();
		final Element el = doc.createElementNS(name.getNamespaceURI(), name.getLocalPart());
		el.setPrefix(name.getPrefix());
		
		
		

		for (int i = 0; i < event.getAttributeCount(); i++) {
			String attrName = event.getAttributeLocalName(i);
			String ns = event.getAttributeNamespace(i);
			String prefix = event.getAttributePrefix(i);
			
			if (ns == null || ns.equals("")) {
				el.setAttribute(attrName, event.getAttributeValue(i));
			} else {
				el.setAttributeNS(ns, attrName, event.getAttributeValue(i));
				el.getAttributeNodeNS(ns, attrName).setPrefix(prefix);
			}

			// was ist mit Namespace?
//			Attr attrNode = el.getAttributeNode(attrName);

			// set user data:
			// setUserData(el, attrNode);
		}
		
		baseUri(el);
		
		// set User data:
		this.udm.setStartElement(el, event);

		elementStack.push(el);
	}

	private void endElement(XMLStreamReader event) throws XMLStreamException {
		addTextIfNeeded();
		final Element closedEl = elementStack.pop();


		if (elementStack.isEmpty()) { // Is this the root element?
			doc.appendChild(closedEl);
		} else {
			final Element parentEl = elementStack.peek();
			parentEl.appendChild(closedEl);
		}
		// set User data:
		this.udm.setEndElement(closedEl, event);
	}

	private void characters(XMLStreamReader event) throws SAXException, XMLStreamException {
		String appendText = event.getText();
		final Node textNode = doc.createTextNode(appendText);
		
		this.textBuffer.add(textNode);

		// set User data:
		this.udm.setNode(textNode, event, true);

	}

	private void processingInstruction(XMLStreamReader event) throws XMLStreamException, IllegalStateException {
		addTextIfNeeded();

		final Node el = elementStack.isEmpty() ? doc : elementStack.peek();
		final Node pi = doc.createProcessingInstruction(event.getPITarget(),
				event.getPIData());

		el.appendChild(pi);

		// user data:
		this.udm.setNode(pi, event);
	}

	private void comment(XMLStreamReader event) throws XMLStreamException {
		addTextIfNeeded();
		final Node el = elementStack.isEmpty() ? doc : elementStack.peek();
		final String comment = event.getText();
		Node commentNode = doc.createComment(comment);

		el.appendChild(commentNode);
		
		
		// set user data:
		this.udm.setNode(commentNode, event);
	}

	// Text buffer
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
			
			this.udm.appendToTextNodeBundle(newTextNode, this.textBuffer);
			

			el.appendChild(newTextNode);
			this.textBuffer = new ArrayList<Node>();
		}
	}
	
	private void baseUri(Element node){
		if(this.elementStack.isEmpty()){
			node.setAttributeNS(ProcessNamespaces.XML_NS, "base", initialBaseUri);
		}
	}
	
}
