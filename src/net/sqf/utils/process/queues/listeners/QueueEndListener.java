package net.sqf.utils.process.queues.listeners;


public interface QueueEndListener<IOType> {

	
	public void finish(IOType result);
}
