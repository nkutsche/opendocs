package net.sqf.xmlUtils.staxParser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sqf.xmlUtils.xpath.NamespaceContextImpl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class UserDataManager {
	private final Document doc;
	private String docBytes;

	private boolean skipWhitespace = true;

	public UserDataManager(Document doc, String docBytes) {
		this.doc = doc;
		this.docBytes = docBytes;
	}

	void setXmlDeclaration(XMLStreamReader event) throws XMLStreamException {
		Range range = getRangeAndNextEvent(event);

		doc.setUserData(
				PositionalXMLReader.NODE_LOCATION_START,
				newLocation(range.start, range.start.getCharacterOffset() * -1),
				null);
		doc.setUserData(
				PositionalXMLReader.NODE_LOCATION_START,
				newLocation(range.start, range.start.getCharacterOffset() * -1
						+ docBytes.length()), null);

		if (range.start.getCharacterOffset() == 0) {
			doc.setUserData(PositionalXMLReader.XML_DECL_LOCATION_START,
					range.start, null);
			doc.setUserData(PositionalXMLReader.XML_DECL_LOCATION_END,
					range.end, null);
		}
	}

	void setDTD(XMLStreamReader event) throws XMLStreamException {
		Range range = getRangeAndNextEvent(event);
		skipWhitespace(range);
		this.doc.setUserData(PositionalXMLReader.HAS_DTD, true, null);
		this.doc.setUserData(PositionalXMLReader.DTD_LOCATION_START,
				range.start, null);
		this.doc.setUserData(PositionalXMLReader.DTD_LOCATION_END, range.end,
				null);
	}

	void setNode(Node node, XMLStreamReader event) throws XMLStreamException {
		setNode(node, event, false);
	}

	void setNode(Node node, XMLStreamReader event, boolean textNode)
			throws XMLStreamException {
		NamespaceContext ns = event.getNamespaceContext();
		Range range = getRangeAndNextEvent(event);
		// if (textNode) {
		// // text nodes sometimes on
		// // content of element</
		// int beforeLastChar = this.docBytes[endLoc.getCharacterOffset() - 2];
		// int lastChar = this.docBytes[endLoc.getCharacterOffset() - 1];
		// int corr = beforeLastChar == '<' && lastChar == '/' ? -2 : lastChar
		// == '<' ? -1 : 0;
		// endLoc = newLocation(endLoc, corr);
		// }
		skipWhitespace(range);
		node.setUserData(PositionalXMLReader.NODE_LOCATION_START, range.start,
				null);
		node.setUserData(PositionalXMLReader.NODE_LOCATION_END, range.end, null);
		node.setUserData(PositionalXMLReader.NAMESPACE_CONTEXT, ns, null);
	}

	void setStartElement(Element element, XMLStreamReader event)
			throws XMLStreamException {
		NamespaceContext nsc = new NamespaceContextImpl(event);
		Range range = getRangeAndNextEvent(event);
		String startTag = this.getString(range);

		element.setUserData(PositionalXMLReader.NODE_LOCATION_START,
				range.start, null);
		element.setUserData(PositionalXMLReader.NODE_INNER_LOCATION_START,
				range.end, null);
		element.setUserData(PositionalXMLReader.NAMESPACE_CONTEXT, nsc, null);
		// element.setUserData(PositionalXMLReader.START_TAG, startTag, null);
		setAttribute(element, range.start, startTag);

		// if (element.hasAttributes()) {
		// String startTag = this.getString(range);
		// setAttribute(element, startTag, range.start,
		// element.getAttributes());
		// } else {
		// setAttribute(element, range.end);
		// }
		this.skipWhitespace = false;
	}

	// set attribute region for elements without attributes
	void setAttribute(Element el, Location start, String startTag) {

		// if(el.hasAttributes()){
		// NamedNodeMap attrMap = el.getAttributes();

		Pattern patternStart = Pattern.compile("\\s");
		Matcher matcherStart = patternStart.matcher(startTag);

		Pattern patternEnd = Pattern.compile("(/)?>$", Pattern.DOTALL);
		Matcher matcherEnd = patternEnd.matcher(startTag);

		int addEndPos = matcherEnd.find() ? matcherEnd.start() : startTag
				.length() - 1;
		int addStartPos = matcherStart.find() ? matcherStart.start()
				: addEndPos;


		if (addStartPos > 0 && addEndPos > 0) {
			el.setUserData(PositionalXMLReader.ATTRIBUTE_REGION_LOCATION_START,
					newLocation(start, addStartPos), null);
			el.setUserData(PositionalXMLReader.ATTRIBUTE_REGION_LOCATION_END,
					newLocation(start, addEndPos), null);

			String attrRegion = startTag.substring(addStartPos, addEndPos);
			el.setUserData(PositionalXMLReader.ATTRIBUT_REGION, attrRegion,
					null);
		}

		// for (int i = 0; i < attrMap.getLength(); i++) {
		// setAttribute(startTag, range.start, attrMap.item(i));
		// }
		// } else {
		// el.setUserData(PositionalXMLReader.ATTRIBUTE_REGION_LOCATION_START,
		// newLocation(range.end, -1), null);
		// el.setUserData(PositionalXMLReader.ATTRIBUTE_REGION_LOCATION_END,
		// newLocation(range.end, -1), null);
		// }

	}

	// void setAttribute(String startTag, Location locStart, Node attr) {
	//
	// Matcher matcher = attrPattern.matcher(startTag);
	// // debug-error! can not be debugged...
	// while (matcher.find()) {
	// String name = matcher.group(1);
	// if (name.equals(attr.getNodeName())) {
	//
	// MatchResult mresult = matcher.toMatchResult();
	// int startPos = mresult.start();
	// int endPos = mresult.end();
	// attr.setUserData(PositionalXMLReader.NODE_LOCATION_START,
	// newLocation(locStart, startPos), null);
	// attr.setUserData(PositionalXMLReader.NODE_LOCATION_END,
	// newLocation(locStart, endPos), null);
	// break;
	// }
	// }
	// }

	void setEndElement(Element element, XMLStreamReader event)
			throws XMLStreamException {
		Node parent = element.getParentNode();
		boolean isEmpty = element.getFirstChild() == null;
		Range range = getRangeAndNextEvent(event);
		Location startLoc = PositionalXMLReader.getLocation(element,
				PositionalXMLReader.NODE_LOCATION_START);
		if (isEmpty
				&& startLoc.getCharacterOffset() == range.start
						.getCharacterOffset()) {
			element.setUserData(PositionalXMLReader.NODE_INNER_LOCATION_START,
					range.end, null);
			element.setUserData(PositionalXMLReader.NODE_INNER_LOCATION_END,
					range.end, null);
			String startTag = getString(new Range(startLoc, range.end));
			// element.setUserData(PositionalXMLReader.START_TAG,
			// getString(starTagRange), null);
			setAttribute(element, startLoc, startTag);
		} else {
			element.setUserData(PositionalXMLReader.NODE_INNER_LOCATION_END,
					range.start, null);
		}
		element.setUserData(PositionalXMLReader.NODE_LOCATION_END, range.end,
				null);
		element.setUserData(PositionalXMLReader.NAMESPACE_CONTEXT,
				new NamespaceContextImpl(element), null);

		// setAttribute(element);
		this.skipWhitespace = parent.getNodeType() == Node.DOCUMENT_NODE;
	}

	// private Location correctStartLocation(Location endLoc) {
	// if (skipWhitespace) {
	// String passage = getString(this.precLocation, endLoc);
	// int corPos = passage.indexOf('<');
	// // not solved problem
	// // String skip = passage.substring(0, corPos);
	// // String[] skipLines = skip.split("\\r\\n");
	// // int corLine = skipLines.length - 1;
	// // int corCol = skipLines[corLine].length();
	// return newLocation(this.precLocation, corPos);
	// } else {
	// return this.precLocation;
	// }
	// }

	private String getString(Range range) {
		return this.docBytes.substring(range.start.getCharacterOffset(),
				range.end.getCharacterOffset());
		// return new String(this.docBytes, range.start.getCharacterOffset(),
		// range.end.getCharacterOffset() - range.start.getCharacterOffset());
	}

	private Location newLocation(final Location loc, int addPositon) {
		final int correctLineNumber = loc.getLineNumber();
		final int correctColumnNumber = loc.getColumnNumber();
		final int correctPosition = loc.getCharacterOffset() + addPositon;
		return new Location() {

			@Override
			public String getSystemId() {
				// TODO Auto-generated method stub
				return loc.getSystemId();
			}

			@Override
			public String getPublicId() {
				// TODO Auto-generated method stub
				return loc.getPublicId();
			}

			@Override
			public int getLineNumber() {
				// TODO Auto-generated method stub
				return correctLineNumber;
			}

			@Override
			public int getColumnNumber() {
				// TODO Auto-generated method stub
				return correctColumnNumber;
			}

			@Override
			public int getCharacterOffset() {
				// TODO Auto-generated method stub
				return correctPosition;
			}
		};
	}

	public void appendToTextNodeBundle(Node newTextNode,
			ArrayList<Node> textBuffer) {
		// TODO Auto-generated method stub
		Node first = textBuffer.get(0);
		Node last = textBuffer.get(textBuffer.size() - 1);
		newTextNode.setUserData(PositionalXMLReader.NODE_LOCATION_START,
				first.getUserData(PositionalXMLReader.NODE_LOCATION_START),
				null);
		newTextNode.setUserData(PositionalXMLReader.NODE_LOCATION_END,
				last.getUserData(PositionalXMLReader.NODE_LOCATION_END), null);
	}

	private void skipWhitespace(Range range) {
		if (this.skipWhitespace) {
			String passage = getString(range);
			int corPosStart = passage.indexOf('<');
			int corPosEnd = passage.lastIndexOf('>') - passage.length() + 1;
			// // not solved problem, colnumber / linenumber
			// // String skip = passage.substring(0, corPos);
			// // String[] skipLines = skip.split("\\r\\n");
			// // int corLine = skipLines.length - 1;
			// // int corCol = skipLines[corLine].length();
			range.start = newLocation(range.start, corPosStart);
			range.end = newLocation(range.end, corPosEnd);

		}
	}

	private Range getRangeAndNextEvent(XMLStreamReader event)
			throws XMLStreamException {
		Location start = event.getLocation();
		event.next();
		Location end = event.getLocation();
		Range range = new Range(start, end);
		return range;
	}

	private class Range {
		public Range(Location start, Location end) {
			this.start = start;
			this.end = end;
		}

		private Location start;
		private Location end;
	}
}
