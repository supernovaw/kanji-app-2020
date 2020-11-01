package supernovaw.kanjiapp2020.gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Scene extends Element implements InputEventsForwarder {
	private final List<Element> elements;
	private Layout layout;

	public Scene(Scene parent) {
		super(parent);
		elements = new ArrayList<>();
	}

	protected Layout getLayout() {
		return layout;
	}

	protected final void setLayout(Layout l) {
		if (this.layout != null) throw new Error("The layout has already been set");
		if (l == null) throw new NullPointerException("The assigned layout can't be null");
		layout = l;
	}

	protected final void addElement(Element e) {
		elements.add(e);
		layout.addElement(e);
	}

	protected final void removeElement(Element e) {
		elements.remove(e);
		layout.removeElement(e);
	}

	protected final List<Element> getElements() {
		return elements;
	}

	protected void onSizeChanged(Dimension size) {
		layout.setSize(size.width, size.height);
		for (Element e : elements)
			if (e instanceof Scene)
				((Scene) e).onSizeChanged(e.getBounds().getSize());
	}

	@Override
	public void iterateOverChildren(Consumer<InputListener> c) {
		for (int i = 0; i < elements.size(); i++) c.accept(elements.get(i));
	}

	@Override
	public final Point getOwnLocation() {
		return getBounds().getLocation();
	}

	@Override
	protected void paint(Graphics2D g) {
		Rectangle r = getBounds();
		g.translate(r.x, r.y);

		Rectangle clip = g.getClipBounds();
		boolean ignoreClip = clip.contains(getRepaintBounds());
		// skip the elements that are not covered by the clip if possible

		if (ignoreClip) elements.forEach(e -> e.paint(g));
		else elements.forEach(element -> {
			if (clip.intersects(element.getRepaintBounds())) element.paint(g);
		});
		g.translate(-r.x, -r.y);
	}

	@Override
	protected void repaint(Rectangle r) {
		Rectangle thisSceneBounds = getBounds();
		Rectangle repaintBoundsInParent = new Rectangle(r);
		repaintBoundsInParent.x += thisSceneBounds.x;
		repaintBoundsInParent.y += thisSceneBounds.y;
		super.repaint(repaintBoundsInParent);
	}

	protected void refreshMousePosition() {
		parent.refreshMousePosition();
	}

	protected void stackAdd(Class<? extends Scene> sceneClass) {
		checkStackAvailability();
		StackScene ss = (StackScene) parent.parent;
		ss.add(sceneClass);
	}

	protected void stackChange(Class<? extends Scene> sceneClass) {
		checkStackAvailability();
		StackScene ss = (StackScene) parent.parent;
		ss.change(sceneClass);
	}

	protected void stackRemove() {
		checkStackAvailability();
		StackScene ss = (StackScene) parent.parent;
		ss.remove();
	}

	protected boolean stackRemoveAvailable() {
		checkStackAvailability();
		StackScene ss = (StackScene) parent.parent;
		return ss.removeAvailable();
	}

	private void checkStackAvailability() {
		if (parent == null || !(parent.parent instanceof StackScene))
			throw new Error("This scene doesn't belong to a StackScene");
	}

	@Override
	public void keyPressed(KeyEvent e) {
		InputEventsForwarder.super.keyPressed(e);
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE && parent != null &&
				parent.parent instanceof StackScene && stackRemoveAvailable()) {
			stackRemove();
		}
	}

	@Override
	protected void setDisplayed(boolean displayed) {
		elements.forEach(e -> e.setDisplayed(displayed));
	}
}
