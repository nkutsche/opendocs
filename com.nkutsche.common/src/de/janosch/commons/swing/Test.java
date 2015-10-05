package de.janosch.commons.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import de.janosch.commons.swing.util.SwingUtil;

/**
 * 
 * @author Janos
 * @version 19.03.2012 | 00:02:31
 * 
 */
public class Test extends JDialog {

	private static final long serialVersionUID = -5989780741821605553L;

	private final JPanel contentPanel = new JPanel();
	private EComboBox comboBox;
	private EButton btnHallo;
	private EComboBox comboBox_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SwingUtil.setSystemLookAndFeel();
		try {
			Test dialog = new Test();
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			SwingUtil.addEscapeExitListeners(dialog);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Test() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			comboBox_1 = new EComboBox();
			comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"a", "b", "c", "d", "e", "f", "g"}));
			contentPanel.add(comboBox_1);
		}
		{
			comboBox = new EComboBox();
			comboBox.setModel(new DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5" }));
			contentPanel.add(comboBox);
		}
		{
			btnHallo = new EButton("Hallo");
			contentPanel.add(btnHallo);
		}
		
		
		
		
		
		
		
		
		btnHallo.addChangeSource(comboBox);
		btnHallo.addChangeSource(comboBox_1);
		btnHallo.setEnablement(new Enablement() {
			@Override
			public boolean isEnabled() {
				return comboBox.getSelectedItem().equals("5") && comboBox_1.getSelectedItem().equals("c");
			}
		});
	}

}
