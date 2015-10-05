package net.sqf.utils.process.log;

import net.sqf.utils.process.exceptions.CancelException;

public class MuteProcessLoger implements ProcessLoger {

	@Override
	public void log(String message, boolean taskEnd) {}

	@Override
	public void log(String message) {}

	@Override
	public void log(Exception exception) throws CancelException {
		throw new CancelException(exception.getMessage());
	}

	@Override
	public void log(Exception exception, boolean forceEnd)
			throws CancelException {
		if(forceEnd){
			throw new CancelException(exception.getMessage());
		}
	}

	@Override
	public void end() {}

}
