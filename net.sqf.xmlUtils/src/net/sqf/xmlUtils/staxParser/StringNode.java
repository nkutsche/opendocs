package net.sqf.xmlUtils.staxParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;

import net.sqf.stringUtils.TextSource;
import net.sqf.utils.process.log.DefaultProcessLoger;
import net.sqf.utils.process.log.ProcessLoger;
import net.sqf.xmlUtils.xpath.XPathReader;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class StringNode {
	private TextSource textSource;
	private Document docNode;
	private String absPath;
	private static PositionalXMLReader pxr = new PositionalXMLReader();
	private ProcessLoger processLogger = new DefaultProcessLoger();
	
	public StringNode(TextSource source) throws IOException, SAXException, XMLStreamException{
		this(source, new DefaultProcessLoger());
		
	}
	
	public StringNode(TextSource source, ProcessLoger processLogger) throws IOException, SAXException, XMLStreamException{
		this.absPath = source.getFile().getAbsolutePath();
		this.processLogger = processLogger;
		setTextReader(source);
	}
	
	public StringNode(File file, ProcessLoger processLogger) throws IOException, SAXException, XMLStreamException{
		this(TextSource.readTextFile(file, false), processLogger);
	}
	
	public StringNode(File file) throws IOException, SAXException, XMLStreamException{
		this(TextSource.readTextFile(file, false));
	}
	
	@SuppressWarnings("unused")
	private StringNode(TextSource textReader, Document docNode, String absPath){
		this.textSource = textReader;
		this.docNode = docNode;
		this.absPath = absPath;
	}
	public void setTextReader(TextSource source) throws IOException, SAXException, XMLStreamException{
		TextSource backupSource = this.textSource;
		try {
			this.textSource = source;
			this.processLogger.log("Finished text reading, start parsing xml");
			actualizeNode();
			this.processLogger.log("Finished parsing xml");
		} catch (IOException e){
			this.textSource = backupSource;
			throw e;
		} catch(SAXException e) {
			this.textSource = backupSource;
			throw e;
		}
	}
	
	
	public void setString(String string, boolean parse) throws IOException, SAXException, XMLStreamException {
		String backupString = this.textSource.toString();
		try {
			this.textSource.updateData(string);
			if(parse){
				this.processLogger.log("Start parsing xml");
				actualizeNode();
			}
			this.processLogger.log("Finished parsing xml");
		} catch (IOException e){
			this.textSource.setData(backupString);
			throw e;
		} catch(SAXException e) {
			this.textSource.setData(backupString);
			throw e;
		} catch (XMLStreamException e) {
			this.textSource.setData(backupString);
			throw e;
		}
	}
	private void actualizeNode() throws IOException, SAXException, XMLStreamException{
		this.docNode = pxr.readXML(this.textSource);
	}
	
	public TextSource getTextSource(){
		return this.textSource;
	}
	public Document getDocument(){
		return this.docNode;
	}
	public String toString(){
		return this.textSource.toString();
	}
	public String getAbsPath(){
		return this.absPath;
	}
	
	public File getFile(){
		return this.textSource.getFile();
	}
	
	public void setProcessLogger(ProcessLoger logger){
		this.processLogger = logger;
	}
	
	private final XPathReader xpr = new XPathReader();
	
//	XPath:
	
	public Node getNode(String xpath) throws XPathExpressionException{
		return xpr.getNode(xpath, this.docNode);
	}
	
	public NodeList getNodeSet(String xpath) throws XPathExpressionException{
		return xpr.getNodeSet(xpath, this.docNode);
	}
	
	
	public NodeInfo getNodeInfo(String xpath) throws XPathExpressionException{
		return PositionalXMLReader.getNodeInfo(this.getNode(xpath));
	}
	
	public ArrayList<NodeInfo> getNodeSetInfo(String xpath) throws XPathExpressionException{
		return PositionalXMLReader.getNodeInfo(this.getNodeSet(xpath));
	}
	
	public String getNodeValue(String xpath) throws XPathExpressionException{
		return xpr.getString(xpath, this.docNode);
	}
	

	public boolean getXPathBoolean(String xpath) throws XPathExpressionException{
		return xpr.getBoolean(xpath, this.docNode);
	}
	
	public StringNode copy(){
		StringNode copy;
		try {
			copy = new StringNode(this.textSource.copy(), this.processLogger);
			return copy;
		} catch (Exception e) {
			return null;
		}
	}
}
