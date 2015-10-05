package net.sqf.xmlUtils.xslt;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.TransformerFactoryImpl;
import net.sqf.stringUtils.TextSource;

public class SaxonUtils {
	private static final ErrorListener errorListener = new ErrorListener() {
		
		@Override
		public void warning(TransformerException arg0) throws TransformerException {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void fatalError(TransformerException arg0)
				throws TransformerException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void error(TransformerException arg0) throws TransformerException {
			// TODO Auto-generated method stub
			
		}
	};

	public static TransformerFactoryImpl getTransFac(){
		TransformerFactoryImpl transfac = new TransformerFactoryImpl();
		transfac.setErrorListener(errorListener);
		return transfac;
	}
//	
	public static StreamSource[] getStreamSources(File[] stylesheets){
		StreamSource[] ss = new StreamSource[stylesheets.length];
		for (int i = 0; i < stylesheets.length; i++) {
			ss[i] = new StreamSource(stylesheets[i]);
		}
		return ss;
	}
	
	public static StreamSource castResultToSource(StreamResult result){
		StreamSource ss = new StreamSource(new StringReader(result.getWriter().toString()));
		String systemId = result.getSystemId();
		if(systemId != null){
			ss.setSystemId(new File(systemId));
		}
		return ss;
	}
	
	public static Source getStreamSource(InputStream stream){
		StreamSource ss = new StreamSource(stream);
		return ss;
	}
	
	public static Source getStreamSource(File file){
		return new StreamSource(file);
	}
	public static Source getStreamSource(TextSource textSource){
		StreamSource ss = new StreamSource(new StringReader(textSource.toString()));
		ss.setSystemId(textSource.getFile());
		return ss;
	}
}
