package net.sqf.openDocs.workingSet;

import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sqf.openDocs.OpenDocsExtension;

import org.apache.batik.ext.swing.GridBagConstants;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import de.janosch.commons.swing.util.SwingUtil;

public class RenameDialog extends JDialog {
	
	private static final long serialVersionUID = -5707759449837039432L;

	private static JFrame getFrameFromSPW(StandalonePluginWorkspace spw){
		Object fo = spw.getParentFrame();
		if(fo instanceof JFrame){
			return (JFrame) fo;
		}
		return null;
	}
	
	private final GridBagLayout gbl = new GridBagLayout();
	private final JPanel contentPanel = new JPanel(gbl);
	private final JTextField nameTf = new TextField(10);
	private final JTextField descTf = new TextField();
	private final WorkingSet ws;
	
	private class TextField extends RenameFields {
		private static final long serialVersionUID = -5363934020982306912L;

		public TextField() {
			super();
		}
		
		public TextField(int col){
			super(col);
		}
		
		@Override
		public void enter() {
			ok();
		}

		@Override
		public void escape() {
			cancel();
		}
		
	}
	
	public final static JLabel[] ICONS = new JLabel[]{
		new JLabel("No icon"),
		new JLabel(OpenDocsExtension.ICONS.getIcon(0, 15)),
		new JLabel(OpenDocsExtension.ICONS.getIcon(2, 15)),
		new JLabel(OpenDocsExtension.ICONS.getIcon(4, 15)),
		new JLabel(OpenDocsExtension.ICONS.getIcon(6, 15)),
		new JLabel(OpenDocsExtension.ICONS.getIcon(8, 15)),
		new JLabel(OpenDocsExtension.ICONS.getIcon(10, 15)),
		new JLabel(OpenDocsExtension.ICONS.getIcon(12, 15)),
		new JLabel(OpenDocsExtension.ICONS.getIcon(14, 15)),
		new JLabel(OpenDocsExtension.ICONS.getIcon(16, 15)),
		new JLabel(OpenDocsExtension.ICONS.getIcon(18, 15))
	};
	
	private final JComboBox<JLabel> iconBox = new IconComboBox(ICONS);
	
	public RenameDialog(StandalonePluginWorkspace spw, final WorkingSet ws){
		super(getFrameFromSPW(spw));
		this.setTitle("Working set preferences");
		this.ws = ws;
		this.setContentPane(contentPanel);
		GridBagLayout body_gbl = new GridBagLayout();
		JPanel body = new JPanel(body_gbl);

		GridBagLayout footer_gbl = new GridBagLayout();
		JPanel footer = new JPanel(footer_gbl);
		
		SwingUtil.addComponent(contentPanel, gbl, body, 		0, 0, 1, 1, 1.0, 1.0, GridBagConstants.NORTH, GridBagConstants.BOTH, new Insets(10, 5, 2, 5));
		SwingUtil.addComponent(contentPanel, gbl, footer, 		0, 1, 1, 1, 1.0, 0.0, GridBagConstants.SOUTH, GridBagConstants.HORIZONTAL, new Insets(3, 5, 5, 5));
		
		SwingUtil.addComponent(body, body_gbl, new JLabel("Set options for new Working set:"), 0, 0, 3, 1, 0.0, 0.0, GridBagConstants.NORTHWEST, GridBagConstants.NONE, new Insets(0, 0, 5, 30));
		SwingUtil.addComponent(body, body_gbl, new JLabel("Short name:"), 		0, 1, 2, 1, 1.0, 0.0, GridBagConstants.NORTHWEST, GridBagConstants.NONE, new Insets(3, 0, 0, 0));
		SwingUtil.addComponent(body, body_gbl, nameTf, 							1, 1, 2, 1, 1.0, 0.0, GridBagConstants.NORTHEAST, GridBagConstants.NONE, new Insets(3, 0, 0, 0));
		SwingUtil.addComponent(body, body_gbl, new JLabel("Description:"), 		0, 2, 2, 1, 1.0, 0.0, GridBagConstants.NORTHWEST, GridBagConstants.NONE, new Insets(3, 0, 0, 0));
		SwingUtil.addComponent(body, body_gbl, descTf, 							1, 2, 2, 1, 1.0, 0.0, GridBagConstants.NORTHEAST, GridBagConstants.NONE, new Insets(3, 0, 0, 0));
		SwingUtil.addComponent(body, body_gbl, new JLabel("Icon:"), 				0, 3, 2, 1, 1.0, 0.0, GridBagConstants.NORTHWEST, GridBagConstants.NONE, new Insets(3, 0, 0, 0));
		SwingUtil.addComponent(body, body_gbl, iconBox, 							1, 3, 2, 1, 1.0, 0.0, GridBagConstants.NORTHEAST, GridBagConstants.NONE, new Insets(3, 0, 0, 0));
		
		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ok();
			}
		});
		JButton cnlBtn = new JButton("Cancel");
		cnlBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cancel();
			}
		});
		SwingUtil.addComponent(footer, footer_gbl, new JPanel(), 				0, 4, 1, 1, 1.0, 0.0, GridBagConstants.NORTHWEST, GridBagConstants.HORIZONTAL);
		SwingUtil.addComponent(footer, footer_gbl, cnlBtn, 						1, 4, 1, 1, 0.0, 0.0, GridBagConstants.NORTHEAST, GridBagConstants.NONE);
		SwingUtil.addComponent(footer, footer_gbl, okBtn, 						2, 4, 1, 1, 0.0, 0.0, GridBagConstants.NORTHEAST, GridBagConstants.NONE);
	}
	@Override
	public void setVisible(boolean visible) {
		if(visible){
			nameTf.setText(ws.getTitle());
			descTf.setText(ws.getDescription());
			iconBox.setSelectedIndex(ws.getIconIndex());
			this.setModal(true);
			this.pack();
//			this.setSize((int)(this.getWidth() * 1.5), this.getHeight());
			net.sqf.view.utils.swing.SwingUtil.centerFrame(this, getOwner());
		} else {
			setModal(false);
		}
			
		super.setVisible(visible);
	}
	private void ok(){
		ws.setTitle(nameTf.getText());
		ws.setDescription(descTf.getText());
		JLabel jl = (JLabel) iconBox.getSelectedItem();
		ws.setIcon(jl.getIcon());
		ws.setIconIndex(iconBox.getSelectedIndex());
		setVisible(false);
	}
	
	private void cancel(){
		setVisible(false);
	}
}
