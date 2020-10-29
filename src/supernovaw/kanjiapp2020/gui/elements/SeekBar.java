package supernovaw.kanjiapp2020.gui.elements;

import supernovaw.kanjiapp2020.gui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.function.Consumer;

public class SeekBar extends Element {
	private final int cursorSizeMax = 11;

	private final FadeOutAnimation jumpAnimation;
	private final Ease hoverEase;
	private final Ease holdEase;

	private double cursorPos;
	private double lastForwardedPosition; // used to avoid multiple reports about the same pos
	private final Consumer<Double> positionUpdateListener;

	public SeekBar(Scene parent, double initialPos, Consumer<Double> positionUpdateListener) {
		super(parent);
		jumpAnimation = new FadeOutAnimation(180, this);
		hoverEase = new Ease(120, this);
		holdEase = new Ease(150, this);
		this.positionUpdateListener = positionUpdateListener;

		lastForwardedPosition = initialPos;
		cursorPos = initialPos;
	}

	@Override
	protected void paint(Graphics2D g) {
		int cursorSizeNormal = 8;
		Rectangle r = getBounds();

		double lineAlpha = hoverEase.getEaseInOutSine();
		g.setColor(Theme.foreground(0.6 + 0.3 * lineAlpha));

		int lineY = r.y + r.height / 2;
		g.fillRect(r.x + cursorSizeMax / 2, lineY - 1, r.width - cursorSizeMax, 2);

		g.setColor(Theme.foreground());
		double pos = getAnimatedCursorPos();
		double cursorX = r.x + cursorSizeMax / 2d + (r.width - cursorSizeMax) * pos;
		double cursorGrow = holdEase.getEaseInOutSine();
		double cursorSize = cursorSizeNormal + (cursorSizeMax - cursorSizeNormal) * cursorGrow;
		Shape cursor = new Ellipse2D.Double(cursorX - cursorSize / 2d, lineY - cursorSize / 2d, cursorSize, cursorSize);
		g.fill(new Area(cursor));

		if (jumpAnimation.isAnimating()) callListener(pos);
	}

	@Override
	protected Rectangle getRepaintBounds() {
		Rectangle r = getBounds();
		r.grow(1, 0);
		return r;
	}

	private double getCursorPos(int x) {
		Rectangle r = getBounds();
		double pos = x - r.x - cursorSizeMax / 2d;
		pos /= r.width - cursorSizeMax;
		if (pos > 1) pos = 1;
		if (pos < 0) pos = 0;
		return pos;
	}

	private double getAnimatedCursorPos() {
		if (!jumpAnimation.isAnimating()) return cursorPos;
		double f = jumpAnimation.getEaseOutCubic();
		double p1 = (double) jumpAnimation.getParameter(0);
		double p2 = cursorPos;
		return p1 + f * (p2 - p1);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e)) return;
		if (!getBounds().contains(e.getPoint())) return;
		holdEase.setSwitched(true);
		jumpAnimation.animate(getAnimatedCursorPos());
		cursorPos = getCursorPos(e.getX());
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		boolean in = getBounds().contains(e.getPoint());
		hoverEase.setSwitched(in | holdEase.isSwitched());

		if (!SwingUtilities.isLeftMouseButton(e)) return;
		if (!holdEase.isSwitched()) return;
		cursorPos = getCursorPos(e.getX());
		callListener(getAnimatedCursorPos());
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e)) return;
		holdEase.setSwitched(false);
		boolean in = getBounds().contains(e.getPoint());
		if (!in) holdEase.setSwitched(false);
		hoverEase.setSwitched(in);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		boolean in = getBounds().contains(e.getPoint());
		hoverEase.setSwitched(in);
	}

	private void callListener(double value) {
		if (lastForwardedPosition == value) return;
		lastForwardedPosition = value;
		positionUpdateListener.accept(value);
	}

	@Override
	protected void setDisplayed(boolean displayed) {
		jumpAnimation.setDisplayed(displayed);
		hoverEase.setDisplayed(displayed);
		holdEase.setDisplayed(displayed);
	}
}
