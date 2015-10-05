package net.sqf.utils.process.queues.listeners;

import net.sqf.utils.process.queues.Task;




public interface QueueListener<IOType> {

	public void processStart(Task<IOType> currentTask);
	public void processCancel(Task<IOType> currentTask, Exception e);
	public void processEnd(Task<IOType> currentTask);
}
