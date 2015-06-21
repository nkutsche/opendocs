package net.sqf.view.utils.borders;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.event.MouseInputAdapter;

public class ResizeableMouseListener extends MouseInputAdapter {

	private final HashMap<Integer, ArrayList<ResizeableMouseListener>> oppositeNeighbours;
	private final HashMap<Integer, ArrayList<ResizeableMouseListener>> insideNeighbours;
	private final JComponent parent;
	private final int borderWidth;
	private final GridBagLayout grandParentGbl;
	private final boolean resizeableOnEmptyBorder;
	
	public ResizeableMouseListener(
			JComponent parent,
			int borderWidth,
			HashMap<Integer, ArrayList<ResizeableMouseListener>> insideNeighbours,
			HashMap<Integer, ArrayList<ResizeableMouseListener>> oppositeNeighbours,
			GridBagLayout grandParentGbl){
		this(parent,borderWidth, insideNeighbours, oppositeNeighbours, grandParentGbl, false);
	}
	
	public ResizeableMouseListener(
			JComponent parent,
			int borderWidth,
			HashMap<Integer, ArrayList<ResizeableMouseListener>> insideNeighbours,
			HashMap<Integer, ArrayList<ResizeableMouseListener>> oppositeNeighbours,
			GridBagLayout grandParentGbl, boolean resizeableOnEmptyBorder) {
		this.parent = parent;
		this.parent.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentResized(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				Component c = arg0.getComponent();
				if(c instanceof JComponent){
					JComponent jc = (JComponent) c;
					jc.updateUI();
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
		this.borderWidth = borderWidth;
		this.insideNeighbours = insideNeighbours;
		this.oppositeNeighbours = oppositeNeighbours;
		this.grandParentGbl = grandParentGbl;
		this.resizeableOnEmptyBorder = resizeableOnEmptyBorder;
		addNeighbours(this.oppositeNeighbours, false);
		addNeighbours(this.insideNeighbours, true);

		parent.addMouseListener(this);
		parent.addMouseMotionListener(this);
		parent.addMouseWheelListener(this);
	}

	public static ArrayList<ResizeableMouseListener> copy(
			ArrayList<ResizeableMouseListener> org) {
		if (org == null)
			return null;
		ArrayList<ResizeableMouseListener> copy = new ArrayList<ResizeableMouseListener>();
		copy.addAll(org);
		return copy;
	}

	private void addNeighbours(
			HashMap<Integer, ArrayList<ResizeableMouseListener>> neighbourMap,
			boolean inside) {
		Set<Integer> typeSet = neighbourMap.keySet();
		for (Integer type : typeSet) {
			ArrayList<ResizeableMouseListener> neighbours = neighbourMap
					.get(type);
			if (neighbours != null) {
				// for (Iterator<ResizeableMouseListener> i =
				// neighbours.iterator(); i.hasNext();) {
				// ResizeableMouseListener neigh = (ResizeableMouseListener)
				// i.next();
				// neigh.addNeighbour(type, this);
				// }
				for (ResizeableMouseListener neigh : neighbours) {
					if (neigh != this) {
						if (inside) {
							neigh.addInsideNeighbour(type, this);
						} else {
							neigh.addOppositeNeighbour(reverseDragType(type),
									this);
						}
					}
				}
			}
		}
	}

	private int[] cursorsByDragType = { -1, Cursor.N_RESIZE_CURSOR, -1,
			Cursor.E_RESIZE_CURSOR, -1, Cursor.S_RESIZE_CURSOR, -1,
			Cursor.W_RESIZE_CURSOR };

	@Override
	public void mouseMoved(MouseEvent e) {
		dragType = getDragType(e.getPoint());
		Component comp = e.getComponent();
		if (dragType < 0 && !isDragging) {
			comp.setCursor(Cursor.getDefaultCursor());
		} else {
			comp.setCursor(Cursor
					.getPredefinedCursor(cursorsByDragType[dragType]));
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (!isDragging) {
			Component comp = e.getComponent();
			comp.setCursor(Cursor.getDefaultCursor());
		}
	}

	private Point startPos = null;
	private int dragType;
	private boolean isDragging = false;

	@Override
	public void mousePressed(MouseEvent e) {
		Point p = e.getPoint();
		dragType = getDragType(p);
		if (dragType != -1) {
			startPos = e.getPoint();
			this.isDragging = true;
		} else {
			startPos = null;
		}

	}

	@Override
	public void mouseDragged(MouseEvent me) {
		Point startPos = this.startPos;
		if (startPos != null) {
			resize(this.parent, startPos, me, dragType);
			ArrayList<ResizeableMouseListener> oppNeighs = oppositeNeighbours
					.get(dragType);
			ArrayList<ResizeableMouseListener> innNeighs = insideNeighbours
					.get(dragType);
			if (oppNeighs != null) {
				for (ResizeableMouseListener neigh : oppNeighs) {
					resize(neigh.getParent(), startPos, me,
							reverseDragType(dragType));
					neigh.didResized();
				}
			}
			if (innNeighs != null) {
				for (ResizeableMouseListener neigh : innNeighs) {
					resize(neigh.getParent(), startPos, me, dragType);
					neigh.didResized();
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		didResized();
//		for (ArrayList<ResizeableMouseListener> neighlist : oppositeNeighbours
//				.values()) {
//			if (neighlist != null) {
//				for (ResizeableMouseListener neigh : neighlist) {
//					neigh.didResized();
//				}
//			}
//		}
//		for (ArrayList<ResizeableMouseListener> neighlist : insideNeighbours
//				.values()) {
//			if (neighlist != null) {
//				for (ResizeableMouseListener neigh : neighlist) {
//					neigh.didResized();
//				}
//			}
//		}
		this.isDragging = false;
		Component comp = e.getComponent();
		comp.setCursor(Cursor.getDefaultCursor());
	}

	private int getDragType(Point p) {
		int width = parent.getWidth();
		int height = parent.getHeight();

		boolean onLeft = p.x <= this.borderWidth;
		boolean onTop = p.y <= this.borderWidth;
		boolean onRight = p.x >= width - this.borderWidth;
		boolean onBottom = p.y >= height - this.borderWidth;
		int dragType;
		if (onTop) {
			dragType = SwingConstants.NORTH;
		} else if (onBottom) {
			dragType = SwingConstants.SOUTH;
		} else if (onLeft) {
			dragType = SwingConstants.WEST;
		} else if (onRight) {
			dragType = SwingConstants.EAST;
		} else {
			dragType = -1;
		}
		ArrayList<ResizeableMouseListener> oppNeigh = this.oppositeNeighbours.get(dragType);
		boolean hasNeigh = false;
		if(oppNeigh != null){
			if(oppNeigh.size() > 0)
				hasNeigh = true;
		}
		if(hasNeigh || this.resizeableOnEmptyBorder){
			return dragType;
		} else {
			return -1;
		}
	}

	private void resize(JComponent comp, Point startPos, MouseEvent me,
			int dragType) {
		int dx = me.getX() - startPos.x;
		int dy = me.getY() - startPos.y;
		switch (dragType) {

		case SwingConstants.NORTH:
			comp.setBounds(comp.getX(), comp.getY() + dy, comp.getWidth(),
					comp.getHeight() - dy);
			break;

		case SwingConstants.SOUTH:
			comp.setBounds(comp.getX(), comp.getY(), comp.getWidth(),
					comp.getHeight() + dy);
			if (comp == this.getParent())
				this.startPos = me.getPoint();
			break;

		case SwingConstants.WEST:
			comp.setBounds(comp.getX() + dx, comp.getY(), comp.getWidth() - dx,
					comp.getHeight());
			break;

		case SwingConstants.EAST:
			comp.setBounds(comp.getX(), comp.getY(), comp.getWidth() + dx,
					comp.getHeight());
			if (comp == this.getParent())
				this.startPos = me.getPoint();
			break;

		default:
			break;
		}
	}

	public void didResized() {
		GridBagConstraints gbc = this.grandParentGbl.getConstraints(this.parent);

		int columnWidth = this.parent.getWidth() / gbc.gridwidth;
		int columnHeight = this.parent.getHeight() / gbc.gridheight;
		if(gbc.gridwidth == 1){
			this.grandParentGbl.columnWidths[gbc.gridx] = columnWidth;
		}
		if(gbc.gridheight == 1){
			this.grandParentGbl.rowHeights[gbc.gridy] = columnHeight;
		}
//		for (int i = gbc.gridx; i < gbc.gridwidth + gbc.gridx; i++) {
//		}
//		
//		for (int i = gbc.gridy; i < gbc.gridheight + gbc.gridy; i++) {
//		}
		
//		double newWidth = this.parent.getWidth();
//		double newHeight = this.parent.getHeight();
//		double gparentWidth = this.parent.getParent().getWidth();
//		double gparentHeight = this.parent.getParent().getHeight();
//
//		
//		gbc.weightx = newWidth / gparentWidth;
//		gbc.weighty = newHeight / gparentHeight;
//		this.grandParentGbl.setConstraints(this.parent, gbc);
		
		
		

		this.parent.repaint();

	}

	private int reverseDragType(int type) {
		switch (type) {
		case SwingConstants.NORTH:
			return SwingConstants.SOUTH;
		case SwingConstants.SOUTH:
			return SwingConstants.NORTH;
		case SwingConstants.WEST:
			return SwingConstants.EAST;
		case SwingConstants.EAST:
			return SwingConstants.WEST;

		default:
			return -1;
		}
	}

	public void addOppositeNeighbour(int type, ResizeableMouseListener neigh) {
		if (oppositeNeighbours.get(type) == null) {
			oppositeNeighbours.put(type,
					new ArrayList<ResizeableMouseListener>());
		}
		oppositeNeighbours.get(type).add(neigh);
	}

	public void addInsideNeighbour(int type, ResizeableMouseListener neigh) {
		if (insideNeighbours.get(type) == null) {
			insideNeighbours
					.put(type, new ArrayList<ResizeableMouseListener>());
		}
		insideNeighbours.get(type).add(neigh);
	}

	public JComponent getParent() {
		return this.parent;
	}

	// public void resize(Dimension oldSize, Dimension newSize) {
	// if(oldSize.width > 0 && oldSize.height > 0){
	// resize(newSize.width / oldSize.width, newSize.height / oldSize.height);
	// }
	// }
	//
	// private void resize(double factX, double factY) {
	// int x = (int) (actualDimension.getX() * factX);
	// int y = (int) (actualDimension.getY() * factY);
	// int w = (int) (actualDimension.getWidth() * factX);
	// int h = (int) (actualDimension.getHeight() * factY);
	// this.parent.setBounds(x, y, w, h);
	// didResized();
	// }

}
