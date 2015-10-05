package net.sqf.openDocs.workingSet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.customizer.EditorPanel;

public class WorkingSetAreaMenu extends JPopupMenu implements MouseListener, ActionListener {

	private static final long serialVersionUID = -7435474982329300953L;
	
	private JMenuItem addNew;
	private final EditorPanel ePanel;
	private JMenuItem removeAll;
	private final WorkingSetArea wsa;

	public WorkingSetAreaMenu(EditorPanel ePanel, WorkingSetArea wsa) {
		this.ePanel = ePanel;
		this.wsa = wsa;
		this.addNew = new JMenuItem("Add new Working set", OpenDocsExtension.ICONS.getIcon(4, 10));
		this.addNew.addActionListener(this);
		this.add(addNew);
		this.removeAll = new JMenuItem("Remove all Working sets", OpenDocsExtension.ICONS.getIcon(18, 22));
		this.removeAll.addActionListener(this);
		this.add(removeAll);
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			this.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			this.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if(addNew == src){
			ePanel.saveWorkingSet();
		} else if (removeAll == src){
			int confirm = ePanel.getSPW().showConfirmDialog("Remove all Working sets", "Are you shure to remove all Working sets?", new String[]{"OK", "Cancel"}, new int[]{0,1});
			if(confirm == 0){
				for (WorkingSet ws : wsa.getWorkingSets()) {
					wsa.removeWorkingSet(ws);
				}
			}
		}
	}

}
