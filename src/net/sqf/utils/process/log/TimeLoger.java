package net.sqf.utils.process.log;

import java.util.ArrayList;
import java.util.Date;

public class TimeLoger extends DefaultProcessLoger implements ProcessLoger {
	private final ArrayList<Log> logs = new ArrayList<TimeLoger.Log>();
	private final Date startTime = new Date();
	
	private class Log {
		final Date d = new Date();
		final String m;
		public Log(String message) {
			m = message;
		}
	}
	
	@Override
	public void log(String message) {
		this.logs.add(new Log(message));
	}
	
	@Override
	public void log(String message, boolean taskEnd) {
		this.log(message);
	}
	
	@Override
	public String toString() {
		String s = "";
		int i=0;
		for (Log l : this.logs) {
			s += l.d + " (" + getDiffInSec(i) + "s) : " + l.m + "\n";
			i++;
		}
		return s;
	}
	
	private long getDiff(int step){
		if(step < 0 || step >= logs.size()){
			return 0;
		}
		Date start = step == 0 ? startTime : logs.get(step - 1).d;
		Date end = logs.get(step).d;
		return end.getTime() - start.getTime();
	}
	
	public long getDiff(){
		int step = logs.size() - 1;
		return logs.get(step).d.getTime() - startTime.getTime();
	}

	public double getDiffInSec(int step){
		return getDiff(step) / 1000.0;
	}
	public double getDiffInSec(){
		return getDiff() / 1000.0;
	}
}
