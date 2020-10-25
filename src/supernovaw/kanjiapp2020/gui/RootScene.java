package supernovaw.kanjiapp2020.gui;

import java.awt.*;
import java.util.function.Consumer;

abstract class RootScene extends Scene {
	private Runnable repaintFull;
	private Consumer<Rectangle> repaintRect;
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

	public final void setRepaintCallers(Runnable repaintFull, Consumer<Rectangle> repaintRect) {
		this.repaintFull = repaintFull;
		this.repaintRect = repaintRect;
	}

	@Override
	protected Point getMousePosition() {
		return mousePosition;
	}

	public final void onMousePositionChanged(Point mousePosition) {
		this.mousePosition = mousePosition;
	}
}
