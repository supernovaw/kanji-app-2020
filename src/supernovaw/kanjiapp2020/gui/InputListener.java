package supernovaw.kanjiapp2020.gui;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public interface InputListener {
	default void keyTyped(KeyEvent e) {
	}

	default void keyPressed(KeyEvent e) {
	}

	default void keyReleased(KeyEvent e) {
	}

	default void mouseClicked(MouseEvent e) {
	}

	default void mousePressed(MouseEvent e) {
	}

	default void mouseReleased(MouseEvent e) {
	}

	default void mouseDragged(MouseEvent e) {
	}

	default void mouseMoved(MouseEvent e) {
	}

	default void mouseWheelMoved(MouseWheelEvent e) {
	}
}
