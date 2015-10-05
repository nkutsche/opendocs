package net.sqf.view.utils.types.panels;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.janosch.commons.swing.util.SwingUtil;

public class ColorPanel extends JPanel implements MouseListener{
	private static final long serialVersionUID = 2661956887580911488L;
	private final GridBagLayout gbl;
	private final int minWidth = 150;
	private final int minHeight = 100;
	private JDialog dialog;
	private final Border outlineBorder = BorderFactory.createBevelBorder(
			BevelBorder.LOWERED, new Color(110, 110, 110), new Color(200, 200,
					200));
	private JFormattedTextField textField;
	private JLabel rgbCode = new JLabel();
	private JSlider[] sliders = new JSlider[3];
	private Color color;
	private final Container owner;
	public ColorPanel(JFormattedTextField field, Container owner){
		this(field, field.getBackground(), owner);
	}
	public ColorPanel(final JFormattedTextField field, Color c, Container owner) {
		this.textField = field;
		this.owner = owner;
		textField.setHorizontalAlignment(JTextField.CENTER);
		color = c;
		setBorder(outlineBorder);
		setBackground(Color.WHITE);
		gbl = new GridBagLayout();
		this.setLayout(gbl);
		
		for (int i = 0; i < sliders.length; i++) {
			JPanel colorBar = getColorBar(i);
			colorBar.setBackground(Color.WHITE);
			SwingUtil.addComponent(this, gbl, colorBar,
					0, i, 1, 1, 1.0, 1.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3));
		}
		MouseListener ml = new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dialog.dispose();
			}
		};
		addMouseListener(ml);
//		SwingUtil.addComponent(this, gbl, rgbCode,
//				3, 1, 1, 1, 1.0, 0.0,
//				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(3, 5, 5, 5));
		rgbCode.setSize(rgbCode.getWidth(), textField.getWidth() / 3);
		setColor();
	}
	private JPanel getColorBar(int i) {
		JPanel jp = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		jp.setLayout(gbl);
		
		JLabel label = new JLabel(i==0 ? "R" : i==1 ? "G" : "B");
		JLabel maxVal = new JLabel("255");
		JLabel minVal = new JLabel("0");
		sliders[i] = new JSlider(JSlider.HORIZONTAL);
		sliders[i].setMinimum(0);
		sliders[i].setMaximum(255);
		sliders[i].setBackground(i==0 ? Color.RED : i==1 ? Color.GREEN : Color.BLUE);
		sliders[i].setPaintTicks(true);
		sliders[i].setPaintTrack(true);
		sliders[i].setOpaque(false);
		sliders[i].setValue(i==0 ? color.getRed() : i==1 ? color.getGreen() : color.getBlue());
		sliders[i].addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				setColor();
			}
		});
		SwingUtil.addComponent(jp, gbl, label,
				0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(5, 5, 5, 5));
		SwingUtil.addComponent(jp, gbl, minVal,
				1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5));
		SwingUtil.addComponent(jp, gbl, sliders[i],
				2, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5));
		SwingUtil.addComponent(jp, gbl, maxVal,
				3, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5));
		return jp;
	}
	protected void setColor() {
		int r = sliders[0].getValue();
		int g = sliders[1].getValue();
		int b = sliders[2].getValue();
		color = new Color(r,g,b);
		
		textField.setBackground(color);
		if(color.getRed()+ color.getGreen() * 1.5  < 334)
			textField.setForeground(Color.WHITE);
		else
			textField.setForeground(Color.BLACK);
		textField.setText(Integer.toHexString(color.getRGB()).substring(2));
	}
	private void getColor() {
		color = textField.getBackground();
		sliders[0].setValue(color.getRed());
		sliders[1].setValue(color.getGreen());
		sliders[2].setValue(color.getBlue());
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		if (textField.isEnabled()) {
			this.getColor();
			if (dialog != null)
				dialog.dispose();
			if(owner instanceof Dialog){
				dialog = new JDialog((Dialog) owner);
			} else if(owner instanceof Frame) {
				dialog = new JDialog((Frame) owner);
			} else if(owner instanceof Window) {
				dialog = new JDialog((Window) owner);
			} else {
				dialog = new JDialog(new JFrame());
			}
			dialog.addWindowFocusListener(new WindowFocusListener() {

				@Override
				public void windowLostFocus(WindowEvent arg0) {
					dialog.dispose();
				}

				@Override
				public void windowGainedFocus(WindowEvent arg0) {
				}
			});
			dialog.setUndecorated(true);
			dialog.setMinimumSize(new Dimension(minWidth, minHeight));
			Point tfLoc = textField.getLocationOnScreen();
			dialog.setSize(textField.getWidth(), textField.getWidth());
			dialog.setLocation(new Point(tfLoc.x + textField.getWidth()
					- dialog.getWidth(), tfLoc.y + textField.getHeight()));
			dialog.add(this);
			dialog.setModal(false);
			dialog.setVisible(true);
		}
	}
//	private Frame getFrame(Component c) {
//		if (c instanceof Frame)
//			return (Frame) c;
//		return getFrame(c.getParent());
//	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
