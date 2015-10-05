package net.sqf.xmlUtils.xpath;

import java.io.IOException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XPathReader {
	
//	Attributes
	
	public String getAttributValue(Node node, String attrName, String namespaceURI, String defaultValue){
		NamedNodeMap attributes = node.getAttributes();
		if(attributes == null)
			return defaultValue;
		Node attrNode = namespaceURI.equals("") ? attributes.getNamedItem(attrName) : attributes.getNamedItemNS(namespaceURI, attrName);
		if(attrNode == null)
			return defaultValue;
		return attrNode.getNodeValue();
	}
	public String getAttributValue(Node node, String attrName){
		return getAttributValue(node, attrName, "", "");
	}
	
	public String getAttributValue(Node node, String attrName, String namespaceURI){
		return getAttributValue(node, attrName, namespaceURI, "");
	}
	
//	XPATH
	
	public String getString(String xpathString, Object node) throws XPathExpressionException{
		return this.getString(xpathString, node, ProcessNamespaces.NAMESPACES);
		}
	
	public String getString(String xpathString, Object node, NamespaceContext namespaces) throws XPathExpressionException{
	Object result = this.evaluate(xpathString,node,XPathConstants.STRING, namespaces);
	return (String) result;
}
	public Number getNumber(String xpathString, Object node) throws XPathExpressionException{
	return this.getNumber(xpathString, node, ProcessNamespaces.NAMESPACES);
	}
	
	public Number getNumber(String xpathString, Object node, NamespaceContext namespaces) throws XPathExpressionException{
	Number result = (Number) this.evaluate(xpathString,node,XPathConstants.NUMBER, namespaces);
	return (Number) result;
}

	public boolean getBoolean(String xpathString, Object node) throws XPathExpressionException{
	return this.getBoolean(xpathString, node, ProcessNamespaces.NAMESPACES);
	}
	
	public boolean getBoolean(String xpathString, Object node, NamespaceContext namespaces) throws XPathExpressionException{
	Object result = this.evaluate(xpathString,node,XPathConstants.BOOLEAN, namespaces);
	return (Boolean) result;
}
	
	public Node getNode(String xpathString, Object node) throws XPathExpressionException{
	return this.getNode(xpathString, node, ProcessNamespaces.NAMESPACES);
}
	
	public Node getNode(String xpathString, Object node, NamespaceContext namespaces) throws XPathExpressionException{
	Object result = this.evaluate(xpathString,node,XPathConstants.NODE, namespaces);
	return (Node) result;
}
	
	public NodeList getNodeSet(String xpathString, Object node) throws XPathExpressionException{
		return getNodeSet(xpathString, node, ProcessNamespaces.NAMESPACES);
	}
	
	public NodeList getNodeSet(String xpathString, Object node, NamespaceContext namespaces) throws XPathExpressionException{
	Object result = this.evaluate(xpathString,node,XPathConstants.NODESET, namespaces);
	return (NodeList) result;
}
	
	public Document readAsNodeSet(String src) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		Document doc = null;
		builder = factory.newDocumentBuilder();
		doc = builder.parse(src);
		return doc;
	}
	
	private Object evaluate(String xpathString, Object node, QName constant, NamespaceContext namespaces) throws XPathExpressionException{
		// Create a XPathFactory
		XPathFactory xFactory = XPathFactory.newInstance();

		// Create a XPath object
		XPath xpath = xFactory.newXPath();
		xpath.setNamespaceContext(namespaces);
		XPathExpression expr = xpath.compile(xpathString);
		Object result = expr.evaluate(node,constant);
		return result;
	}
	
	
}
