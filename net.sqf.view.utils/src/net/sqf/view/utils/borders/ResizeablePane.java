package net.sqf.view.utils.borders;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import de.janosch.commons.swing.util.SwingUtil;

public class ResizeablePane {
	private GridBagLayout gbl = new GridBagLayout();
	private final JPanel panel = new JPanel();
	HashMap<Integer, ArrayList<ResizeableMouseListener>> compByXStart = new HashMap<Integer, ArrayList<ResizeableMouseListener>>();
	HashMap<Integer, ArrayList<ResizeableMouseListener>> compByXEnd = new HashMap<Integer, ArrayList<ResizeableMouseListener>>();
	HashMap<Integer, ArrayList<ResizeableMouseListener>> compByYStart = new HashMap<Integer, ArrayList<ResizeableMouseListener>>();
	HashMap<Integer, ArrayList<ResizeableMouseListener>> compByYEnd = new HashMap<Integer, ArrayList<ResizeableMouseListener>>();
	private Dimension panelSize;
	private final ArrayList<ResizeableMouseListener> allRmls = new ArrayList<ResizeableMouseListener>();
	
	public ResizeablePane(int cols, int rows){
		panel.setLayout(gbl);
		gbl.columnWidths = new int[cols];
		gbl.rowHeights = new int[rows];
		this.panelSize = panel.getSize();
		this.panel.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentResized(ComponentEvent arg0) {
				Dimension size = panelSize;
				panelSize = panel.getSize();
				for (ResizeableMouseListener rml : allRmls) {
					rml.didResized();
				}
			}
			
			@Override
			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	

	public void add(JComponent comp, int x, int y, int width, int height, int anchor, int fill) {
		makeResizeable(comp, x, y, width, height);
		SwingUtil.addComponent(panel, gbl, comp, x, y, width, height, 1.0, 1.0, anchor, fill);
		
	}
	private void makeResizeable(JComponent comp, int x, int y, int width, int height) {
		HashMap<Integer, ArrayList<ResizeableMouseListener>> oppositeNeighbours = new HashMap<Integer, ArrayList<ResizeableMouseListener>>();
		oppositeNeighbours.put(SwingConstants.NORTH, ResizeableMouseListener.copy(this.compByYEnd.get(y)));
		oppositeNeighbours.put(SwingConstants.SOUTH, ResizeableMouseListener.copy(this.compByYStart.get(y + height)));
		oppositeNeighbours.put(SwingConstants.WEST, ResizeableMouseListener.copy(this.compByXEnd.get(x)));
		oppositeNeighbours.put(SwingConstants.EAST, ResizeableMouseListener.copy(this.compByXStart.get(x + width)));
		HashMap<Integer, ArrayList<ResizeableMouseListener>> inlineNeighbours = new HashMap<Integer, ArrayList<ResizeableMouseListener>>();
		inlineNeighbours.put(SwingConstants.NORTH, ResizeableMouseListener.copy(this.compByYStart.get(y)));
		inlineNeighbours.put(SwingConstants.SOUTH, ResizeableMouseListener.copy(this.compByYEnd.get(y + height)));
		inlineNeighbours.put(SwingConstants.WEST, ResizeableMouseListener.copy(this.compByXStart.get(x)));
		inlineNeighbours.put(SwingConstants.EAST, ResizeableMouseListener.copy(this.compByXEnd.get(x + width)));
		
		
		ResizeableMouseListener rml = new ResizeableMouseListener(comp, 6, inlineNeighbours, oppositeNeighbours, gbl);
		this.allRmls.add(rml);
		addCompToMap(rml, x, y, width, height);
	}
	
	

	public JPanel asJPanel(){
		return this.panel;
	}
	
	private void addCompToMap(ResizeableMouseListener rml, int x, int y, int width, int height){
		addCompToMap(compByXStart, rml, x);
		addCompToMap(compByYStart, rml, y);
		addCompToMap(compByXEnd, rml, x + width);
		addCompToMap(compByYEnd, rml, y + height);
		
	}
	
	private void addCompToMap(HashMap<Integer, ArrayList<ResizeableMouseListener>> map, ResizeableMouseListener comp, int pos){
		if(!map.containsKey(pos)){
			map.put(pos, new ArrayList<ResizeableMouseListener>());
		}
		map.get(pos).add(comp);
	}
	
//	private class RMLPosition {
//		ResizeableMouseListener rml;
//		int x;
//		int y;
//		int w;
//		int h;
//		public RMLPosition(ResizeableMouseListener rml, int x, int y, int w, int h){
//			this.rml = rml;
//			this.x = x;
//			this.y = y;
//			this.w = w;
//			this.h = h;
//			
//		}
//	}
}
