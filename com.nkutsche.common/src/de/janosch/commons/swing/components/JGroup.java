package de.janosch.commons.swing.components;

import java.awt.Color;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * 
 * @author Janos
 * @version 15.08.2010 | 15:40:36
 * 
 */
public class JGroup extends JPanel {

	private static final long serialVersionUID = 3866478971884787646L;
	protected final String groupName;

	public JGroup(final String groupName) {
		super();
		this.groupName = groupName;
		doBorder();
	}

	public JGroup(final String groupName, final boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		this.groupName = groupName;
		doBorder();
	}

	public JGroup(final String groupName, final LayoutManager layout, final boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		this.groupName = groupName;
		doBorder();
	}

	public JGroup(final String groupName, final LayoutManager layout) {
		super(layout);
		this.groupName = groupName;
		doBorder();
	}
	
	private void doBorder() {
		TitledBorder border = BorderFactory.createTitledBorder(this.groupName);
		border.setTitleColor(new Color(0, 0, 192));
		this.setBorder(border);
	}

}
