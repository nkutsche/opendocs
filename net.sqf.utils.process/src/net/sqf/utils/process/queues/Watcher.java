package net.sqf.utils.process.queues;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

class Watcher extends SimpleTask<Void, Void> {
	
	private final ArrayList<WatchTask> tasks = new ArrayList<WatchTask>();
	
	private int intervall;
	public int counter = 0;
	private boolean isProcessing = true;

	protected Watcher(String title, double intervallInSec) {
		super(title, Void.class, Void.class);
		this.setInterval(intervallInSec);
	}

	@Override
	public Void process(Void Input) throws Exception {
		while (isProcessing) {
			synchronized (tasks) {
				try {
					for (WatchTask task : tasks) {
						task.watch();
					}
				} catch (ConcurrentModificationException e) {
					System.err.println("catched concurrent modification exception!");
				}
				counter++;
			}
			Thread.sleep(intervall);
		}
		return null;
	}
	


	public void cancel(){
		this.isProcessing = false;
	}

	public void setInterval(double intervallInSec) {
		this.intervall = (int) (intervallInSec * 1000.0);
		
	}
	
	public void addTask(WatchTask task){
		this.tasks.add(task);
	}
}
