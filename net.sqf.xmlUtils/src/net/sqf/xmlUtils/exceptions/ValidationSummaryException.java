package net.sqf.xmlUtils.exceptions;

import java.util.ArrayList;
import java.util.Iterator;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ValidationSummaryException extends Exception {
	

	
	private static final long serialVersionUID = 4684265811930822469L;
	private final ArrayList<ValidationException> exceptionList;
	private final String title;
	

	private static String createMessage(String title, ArrayList<ValidationException> exceptionList){
		String errorMessage = title + "\n";
		for (Iterator<ValidationException> i = exceptionList.iterator(); i.hasNext();) {
			ValidationException error =  i.next();
			errorMessage += error.getLocalizedMessage() + "\n";
		}
		return errorMessage;
	}
	
	public static ValidationSummaryException createValidationSummary(String title, ArrayList<SAXParseException> errorList){
		ArrayList<ValidationException> veList = new ArrayList<ValidationException>();
		for (SAXParseException saxE : errorList) {
			veList.add(new ValidationException(saxE));
		}
		return new ValidationSummaryException(title, veList);
	}

	public static ValidationSummaryException createValidationSummary(String title, ArrayList<SAXParseException> errorList, String systemId){
		ArrayList<ValidationException> veList = new ArrayList<ValidationException>();
		for (SAXParseException saxE : errorList) {
			veList.add(new ValidationException(saxE, systemId));
		}
		return new ValidationSummaryException(title, veList);
	}
	
	public ValidationSummaryException(String title, ArrayList<ValidationException> exceptionList) {
		super(title);
		this.title = title;
		this.exceptionList = exceptionList;
	}

	public String getTitle() {
		return title;
	}

	public ArrayList<ValidationException> getExceptionList() {
		return exceptionList;
	}
	
	public void addException(ValidationSummaryException vse){
		this.addException(vse.getExceptionList());
	}
	
	public void addException(ArrayList<ValidationException> exs){
		this.exceptionList.addAll(exs);
	}
	

	public void addException(ValidationException ve){
		this.exceptionList.add(ve);
	}
	
	public void addException(SAXException se){

		this.exceptionList.add(new ValidationException(se));
	}
	
	public void addException(Exception e){
		this.exceptionList.add(new ValidationException(e));
	} 
	
	@Override
	public String getMessage() {
		return createMessage(getTitle(), getExceptionList());
	}
	
}
