package net.sqf.view.utils.types.panels;


import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
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
import java.io.IOException;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import net.sqf.view.utils.images.IconMap;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.janosch.commons.swing.util.SwingUtil;

public class CalendarPanel2 extends JPanel implements MouseListener {
	private static final long serialVersionUID = -4261661006435142610L;
	private final JDialog dialog;
	private final JTextField textField;
	
	private final Font defFont;
	
	private final int padding;
	private final GridBagLayout gbl = new GridBagLayout();

	private final int minWidth;
	private final int minHeight;
	private JPanel contentPanel = new JPanel();
	
	private final Border defaultBorder = BorderFactory.createBevelBorder(
			BevelBorder.RAISED, new Color(240, 240, 240), new Color(150, 150,
					150));
	private final Border greenBorder = BorderFactory.createBevelBorder(
			BevelBorder.LOWERED, new Color(0, 110, 0), new Color(0, 200,
					0));
	private final Border selectBorder = BorderFactory.createBevelBorder(
			BevelBorder.RAISED, new Color(30, 20, 120),
			new Color(110, 100, 200));

	public CalendarPanel2(JFormattedTextField field, int fontSize, Container owner) {
		
		if(owner instanceof Dialog){
			dialog = new JDialog((Dialog) owner);
		} else if(owner instanceof Frame) {
			dialog = new JDialog((Frame) owner);
		} else if(owner instanceof Window) {
			dialog = new JDialog((Window) owner);
		} else {
			dialog = new JDialog(new JFrame());
		}
		
		this.textField = field;
		Font defFont = new JLabel().getFont();
		this.defFont = new Font(defFont.getName(), defFont.getStyle(), fontSize);
		
		this.minWidth = 25 * fontSize;
		this.minHeight = this.minWidth;
		this.padding  = (int) Math.round(fontSize * 0.75);
		this.setLayout(gbl);

		String text = this.textField.getText();
		DateTime actuellDat = convert(text);
		buildCalendar(actuellDat, actuellDat);
		
		// this.setBackground(Color.RED);

		dialog.addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowLostFocus(WindowEvent arg0) {
				dispose();
			}

			@Override
			public void windowGainedFocus(WindowEvent arg0) {
			}
		});
		dialog.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				dispose();
			}
		});
		dialog.setMinimumSize(new Dimension(minWidth, minHeight));
		dialog.setUndecorated(true);
		dialog.add(this);
	}

	private void dispose() {
		this.dialog.dispose();
		this.textField.setEnabled(true);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (textField.isEnabled()) {
			
			String text = this.textField.getText();
			DateTime actuellDat = convert(text);
			buildCalendar(actuellDat, actuellDat);

//			dialog.setSize(textField.getWidth(), textField.getWidth());
			dialog.setLocation(getDialogBounds());
			// System.out.println(tfLoc.y);
			dialog.setModal(false);
			dialog.setVisible(true);
//			dialog.pack();
			textField.setEnabled(false);
		} else {
			dispose();
		}
	}
	
	private Point getDialogBounds(){

		Point tfLoc = textField.getLocationOnScreen();
		Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		
		int x = tfLoc.x + textField.getWidth() - dialog.getWidth();
		x = x < 0 ? 0 : x;
		
		int y = tfLoc.y + textField.getHeight();
		y = y < 0 ? 0 : y;
		
		if((x + dialog.getWidth() ) > screen.width){
			x = screen.width - dialog.getWidth();
		}
		if((y + dialog.getHeight()) > screen.height){
			y = tfLoc.y - dialog.getHeight();
		}
		
		return new Point(x, y);
	}
	

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	private void clear() {
		this.remove(contentPanel);
	}

	private void buildCalendar(final DateTime actuell,
			final DateTime selectedDate) {
		clear();
		this.setBorder(defaultBorder);
		contentPanel = new JPanel();
		contentPanel.setLayout(gbl);
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setOpaque(true);
		GridBagLayout gblRoot = new GridBagLayout();
		this.setLayout(gblRoot);
		SwingUtil.addComponent(this, gblRoot, contentPanel, 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH);
		
		JPanel head = buildHeader(actuell, selectedDate);
		SwingUtil.addComponent(contentPanel, gbl, head, 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH);
		

		JPanel body = new JPanel();
		GridBagLayout gblBody = new GridBagLayout();
		body.setLayout(gblBody);
		body.setOpaque(false);
		SwingUtil.addComponent(contentPanel, gbl, body, 0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH);
		
		final int month = actuell.getMonthOfYear();
		DateTime monthStart = actuell.minusDays(actuell.getDayOfMonth() - 1);
		DateTime nextMonth = actuell.plusMonths(1);
		DateTime monthEnd = nextMonth.minusDays(nextMonth.getDayOfMonth());
		
		int weeks = monthEnd.getWeekOfWeekyear() - monthStart.getWeekOfWeekyear() + 1;
		
		DateTime start = monthStart.minusDays(monthStart.getDayOfWeek() - 1);
		DateTime end = monthEnd.plusDays(7 - monthEnd.getDayOfWeek());
		
		weeks = weeks < 0 ? monthEnd.minusDays(monthEnd.getDayOfWeek()).getWeekOfWeekyear() - monthStart.getWeekOfWeekyear() + 2 : weeks;
		if (weeks < 0) {
		} else if (weeks < 5){
			end = end.plusWeeks(1);
			start = start.minusWeeks(1);
		} else if(weeks < 6) {
			if (monthStart.getDayOfWeek() - 1 > 7 - monthEnd.getDayOfWeek()){
				end = end.plusWeeks(1);
			} else {
				start = start.minusWeeks(1);
			}
		}
		
		
		for (int i = 0; i < 7; i++) {
			String labelString = start.plusDays(i).toString(
					DateTimeFormat.forPattern("E").withLocale(Locale.ENGLISH));
			JLabel weekLabel = new JLabel(labelString.substring(0, 1));
			weekLabel.setFont(defFont);
			SwingUtil.addComponent(body, gblBody, weekLabel, i, 0, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(padding, padding, padding, padding));
		}

		int y = 1;
		for (DateTime date = start; date.isBefore(end.plusDays(1)); date = date
				.plusDays(1)) {
			final DateTime finalDate = date;
			final DateView label = new DateView(date, selectedDate, month){
				private static final long serialVersionUID = 1L;
				@Override
				public void performAction() {
					confirmDate(finalDate);
				}
			};

			SwingUtil.addComponent(body, gblBody, label,
					(date.getDayOfWeek() - 1), y, 1, 1, 1.0, 1.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH);
			if (date.dayOfWeek().get() == 7)
				y++;
		}
		this.contentPanel.repaint();
		this.repaint();
		this.updateUI();
	}

//
//	 H E A D E R
//	
	private JPanel buildHeader(final DateTime actuell, final DateTime selectedDate){
		JPanel head = new JPanel();
		GridBagLayout gblHead = new GridBagLayout();
		head.setLayout(gblHead);
		head.setOpaque(false);
		
		IconMap icons = null;;
		try {
			icons = new IconMap();
		} catch (IOException e) {
		}
		
		DateView yearMinus = new DateView(icons.getIcon(10, 13, true)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void performAction() {
				buildCalendar(actuell.minusYears(1), selectedDate);
			}
		};
		SwingUtil.addComponent(head, gblHead, yearMinus, 0, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE);

		DateView yearPlus = new DateView(icons.getIcon(8, 13, true)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void performAction() {
				buildCalendar(actuell.plusYears(1), selectedDate);
			}
		};
		SwingUtil.addComponent(head, gblHead, yearPlus, 2, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.EAST, GridBagConstraints.NONE);

		JLabel yearLabel = new JLabel(actuell.toString(DateTimeFormat
				.forPattern("yyyy")));
		yearLabel.setFont(defFont);
		yearLabel.setHorizontalAlignment(JLabel.CENTER);
		SwingUtil.addComponent(head, gblHead, yearLabel, 1, 0, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);

		
		
		DateView monthMinus = new DateView(icons.getIcon(2, 13, true)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void performAction() {
				buildCalendar(actuell.minusMonths(1), selectedDate);
			}
		};
		SwingUtil.addComponent(head, gblHead, monthMinus, 0, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE);

		DateView monthPlus = new DateView(icons.getIcon(0, 13, true)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void performAction() {
				buildCalendar(actuell.plusMonths(1), selectedDate);
			}
		};
		SwingUtil.addComponent(head, gblHead, monthPlus, 2, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.EAST, GridBagConstraints.NONE);

		JLabel monthLabel = new JLabel(actuell.toString(DateTimeFormat
				.forPattern("MMMM").withLocale(Locale.ENGLISH)));
		monthLabel.setFont(defFont);
		monthLabel.setHorizontalAlignment(JLabel.CENTER);
		monthLabel.setBackground(Color.WHITE);
		monthLabel.setOpaque(true);
		SwingUtil.addComponent(head, gblHead, monthLabel, 1, 1, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);


		
		return head;
	}
	
	private abstract class DateView extends JPanel {
		private static final long serialVersionUID = 1L;
		private final JLabel label;
		private Color foreground;
		private Color background = Color.WHITE;
		private Border defaultBorder = CalendarPanel2.this.defaultBorder;
		
		private DateView(int padding){
			this.label = new JLabel();
			this.foreground = Color.BLACK;
			this.label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
			this.label.setAlignmentY(JLabel.CENTER_ALIGNMENT);
			
			DateView.this.setSelect(false);

			this.label.setFont(defFont);
			Insets insets = new Insets(padding, padding, padding, padding);
			JPanel labelPanel = new JPanel();
			labelPanel.add(label);
			SwingUtil.addComponent(this, gbl, label, 0, 0, 1, 1, 1.0, 1.0,
					GridBagConstraints.CENTER, GridBagConstraints.NONE,
					insets);
			addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent arg0) {}
				@Override
				public void mousePressed(MouseEvent arg0) {}
				@Override
				public void mouseExited(MouseEvent arg0) {
					DateView.this.setSelect(false);
				}
				@Override
				public void mouseEntered(MouseEvent arg0) {
					DateView.this.setSelect(true);
				}
				@Override
				public void mouseClicked(MouseEvent arg0) {
					DateView.this.setSelect(false);
					performAction();
				}
			});
			
		}
		
		private DateView(String labelString){
			this(100);
			this.label.setText(labelString);
		}
		
		private DateView(Icon icon){
			this(0);
			this.label.setIcon(icon);
//			this.label.setBackground(Color.YELLOW);
//			this.label.setOpaque(true);
		}
		
		private DateView(DateTime date,
				DateTime selectedDate, int month) {
			this("" + date.getDayOfMonth());
			this.foreground = date.getMonthOfYear() == month ? foreground
					: Color.GRAY;

			GridBagLayout gbl = new GridBagLayout();
			this.setLayout(gbl);
			
			this.label.setFont(defFont);
			
			if(date.isEqual(selectedDate)){
				this.defaultBorder = CalendarPanel2.this.greenBorder;
			}
			
			setSelect(false);
			
		}
		
		public abstract void performAction();
		
		private void setSelect(boolean isSelect) {
			if (isSelect) {
				this.setBorder(selectBorder);
				setBackground(new Color(205, 225, 225));
//				this.label.setForeground(Color.WHITE);
			} else {
				this.setBorder(this.defaultBorder);
				setBackground(this.background);
				this.label.setForeground(this.foreground);
			}
			this.setOpaque(true);
		}
	}

	private void confirmDate(DateTime date) {
		this.textField.setText(convert(date));
		dispose();
	}

	static DateTimeFormatter FMT = DateTimeFormat.forPattern("yyyy-MM-dd");

	private static String convert(DateTime date) {
		return date.toString(FMT);
	}

	private static DateTime convert(String string) {
		try {
			return DateTime.parse(string, FMT);
		} catch (IllegalArgumentException e) {
			return new DateTime();
		}
	}

}
