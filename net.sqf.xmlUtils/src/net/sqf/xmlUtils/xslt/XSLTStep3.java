package net.sqf.xmlUtils.xslt;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sqf.stringUtils.TextSource;
import net.sqf.xmlUtils.exceptions.XSLTErrorListener;

public class XSLTStep3 {

	private static TransformerFactory transfac;
	private static XSLTErrorListener factoryErrorListener;
	
	static {
		try {
			factoryErrorListener = new XSLTErrorListener();
			transfac = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
			transfac.setErrorListener(factoryErrorListener);
		} catch (Exception e){
			System.err.println(e.getMessage());
			
		}
		
	}

	private final Transformer transformer;
	
	private XSLTErrorListener listener = new XSLTErrorListener();
	
//	public class XSLTErrorListener extends Exception implements ErrorListener {
//
//		private static final long serialVersionUID = -5657828660296028068L;
//		
//		private ArrayList<TransformerException> warnings = new ArrayList<TransformerException>();
//		private ArrayList<TransformerException> errors = new ArrayList<TransformerException>();
//		private ArrayList<TransformerException> fatalErrors = new ArrayList<TransformerException>();
//		
//		@Override
//		public void warning(TransformerException arg0)
//				throws TransformerException {
//			warnings.add(arg0);
//		}
//
//		@Override
//		public void fatalError(TransformerException arg0)
//				throws TransformerException {
//			fatalErrors.add(arg0);
//		}
//
//		@Override
//		public void error(TransformerException arg0)
//				throws TransformerException {
//			errors.add(arg0);
//
//		}
//		
//		@Override
//		public String getLocalizedMessage() {
//			String message = "";
//			for (TransformerException exc : warnings) {
//				message += exc.getLocalizedMessage();
//			}
//			for (TransformerException exc : errors) {
//				message += exc.getLocalizedMessage();
//			}
//			for (TransformerException exc : fatalErrors) {
//				message += exc.getLocalizedMessage();
//			}
//			return message;
//		}
//		
//		@Override
//		public String getMessage() {
//			String message = "";
//			for (TransformerException exc : warnings) {
//				message += exc.getMessage();
//			}
//			for (TransformerException exc : errors) {
//				message += exc.getMessage();
//			}
//			for (TransformerException exc : fatalErrors) {
//				message += exc.getMessage();
//			}
//			return message;
//		}
//		public boolean hasWarnings(){
//			return warnings.size() > 0; 
//		}
//		public boolean hasNormalErrors(){
//			return errors.size() > 0; 
//		}
//		public boolean hasFatalErrors(){
//			return fatalErrors.size() > 0; 
//		}
//		public boolean hasErrors(){
//			return hasWarnings() || hasNormalErrors() || hasFatalErrors();
//		}
//	}
	
//	public XSLTStep(InputStream virtualXSL, ArrayList<Parameter> params) throws TransformerConfigurationException{
//		this(SaxonUtils.getStreamSource(virtualXSL), params);
//	}
	
	public XSLTStep3(TextSource virtualXSL, XSLTErrorListener errorListener, ArrayList<Parameter> params) throws XSLTErrorListener {
		this(SaxonUtils.getStreamSource(virtualXSL), errorListener, params);
	}

	public XSLTStep3(File xslsheet, XSLTErrorListener errorListener, ArrayList<Parameter> params) throws XSLTErrorListener {
		this(SaxonUtils.getStreamSource(xslsheet), errorListener, params);
	}
//	ErrorListener elt = transfac.getErrorListener();
	public XSLTStep3(Source xsl, final XSLTErrorListener el, ArrayList<Parameter> params) throws XSLTErrorListener{
		this.listener = el;
//		transfac.setErrorListener(el);
		try {
			transformer = transfac.newTransformer(xsl);
		} catch (TransformerConfigurationException e) {
			try {
				listener.fatalError(e);
				listener.copyErrors(factoryErrorListener);
				factoryErrorListener.clear();
				
				throw listener;

			} catch (TransformerException e1) {
				throw factoryErrorListener;
			}
		}
		transformer.setErrorListener(el);
		transformer.setURIResolver(new URIResolver() {
			
			@Override
			public Source resolve(String arg0, String arg1) throws TransformerException {
				System.out.println(arg0);
				System.out.println(arg1);
				return null;
			}
		});
		setParameters(params);
	}
	public XSLTStep3(Source xslStream, ArrayList<Parameter> params) throws XSLTErrorListener{
		this(xslStream, new XSLTErrorListener(), params);
	}
	
	public XSLTStep3(TextSource virtualXSL, ArrayList<Parameter> params) throws XSLTErrorListener{
		this(virtualXSL, new XSLTErrorListener(), params);
	}
	
	public XSLTStep3(File xslsheet, ArrayList<Parameter> params) throws XSLTErrorListener{
		this(xslsheet, new XSLTErrorListener(), params);
	}
	
	public void setOutputProperty(Properties props){
		transformer.setOutputProperties(props);
	}
	
	private void setParameters(ArrayList<Parameter> params){
		for (Iterator<Parameter> i = params.iterator(); i.hasNext();) {
			Parameter p = i.next();
			this.transformer.setParameter(p.getQName(), p.getValue());
		}
	}
	
	public TextSource transform(TextSource source, ArrayList<Parameter> params){
		setParameters(params);
		return transform(source, source.getFile());
	}
	
	public TextSource transform(TextSource source){
		return transform(source, source.getFile());
	}
	
	public TextSource transform(TextSource source, File outFile, ArrayList<Parameter> params){
		setParameters(params);
		return transform(source, outFile);
	}
	
	public TextSource transform(TextSource source, File outFile){
		StreamSource ss = new StreamSource(new StringReader(source.toString()));
		ss.setSystemId(source.getFile());
		StringWriter writer = new StringWriter();
		Result result = new StreamResult(writer);
		result.setSystemId(outFile.getAbsolutePath());
		transform(ss, result);
		TextSource resultSource = TextSource.createVirtualTextSource(outFile);
		resultSource.setData(writer.toString());
		return resultSource;
	}
	
	
	private Result transform(Source source, Result result){
//		this.transformer.reset();
//		this.transformer.clearParameters();
		try {
			this.transformer.transform(source, result);
		} catch (TransformerException e) {
			try {
				this.listener.error(e);
			} catch (TransformerException e1) {
				e1.printStackTrace();
			}
		}
		return result;
	}

	public ArrayList<TransformerException> getErrors(int level) {
		return listener.getErrors(level);
	}

	public ArrayList<TextSource> getSecondaryResults() {
		return new ArrayList<TextSource>();
	}
	
	

}
