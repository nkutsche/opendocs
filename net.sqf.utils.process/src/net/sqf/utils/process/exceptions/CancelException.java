package net.sqf.utils.process.exceptions;

public class CancelException extends Exception {
	private static final long serialVersionUID = -4869486387543095667L;
	public CancelException() {
		this("Process canceled by user");
	}
	public CancelException(String message) {
		super(message);
	}
}
