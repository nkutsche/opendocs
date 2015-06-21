package net.sqf.view.utils.splitPane;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class CollapsListener implements ComponentListener {
	
	private final MultiSplit multiSplit;

	public CollapsListener(MultiSplit multiSplit) {
		this.multiSplit = multiSplit;
		
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		if(multiSplit.isFirstCollapsed()){
			multiSplit.collapseFirst();
		} else if(multiSplit.isSecondCollapsed()){
			multiSplit.collapseSecond();
		}
		
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

}
