package net.sqf.view.utils.splitPane;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

public class MultiSplit extends JSplitPane {

	private static final long serialVersionUID = -2149523834046794896L;
	private final MultiSplit childSplitPane;
	private final int orientation;

	private final JComponent child1;
	private final JComponent child2;
	
	private int dividerSize;

	public MultiSplit(int[] orientations, JComponent[] components) {
		super(orientations[0]);
		this.orientation = orientations[0];
		this.child1 = components[0];
		if (components.length == 2) {
			this.childSplitPane = null;
			this.child2 = components[1];
		} else {
			JComponent[] restComponents = getLastComponents(components);
			int[] restOrientations = getLastIntegers(orientations);
			this.childSplitPane = new MultiSplit(restOrientations,
					restComponents);
			this.child2 = this.childSplitPane;
		}
		setComponets();
		this.addComponentListener(new CollapsListener(this));
		this.setOneTouchExpandable(false);
	}

	public MultiSplit(int orientation, JComponent[] components) {
		super(orientation);
		this.orientation = orientation;
		this.child1 = components[0];

		if (components.length == 2) {
			this.childSplitPane = null;
			this.child2 = components[1];
		} else {
			JComponent[] restComponents = getLastComponents(components);
			this.childSplitPane = new MultiSplit(orientation, restComponents);
			this.child2 = this.childSplitPane;
		}
		setComponets();
		this.addComponentListener(new CollapsListener(this));
		this.setOneTouchExpandable(false);
	}
	
	public MultiSplit(int orientation, JComponent child, MultiSplit childSplitPane){
		super(orientation);
		this.orientation = orientation;
		this.child1 = child;
		this.childSplitPane = childSplitPane;
		this.child2 = childSplitPane;
		setComponets();
		this.addComponentListener(new CollapsListener(this));
		this.setOneTouchExpandable(false);
	}
	

	private void setComponets() {
		if (this.orientation == JSplitPane.HORIZONTAL_SPLIT) {
			this.setLeftComponent(this.child1);
			this.setRightComponent(this.child2);
		} else {
			this.setTopComponent(this.child1);
			this.setBottomComponent(this.child2);
		}
		
	}



	public void show(JComponent comp) {
		if(this.child1 == comp && isFirstCollapsed()){
			expand();
		} else if(this.child2 == comp){
			if(isSecondCollapsed()){
				expand();
			}
		} else if (isSecondCollapsed()){
			expand();
		} else if(this.childSplitPane != null){
			this.childSplitPane.show(comp);
		}
	}
	
	private boolean isFirstCollapsed = false;
	private boolean isSecondeCollapsed = false;
	private double lastDividerLocation = 0.0;
	
	protected void expand() {
		this.setDividerLocation(this.getResizeWeight());
		this.isFirstCollapsed = false;
		this.isSecondeCollapsed = false;
		super.setDividerSize(dividerSize);
	}
	
	protected void collapseFirst(){
		setLastDividerLocation();
		this.isFirstCollapsed = true;
		this.isSecondeCollapsed = false;
		this.setDividerLocation(0.0);
		super.setDividerSize(0);
	}
	
	protected void collapseSecond(){
		if(!this.isSecondeCollapsed){
			setLastDividerLocation();
		}
		this.isFirstCollapsed = false;
		this.isSecondeCollapsed = true;
		this.setDividerLocation(1.0);
		super.setDividerSize(0);
	}
	
	public void setLastDividerLocation() {
		int size = this.orientation == JSplitPane.HORIZONTAL_SPLIT ? this.getWidth() : this.getHeight();
		if(size == 0){
			this.lastDividerLocation = this.getResizeWeight();
		} else {
			this.lastDividerLocation = this.getDividerLocation() / size;
		}
	}

	protected boolean isFirstCollapsed() {
		return this.isFirstCollapsed;
	}
	protected boolean isSecondCollapsed() {
		return this.isSecondeCollapsed;
	}

	public void hide(JComponent comp) {
		if (this.child1 == comp) {
			collapseFirst();
		} else if (this.child2 == comp) {
			collapseSecond();
		} else if (this.childSplitPane != null) {
			this.childSplitPane.hide(comp);
		}
	}

	public void hideFrom(JComponent comp) {
		if (this.child1 == comp) {
			return;
		} else if (this.child2 == comp) {
			hide(comp);
		} else if (this.childSplitPane != null) {
			if (this.childSplitPane.getFirstChild() == comp) {
				collapseSecond();
			} else {
				this.childSplitPane.hideFrom(comp);
			}
		}
	}

	private JComponent getFirstChild() {
		return this.child1;
	}
	
	public void setResizeWeight(double[] weights){
		this.setResizeWeight(weights[0] / sum(weights));
		
		if(this.childSplitPane != null){
			this.childSplitPane.setResizeWeight(getLastDoubles(weights));
		} 
	}
	
	
//	
//	Overrides
//	
	
	@Override
	public void setContinuousLayout(boolean newContinuousLayout) {
		if (this.childSplitPane != null) {
			this.childSplitPane.setContinuousLayout(newContinuousLayout);
		}
		// TODO Auto-generated method stub
		super.setContinuousLayout(newContinuousLayout);
	}
	
	
	
//	@Override
//	public void setOneTouchExpandable(boolean newValue) {
//		if (this.childSplitPane != null) {
//			this.childSplitPane.setOneTouchExpandable(newValue);
//		}
//		// TODO Auto-generated method stub
//		super.setOneTouchExpandable(newValue);
//	}

	@Override
	public void setDividerSize(int newSize) {
		this.dividerSize = newSize;
		if (this.childSplitPane != null) {
			this.childSplitPane.setDividerSize(newSize);
		}
		// TODO Auto-generated method stub
		super.setDividerSize(newSize);
	}

	
//	
//	Static methods
//	

	
	private static double sum(double[] values){
		double sum = 0.0;
		for (int i = 0; i < values.length; i++) {
			sum += values[i];
		}
		return sum;
	}
	
	private static JComponent[] getLastComponents(JComponent[] components) {
		if (components.length == 1) {
			return null;
		}
		JComponent[] newComp = new JComponent[components.length - 1];
		for (int i = 0; i < newComp.length; i++) {
			newComp[i] = components[i + 1];
		}
		return newComp;
	}

	private int[] getLastIntegers(int[] ints) {
		if (ints.length == 1) {
			return null;
		}
		int[] newInts = new int[ints.length - 1];
		for (int i = 0; i < newInts.length; i++) {
			newInts[i] = ints[i + 1];
		}
		return newInts;
	}
	
	private double[] getLastDoubles(double[] doubles) {
		if (doubles.length == 1) {
			return null;
		}
		double[] newDoubles = new double[doubles.length - 1];
		for (int i = 0; i < newDoubles.length; i++) {
			newDoubles[i] = doubles[i + 1];
		}
		return newDoubles;
	}

}
