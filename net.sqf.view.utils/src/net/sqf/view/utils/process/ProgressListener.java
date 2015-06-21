package net.sqf.view.utils.process;

import javax.swing.JFrame;

import net.sqf.utils.process.exceptions.CancelException;
import net.sqf.utils.process.exceptions.ErrorViewer;
import net.sqf.utils.process.log.ProcessLoger;

public class ProgressListener implements ProcessLoger {
	private final int steps;
	private ProgressViewer progressViewer;
	private int currentStep = 1;
	private final int delayOnEnd;
	private final ErrorViewer errorViewer;

	public ProgressListener(int steps, String description, ErrorViewer ev) throws CancelException{
		this(steps, description, ev, 1000);
	}
	
	public ProgressListener(int steps, String description, ErrorViewer ev, int delayOnEnd) throws CancelException{
		this.steps = steps;
		this.delayOnEnd = delayOnEnd;
		this.progressViewer = new ProgressViewer(description, ev);
		this.errorViewer = ev;
	}
	
	public ProgressListener(int steps, String description, JFrame parent, ErrorViewer ev) throws CancelException{
		this(steps, description, parent, ev, 1000);
	}
	
	public ProgressListener(int steps, String description, JFrame parent, ErrorViewer ev, int delayOnEnd) throws CancelException{
		this(steps, description, ev, delayOnEnd);
		this.progressViewer = new ProgressViewer(description, parent, ev);
	}
	
	@Override
	public void log(String message) {
		if(!progressViewer.isVisible()){
			progressViewer.start();
		}
		progressViewer.setProcessState((currentStep * 100) / steps, message);
//		if(currentStep == steps){
//			end();
//		}
		currentStep++;
	}

	@Override
	public void log(String message, boolean taskEnd) {
		log(message);
		if(taskEnd){
			end();
		}
	}
	
	@Override
	public void end() {
		// TODO Auto-generated method stub
		if(delayOnEnd > 0 && currentStep == steps){
			try {
				Thread.sleep(delayOnEnd);
			} catch (InterruptedException e) {
			}
		}
		progressViewer.end();
	}

	@Override
	public void log(Exception exception) throws CancelException {
		errorViewer.viewException(exception);
	}
	
	@Override
	public void log(Exception exception, boolean forceEnd) throws CancelException {
		this.log(exception);
		if(forceEnd){
			this.log(exception.getLocalizedMessage(), true);
		}
	}

	
}
