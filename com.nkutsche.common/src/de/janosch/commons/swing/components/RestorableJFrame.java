package de.janosch.commons.swing.components;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

/**
 * {@link JFrame}, which continuously stores its window preferences in the system preferences.
 * 
 * On re-launch, the same system preferences are used to restore the previous preferences of the
 * frame.
 * 
 * TODO: Provide function to set an initial window size for the first start.
 * 
 * @author Janos
 * @version 04.10.2011 | 15:03:39
 * 
 */
public class RestorableJFrame extends JFrame {

	private static final long serialVersionUID = 3271189853029773977L;

	protected static final String PREF_NODE_ID = RestorableJFrame.class.getName() + ".windowRestoreData";
	protected static final String KEY_DIMENSION_WIDTH = "dimWidth";
	protected static final String KEY_DIMENSION_WIDTH_PREV = "dimWidthPrev";
	protected static final String KEY_DIMENSION_HEIGHT = "dimHeight";
	protected static final String KEY_DIMENSION_HEIGHT_PREV = "dimHeightPrev";
	protected static final String KEY_LOCATION_X = "posX";
	protected static final String KEY_LOCATION_X_PREV = "posXprev";
	protected static final String KEY_LOCATION_Y = "posY";
	protected static final String KEY_LOCATION_Y_PREV = "posYprev";
	protected static final String KEY_WINDOW_MAXIMIZED = "windowState";

	protected final Preferences preferences;

	protected boolean bInitialized = false;

	public RestorableJFrame(final Preferences prefs) {

		this.preferences = prefs.node(PREF_NODE_ID);

		addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(final WindowEvent e) {
				preferences.putBoolean(KEY_WINDOW_MAXIMIZED, e.getNewState() == Frame.MAXIMIZED_BOTH);
				/**
				 * TODO: This is a hack. Here we assume, that in the event of maximization, the
				 * events occur in the following order: <br>
				 * 1. set location to somewhere near [0/0]. (Here, it is unknown, that a state
				 * change will follow!) <br>
				 * 2. change state to MAXIMIZED_BOTH (actual maximization) <br>
				 * 3. set dimension to fullscreen. <br>
				 * <br>
				 * The 3rd event can be ignored for persisting, since it is known that the frame is
				 * maximized. However, the 1st event will be already persisted. So in the event of
				 * the state change, this persisting event can be undone. <br>
				 * However, if on some operation systems or even different layout managers, this
				 * order of events behaves differently, the persisting order will also behave
				 * different and the whole window restore procedure will fail.
				 */
				if (getMaximized(preferences)) {
					// Maximized -> Restore previous position (undo previous MOVE)
					preferences.putInt(KEY_LOCATION_X, preferences.getInt(KEY_LOCATION_X_PREV, getX()));
					preferences.putInt(KEY_LOCATION_Y, preferences.getInt(KEY_LOCATION_Y_PREV, getY()));
				}
			}
		});

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				if (!getMaximized(preferences)) {
					storeDimension(getSize(), preferences);
				}
			}

			@Override
			public void componentMoved(final ComponentEvent e) {
				if (!getMaximized(preferences)) {
					storeLocation(getLocation(), preferences);
				}
			}
		});

	}

	@Override
	public void setVisible(final boolean b) {
		if (!bInitialized) {
			restoreWindowBoundsAndState(preferences);
		}
		super.setVisible(b);
	}

	private boolean getMaximized(final Preferences preferences) {
		return preferences.getBoolean(KEY_WINDOW_MAXIMIZED, false);
	}

	private void restoreWindowBoundsAndState(final Preferences preferences) {
		this.setBounds(getBounds(preferences));
		if (getMaximized(preferences)) {
			this.setExtendedState(Frame.MAXIMIZED_BOTH);
		}
	}

	private void storeLocation(final Point location, final Preferences preferences) {
		final Point current = getLocation(preferences);
		preferences.putInt(KEY_LOCATION_X_PREV, current.x);
		preferences.putInt(KEY_LOCATION_Y_PREV, current.y);

		preferences.putInt(KEY_LOCATION_X, location.x);
		preferences.putInt(KEY_LOCATION_Y, location.y);
	}

	private void storeDimension(final Dimension dimension, final Preferences preferences) {
		final Dimension current = getDimension(preferences);
		preferences.putInt(KEY_DIMENSION_WIDTH_PREV, current.width);
		preferences.putInt(KEY_DIMENSION_HEIGHT_PREV, current.height);

		preferences.putInt(KEY_DIMENSION_WIDTH, dimension.width);
		preferences.putInt(KEY_DIMENSION_HEIGHT, dimension.height);
	}

	private Dimension getDimension(final Preferences preferences) {
		return new Dimension(preferences.getInt(KEY_DIMENSION_WIDTH, this.getSize().width), preferences.getInt(KEY_DIMENSION_HEIGHT,
				this.getSize().height));
	}

	private Point getLocation(final Preferences preferences) {
		return new Point(preferences.getInt(KEY_LOCATION_X, this.getX()), preferences.getInt(KEY_LOCATION_Y, this.getY()));
	}

	private Rectangle getBounds(final Preferences preferences) {
		return new Rectangle(getLocation(preferences), getDimension(preferences));
	}

	@Override
	public void setLocation(final int x, final int y) {
		bInitialized = true;
		super.setLocation(x, y);
	}

	@Override
	public void setLocationByPlatform(final boolean locationByPlatform) {
		bInitialized = true;
		super.setLocationByPlatform(locationByPlatform);
	}

	@Override
	public void setSize(final Dimension d) {
		bInitialized = true;
		super.setSize(d);
	}

	@Override
	public void setSize(final int width, final int height) {
		bInitialized = true;
		super.setSize(width, height);
	}

	@Override
	public void setBounds(final int x, final int y, final int width, final int height) {
		bInitialized = true;
		super.setBounds(x, y, width, height);
	}

	@Override
	public synchronized void setState(final int state) {
		bInitialized = true;
		super.setState(state);
	}

}
