package net.sqf.utils.process.exceptions;


public interface ErrorViewer {
	public void viewException(Exception e) throws CancelException;
}
