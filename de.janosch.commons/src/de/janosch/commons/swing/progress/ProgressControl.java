package de.janosch.commons.swing.progress;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventListener;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import de.janosch.commons.swing.util.SwingUtil;

/**
 * 
 * @author Janos
 * @version 16.08.2010 | 00:52:13
 * 
 */
public class ProgressControl<T> extends JPanel {

	private static final long serialVersionUID = -9092273310002347932L;

	private static final String WORKERSTATE_STATE = "state"; //$NON-NLS-1$
	private static final String WORKERSTATE_PROGRESS = "progress"; //$NON-NLS-1$

	private final JProgressBar progressBar;

	private T result;
	protected SwingWorker<T, ?> theWorker;
	private ProgressListener<T> theListener = new ProgressAdapter<T>();

	public ProgressControl(final boolean indetermindate) {
		super();
		final GridBagLayout gbl = new GridBagLayout();

		this.setLayout(gbl);

		this.progressBar = new JProgressBar();
		this.progressBar.setMaximum(100);

		this.progressBar.setIndeterminate(indetermindate);

		SwingUtil.addComponent(this, gbl, this.progressBar, 0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
	}

	public void execute(final SwingWorker<T, ?> worker) {
		execute(worker, new ProgressAdapter<T>());
	}

	public void execute(final SwingWorker<T, ?> worker, final ProgressListener<T> progressListener) {

		if (worker == null) {
			return;
		}

		this.theWorker = worker;
		this.theListener = progressListener == null ? new ProgressAdapter<T>() : progressListener;

		this.progressBar.setValue(0);
		this.result = null;

		this.theWorker.addPropertyChangeListener(this.workerListener);

		this.theWorker.execute();
	}

	private void workerProgress(final int progress) {
		if (this.theWorker.isDone() || this.theWorker.isCancelled()) {
			return;
		}
		this.progressBar.setValue(progress);
		this.theListener.progress(progress);
	}

	protected void workerCancelled() {
		this.result = null;
		this.progressBar.setValue(0);
		this.theWorker.removePropertyChangeListener(this.workerListener);
		this.theListener.cancelled();
	}
	
	protected void workerExcepioned(final Throwable cause) {
		this.result = null;
		this.progressBar.setValue(0);
		this.theWorker.removePropertyChangeListener(this.workerListener);
		this.theListener.exceptioned(cause);
	}

	protected void workerFinished(final T result) {
		this.result = result;
		this.progressBar.setValue(100);
		this.theWorker.removePropertyChangeListener(this.workerListener);
		this.theListener.finished(result);
	}

	public T getResult() {
		return this.result;
	}

	private final PropertyChangeListener workerListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			if (WORKERSTATE_PROGRESS == evt.getPropertyName()) {
				// progress update
				final int progress = (Integer) evt.getNewValue();
				workerProgress(progress);
			}
			if (WORKERSTATE_STATE == evt.getPropertyName() && SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
				// worker finished

				if (ProgressControl.this.theWorker == null) {
					return;
				}

				if (ProgressControl.this.theWorker.isCancelled()) {
					workerCancelled();
					return;
				}

				try {
					final T result = ProgressControl.this.theWorker.get();
					workerFinished(result);
				} catch (final Exception e) {
					/**
					 * TODO: Is this right?
					 * I've found out, that rather than the thrown exception by the worker, 
					 * a java.util.concurrent.ExecutionException is thrown, which has the
					 * actully thrown exception as it's cause.
					 * I'm not sure if that is always the case.
					 * For the uses here, it worked to pass the nested exception to the receiver.
					 */
					workerExcepioned(e.getCause());
				}
			}
		}
	};

	public static interface ProgressListener<T> extends EventListener {
		public void progress(int progress);
		public void finished(T result);
		public void cancelled();
		public void exceptioned(Throwable cause);
	}

	public static class ProgressAdapter<T> implements ProgressListener<T> {
		@Override
		public void cancelled() {
		}
		@Override
		public void finished(final T result) {
		}
		@Override
		public void progress(final int progress) {
		}
		@Override
		public void exceptioned(final Throwable cause) {
		}
	}

}
