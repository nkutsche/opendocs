package net.sqf.utils.process.queues;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.SwingWorker;

import net.sqf.utils.process.queues.listeners.QueueListener;

public abstract class Task<IOType> extends SwingWorker<IOType, IOType> implements PropertyChangeListener {
	
	private final String description;

	private IOType input = null;
	

	private ArrayList<QueueListener<IOType>> startListener = new ArrayList<QueueListener<IOType>>();
	private ArrayList<QueueListener<IOType>> endListener = new ArrayList<QueueListener<IOType>>();

	private final ArrayList<QueueListener<IOType>> cancelListener = new ArrayList<QueueListener<IOType>>();
	
	public Task(String description){
		this.description = description;
		this.addPropertyChangeListener(this);
	}
	
	public void start(IOType input) {
		this.input = input;
		processStartListener();
		this.execute();
	}
	
	@Override
	protected IOType doInBackground() throws Exception{
		try {
			IOType output = process(this.input);
			this.setProgress(100);
			return output;
		} catch (Exception e) {
			this.cancel(e);
			throw e;
		}
	}
	
	@Override
	protected void done() {
		// TODO Auto-generated method stub
		super.done();
	}
	
	
	
	public abstract IOType process(IOType input) throws Exception;
	
	public boolean isFinished() {
		return this.getState().equals(SwingWorker.StateValue.DONE);
	}
	
	@Override
	public String toString() {
		return this.description;
	}
	
	protected void cancel(Exception e){
		for (QueueListener<IOType> lis : this.cancelListener) {
			lis.processCancel(this, e);
		}
		this.cancel(true);
	}
	
	public void addCancelListener(QueueListener<IOType> listener){
		this.cancelListener.add(listener);
	}
	
	
	public void addEndListener(QueueListener<IOType> listener) {
		this.endListener.add(listener);
	}

	private void processEndListener() {
		for (QueueListener<IOType> lis : this.endListener) {
			lis.processEnd(this);
		}
	}
	
	public void addStartListener(QueueListener<IOType> listener) {
		this.startListener.add(listener);
	}

	private void processStartListener() {
		for (QueueListener<IOType> lis : this.startListener) {
			lis.processStart(this);
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		if ("state".equals(evt.getPropertyName())
                && SwingWorker.StateValue.DONE == evt.getNewValue()) {
			processEndListener();
		}
	}

}
