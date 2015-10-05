package net.sqf.xmlUtils.exceptions;

import org.xml.sax.SAXParseException;

public class ValidationException extends Exception {
	
	private static final long serialVersionUID = -6373791422783698008L;

	public final static String TYPE_SCHEMA = "XSD";
	public final static String TYPE_SCHEMATRON = "SCH";
	public final static String TYPE_OTHER = "OTHER";
	
	private final String systemId;
	private final int lineNumber;
	private final int colNumber;
	private final String type;
	
	public ValidationException(Exception e) {
		this(e.getMessage(), "", -1, -1, TYPE_OTHER);
	}
	

	public ValidationException(Exception e, String systemId) {
		this(e, systemId, -1, -1);
	}
	
	public ValidationException(Exception e, String systemId, int lineNumber, int colNumber) {
		this(e.getMessage(), systemId, lineNumber, colNumber, TYPE_OTHER);
	}
	public ValidationException(SAXParseException ex, String systemId) {
		this(ex.getMessage(), ex.getSystemId() == null ? systemId : ex.getSystemId(), ex.getLineNumber(), ex.getColumnNumber(), TYPE_SCHEMA);
	}
	public ValidationException(SAXParseException ex) {
		this(ex.getMessage(), ex.getSystemId(), ex.getLineNumber(), ex.getColumnNumber(), TYPE_SCHEMA);
	}

	public ValidationException(String message, String systemId, int lineNumber, int colNumber, String type) {
		super(message);
		this.systemId = systemId;
		this.lineNumber = lineNumber;
		this.colNumber = colNumber;
		this.type = type;
	}

	public String getSystemId() {
		return systemId;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public int getColNumber() {
		return colNumber;
	}
	
	public String getType() {
		return type;
	}


	@Override
	public String getLocalizedMessage() {
		return "systemId: " + getSystemId() +"; lineNumber: " + getLineNumber() + "; columnNumber: " + getColNumber() + "; " + super.getLocalizedMessage();
	};
}
