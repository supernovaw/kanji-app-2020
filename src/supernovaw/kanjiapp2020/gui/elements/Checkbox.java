package supernovaw.kanjiapp2020.gui.elements;

import supernovaw.kanjiapp2020.gui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.*;

public class Checkbox extends Element {
	// corners round arc
	private static final int RND_CORNERS = 8;
	// amount of pixels to expand each side when hovering
	private static final int EXPAND = 2;

	private final int checkboxSize = 16; // side of square
	private final int checkboxX; // square offset from bounds start

	private final String text;
	private boolean checked;
	private final Ease hoverEase;
	private final Ease activationEase;
	private boolean hold;
	private Area textArea;

	public Checkbox(Scene parent, String text) {
		super(parent);
		hoverEase = new Ease(120, this);
		activationEase = new Ease(160, this);

		this.text = text;
		checkboxX = EXPAND + 1; // gap distance from left bounds side to fit when expanding outline
	}

	@Override
	protected void paint(Graphics2D g) {
		Rectangle r = getBounds();

		g.setColor(Theme.foreground());
		g.setFont(Theme.getUiFont());
		if (textArea == null)
			textArea = TextUtils.getTextArea(text, 10 + checkboxX + checkboxSize, TextUtils.centerStringY(g, r.height / 2), g);
		g.fill(textArea.createTransformedArea(AffineTransform.getTranslateInstance(r.x, r.y)));

		paintCheckbox(g);
	}

	private void paintCheckbox(Graphics2D g) {
		double hoverPhase = hoverEase.getEaseInOutSine();
		// when mouse hovers the checkbox, it expands
		float stokeOuterWidth = (float) (EXPAND * hoverPhase + 1);

		// stroke width is multiplied by 2 because it counts for both inner and outer
		Stroke outerStroke = new BasicStroke(stokeOuterWidth * 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
		RoundRectangle2D checkboxRound = getCheckboxRound();

		Area checkboxOuterArea = new Area(outerStroke.createStrokedShape(checkboxRound));
		checkboxOuterArea.subtract(new Area(checkboxRound));
		g.fill(checkboxOuterArea);

		Color applyAlphaTo = g.getColor();
		double alpha = activationEase.getEaseInOutSine();
		int innerFillAlpha = (int) Math.round(applyAlphaTo.getAlpha() * alpha);
		Color innerFill = new Color((applyAlphaTo.getRGB() & 0xffffff) | (innerFillAlpha << 24), true);
		g.setColor(innerFill);

		Area innerArea = new Area(checkboxRound);
		double markRatio = activationEase.getEaseInOutCubic();
		innerArea.subtract(new Area(getMarkArea(markRatio)));
		g.fill(innerArea);
	}

	private Shape getMarkArea(double part) { // returns mark area, expanding it from left to right
		float markThickness = 1.5f;
		Rectangle r = getBounds();

		int startX = r.x + checkboxX, startY = r.y + (r.height - checkboxSize) / 2;
		Point2D point1 = new Point2D.Double(startX + checkboxSize * .2, startY + checkboxSize * .55);
		Point2D point2 = new Point2D.Double(startX + checkboxSize * .4, startY + checkboxSize * .75);
		Point2D point3 = new Point2D.Double(startX + checkboxSize * .8, startY + checkboxSize * .30);
		Path2D path = getCutPath(new Point2D[]{point1, point2, point3}, part);

		Stroke stroke = new BasicStroke(markThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		return stroke.createStrokedShape(path); // give path a BasicStroke outline
	}

	// cuts a path so that only 'part' fraction of its length is left starting from array start
	private Path2D getCutPath(Point2D[] path, double part) {
		Path2D result = new Path2D.Double(Path2D.WIND_NON_ZERO, path.length);

		if (part == 1) {
			for (int i = 1; i < path.length; i++)
				result.append(new Line2D.Double(path[i - 1], path[i]), true);
			return result;
		}

		double len = 0;
		double[] dist = new double[path.length - 1];
		for (int i = 0; i < path.length - 1; i++) {
			dist[i] = path[i + 1].distance(path[i]);
			len += dist[i]; // find total length
		}
		len *= part; // find length needed

		for (int i = 0; len != 0; i++) {
			if (len >= dist[i]) // if it's not the last segment of path, insert full line
				result.append(new Line2D.Double(path[i], path[i + 1]), true);
			else {
				// only insert part of last segment
				double finalPart = len / dist[i];
				Point2D mid = new Point2D.Double((1 - finalPart) * path[i].getX() + finalPart * path[i + 1].getX(),
						(1 - finalPart) * path[i].getY() + finalPart * path[i + 1].getY());
				result.append(new Line2D.Double(path[i], mid), true);
				break;
			}
			len -= dist[i];
		}
		return result;
	}

	private RoundRectangle2D.Double getCheckboxRound() {
		Rectangle r = getBounds();
		return new RoundRectangle2D.Double(r.x + checkboxX, r.y + (r.height - checkboxSize) / 2d,
				checkboxSize, checkboxSize, RND_CORNERS, RND_CORNERS);
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		activationEase.setInitially(checked);
		this.checked = checked;
	}

	private void click() {
		checked = !checked;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e)) return;
		if (!getBounds().contains(e.getPoint())) return;
		hold = true; // remember if holding inside buttons
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e))
			return;
		if (hold) { // only perform click if mouse originally pressed inside bounds
			hold = false;
			click();
			activationEase.setSwitched(checked);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		hoverEase.setSwitched(getBounds().contains(e.getPoint()));
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
		if (!SwingUtilities.isLeftMouseButton(e))
			return;
		if (!getBounds().contains(e.getPoint())) {
			hold = false; // when dragging outside bounds, lose focus (hold)
		}
	}

	@Override
	protected void setDisplayed(boolean displayed) {
		hoverEase.setDisplayed(displayed);
		activationEase.setDisplayedSaveCondition(displayed);
		hold &= displayed;
	}
}
