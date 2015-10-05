package net.sqf.xmlUtils.xpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;

import net.sqf.xmlUtils.staxParser.PositionalXMLReader;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ctc.wstx.cfg.ErrorConsts;

public class NamespaceContextImpl implements NamespaceContext {

	private HashMap<String, String> uriByPrefix = new HashMap<String, String>();
	private HashMap<String, String> prefixByUri = new HashMap<String, String>();
	private ArrayList<String> allPrefies = new ArrayList<String>();

	public NamespaceContextImpl(Element el) {
		Node parent = el.getParentNode();
		Object parentNSC = parent
				.getUserData(PositionalXMLReader.NAMESPACE_CONTEXT);
		Object childNSC = el.getUserData(PositionalXMLReader.NAMESPACE_CONTEXT);
		if (parentNSC != null)
			addAllNs((NamespaceContextImpl) parentNSC);
		if (childNSC != null)
			addAllNs((NamespaceContextImpl) childNSC);
	}

	public NamespaceContextImpl(XMLStreamReader event) {
		for (int i = 0; i < event.getNamespaceCount(); i++) {
			String uri = event.getNamespaceURI(i);
			String pre = event.getNamespacePrefix(i);
			addNS(pre, uri);
		}
	}

	private void addAllNs(NamespaceContextImpl nsc) {
		for (Iterator<String> i = nsc.allPrefies.iterator(); i.hasNext();) {
			String pre = i.next();
			String uri = nsc.getNamespaceURI(pre);
			addNS(pre, uri);
		}
	}

	private void addNS(String pre, String uri) {
		uriByPrefix.put(pre, uri);
		prefixByUri.put(uri, pre);
		allPrefies.add(pre);
	}

	@Override
	public String getNamespaceURI(String prefix) {
		if (prefix == null) {
			throw new IllegalArgumentException(ErrorConsts.ERR_NULL_ARG);
		}
		if (prefix.equals(XMLConstants.XML_NS_PREFIX)) {
			return XMLConstants.XML_NS_URI;
		}
		if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
			return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
		}
		return uriByPrefix.get(prefix);
	}

	@Override
	public String getPrefix(String uri) {
		if (uri == null || uri.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (uri.equals(XMLConstants.XML_NS_URI)) {
            return XMLConstants.XML_NS_PREFIX;
        }
        if (uri.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            return XMLConstants.XMLNS_ATTRIBUTE;
        }
		return prefixByUri.get(uri);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Iterator getPrefixes(String arg0) {
		return null;
	}
	
	public ArrayList<String> getAllPrefixes(){
		return this.allPrefies;
	}

}
