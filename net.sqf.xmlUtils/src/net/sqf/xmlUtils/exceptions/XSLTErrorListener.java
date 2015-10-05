package net.sqf.xmlUtils.exceptions;

import java.util.ArrayList;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

public class XSLTErrorListener extends Exception implements ErrorListener {

	public XSLTErrorListener() {

	}

	private static final long serialVersionUID = -5657828660296028068L;

	public static final int LEVEL_WARNING = 0;
	public static final int LEVEL_ERROR = 1;
	public static final int LEVEL_FATAL = 2;

	private class TransformError {

		private final TransformerException exception;
		private final int level;

		public TransformError(TransformerException exception, int level) {
			this.exception = exception;
			this.level = level;
		}
	}

	private ArrayList<TransformError> allErros = new ArrayList<TransformError>();
	private ArrayList<TransformerException> warnings = new ArrayList<TransformerException>();
	private ArrayList<TransformerException> errors = new ArrayList<TransformerException>();
	private ArrayList<TransformerException> fatalErrors = new ArrayList<TransformerException>();

	private void addError(TransformError err) {
		allErros.add(err);
		
		switch (err.level) {
		
		case LEVEL_WARNING:
			warnings.add(err.exception);
			break;
		case LEVEL_ERROR:
			errors.add(err.exception);
			break;
		case LEVEL_FATAL:
			fatalErrors.add(err.exception);
			break;

		default:
			break;
		}
	}

	@Override
	public void warning(TransformerException warning) throws TransformerException {
		addError(new TransformError(warning, LEVEL_WARNING));
	}

	@Override
	public void fatalError(TransformerException fatal)
			throws TransformerException {
		addError(new TransformError(fatal, LEVEL_FATAL));
	}

	@Override
	public void error(TransformerException error) throws TransformerException {
		addError(new TransformError(error, LEVEL_ERROR));

	}

	@Override
	public String getLocalizedMessage() {
		String message = "";
		for (TransformError exc : allErros) {
			message += exc.exception.getLocalizedMessage() + "\n";
		}
		return message;
	}

	@Override
	public String getMessage() {
		String message = "";
		for (TransformError exc : allErros) {
			message += exc.exception.getMessage() + "\n";
		}
		return message;
	}

	public boolean hasWarnings() {
		return warnings.size() > 0;
	}

	public boolean hasNormalErrors() {
		return errors.size() > 0;
	}

	public boolean hasFatalErrors() {
		return fatalErrors.size() > 0;
	}

	public boolean hasErrors() {
		return hasWarnings() || hasNormalErrors() || hasFatalErrors();
	}

	// level:
	// 0 : include warnings
	// 1 : just errors
	// 2 : just fatal errors
	public ArrayList<TransformerException> getErrors(int level) {
		ArrayList<TransformerException> errorList = new ArrayList<TransformerException>();
		for (TransformError error : this.allErros) {
			if(error.level >= level){
				errorList.add(error.exception);
			}
		}
		return errorList;
	}
	
	public void copyErrors(XSLTErrorListener copy){
		for (TransformError error : copy.allErros) {
			this.addError(error);
		}
	}

	public void clear() {
		this.allErros = new ArrayList<XSLTErrorListener.TransformError>();
		this.warnings = new ArrayList<TransformerException>();
		this.errors = new ArrayList<TransformerException>();
		this.fatalErrors = new ArrayList<TransformerException>();
	}
}