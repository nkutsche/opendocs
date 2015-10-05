package net.sqf.view.utils.types.panels;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;

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
import net.sqf.view.utils.types.IntegerAreaVerifier;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.janosch.commons.swing.util.SwingUtil;

public class TimePanel extends JPanel implements MouseListener {
	private static final long serialVersionUID = -4261661006435142610L;
	private final JDialog dialog;
	private final JTextField textField;

	private final Font defFont;

	@SuppressWarnings("unused")
	private final int padding;
	private final GridBagLayout gbl = new GridBagLayout();

	// private final int minWidth;
	// private final int minHeight;
	private JPanel contentPanel = new JPanel();

	JFormattedTextField hour = new JFormattedTextField();
	JFormattedTextField minutes = new JFormattedTextField();
	JFormattedTextField sec = new JFormattedTextField();

	private final Border defaultBorder = BorderFactory.createBevelBorder(
			BevelBorder.RAISED, new Color(240, 240, 240), new Color(150, 150,
					150));
	private final Border greenBorder = BorderFactory.createBevelBorder(
			BevelBorder.LOWERED, new Color(0, 110, 0), new Color(0, 200, 0));
	private final Border selectBorder = BorderFactory.createBevelBorder(
			BevelBorder.RAISED, new Color(30, 20, 120),
			new Color(110, 100, 200));
	private String initialValue;

	public TimePanel(JFormattedTextField field, int fontSize, Container owner) {
		
		if(field.isEnabled()){
			initialValue = field.getText();
			setTime(convert(initialValue));
		} else {
			initialValue = null;
		}
		
		if (owner instanceof Dialog) {
			dialog = new JDialog((Dialog) owner);
		} else if (owner instanceof Frame) {
			dialog = new JDialog((Frame) owner);
		} else if (owner instanceof Window) {
			dialog = new JDialog((Window) owner);
		} else {
			dialog = new JDialog(new JFrame());
		}
		this.setLayout(gbl);
		this.textField = field;
		Font defFont = new JLabel().getFont();
		this.defFont = new Font(defFont.getName(), defFont.getStyle(), fontSize);

		// this.minWidth = 25 * fontSize;
		// this.minHeight = 25 * fontSize;
		this.padding = (int) Math.round(fontSize * 0.1);
		this.setLayout(gbl);

		String text = this.textField.getText();
		DateTime actuellDat = convert(text);
		buildCalendar(actuellDat, actuellDat);
		Dimension minDim = new Dimension(fontSize * 7 / 4, fontSize * 2);
		hour.setMinimumSize(minDim);
		hour.setPreferredSize(minDim);
		minutes.setMinimumSize(minDim);
		minutes.setPreferredSize(minDim);
		sec.setMinimumSize(minDim);
		sec.setPreferredSize(minDim);


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
		// dialog.setMinimumSize(new Dimension(minWidth, minHeight));
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

			// dialog.setSize(textField.getWidth(), textField.getWidth());
			int finalWidth = textField.getWidth() * 2;
			int finalHeight = textField.getHeight() * 2;
			dialog.setSize(finalWidth, finalHeight);
			
			dialog.pack();
			
			dialog.setLocation(getDialogBounds());
			// System.out.println(tfLoc.y);
			dialog.setModal(false);
			dialog.setVisible(true);
			textField.setEnabled(false);
		} else {
			dispose();
		}
	}

	private Point getDialogBounds() {

		Point tfLoc = textField.getLocationOnScreen();

		return new Point((int) (tfLoc.x - dialog.getWidth() * 0.25),
				(int) (tfLoc.y - dialog.getHeight() * 0.25));
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

		IconMap icons = null;
		;
		try {
			icons = new IconMap();
		} catch (IOException e) {
		}
		GridBagLayout gblContent = new GridBagLayout();

		this.setBorder(defaultBorder);
		contentPanel = new JPanel();
		contentPanel.setLayout(gblContent);
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setOpaque(true);

		SwingUtil.addComponent(this, gbl, contentPanel, 0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH);

		// JPanel head = buildHeader(actuell, selectedDate);
		// SwingUtil.addComponent(contentPanel, gbl, head, 0, 0, 1, 1, 0.0, 0.0,
		// GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH);

		IntegerAreaVerifier iavHour = new IntegerAreaVerifier(0, 23);
		IntegerAreaVerifier iavMinutes = new IntegerAreaVerifier(0, 59);
		IntegerAreaVerifier iavSec = new IntegerAreaVerifier(0, 59);

		iavHour.setVerifier(hour, dialog, false);

		iavMinutes.setVerifier(minutes, dialog, false);

		iavSec.setVerifier(sec, dialog, false);

		setTime(actuell);
//		int tinySize = 25;
//		JButton hourP = new PanelButton(icons.getIcon(6, 13));
//		hourP.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				setTime(getTime().plusHours(1));
//			}
//		});
//		JButton hourM = new PanelButton(icons.getIcon(4, 13));
//		hourM.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				setTime(getTime().minusHours(1));
//			}
//		});
//		JButton minP = new PanelButton(icons.getIcon(6, 13));
//		hourM.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				setTime(getTime().plusMinutes(1));
//			}
//		});
//		JButton minM = new PanelButton(icons.getIcon(4, 13));
//		hourM.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				setTime(getTime().minusMinutes(1));
//			}
//		});
//		JButton secP = new PanelButton(icons.getIcon(6, 13));
//		hourM.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				setTime(getTime().plusSeconds(1));
//			}
//		});
//		JButton secM = new PanelButton(icons.getIcon(4, 13));
//		hourM.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				setTime(getTime().minusSeconds(1));
//			}
//		});
//		JButton conformBtn = new PanelButton(icons.getIcon(2, 10));
//		hourM.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				confirmDate(getTime());
//			}
//		});
		
		
		DateView hourP = new DateView(icons.getIcon(6, 13)) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void performAction() {
				setTime(getTime().plusHours(1));
			}
		};
		
		DateView hourM = new DateView(icons.getIcon(4, 13)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void performAction() {
				setTime(getTime().minusHours(1));
			}
		};
		DateView minP = new DateView(icons.getIcon(6, 13)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void performAction() {
				setTime(getTime().plusMinutes(1));
			}
		};
		DateView minM = new DateView(icons.getIcon(4, 13)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void performAction() {
				setTime(getTime().minusMinutes(1));
			}
		};
		DateView secP = new DateView(icons.getIcon(6, 13)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void performAction() {
				setTime(getTime().plusSeconds(1));
			}
		};
		DateView secM = new DateView(icons.getIcon(4, 13)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void performAction() {
				setTime(getTime().minusSeconds(1));
			}
		};
		DateView conformBtn = new DateView(icons.getIcon(2, 10)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void performAction() {
				confirmDate(getTime());
			}
		};
		DateView clearBtn = new DateView(icons.getIcon(10, 11)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void performAction() {
				unsetTextAndDispose();
			}
		};

		SwingUtil.addComponent(contentPanel, gblContent, hour, 0, 0, 1, 2, 0.0,
				1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);
		SwingUtil
				.addComponent(contentPanel, gblContent, hourP, 1, 0, 1, 1, 0.0,
						0.0, GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE);
		SwingUtil
				.addComponent(contentPanel, gblContent, hourM, 1, 1, 1, 1, 0.0,
						0.0, GridBagConstraints.SOUTHWEST,
						GridBagConstraints.NONE);
		SwingUtil.addComponent(contentPanel, gblContent, minutes, 2, 0, 1, 2,
				0.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH);
		SwingUtil.addComponent(contentPanel, gblContent, minP, 3, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTH, GridBagConstraints.NONE);
		SwingUtil.addComponent(contentPanel, gblContent, minM, 3, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.SOUTH, GridBagConstraints.NONE);
		SwingUtil.addComponent(contentPanel, gblContent, sec, 4, 0, 1, 2, 0.0,
				1.0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH);
		SwingUtil.addComponent(contentPanel, gblContent, secP, 5, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);
		SwingUtil.addComponent(contentPanel, gblContent, secM, 5, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE);

		SwingUtil.addComponent(contentPanel, gblContent, conformBtn, 6, 0, 1,
				1, 0.0, 0.0, GridBagConstraints.SOUTHWEST,
				GridBagConstraints.NONE);
		SwingUtil.addComponent(contentPanel, gblContent, clearBtn, 6, 1, 1,
				1, 0.0, 0.0, GridBagConstraints.SOUTHWEST,
				GridBagConstraints.NONE);

		this.contentPanel.repaint();
		this.repaint();
		this.updateUI();
	}
	
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

	private DateTime getTime() {
		String timeString = "T" + hour.getText() + ":" + minutes.getText()
				+ ":" + sec.getText();
		DateTime time = DateTime.parse(timeString);
		return time;
	}
	

	private void setTime(DateTime time) {
		setIntToField(hour, time.getHourOfDay());
		setIntToField(minutes, time.getMinuteOfHour());
		setIntToField(sec, time.getSecondOfMinute());
	}

	private void setIntToField(JFormattedTextField field, int value) {
		String valueString = "" + value;
		if (valueString.length() == 1) {
			valueString = "0" + valueString;
		}
		field.setText(valueString);
	}
	

	private abstract class DateView extends JLabel {
		private static final long serialVersionUID = 1L;
		private final JLabel label;
		private Color foreground;
		private Color background = Color.WHITE;
		private Border defaultBorder = TimePanel.this.defaultBorder;

		private DateView(int padding) {
			this.label = new JLabel();
			this.foreground = Color.BLACK;
//			this.label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
//			this.label.setAlignmentY(JLabel.CENTER_ALIGNMENT);

			setSelect(false);
//			this.setBackground(Color.RED);
//			this.label.setBackground(Color.BLUE);
//			this.label.setOpaque(true);

			this.setFont(defFont);
//			Insets insets = new Insets(padding, padding, padding, padding);
//			SwingUtil.addComponent(this, gbl, label, 0, 0, 1, 1, 1.0, 1.0,
//					GridBagConstraints.CENTER, GridBagConstraints.NONE, insets);
			addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent arg0) {
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
				}

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
//					DateView.this.setSelect(false);
					performAction();
				}
			});

		}

		private DateView(String labelString) {
			this(100);
			this.label.setText(labelString);
		}

		private DateView(Icon icon) {
			this(0);
			this.setIcon(icon);
			Dimension dim = new Dimension(icon.getIconWidth() + 1, icon.getIconHeight() + 1);
			this.setMinimumSize(dim);
			this.setSize(dim);
			// this.label.setBackground(Color.YELLOW);
			// this.label.setOpaque(true);
		}

		private DateView(Icon icon, int size) {
			this(icon);
			Dimension dim = new Dimension(size, size);
			this.setMaximumSize(dim);
			setSize(dim);
			setPreferredSize(dim);
		}

		private DateView(DateTime date, DateTime selectedDate, int month) {
			this("" + date.getDayOfMonth());
			this.foreground = date.getMonthOfYear() == month ? foreground
					: Color.GRAY;

			GridBagLayout gbl = new GridBagLayout();
			this.setLayout(gbl);

			this.label.setFont(defFont);

			if (date.isEqual(selectedDate)) {
				this.defaultBorder = TimePanel.this.greenBorder;
			}

			setSelect(false);

		}

		public abstract void performAction();

		private void setSelect(boolean isSelect) {
			if (isSelect) {
				this.setBorder(selectBorder);
				setBackground(new Color(205, 225, 225));
				// this.label.setForeground(Color.WHITE);
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

	static DateTimeFormatter FMT = DateTimeFormat.forPattern("HH:mm:ss");

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
