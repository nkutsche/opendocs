package net.sqf.utils.process.log;

import net.sqf.utils.process.exceptions.CancelException;

public interface ProcessLoger {
	public void log(String message, boolean taskEnd);
	public void log(String message);

	public void log(Exception exception) throws CancelException;
	public void log(Exception exception, boolean forceEnd) throws CancelException;
	public void end();
	
}
