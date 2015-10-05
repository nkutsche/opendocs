package net.sqf.view.utils.lists;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneLayout;
import javax.swing.border.Border;

import net.sqf.view.utils.lists.items.AbstractListItem;
import net.sqf.view.utils.lists.items.ItemGroup;
import net.sqf.view.utils.lists.items._ListGroupNode;
import net.sqf.view.utils.lists.items._ListNode;
import de.janosch.commons.swing.util.SwingUtil;

public abstract class AbstractList<ModelNodeType extends _ListNode, ListItemType extends AbstractListItem<ModelNodeType> & Comparable<ListItemType>>
		extends JPanel implements KeyListener {

	private Border border;
	private final GridBagLayout gbl_content = new GridBagLayout();
	private final JPanel content = new JPanel(gbl_content);
	protected JPanel endPanel = new JPanel();
	private final JPanel toolbarPanel = new JPanel();
	private final JPanel bottombarPanel = new JPanel();
	private final GridBagLayout gbl_toolbarPanel = new GridBagLayout();
	private final GridBagLayout gbl_bottombarPanel = new GridBagLayout();
	protected boolean isMultiSelectable = true;

	private ArrayList<ListItemType> itemList = new ArrayList<ListItemType>();

	private int messageIndex = 0;

	@SuppressWarnings("rawtypes")
	private HashMap<AbstractListItem, Integer> itemByPosition = new HashMap<AbstractListItem, Integer>();
	private ArrayList<ListItemType> selectedItem = new ArrayList<ListItemType>();

	public AbstractList() {
		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		toolbarPanel.setLayout(gbl_toolbarPanel);
		bottombarPanel.setLayout(gbl_bottombarPanel);

		final JScrollPane scrollPane = new JScrollPane(this.content);
		ScrollPaneLayout spl = new ScrollPaneLayout();
		scrollPane.setLayout(spl);

		scrollPane.setBackground(Color.WHITE);
		scrollPane.setOpaque(true);

		JScrollBar vertScrollBar = new JScrollBar();
		vertScrollBar.setUnitIncrement(100);
		JScrollBar horScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
		horScrollBar.setUnitIncrement(100);

		scrollPane.setVerticalScrollBar(vertScrollBar);
		scrollPane.setHorizontalScrollBar(horScrollBar);

		endPanel.setBackground(Color.WHITE);

		SwingUtil.addComponent(this, gbl, toolbarPanel, 	0, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);
		SwingUtil.addComponent(this, gbl, scrollPane, 		0, 1, 1, 1, 0.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);
		SwingUtil.addComponent(this, gbl, bottombarPanel, 	0, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);
		border = BorderFactory.createEmptyBorder();
		this.setBorder(border);
		
		// border = BorderFactory.createLineBorder(new Color(0, 100, 100, 70));
		this.content.setBackground(Color.WHITE);
		
	}
	
	

	public AbstractList(String title) {
		this();
		if(title != null){
			border = BorderFactory
					.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
							Color.BLACK), title);
			this.setBorder(border);
		}
	}

	protected void addComponentToToolbar(JComponent comp, int x, int y,
			int width, int height, double weightx, double weighty, int anchor,
			int fill) {
		SwingUtil.addComponent(this.toolbarPanel, gbl_toolbarPanel, comp, x, y,
				width, height, weightx, weighty, anchor, fill);
	}
	
	protected void addComponentToBottombar(JComponent comp, int x, int y,
			int width, int height, double weightx, double weighty, int anchor,
			int fill) {
		SwingUtil.addComponent(this.bottombarPanel, gbl_bottombarPanel, comp, x, y,
				width, height, weightx, weighty, anchor, fill);
	}

	public void addToolbar(JToolBar toolbar) {
		this.toolbarPanel.add(toolbar);
	}

	public void addListItem(ListItemType item) {
		if (!this.content.isEnabled())
			this.content.setEnabled(true);

		this.itemList.add(item);

		this.content.remove(this.endPanel);
		SwingUtil.addComponent(this.content, gbl_content, item, 0,
				messageIndex++, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH);
		SwingUtil.addComponent(this.content, gbl_content, this.endPanel, 0,
				messageIndex + 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH);

		this.itemByPosition.put(item, messageIndex);
	}

	public void addListItem(ListItemType[] items) {
		Arrays.sort(items);
		for (ListItemType item : items) {
			addListItem(item);
		}

	}

	public void addListItem(ArrayList<ListItemType> items) {
		Collections.sort(items);
		for (ListItemType item : items) {
			addListItem(item);
		}
	}
	
	protected ArrayList<ListItemType> getListItems(){
		return new ArrayList<ListItemType>(this.itemList);
	}
	
	public ItemGroup<ModelNodeType, ListItemType> createItemGroup(ArrayList<ListItemType> items, ArrayList<ItemGroup<ModelNodeType, ListItemType>> subGroups, _ListGroupNode node){
		Collections.sort(items);
		ItemGroup<ModelNodeType, ListItemType> group = new ItemGroup<ModelNodeType, ListItemType>(
				node, items, subGroups, 0, this, null) {
			private static final long serialVersionUID = -420997520077674539L;

			@Override
			public void expand() {
				for (ListItemType item : this.items) {
					showItem(item);
				}
				super.expand();
			}

			@Override
			public void collapse() {
				for (ListItemType item : this.items) {
					hideItem(item);
				}
				super.collapse();
			}
			
		};
		
		return group;
		
	}
	
	protected void addListItemAsGroup(ArrayList<ListItemType> items,
			_ListGroupNode node) {
		addListItemAsGroup(items, new ArrayList<ItemGroup<ModelNodeType, ListItemType>>(), node);
	}
	
	protected void addListItemAsGroup(ArrayList<ListItemType> items, ArrayList<ItemGroup<ModelNodeType, ListItemType>> subGroups,
			_ListGroupNode node) {
		ItemGroup<ModelNodeType, ListItemType> group = createItemGroup(items, subGroups, node);
		this.addListItem(group);
	}

	@SuppressWarnings("rawtypes")
	private void showItem(AbstractListItem item) {
		item.setVisible(true);
	}

	@SuppressWarnings("rawtypes")
	private void hideItem(AbstractListItem item) {
		item.setVisible(false);
	}

	public void addListItem(ItemGroup<ModelNodeType, ListItemType> group) {
		if (!this.content.isEnabled())
			this.content.setEnabled(true);
		synchronized (group) {
			
			this.content.remove(this.endPanel);
			SwingUtil.addComponent(this.content, gbl_content, group, 0,
					messageIndex++, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH,
					GridBagConstraints.BOTH);
			SwingUtil.addComponent(this.content, gbl_content, this.endPanel, 0,
					messageIndex + 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
					GridBagConstraints.BOTH);
			
			this.itemByPosition.put(group, messageIndex);
			
			for (ItemGroup<ModelNodeType, ListItemType> subGroup : group.getSubGroups()) {
				this.addListItem(subGroup);
			}
			
		}

		this.addListItem(group.getItems());

	}

	public void removeAllItems() {
		this.content.removeAll();
		this.messageIndex = 0;
		this.itemList = new ArrayList<ListItemType>();
		this.content.setEnabled(false);
		SwingUtil.addComponent(this.content, gbl_content, this.endPanel, 0,
				messageIndex + 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH);
	}

	public boolean isSelectedItem(ListItemType item) {
		return getSelectedItem().contains(item);
	}

	public ArrayList<ListItemType> getSelectedItem() {
		return this.selectedItem;
	}

	public void unselectItems() {
		if (this.selectedItem != null) {
			for (ListItemType item : this.selectedItem) {
				item.setSelection(false);
			}
			this.selectedItem = new ArrayList<ListItemType>();
		}

	}

	private void unselectItem(ListItemType item) {
		selectedItem.remove(item);
		item.setSelection(false);
	}
	public void selectUnselectItem(ArrayList<ListItemType> items) {
		unselectItems();
		for (ListItemType item : items) {
			select(item);
		}
	}
	public void selectUnselectItem(MouseEvent e, ArrayList<ListItemType> items) {
		if(this.isMultiSelectable){
			if (!e.isControlDown()) {
				unselectItems();
			}
			for (ListItemType item : items) {
				select(item);
			}
		}
	}

	public void selectUnselectItem(MouseEvent e, ListItemType item) {
		if (e.isShiftDown() && this.isMultiSelectable) {
			shiftSelectUnselect(item);
		} else if (e.isControlDown() && this.isMultiSelectable) {
			ctrlSelectUnselect(item);
		} else {
			unselectItems();
			select(item);
		}
	}

	private void shiftSelectUnselect(ListItemType item) {
		ListItemType lastSelectedItem = selectedItem.size() > 0 ? selectedItem
				.get(selectedItem.size() - 1) : null;
		unselectItems();
		if (lastSelectedItem != null) {
			select(lastSelectedItem, item);
		} else {
			select(item);
		}
	}

	private void ctrlSelectUnselect(ListItemType item) {
		if (item.isSelected()) {
			unselectItem(item);
		} else {
			select(item);
		}
	}

	private void select(ListItemType item) {
		this.selectedItem.add(item);
		item.setSelection(true);
	}

	private void select(ListItemType from, ListItemType to) {
		boolean selecting = false;
		if(this.isMultiSelectable){
			for (ListItemType item : this.itemList) {
				if (item == from || item == to) {
					selecting = !selecting;
					select(item);
				} else if (selecting) {
					select(item);
				}
			}
		} else {
			select(from);
		}
	}

	public ListItemType getListItemByNode(_ListNode node) {
		for (ListItemType item : this.itemList) {
			if (item.getModelNode() == node)
				return item;
		}
		return null;
	}

	public ArrayList<ListItemType> getAllItems() {
		return this.itemList;
	}

	private static final long serialVersionUID = 2271347445049935275L;

	//
	// Key Listener
	//

	public void implementShortCut(Container comp) {
		comp.removeKeyListener(this);
		comp.addKeyListener(this);
		for (Component childComp : comp.getComponents()) {
			this.implementShortCut(childComp);
		}
	}

	public void implementShortCut(Component comp) {
		if (comp instanceof Container) {
			this.implementShortCut((Container) comp);
		} else {
			comp.removeKeyListener(this);
			comp.addKeyListener(this);
		}
	}

	@Override
	public Component add(Component comp) {
		comp.removeKeyListener(this);
		comp.addKeyListener(this);
		return super.add(comp);
	}

	@Override
	public void keyTyped(KeyEvent ke) {

	}

	@Override
	public void keyReleased(KeyEvent ke) {
//		if (KeyEvent.VK_CONTROL == ke.getExtendedKeyCode()) {
//			ctrlKeyPressed = false;
//		} else if (KeyEvent.VK_SHIFT == ke.getExtendedKeyCode()) {
//			shiftKeyPressed = false;
//		}
	}

	@Override
	public void keyPressed(KeyEvent ke) {
//		if (KeyEvent.VK_CONTROL == ke.getExtendedKeyCode()) {
//			ctrlKeyPressed = true;
//		} else if (KeyEvent.VK_SHIFT == ke.getExtendedKeyCode()) {
//			shiftKeyPressed = true;
//		}
	}

	// public boolean isCtrlPressed(){
	// return ctrlKeyPressed;
	// }
	//
	// public boolean isShiftPressed(){
	// return shiftKeyPressed;
	// }
}
