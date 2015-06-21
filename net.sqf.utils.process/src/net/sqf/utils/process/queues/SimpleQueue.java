package net.sqf.utils.process.queues;

import java.util.ArrayList;

import net.sqf.utils.process.log.DefaultProcessStateListener;
import net.sqf.utils.process.log.ProcessStateListener;
import net.sqf.utils.process.queues.listeners.QueueEndListener;
import net.sqf.utils.process.queues.listeners.QueueListener;

public class SimpleQueue<IOType> implements QueueListener<IOType> {
	private ArrayList<Task<IOType>> tasks = new ArrayList<Task<IOType>>();
	private ProcessStateListener psl = new DefaultProcessStateListener();
	private final String description;
	private ArrayList<Exception> exceptions = new ArrayList<Exception>();
	
	private ArrayList<IOType> tempResults = new ArrayList<IOType>();
	private Task<IOType> currentTask;

	private ArrayList<QueueEndListener<IOType>> endListener = new ArrayList<QueueEndListener<IOType>>();
	private final ArrayList<QueueListener<IOType>> cancelListener = new ArrayList<QueueListener<IOType>>();
	
	public SimpleQueue(String description, ProcessStateListener psl){
		this.description = description;
		if(psl != null){
			this.psl = psl;
		}
		
	}
	
	
	public void addTask(Task<IOType> task){
		
		this.tasks.add(task);
	}
	
	public void addTask(ArrayList<Task<IOType>> tasks){
		this.tasks.addAll(tasks);
	}
	
	public void start(final IOType input){
		this.exceptions = new ArrayList<Exception>();
		this.tempResults = new ArrayList<IOType>();
		this.tempResults.add(input);
		
		for (final Task<IOType> task : tasks) {
			task.addCancelListener(this);
			task.addEndListener(this);
			task.addStartListener(this);
		}
		
		tasks.get(0).start(input);
	}
	

	
	public IOType get() throws Exception{
		if(isCanceled()){
			throw exceptions.get(0);
		}
		return tasks.get(tasks.size() - 1).get();
	}


	@Override
	public void processCancel(Task<IOType> currentTask, Exception e) {
		this.exceptions.add(e);
		this.psl.end(e);
		this.cancel(e);
	};


	@Override
	public void processStart(Task<IOType> currentTask) {
		this.currentTask = currentTask;
		this.psl.setProcessState(getProgress(), currentTask.toString());
	}
	
	private int getTaskIndex(Task<IOType> currentTask){
		return this.tasks.indexOf(currentTask);
	}
	
	private double getProgress(){
		return (getTaskIndex(currentTask) + 0.0) / this.tasks.size();
	}
	
	@Override
	public void processEnd(Task<IOType> currentTask) {
		int i = getTaskIndex(currentTask);
		if(isCanceled()){
//			this.psl.setProcessState(getProgress(), currentTask.toString() + " (canceled by Exception)");
		} else if(i + 1 < tasks.size()){
			Task<IOType> nextTask = this.tasks.get(i + 1);
			try {
				IOType temp = currentTask.get();
				this.tempResults.add(temp);
				nextTask.start(temp);
			} catch (Exception e) {
				nextTask.cancel(e);
			}
		} else {
//			implement finish queue
			try {
				this.tempResults.add(currentTask.get());
				this.psl.setProcessState(1.0, this.toString());
				processEndListener();
				this.psl.end();
			} catch (Exception e) {
				this.processCancel(currentTask, e);
			}
		}
		
	}
	
	public ArrayList<IOType> getTempResults(){
		return this.tempResults;
	}

	private boolean isCanceled() {
		// TODO Auto-generated method stub
		return exceptions.size() > 0;
	}

	public void addCancelListener(QueueListener<IOType> listener){
		this.cancelListener.add(listener);
	}
	
	protected void cancel(Exception e){
		for (QueueListener<IOType> lis : this.cancelListener) {
			lis.processCancel(this.currentTask, e);
		}
	}
	
	
	public void addEndListener(QueueEndListener<IOType> listener) {
		this.endListener.add(listener);
	}

	private void processEndListener() {
		for (QueueEndListener<IOType> lis : this.endListener) {
			try {
				lis.finish(get());
			} catch (Exception e) {
				cancel(e);
			}
		}
	}

	@Override
	public String toString() {
		return this.description;
	}
	
	
}
