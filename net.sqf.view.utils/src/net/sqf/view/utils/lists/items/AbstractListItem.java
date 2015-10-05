package net.sqf.view.utils.lists.items;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;

import de.janosch.commons.swing.util.SwingUtil;

public abstract class AbstractListItem<ModelNodeType extends _ListNode> extends JPanel {
	
	private static final Color HOVER_COLOR = Color.WHITE;
	private static final Color DEFAULT_COLOR = Color.WHITE;
	private static final Color DEFAULT_FONT_COLOR = Color.BLACK;
	private static final Color HOVER_FONT_COLOR = Color.BLACK;
	private static final Color SELECTION_COLOR = new Color(0, 255, 255);
	private static final Color SELECTION_FONT_COLOR = Color.BLACK;
	private static final Border DEFAULT_BORDER = BorderFactory.createEmptyBorder(1, 0, 1, 0);
	private static final Border HOVER_SELECT_BORDER = BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0, 150, 255));
	
	

	private static final long serialVersionUID = 159899019060744554L;
	
	private GridBagLayout gbl = new GridBagLayout();

	private Color usedBgColor = DEFAULT_COLOR;
	private Color usedFontColor = DEFAULT_FONT_COLOR;
	
	private Color defaultFontColor = DEFAULT_FONT_COLOR;
	private Color selectFontColor = SELECTION_FONT_COLOR;
	
	private JLabel titleLabel;
	private boolean isSelected;
	protected final ModelNodeType node;
	protected final ControlPanel controlPanel = new ControlPanel(new Mouse());
	private final JLabel iconLabel;
	private MouseListener selcetionListener;
	private int paddingLabel;
//	private JPopupMenu menu = null;
	private HashMap<Integer, JPopupMenu> menuByModifier = new HashMap<Integer, JPopupMenu>();
	private Color howerFontColor = HOVER_FONT_COLOR;
	
	
	public AbstractListItem(ModelNodeType node, Icon defaultIcon){
		this(node, 0, defaultIcon);
	}
	
	
	public AbstractListItem(ModelNodeType node, int level, Icon defaultIcon) {
		this.node = node;
		this.setLayout(gbl);
		titleLabel = new JLabel(node.toString());
		
		Icon icon = null;
		if(node.hasIcon()){
			try {
				icon = node.getIcon();
			} catch (IOException e) {
				icon = defaultIcon;
			}
		} else {
			icon = defaultIcon;
		}
		iconLabel = new JLabel(icon);
		
		int iconW = icon != null ? icon.getIconWidth() : 0;
		paddingLabel = 2;
		if(iconW > 16){
			paddingLabel += iconW - 16;
		}
		
		SwingUtil.addComponent(this, gbl, iconLabel, 			0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, level * 10, 0, paddingLabel));
		SwingUtil.addComponent(this, gbl, titleLabel, 			1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH);
		SwingUtil.addComponent(this, gbl, this.controlPanel, 	2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5));
		
		this.setBackground(Color.WHITE);
		this.setBorder(DEFAULT_BORDER);
		
		this.addMouseListener(new Mouse());
	}
	
	public void addSelectionListener(MouseListener ml){
//		this.addMouseListener(ml);
		this.selcetionListener = ml;
	}
	
	public void setLevel(int level){
		GridBagConstraints gbc = gbl.getConstraints(iconLabel);
		gbc.insets = new Insets(0, level * 10, 0, paddingLabel);
		gbl.setConstraints(iconLabel, gbc);
		this.repaint();
	}
	
	protected void addIconMouseAdapter(MouseAdapter mouse){
		this.iconLabel.addMouseListener(mouse);
		this.iconLabel.addMouseMotionListener(mouse);
		this.iconLabel.addMouseWheelListener(mouse);
	}
	
	public void implementPopupMenu(JPopupMenu menu, int modifier){
		this.menuByModifier.put(modifier, menu);
	}
	public void implementPopupMenu(JPopupMenu[] menus){
		this.implementPopupMenu(menus, new int[]{NO_MODIFIER, SHIFT_DOWN, CTRL_DOWN});
	}
	public void implementPopupMenu(JPopupMenu[] menus, int[] modifiers){
		int i = 0;
		for (int mod : modifiers) {
			if(i < menus.length){
				this.menuByModifier.put(mod, menus[i]);
			}
			i++;
		}
	}
	
	public final static int NO_MODIFIER = 0;
	
	public final static int ALT_DOWN = MouseEvent.ALT_DOWN_MASK;
	public final static int CTRL_DOWN = MouseEvent.CTRL_DOWN_MASK;
	public final static int SHIFT_DOWN = MouseEvent.SHIFT_DOWN_MASK;
	
	public void implementPopupMenu(JPopupMenu menu){
		this.implementPopupMenu(menu, NO_MODIFIER);
	}
	
	public class Mouse extends MouseAdapter {
		
		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			if(e.isPopupTrigger()){
				doPop(e);
			}
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			if(e.isPopupTrigger()){
				doPop(e);
			}
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			if(isSelected){
				setBorder(HOVER_SELECT_BORDER);
			} else {
				setBackground(HOVER_COLOR);
				titleLabel.setForeground(howerFontColor);
			}
			updateUI();
			super.mouseEntered(e);
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			if(selcetionListener != null){
				selcetionListener.mouseClicked(e);
			}
		} 
		
		private void doPop(MouseEvent e){
			if((!isSelected) && selcetionListener != null){
				selcetionListener.mouseClicked(e);
			}
			int modifier = NO_MODIFIER;
			if(e.isShiftDown()){
				modifier = SHIFT_DOWN;
			} else if (e.isControlDown()) {
				modifier = CTRL_DOWN;
			} 
			
			if(!menuByModifier.containsKey(modifier)){
				modifier = NO_MODIFIER;
			}
			
			JPopupMenu menu = null;
			if(menuByModifier.containsKey(modifier)){
				menu = menuByModifier.get(modifier);
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
	    }
		
		@Override
		public void mouseExited(MouseEvent e) {
			setBorder(DEFAULT_BORDER);
			setBackground(usedBgColor);
			titleLabel.setForeground(usedFontColor);
			updateUI();
			super.mouseExited(e);
		}
	}
	
	
//	public abstract Icon getDefaultIcon();
	

	public void setSelection(boolean isSelected) {
		this.isSelected = isSelected;
		if(isSelected){
			this.setBackground(SELECTION_COLOR);
			titleLabel.setForeground(this.selectFontColor);
			this.usedBgColor = SELECTION_COLOR;
			this.usedFontColor = this.selectFontColor;
		} else {
			this.setBackground(DEFAULT_COLOR);
			titleLabel.setForeground(this.defaultFontColor);
			this.usedBgColor = DEFAULT_COLOR;
			this.usedFontColor = this.defaultFontColor;
		}
		repaint();
	}
	
	protected void updateText(){
		titleLabel.setText(node.toString());
		this.repaint();
	}
	
	protected void setLabelStyle(int style){
		Font f = titleLabel.getFont();
		f = new Font(f.getName(), style, f.getSize());
		titleLabel.setFont(f);
		titleLabel.repaint();
	}
	protected void setLabelStyle(Color fg){
		titleLabel.setForeground(fg);
		this.usedFontColor = fg;
		this.defaultFontColor = fg;
		this.selectFontColor = fg;
		this.howerFontColor = fg;
	}
	
	public boolean isSelected(){
		return this.isSelected;
	}
	
	public ModelNodeType getModelNode(){
		return this.node;
	}
	
	@Override
	public String toString() {
		return node.toString();
	}
	
	public boolean isOnControllIcon(Point point) {
		return this.controlPanel.contains(point);
	}
	
}
