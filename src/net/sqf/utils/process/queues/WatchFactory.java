package net.sqf.utils.process.queues;

public class WatchFactory {
	private static Watcher watcher;
	private static void instanceWatcher(){
		instanceWatcher(0.5);
	}
	private static void instanceWatcher(double interval){
		if(watcher == null){
			watcher = new Watcher("Interval watcher", interval);
			watcher.start(null);
		}
	}
	
	public static void setWatcherInterval(double interval){
		if(watcher == null){
			instanceWatcher(interval);
		} else {
			watcher.setInterval(interval);
		}
	}
	
	public static void addWatchTask(WatchTask task){
		if(watcher == null){
			instanceWatcher();
		}
		watcher.addTask(task);
	}
}
