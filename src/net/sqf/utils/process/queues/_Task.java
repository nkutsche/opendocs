package net.sqf.utils.process.queues;

import java.beans.PropertyChangeListener;

public interface _Task<Input, Output> {

	public abstract void start(Input input);

	public abstract Output getOutput() throws Exception;

	public abstract void addPropertyChangeListener(
			PropertyChangeListener listener);

	public abstract int getProgress();

	public abstract boolean isFinished();
	
	public abstract Class<Input> getInputClass();
	
	public abstract Class<Output> getOutputClass();

//	public abstract int getFinishedCount();
//
//	public abstract int getTaskCount();

}