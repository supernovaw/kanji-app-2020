package supernovaw.kanjiapp2020.gui.elements;

import supernovaw.kanjiapp2020.gui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class Button extends Element {
	// corners round arc
	private static final int RND_CORNERS = 12;
	// amount of pixels to expand each side when hovering
	private static final int EXPAND = 2;

	private final String text;
	private final Runnable run;
	private final Ease hoverEase;
	private final Ease holdEase;

	public Button(Scene parent, String text, Runnable r) {
		super(parent);
		run = r;
		this.text = text;
		hoverEase = new Ease(120, this);
		holdEase = new Ease(150, this);
	}

	private void click() {
		run.run();
	}

	@Override
	protected void paint(Graphics2D g) {
		Rectangle r = getBounds();

		g.setColor(Theme.background(0.2)); // fill background with this alpha
		g.fill(getButtonForm());

		g.setFont(Theme.getUiFont());
		g.setColor(Theme.foreground());

		double holdPhase = holdEase.getEaseInOutCubic();
		double hoverPhase = hoverEase.getEaseInOutSine();
		// paint text and fill (in filled areas, text is engraved)
		Area area = new Area();
		if (holdPhase == 0) { // just paint button text
			area.add(getTextArea(g));
		} else { // fill button to animate holding
			Area textArea = getTextArea(g);

			double fillWidth = holdPhase * r.width / 2;
			Rectangle2D.Double fillRect = new Rectangle2D.Double(r.x + r.width / 2d - fillWidth - 1,
					r.y - 1, fillWidth * 2 + 3, r.height + 3);
			Area fillArea = new Area(fillRect); // the rectangle which gets wider when holding

			Area innerText = new Area(textArea);
			innerText.intersect(fillArea); // the only part of text within animating rectangle

			area.add(textArea);
			area.add(fillArea);
			area.subtract(innerText);
		}
		// round corners and cut text in case it's out of bounds
		area.intersect(new Area(getButtonForm()));

		// button outline (1.0f is for not hovering)
		double strokeOuterWidth = hoverPhase * EXPAND + 1f;
		// stroke width is multiplied by 2 because it counts for both inner and outer
		Stroke stroke = new BasicStroke((float) strokeOuterWidth * 2f);
		Area outerStroke = new Area(stroke.createStrokedShape(getButtonForm()));
		// remove inner part of stroke
		outerStroke.subtract(new Area(getButtonForm())); // remove inner stroke
		area.add(outerStroke);

		g.fill(area);
	}

	@Override
	public Rectangle getRepaintBounds() {
		Rectangle r = getBounds();
		r.grow(EXPAND + 1, EXPAND + 1);
		return r;
	}

	private Area getTextArea(Graphics2D g) {
		return TextUtils.getTextArea(text, getBounds(), g);
	}

	private RoundRectangle2D.Double getButtonForm() {
		Rectangle r = getBounds();
		return new RoundRectangle2D.Double(
				r.x, r.y, r.width - 2, r.height - 2, RND_CORNERS, RND_CORNERS);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e))
			return;
		if (!getButtonForm().contains(e.getPoint()))
			return;
		holdEase.setSwitched(true);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e))
			return;
		if (holdEase.isSwitched()) {
			holdEase.setSwitched(false);
			click();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		hoverEase.setSwitched(getButtonForm().contains(e.getPoint()));
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
		if (!SwingUtilities.isLeftMouseButton(e))
			return;
		if (!getButtonForm().contains(e.getPoint())) {
			holdEase.setSwitched(false); // when dragging outside bounds, lose focus (hold)
		}
	}

	@Override
	public void setDisplayed(boolean displayed) {
		hoverEase.setDisplayed(displayed);
		holdEase.setDisplayed(displayed);
	}
}
