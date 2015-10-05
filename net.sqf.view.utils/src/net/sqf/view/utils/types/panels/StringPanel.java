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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import net.sqf.view.utils.images.IconMap;
import net.sqf.view.utils.types._Verifier;
import de.janosch.commons.swing.util.SwingUtil;

public class StringPanel extends JPanel implements MouseListener, FocusListener {
	private static final long serialVersionUID = 2661956887580911488L;
	private final GridBagLayout gbl;
	private final int minWidth = 0;
	private final int minHeight = 0;
	private JDialog dialog;
//	private final Border defaultBorder = BorderFactory.createBevelBorder(
//			BevelBorder.RAISED, new Color(240, 240, 240), new Color(150, 150,
//					150));
	private final Border outlineBorder = BorderFactory.createBevelBorder(
			BevelBorder.LOWERED, new Color(110, 110, 110), new Color(200, 200,
					200));
//	private final Border selectBorder = BorderFactory.createBevelBorder(
//			BevelBorder.RAISED, new Color(30, 20, 120),
//			new Color(110, 100, 200));
	private JFormattedTextField textField;
	
	private String value;
	private final String initialValue;
	
	private JFormattedTextField entryField;
	private final Container owner;
	
	private final JPanel buttonPanel = new JPanel();
	private final JButton okBtn;
	@SuppressWarnings("unused")
	private final JButton cancelBtn;
	private final JButton clearBtn; 
	
	private class PanelButton extends JButton{
		private static final long serialVersionUID = 1699184718806511284L;
		public PanelButton(Icon i){
			this.setIcon(i);
			Dimension dim = new Dimension(i.getIconWidth() + 1, i.getIconHeight() + 1);
			this.setMinimumSize(dim);
			this.setSize(dim);
		}
		public PanelButton(String text){
			super(text);
		}
	}
	
	public StringPanel(final JFormattedTextField field, _Verifier verifier, Container owner){
		this(field, owner);
		verifier.setVerifier(entryField, owner, false);
	}
	public StringPanel(final JFormattedTextField field, Container owner) {
		this.owner = owner;
		this.textField = field;
		textField.setHorizontalAlignment(JTextField.CENTER);
		if(field.isEnabled()){
			initialValue = field.getText();
			value = initialValue;
		} else {
			initialValue = null;
			value = null;
		}
		setBorder(outlineBorder);
		setBackground(Color.WHITE);
		gbl = new GridBagLayout();
		this.setLayout(gbl);
		
		GridBagLayout buttonGbl = new GridBagLayout();
		buttonPanel.setLayout(buttonGbl);
		buttonPanel.setVisible(false);
		
		if(IconMap.ICONS != null){
			IconMap icons = IconMap.ICONS;
			this.okBtn = new PanelButton(icons.getIcon(2, 10));
			this.clearBtn = new PanelButton(icons.getIcon(10, 11));
			this.cancelBtn = new PanelButton(icons.getIcon(0, 10));
		} else {
			this.okBtn = new PanelButton("ok");
			this.clearBtn = new PanelButton("x");
			this.cancelBtn = new PanelButton("c");
		}
		
		this.okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		this.clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				unsetTextAndDispose();
			}
		});

//		this.okBtn.setVisible(false);
//		this.clearBtn.setVisible(false);
//		this.cancelBtn.setVisible(false);
		
		
		entryField = new JFormattedTextField();
		if(value == null){
			entryField.setText("");
		} else {
			entryField.setText(value);
		}
		KeyListener[] kls = textField.getKeyListeners();
		
		for (int i = 0; i < kls.length; i++) {
			entryField.addKeyListener(kls[i]);
		}
		entryField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyChar()=='\n')
					dispose();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		SwingUtil.addComponent(this, gbl, entryField,
				0, 0, 1, 3, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(3, 3, 3, 0));
		

		SwingUtil.addComponent(this, gbl, okBtn,
				1, 0, 1, 1, 0.0, 1.0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.VERTICAL, new Insets(3, 0, 0, 3));
		

//		SwingUtil.addComponent(this, gbl, cancelBtn,
//				1, 1, 1, 1, 0.0, 1.0,
//				GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 3));

		SwingUtil.addComponent(this, gbl, clearBtn,
				1, 1, 1, 1, 0.0, 1.0,
				GridBagConstraints.SOUTHEAST, GridBagConstraints.VERTICAL, new Insets(0, 0, 3, 3));
		
//		SwingUtil.addComponent(this, gbl, buttonPanel,
//				0, 1, 1, 1, 1.0, 1.0,
//				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3));
//
//		SwingUtil.addComponent(buttonPanel, buttonGbl, clearBtn,
//				0, 0, 1, 1, 1.0, 1.0,
//				GridBagConstraints.CENTER, GridBagConstraints.BOTH);
//		SwingUtil.addComponent(buttonPanel, buttonGbl, cancelBtn,
//				1, 0, 1, 1, 1.0, 1.0,
//				GridBagConstraints.CENTER, GridBagConstraints.BOTH);
//		SwingUtil.addComponent(buttonPanel, buttonGbl, okBtn,
//				2, 0, 1, 1, 1.0, 1.0,
//				GridBagConstraints.CENTER, GridBagConstraints.BOTH);
		
		MouseListener ml = new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {

//				switchToField();
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {

//				switchToButtons();
			}
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dispose();
			}
		};
		
		addMouseListener(ml);
//		SwingUtil.addComponent(this, gbl, rgbCode,
//				3, 1, 1, 1, 1.0, 0.0,
//				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(3, 5, 5, 5));
//		setText();
	}
	
//	private void switchToButtons(){
//		this.buttonPanel.setVisible(true);
////		this.entryField.setVisible(false);
//		dialog.setSize(dialog.getWidth(), dialog.getHeight() * 2);
//	}
//	private void switchToField(){
//		this.buttonPanel.setVisible(false);
//		dialog.setSize(dialog.getWidth(), dialog.getHeight() / 2);
////		this.entryField.setVisible(true);
//	}
	
	protected void unsetTextAndDispose() {
		if(initialValue != null){
			this.textField.setEnabled(true);
		} else {
			this.textField.setEnabled(false);
		}
		this.textField.setText(initialValue);
		this.textField.repaint();

		this.dialog.dispose();
	}
	
	protected void setText() {
		this.textField.setEnabled(true);
		this.textField.setText(this.entryField.getText());
		textField.repaint();
	}
	private void getText() {
		if(textField.isEnabled()){
			value = textField.getText();
		} else {
			value = "";
		}
		entryField.setText(value);
		
		if(!value.equals("")){
			entryField.setSelectionStart(0);
			entryField.setSelectionEnd(value.length());
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		activate();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	private void activate(){
//		if (textField.isEnabled()) {
			this.getText();
			if (dialog != null) {
				dispose();
			}
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
			int finalWidth = textField.getWidth() * 2;
			int finalHeight = textField.getHeight() * 2;
			dialog.setSize(finalWidth, finalHeight);
			dialog.setLocation(new Point((int) (tfLoc.x - finalWidth * 0.25),
										 (int) (tfLoc.y - finalHeight * 0.25)));
			dialog.add(this);
			dialog.setModal(false);
			dialog.setVisible(true);
//		}
	}
	
	private void dispose(){
		this.setText();
		this.dialog.dispose();
	}
	
	
	@Override
	public void focusGained(FocusEvent arg0) {

	}
	@Override
	public void focusLost(FocusEvent arg0) {
//		dispose();
	}
}
