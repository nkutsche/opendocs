package de.janosch.commons.swing.progress;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingWorker;

import de.janosch.commons.swing.components.ImageButton;
import de.janosch.commons.swing.util.SwingUtil;

/**
 * 
 * @author Janos
 * @version 15.08.2010 | 21:35:21
 * 
 */
public class CancelableProgressControl<T> extends ProgressControl<T> implements ActionListener {

	private static final long serialVersionUID = -8336857688221587816L;

	private static final String CANCEL_ACTION_COMMAND = "cancel"; //$NON-NLS-1$

	private final JButton cancelButton;

	public CancelableProgressControl(final boolean indeterminate, final Icon cancelIcon) {
		super(indeterminate);
		final GridBagLayout gbl = new GridBagLayout();

		this.cancelButton = new ImageButton(cancelIcon);
		this.cancelButton.setActionCommand(CANCEL_ACTION_COMMAND);
		this.cancelButton.addActionListener(this);

		SwingUtil.addComponent(this, gbl, this.cancelButton, 1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
	}

	@Override
	public void execute(final SwingWorker<T, ?> worker, final ProgressListener<T> progressListener) {
		this.cancelButton.setEnabled(true);
		super.execute(worker, progressListener);
	}

	@Override
	protected void workerCancelled() {
		this.cancelButton.setEnabled(false);
		super.workerCancelled();
	}

	@Override
	protected void workerFinished(final T result) {
		this.cancelButton.setEnabled(false);
		super.workerFinished(result);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (CANCEL_ACTION_COMMAND.equals(e.getActionCommand())) {
			if (this.theWorker == null) {
				return;
			}
			this.theWorker.cancel(true);
		}
	}

}
