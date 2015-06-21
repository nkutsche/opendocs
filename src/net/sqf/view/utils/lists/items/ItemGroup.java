package net.sqf.view.utils.lists.items;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.Icon;

import net.sqf.view.utils.lists.AbstractList;

public abstract class ItemGroup<SubNodeType extends _ListNode, SubItemType extends AbstractListItem<SubNodeType> & Comparable<SubItemType>>
		extends AbstractListItem<_ListGroupNode> {

	private static final long serialVersionUID = -3830254020006810672L;

	protected final ArrayList<SubItemType> items;

	private boolean isExpanded = true;

	private final AbstractList<SubNodeType, SubItemType> parentList;
	private final ArrayList<ItemGroup<SubNodeType, SubItemType>> subGroups;

	public ItemGroup(_ListGroupNode node, ArrayList<SubItemType> items,
			ArrayList<ItemGroup<SubNodeType, SubItemType>> subGroups,
			int level, AbstractList<SubNodeType, SubItemType> parentList, Icon defaultIcon) {
		super(node, level, defaultIcon);

		this.items = items;
		this.subGroups = subGroups;
		this.parentList = parentList;

		setLevel(level);

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selectSubItems(e);
				super.mouseClicked(e);
			}
		});
		this.addIconMouseAdapter(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (isExpanded) {
					collapse();
				} else {
					expand();
				}
				super.mouseClicked(me);
			}
		});
	}

	@Override
	public void setLevel(int level) {
		super.setLevel(level);

		for (ItemGroup<SubNodeType, SubItemType> subGroup : subGroups) {
			subGroup.setLevel(level + 1);
		}
		for (SubItemType item : items) {
			item.setLevel(level + 1);
		}
	}

	protected void selectSubItems(MouseEvent e) {
		ArrayList<SubItemType> descItems = getDescandentItems();

		this.parentList.selectUnselectItem(e, descItems);
	}

	private ArrayList<SubItemType> getDescandentItems() {
		ArrayList<SubItemType> descItems = new ArrayList<SubItemType>();

		for (ItemGroup<SubNodeType, SubItemType> group : this.getSubGroups()) {
			descItems.addAll(group.getDescandentItems());
		}

		descItems.addAll(getItems());

		return descItems;
	}

	// @Override
	// public Icon getDefaultIcon() {
	// // if(this.node.getMessageCount() > 0){
	// // return ema.getIcon(IconMap.MESSAGE_GROUP_FAILED);
	// // } else {
	// // return ema.getIcon(IconMap.MESSAGE_GROUP_SUCCEDED);
	// // }
	// return null;
	// }

	public ArrayList<SubItemType> getItems() {
		ArrayList<SubItemType> subItems = new ArrayList<SubItemType>();
		// for (ItemGroup<SubNodeType, SubItemType> group : subGroups) {
		// subItems.addAll(group.getItems());
		// }
		subItems.addAll(items);
		return subItems;
	}

	public void expand() {
		for (ItemGroup<SubNodeType, SubItemType> group : subGroups) {
			group.setVisible(true);
			group.expand();
		}
		for (SubItemType subItem : this.items) {
			subItem.setVisible(true);
		}
		this.isExpanded = true;
	};

	public void collapse() {
		for (ItemGroup<SubNodeType, SubItemType> group : subGroups) {
			group.setVisible(false);
			group.collapse();
		}
		for (SubItemType subItem : this.items) {
			subItem.setVisible(false);
		}
		this.isExpanded = false;
	}

	public ArrayList<ItemGroup<SubNodeType, SubItemType>> getSubGroups() {
		return this.subGroups;
	}

}
