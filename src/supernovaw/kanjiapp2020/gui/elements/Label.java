package supernovaw.kanjiapp2020.gui.elements;

import supernovaw.kanjiapp2020.cards.FuriganaText;
import supernovaw.kanjiapp2020.gui.*;

import java.awt.*;
import java.awt.geom.Area;

public class Label extends Element {
	private String text;
	private Area textArea;
	private final int alignHorizontal;
	private final Font font;
	// textChangeAnimate has object parameter Area (old textArea) or String if no Area is available
	private final FadeOutAnimation fadingText;
	private boolean displayFurigana;

	public Label(Scene parent, String text, int alignHorizontal, Font f) {
		super(parent);
		this.text = text;
		this.alignHorizontal = alignHorizontal;
		font = f;
		fadingText = new FadeOutAnimation(500, this);
	}

	public Label(Scene parent, String text, int alignHorizontal, Font f, double fontScaleFactor) {
		this(parent, text, alignHorizontal, f.deriveFont((float) (f.getSize2D() * fontScaleFactor)));
	}

	public Label(Scene parent, String text, Font f) {
		this(parent, text, 0, f);
	}

	public Label(Scene parent, String text, int alignHorizontal) {
		this(parent, text, alignHorizontal, Theme.getUiFont());
	}

	public Label(Scene parent, String text) {
		this(parent, text, Theme.getUiFont());
	}

	public void setDisplayFurigana(boolean displayFurigana) {
		this.displayFurigana = displayFurigana;
	}

	public void changeText(String newText) {
		if (textArea == null) { // if the value hasn't been set since last changeText run
			fadingText.animate(text);
		} else {
			fadingText.animate(textArea);
		}
		textArea = null; // cause it to be updated on the next repaint
		text = newText;
	}

	// changes text of this label immediately with no animation
	public void setText(String newText) {
		textArea = null; // cause it to be updated on the next repaint
		text = newText;
	}

	@Override
	protected void paint(Graphics2D g) {
		g.setFont(font);

		if (textArea == null) {
			textArea = getTextArea(text, g);
		}

		Rectangle r = getBounds();
		g.translate(r.x, r.y);

		double fadePhase = fadingText.getEaseInOutSine();
		if (fadePhase == 1) { // just paint current text
			g.setColor(Theme.foreground());
			g.fill(textArea);
		} else { // paint current and fading text
			g.setColor(Theme.foreground(fadePhase));
			g.fill(textArea);
			g.setColor(Theme.foreground(1d - fadePhase));

			Object textParameter = fadingText.getParameter(0);
			if (textParameter instanceof String) {
				textParameter = getTextArea((String) textParameter, g);
				fadingText.setParameter(0, textParameter);
			}
			g.fill((Area) textParameter);
		}

		g.translate(-r.x, -r.y);
	}

	@Override
	protected Rectangle getRepaintBounds() {
		Rectangle r = getBounds();
		if (displayFurigana) r.grow(font.getSize(), 0);
		return r;
	}

	private Area getTextArea(String text, Graphics2D g) {
		int w;
		if (displayFurigana) w = g.getFontMetrics().stringWidth(new FuriganaText(text).getText());
		else w = g.getFontMetrics().stringWidth(text);

		Rectangle r = getBounds();
		int fitW = r.width - 10;
		if (w > fitW) {
			float oldSize = g.getFont().getSize2D();
			float scale = (float) fitW / w;
			g.setFont(g.getFont().deriveFont(oldSize * scale));

			double textX;
			double textY = TextUtils.centerStringY(g, r.height / 2);
			Area result;
			if (displayFurigana) {
				textX = TextUtils.alignStringX(g, new FuriganaText(text).getText(), 0, r.width, alignHorizontal);
				result = TextUtils.getTextArea(new FuriganaText(text), textX, textY, g);
			} else {
				textX = TextUtils.alignStringX(g, text, 0, r.width, alignHorizontal);
				result = TextUtils.getTextArea(text, textX, textY, g);
			}

			g.setFont(g.getFont().deriveFont(oldSize));
			return result;
		} else {
			double textX;
			double textY = TextUtils.centerStringY(g, r.height / 2);
			if (displayFurigana) {
				textX = TextUtils.alignStringX(g, new FuriganaText(text).getText(), 0, r.width, alignHorizontal);
				return TextUtils.getTextArea(new FuriganaText(text), textX, textY, g);
			} else {
				textX = TextUtils.alignStringX(g, text, 0, r.width, alignHorizontal);
				return TextUtils.getTextArea(text, textX, textY, g);
			}
		}
	}

	@Override
	protected void setDisplayed(boolean displayed) {
		fadingText.setDisplayed(displayed);
	}
}
