package net.sqf.xmlUtils.xsd;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.transform.Source;

import net.sqf.stringUtils.TextSource;
import net.sqf.xmlUtils.exceptions.ValidationSummaryException;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.parsers.StandardParserConfiguration;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;



public class Xerces {
	private final DOMParser domp;
	private ArrayList<SAXParseException> errorList = new ArrayList<SAXParseException>();
	private ArrayList<SAXParseException> warningList = new ArrayList<SAXParseException>();
	
	@SuppressWarnings("unused")
	private class SQFSaxParseException {
		public final SAXParseException e;
		private final int level;
		public SQFSaxParseException(SAXParseException e, int level){
			this.e = e;
			this.level = level;
		}
	}
	
	private ErrorHandler handler = new ErrorHandler() {
		
		@Override
		public void warning(SAXParseException exception) throws SAXException {
			warningList.add(exception);
		}
		
		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			errorList.add(exception);
		}
		
		@Override
		public void error(SAXParseException exception) throws SAXException {
			errorList.add(exception);
		}
	};
	
	private Xerces(String schemaProperty, String schemaDescriptor) throws SAXNotRecognizedException, SAXNotSupportedException{
		this.domp = new DOMParser(new StandardParserConfiguration());
		domp.setFeature("http://xml.org/sax/features/validation", true);
		domp.setFeature("http://apache.org/xml/features/validation/schema",
				true);
		domp.setFeature(
				"http://apache.org/xml/features/validation/schema-full-checking",
				true);
//		domp.setFeature("http://apache.org/xml/features/xinclude", true);
		domp.setFeature(
				"http://apache.org/xml/features/honour-all-schemaLocations",
				true);
		domp.setProperty(schemaProperty, schemaDescriptor);
		domp.setErrorHandler(this.handler);
	}
	
	public Xerces(File schema) throws MalformedURLException, SAXNotRecognizedException, SAXNotSupportedException{
		this("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", schema.toURI().toURL().toString());
	}
	
	public Xerces(String namespace, File schema) throws SAXNotRecognizedException, SAXNotSupportedException, MalformedURLException {
		this("http://apache.org/xml/properties/schema/external-schemaLocation", namespace + " " + schema.toURI().toURL().toString());
	}
	
	public Xerces(String namespace, Source schema) throws SAXNotRecognizedException, SAXNotSupportedException, MalformedURLException {
		this("http://apache.org/xml/properties/schema/external-schemaLocation", namespace + " " + schema.getSystemId());
	}
	
	private void validateSource(InputSource source) throws IOException, SAXException, ValidationSummaryException {
		this.errorList = new ArrayList<SAXParseException>();
		String instanceFile = source.getSystemId();
		
		try {
			domp.parse(source);
		} catch (SAXException e) {
			throw e;
		} catch (IOException e) {
			throw new IOException("File " + instanceFile + "not found!");
		}
		
		if(this.errorList.size() > 0)
			throw ValidationSummaryException.createValidationSummary("Schema error(s) in the instance " + instanceFile + ":", this.errorList, instanceFile);
		if(this.warningList.size() > 0){
			ValidationSummaryException exc = ValidationSummaryException.createValidationSummary("Schema warnings(s) in the instance " + instanceFile + ":", this.warningList, instanceFile);
			System.err.println(exc.getMessage());
		}
	}

	public void validateSource(TextSource source) throws IOException, SAXException, ValidationSummaryException {
		InputSource is = new InputSource(new StringReader(source.toString()));
		is.setSystemId(source.getFile().toURI().toString());
		validateSource(is);
	}
	public void validateSource(File instanceFile) throws IOException,  SAXException, ValidationSummaryException {
		this.errorList = new ArrayList<SAXParseException>();
		
		try {
			domp.parse(new InputSource(instanceFile.toURI().toString()));
		} catch (SAXException e) {
			throw e;
		} catch (IOException e) {
			throw new IOException("File " + instanceFile + "not found!");
		}
		
		if(this.errorList.size() > 0)
			throw ValidationSummaryException.createValidationSummary("Schema error(s) in the instance " + instanceFile.getName() + ":", this.errorList);
		if(this.warningList.size() > 0){
			ValidationSummaryException exc = ValidationSummaryException.createValidationSummary("Schema warnings(s) in the instance " + instanceFile.getName() + ":", this.warningList);
			System.err.println(exc.getMessage());
		}
			
	}
	
	
	
//	private SAXException createErrorMessage(String title, ArrayList<SAXParseException> exceptionList) {
//		String errorMessage = title + "\n";
//		for (Iterator<SAXParseException> i = exceptionList.iterator(); i.hasNext();) {
//			SAXParseException error =  i.next();
//			errorMessage += "systemId: " + error.getSystemId() +"; lineNumber: " + error.getLineNumber() + "; columnNumber: " + error.getColumnNumber() + "; " + error.getMessage();
//			errorMessage += "\n";
//		}
//		return new SAXException(errorMessage);
//	}

	public static void main(String[] args) {
		File instance = new File(args[0]);
		File schema = new File(args[1]);
		Xerces xerc;
		try {
		if(args.length > 3){
			String namespace = args[2];
			xerc = new Xerces(namespace, schema);
		} else {
				xerc = new Xerces(schema);
		}
		
		xerc.validateSource(instance);
		
		} catch (MalformedURLException e) {
			System.err.println(e.getLocalizedMessage());
		} catch (SAXNotRecognizedException e) {
			System.err.println(e.getLocalizedMessage());
		} catch (SAXNotSupportedException e) {
			System.err.println(e.getLocalizedMessage());
		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
		} catch (SAXException e) {
			System.err.println(e.getLocalizedMessage());
		} catch (ValidationSummaryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
