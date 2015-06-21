package net.sqf.openDocs.options;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import de.janosch.commons.swing.util.SwingUtil;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.OpenDocsPlugin;
import net.sqf.view.utils.images.IconMap;

public class AboutOpenDocs extends JDialog {

	private static final long serialVersionUID = -3157299957451215553L;

	private final GridBagLayout gbl = new GridBagLayout();


	// public AboutDocViewer() {
	// this.setTitle("About DocViewer");
	// this.setIconImage(OpenDocsExtension.ICONS.getIcon(13, 20).getImage());
	// this.setLayout(gbl);
	// String textRows = "";
	//
	// textRows += "Version: 0.1.1";
	// textRows += "\n";
	// textRows += "� 2015 Nico Kutscherauer. All Rights Reserved.";
	//
	// String[] texts = textRows.split("\n");
	//
	// int row = 0;
	// for (String text : texts) {
	// SwingUtil.addComponent(this, gbl, new JLabel(text), 0, row++, 1, 1, 1.0,
	// 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new
	// Insets(5, 5, 5, 5));
	// }
	//
	//
	// this.setModal(true);
	//
	// this.addFocusListener(new FocusListener() {
	// @Override
	// public void focusLost(FocusEvent arg0) {
	// AboutDocViewer.this.setVisible(false);
	// }
	//
	// @Override
	// public void focusGained(FocusEvent arg0) {
	//
	// }
	// });
	// }
	public AboutOpenDocs(){
		this(new JFrame(), OpenDocsPlugin.getInstance().getDescriptor().getBaseDir());
	}
	
	public AboutOpenDocs(JFrame parent){
		this(parent, OpenDocsPlugin.getInstance().getDescriptor().getBaseDir());
	}
	
	public AboutOpenDocs(JFrame parent, File baseDir) {
		super(parent);
		this.setTitle("About DocViewer");
		this.setIconImage(OpenDocsExtension.ICONS.getIcon(13, 20).getImage());


		this.setLayout(gbl);

		this.setModal(true);
		
		File aboutFile = Config.getAbout(baseDir);
		
		
		String html = "<html></html>";

		JEditorPane jep;
		try {
			jep = new JEditorPane(aboutFile.toURI().toString());
		} catch (IOException e2) {
			jep = new JEditorPane("text/html", html);
		}
		jep.setEditable(false);
		jep.setFocusable(false);
		this.setResizable(false);

		this.add(jep);
		jep.setBackground(new Color(238,238,238));
		this.setMaximumSize(new Dimension(100, getMaximumSize().height));

		this.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
				AboutOpenDocs.this.setVisible(false);
			}

			@Override
			public void focusGained(FocusEvent arg0) {

			}
		});

		jep.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
	}
	
	@Override
	public void setVisible(boolean visible) {
		Window owner = getOwner();
		if(owner != null){
			net.sqf.view.utils.swing.SwingUtil.centerFrame(this, owner);
		}
		
		super.setVisible(visible);
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		OpenDocsExtension.ICONS = new IconMap(new File(new File("bindata"), "icons/diagona_my.gif"));
		AboutOpenDocs adv = new AboutOpenDocs(new JFrame(), new File("bindata"));
		adv.pack();
		adv.setVisible(true);
	}
}
