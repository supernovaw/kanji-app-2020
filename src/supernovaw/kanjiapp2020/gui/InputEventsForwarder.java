package supernovaw.kanjiapp2020.gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.function.Consumer;

// Used by scenes to forward events to children elements
public interface InputEventsForwarder extends InputListener {
	@Override
	default void keyTyped(KeyEvent e) {
		iterateOverChildren(c -> c.keyTyped(e));
	}

	@Override
	default void keyPressed(KeyEvent e) {
		iterateOverChildren(c -> c.keyPressed(e));
	}

	@Override
	default void keyReleased(KeyEvent e) {
		iterateOverChildren(c -> c.keyReleased(e));
	}

	@Override
	default void mouseClicked(MouseEvent e) {
		MouseEvent s = InputEvents.shift(getOwnLocation(), e);
		iterateOverChildren(c -> c.mouseClicked(s));
	}

	@Override
	default void mousePressed(MouseEvent e) {
		MouseEvent s = InputEvents.shift(getOwnLocation(), e);
		iterateOverChildren(c -> c.mousePressed(s));
	}

	@Override
	default void mouseReleased(MouseEvent e) {
		MouseEvent s = InputEvents.shift(getOwnLocation(), e);
		iterateOverChildren(c -> c.mouseReleased(s));
	}

	@Override
	default void mouseDragged(MouseEvent e) {
		MouseEvent s = InputEvents.shift(getOwnLocation(), e);
		iterateOverChildren(c -> c.mouseDragged(s));
	}

	@Override
	default void mouseMoved(MouseEvent e) {
		MouseEvent s = InputEvents.shift(getOwnLocation(), e);
		iterateOverChildren(c -> c.mouseMoved(s));
	}

	@Override
	default void mouseWheelMoved(MouseWheelEvent e) {
		MouseWheelEvent s = (MouseWheelEvent) InputEvents.shift(getOwnLocation(), e);
		iterateOverChildren(c -> c.mouseWheelMoved(s));
	}

	void iterateOverChildren(Consumer<InputListener> c);

	Point getOwnLocation();
}
