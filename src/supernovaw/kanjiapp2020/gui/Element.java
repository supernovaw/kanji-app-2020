package supernovaw.kanjiapp2020.gui;

import java.awt.*;

public abstract class Element implements InputListener {
	private final Scene parent;

	public Element(Scene parent) {
		if (parent == null && !(this instanceof RootScene))
			throw new NullPointerException("Null parent of a non-root scene");

		this.parent = parent;
	}

	protected abstract void paint(Graphics2D g);

	protected void repaint() {
		parent.repaint(getRepaintBounds());
	}

	protected void repaint(Rectangle r) {
		parent.repaint(r);
	}

	protected Rectangle getRepaintBounds() {
		return getBounds();
	}

	protected Dimension getParentSize() {
		return parent.getBounds().getSize();
	}

	protected final Rectangle getBounds() {
		if (parent == null) {
			// parent can only be null if we're the root scene
			Scene myself = (Scene) this;
			return new Rectangle(myself.getLayout().getSize());
		}
		return parent.getLayout().getBounds(this);
	}

	public Dimension getMinimumSize() {
		return null;
	}

	public Dimension getMaximumSize() {
		return null;
	}

	// returned position is relative to the parent of this element
	protected Point getMousePosition() {
		Rectangle parentLocation = parent.getBounds();
		Point mousePosInParent = new Point(parent.getMousePosition());
		mousePosInParent.x -= parentLocation.x;
		mousePosInParent.y -= parentLocation.y;
		return mousePosInParent;
	}

	// implementations should turn their animations on and off here
	protected void setDisplayed(boolean displayed) {
	}
}
