package supernovaw.kanjiapp2020.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TransitionScene extends Scene {
	private static final int SLIDE_DISTANCE = 130;
	private static final int ANIMATION_DURATION = 750;

	private Element current;
	private final FadeOutAnimation fadeOut;

	public TransitionScene(Scene parent) {
		super(parent);
		setLayout(new TransitionLayout());
		fadeOut = new FadeOutAnimation(ANIMATION_DURATION, this);
	}

	public void setCurrent(Scene s) {
		if (s == null) throw new IllegalArgumentException("Null argument scene");

		if (fadeOut.isAnimating()) return;
		if (!getElements().contains(s)) {
			addElement(s);
			s.onSizeChanged(getLayout().getBounds(s).getSize());
		}
		if (current != null) {
			current.setDisplayed(false);
			fadeOut.animate(current);
		}
		current = s;
		s.setDisplayed(true);

		repaint();
	}

	@Override
	public void iterateOverChildren(Consumer<InputListener> c) {
		if (current != null) c.accept(current);
		if (fadeOut.isAnimating()) c.accept((Scene) fadeOut.getParameter(0));
	}

	@Override
	protected void paint(Graphics2D g) {
		Rectangle r = getBounds();
		g.translate(r.x, r.y);

		if (fadeOut.isAnimating()) {
			refreshMousePosition();
			Composite before = g.getComposite();
			float f = (float) fadeOut.getEaseInOutSine();

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - f));
			Scene fading = (Scene) fadeOut.getParameter(0);
			fading.paint(g);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, f));
			if (current != null) current.paint(g);

			g.setComposite(before);
		} else {
			if (current != null) current.paint(g);
		}

		g.translate(-r.x, -r.y);
	}

	@Override
	protected void setDisplayed(boolean displayed) {
		super.setDisplayed(displayed);
		fadeOut.setDisplayed(displayed);
	}

	private class TransitionLayout extends Layout {
		private final List<Element> elements = new ArrayList<>();
		private int width, height;

		@Override
		public void addElement(Element e) {
			elements.add(e);
		}

		@Override
		public void removeElement(Element e) {
			elements.remove(e);
		}

		@Override
		public Rectangle getBounds(Element e) {
			if (!elements.contains(e)) throw new IllegalArgumentException(
					"This element " + e + " has not been added to the scene");
			if (fadeOut.isAnimating()) {
				double f = fadeOut.getEaseInOutSine();
				int distance = (int) Math.round(f * SLIDE_DISTANCE);
				if (e == current)
					return new Rectangle(0, distance - SLIDE_DISTANCE, width, height);
				if (e == fadeOut.getParameter(0))
					return new Rectangle(0, distance, width, height);
			}
			return new Rectangle(0, 0, width, height);
		}

		@Override
		public void setSize(int width, int height) {
			this.width = width;
			this.height = height;
		}

		@Override
		public Dimension getSize() {
			return new Dimension(width, height);
		}
	}
}
