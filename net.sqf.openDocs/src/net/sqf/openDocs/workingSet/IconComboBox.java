package net.sqf.openDocs.workingSet;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class IconComboBox extends JComboBox<JLabel> {
	private static final long serialVersionUID = 1L;
	private class Renderer implements ListCellRenderer<JLabel> {

		@Override
		public Component getListCellRendererComponent(
				JList<? extends JLabel> list, JLabel value, int index,
				boolean isSelected,
                boolean cellHasFocus) {
			if (isSelected) {
//	            value.setBackground(Color.BLUE);
//	            value.setForeground(Color.WHITE);
	            value.setBorder(BorderFactory.createLineBorder(Color.BLUE));
	            value.repaint();
	        } else {
//	        	value.setBackground(list.getBackground());
//	        	value.setForeground(list.getForeground());
	            value.setBorder(BorderFactory.createLineBorder(Color.WHITE));
	        }
			
			return value;
		}
	}
	public IconComboBox(JLabel[] labels){
		super(labels);
		this.setOpaque(true);
		this.setRenderer(new Renderer());
	}
}
