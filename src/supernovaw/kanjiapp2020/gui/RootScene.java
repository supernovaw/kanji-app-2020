package supernovaw.kanjiapp2020.gui;

import java.awt.*;
import java.util.function.Consumer;

abstract class RootScene extends Scene {
	private Runnable repaintFull;
	private Consumer<Rectangle> repaintRect;
	private Runnable refreshMousePosition;
	private Point mousePosition;

	public RootScene() {
		super(null);
	}

	@Override
	protected final void repaint() {
		repaintFull.run();
	}

	@Override
	protected final void repaint(Rectangle r) {
		repaintRect.accept(r);
	}

	@Override
	protected final void repaintEntirely() {
		repaintFull.run();
	}

	@Override
	protected void refreshMousePosition() {
		refreshMousePosition.run();
	}

	public final void setCallers(Runnable repaintFull, Consumer<Rectangle> repaintRect, Runnable refreshMousePosition) {
		this.repaintFull = repaintFull;
		this.repaintRect = repaintRect;
		this.refreshMousePosition = refreshMousePosition;
	}

	@Override
	protected Point getMousePosition() {
		return mousePosition;
	}

	public final void onMousePositionChanged(Point mousePosition) {
		this.mousePosition = mousePosition;
	}
}
