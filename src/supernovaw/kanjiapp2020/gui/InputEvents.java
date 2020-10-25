package supernovaw.kanjiapp2020.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public final class InputEvents {
	// Adds all the necessary listeners to JPanel and forwards the events to InputListener
	public static void redirectEvents(JFrame frame, InputListener listener) {
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				listener.keyTyped(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				listener.keyPressed(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				listener.keyReleased(e);
			}
		});
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				listener.mouseClicked(e);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				listener.mousePressed(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				listener.mouseReleased(e);
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				listener.mouseWheelMoved(e);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				listener.mouseDragged(e);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				listener.mouseMoved(e);
			}
		};
		Container c = frame.getContentPane();
		c.addMouseListener(mouseAdapter);
		c.addMouseMotionListener(mouseAdapter);
		c.addMouseWheelListener(mouseAdapter);
	}

	/* When scene forwards a mouse event to its children, it has to make the
	 * coordinates of it relative to its own location, which is done by this method */
	public static MouseEvent shift(Point parentLocation, MouseEvent event) {
		if (!(event instanceof MouseWheelEvent)) {
			Component src = event.getComponent();
			int id = event.getID();
			long when = event.getWhen();
			int mods = event.getModifiersEx();
			int x = event.getX() - parentLocation.x;
			int y = event.getY() - parentLocation.y;
			int xAbs = event.getXOnScreen();
			int yAbs = event.getYOnScreen();
			int cc = event.getClickCount();
			boolean pt = event.isPopupTrigger();
			int btn = event.getButton();
			return new MouseEvent(src, id, when, mods, x, y, xAbs, yAbs, cc, pt, btn);
		} else {
			MouseWheelEvent w = (MouseWheelEvent) event;
			Component src = w.getComponent();
			int id = w.getID();
			long when = w.getWhen();
			int mods = w.getModifiersEx();
			int x = w.getX() - parentLocation.x;
			int y = w.getY() - parentLocation.y;
			int xAbs = w.getXOnScreen();
			int yAbs = w.getYOnScreen();
			int cc = w.getClickCount();
			boolean pt = w.isPopupTrigger();
			int st = w.getScrollType();
			int sa = w.getScrollAmount();
			int wr = w.getWheelRotation();
			double pwr = w.getPreciseWheelRotation();
			return new MouseWheelEvent(src, id, when, mods, x, y, xAbs, yAbs, cc, pt, st, sa, wr, pwr);
		}
	}
}
