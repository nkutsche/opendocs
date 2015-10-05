package net.sqf.utils.process.log;

import java.util.Date;

import net.sqf.utils.process.exceptions.CancelException;

public class DefaultProcessLoger implements ProcessLoger {

	@Override
	public void log(String message, boolean taskEnd) {
		log(message);
	}

	@Override
	public void log(String message) {
		System.out.println(new Date().toString() + ": " + message);
	}

	@Override
	public void log(Exception exception) throws CancelException {
		exception.printStackTrace();
	}

	@Override
	public void log(Exception exception, boolean forceEnd)
			throws CancelException {
		log(exception);
	}

	@Override
	public void end() {
		
	}


}
