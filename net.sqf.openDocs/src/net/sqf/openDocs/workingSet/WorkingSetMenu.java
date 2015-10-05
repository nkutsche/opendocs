package net.sqf.openDocs.workingSet;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;

import net.sqf.openDocs.OpenDocsExtension;
import net.sqf.openDocs.buttons.MenuItemSupplier;
import net.sqf.openDocs.customizer.EditorItem;
import net.sqf.view.utils.images.IconMap;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class WorkingSetMenu extends JPopupMenu implements MouseListener,
		ActionListener, MenuItemSupplier {
	private static final long serialVersionUID = 1L;
	private final WorkingSet ws;

	private Icon openIcon = OpenDocsExtension.ICONS.getIcon(12, 6);
	private Icon closeIcon = OpenDocsExtension.ICONS.getIcon(0, 10);
	private Icon deleteIcon = OpenDocsExtension.ICONS.getIcon(18, 22);
	private Icon renameIcon = OpenDocsExtension.ICONS.getIcon(16, 1);

	JMenuItem open = new JMenuItem("Open", openIcon);
	JMenuItem delete = new JMenuItem("Delete", deleteIcon);
	JMenuItem close = new JMenuItem("Close", closeIcon);
	JMenuItem closeOther = new JMenuItem("Close other files", closeIcon);
	JMenuItem rename = new JMenuItem("Edit", renameIcon);
	private WorkingSetArea workingSetArea;
	private final StandalonePluginWorkspace spw;

	public WorkingSetMenu(WorkingSet ws, WorkingSetArea workingSetArea,
			StandalonePluginWorkspace spw) {
		this.ws = ws;
		this.workingSetArea = workingSetArea;
		this.spw = spw;
		open.addActionListener(this);
		delete.addActionListener(this);
		close.addActionListener(this);
		closeOther.addActionListener(this);
		rename.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == closeOther) {
			ws.closeOtherInEditor();
			return;
		} else if (src == close) {
			ws.closeInEditor();
			return;
		} else if (src == open) {
			ws.openInEditor();
			return;
		} else if (src == delete) {
			workingSetArea.removeWorkingSet(ws);
			return;
		} else if (src == rename) {
			ws.rename();
			return;
		}
	}

	@Override
	public void show(Component c, int x, int y) {
		this.removeAll();
		for (JComponent item : this.getMenuItems()) {
			this.add(item);
		}
		super.show(c, x, y);
	}

	private final static int MENU_TYPE_OPEN = 0;
	private static final int MENU_TYPE_CLOSE = 1;
	private static final int MENU_TYPE_DELETE = 2;

	private abstract class Item extends JMenuItem implements ActionListener,
			MenuKeyListener {

		private static final long serialVersionUID = 4168892755405154563L;
		
		MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				isHovered = true;
			}

			@Override
			public void mouseExited(MouseEvent e) {
				isHovered = false;
				setIcon(defaultLabel.getIcon());
				setText(defaultLabel.getText());
				repaint();
			}
		};
		private boolean isHovered = false;
		// private final URL url;
		private final JLabel defaultLabel;
		private JLabel ctrlLabel = null;
		private JLabel shiftLabel = null;

		Item(JLabel defaultLabel, JLabel ctrlLabel, JLabel shiftLabel) {
			this(defaultLabel, ctrlLabel);
			this.shiftLabel = shiftLabel;
		}

		Item(JLabel defaultLabel, JLabel ctrlLabel) {
			super(defaultLabel.getText(), defaultLabel.getIcon());
			this.ctrlLabel = ctrlLabel;
			this.defaultLabel = defaultLabel;
			// this.url = url;
			this.addActionListener(this);
			this.addMenuKeyListener(this);
			this.addMouseListener(ma);
		}

		@Override
		public void menuKeyPressed(MenuKeyEvent e) {
			if (e.isControlDown() && isHovered) {
				if (ctrlLabel != null) {
					setIcon(ctrlLabel.getIcon());
					setText(ctrlLabel.getText());
				}
			} else if (e.isShiftDown() && isHovered) {
				if (shiftLabel != null) {
					setIcon(shiftLabel.getIcon());
					setText(shiftLabel.getText());
				}
			}
			repaint();
		}

		@Override
		public void menuKeyReleased(MenuKeyEvent e) {
			setIcon(defaultLabel.getIcon());
			setText(defaultLabel.getText());
			repaint();
		}

		@Override
		public void menuKeyTyped(MenuKeyEvent e) {
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getModifiers() == 18
					|| e.getModifiers() == ActionEvent.CTRL_MASK) {
				ctrlAction(e);
			} else if (e.getModifiers() == 17 || e.getModifiers() == ActionEvent.SHIFT_MASK) {
				shiftAction(e);
			} else {
				action(e);
			}
		}

		public void shiftAction(ActionEvent e) {
			action(e);
		};

		public abstract void action(ActionEvent e);

		public void ctrlAction(ActionEvent e) {
			action(e);
		};

	}

	private JMenuItem createSingleFileMenu(final URL url) {

		EditorItem editorItem = ws.itemByUrl(url);
		final Icon icon = editorItem.getEditorNode().getIcon();
		Icon[] adjustedIcons = IconMap.adjustIcons(new Icon[]{icon, closeIcon, deleteIcon}); 
		final JMenuItem menu = new Item(new JLabel(editorItem.toString(), adjustedIcons[0],
				0), new JLabel("Close " + editorItem.toString(), adjustedIcons[1], 0), new JLabel("Remove " +editorItem.toString(), adjustedIcons[2], 0)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void action(ActionEvent e) {
				spw.open(url);
			}

			@Override
			public void ctrlAction(ActionEvent e) {
				spw.close(url);
			}

			@Override
			public void shiftAction(ActionEvent e) {
				ws.delete(url);
			}

		};
		return menu;
	}

	private JMenu createSingleFileMenu(final int type, String title, Icon icon) {

		JMenu menu = new JMenu(title);
		menu.setIcon(icon);
		for (final URL url : ws.getURLs()) {
			EditorItem editorItem = ws.itemByUrl(url);
			JMenuItem item = new JMenuItem(editorItem.toString());
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					switch (type) {
					case MENU_TYPE_OPEN:
						spw.open(url);
						break;
					case MENU_TYPE_CLOSE:
						spw.close(url);
						break;
					case MENU_TYPE_DELETE:
						ws.delete(url);
						break;

					default:
						break;
					}
				}
			});
			menu.add(item);
			item.setIcon(editorItem.getEditorNode().getIcon());
		}
		return menu;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

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

	public ArrayList<JComponent> getMenuItems() {
		ArrayList<JComponent> items = new ArrayList<JComponent>();
		if (workingSetArea.groupMenuPerFile()) {
			for (URL url : ws.getURLs()) {
				items.add(createSingleFileMenu(url));
			}
		} else {
			items.add(createSingleFileMenu(MENU_TYPE_OPEN, "Open single file",
					openIcon));
			items.add(createSingleFileMenu(MENU_TYPE_CLOSE,
					"Close single file", closeIcon));
			items.add(createSingleFileMenu(MENU_TYPE_DELETE,
					"Delete single file", deleteIcon));
		}
		if (ws.getURLs().size() > 0) {
			JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
			sep.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
			items.add(sep);
		}
		items.add(closeOther);
		items.add(close);
		items.add(rename);
		items.add(delete);
		items.add(open);
		return items;
	}

}