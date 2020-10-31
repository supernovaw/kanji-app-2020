package supernovaw.kanjiapp2020.gui;

import supernovaw.kanjiapp2020.cards.FuriganaText;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

// A bunch of code for aligning and rendering text
public class TextUtils {
	public static final FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext(null, true, true);

	public static double centerStringX(Graphics2D g, String s, int x) {
		return alignStringX(g, s, x, 0, 0);
	}

	public static double centerStringX(Graphics2D g, String s, Rectangle bounds) {
		return alignStringX(g, s, bounds.x + bounds.width / 2f, 0, 0);
	}

	public static int centerStringY(Graphics2D g, int y) {
		FontMetrics fm = g.getFontMetrics();
		return y + (fm.getAscent() - fm.getDescent()) / 2;
	}

	public static int centerStringY(Graphics2D g, Rectangle bounds) {
		FontMetrics fm = g.getFontMetrics();
		return bounds.y + bounds.height / 2 + (fm.getAscent() - fm.getDescent()) / 2;
	}

	public static double alignStringX(Graphics2D g, String s, float x, float w, int align) {
		return switch (align) {
			case -1 -> x;
			case 0 -> x + (w - stringWidth(g, s)) / 2d;
			case 1 -> x + w - stringWidth(g, s);
			default -> throw new IllegalArgumentException("Unknown align " + align);
		};
	}

	public static double stringWidth(Graphics2D g, String s) {
		return stringWidth(s, g.getFont());
	}

	public static double stringWidth(String s, Font font) {
		return font.getStringBounds(s, FONT_RENDER_CONTEXT).getWidth();
	}

	// getTextArea should be preferably used because it behaves independently from graphics transform
	public static Area getTextArea(String text, double x, double y, Graphics2D g) {
		return getTextArea(text, x, y, g.getFont());
	}

	public static Area getTextArea(String text, double x, double y, Font f) {
		Area area = new Area(f.createGlyphVector(FONT_RENDER_CONTEXT, text).getOutline());
		area.transform(AffineTransform.getTranslateInstance(x, y));
		return area;
	}

	public static Area getTextArea(String text, Rectangle bounds, Graphics2D g) {
		return getTextArea(text, centerStringX(g, text, bounds), centerStringY(g, bounds), g);
	}

	public static Area getTextArea(FuriganaText text, double x, double y, Font f) {
		Area area = new Area(f.createGlyphVector(FONT_RENDER_CONTEXT, text.getText()).getOutline());
		Font furiganaFont = f.deriveFont(f.getSize2D() * FuriganaText.FURIGANA_SCALE);

		double partX = 0;
		for (int i = 0; i < text.getParts(); i++) {
			double width = stringWidth(text.getTextPart(i), f);
			String furigana = text.getFuriganaPart(i);
			if (furigana == null) {
				partX += width;
				continue;
			}
			double furiganaX = partX - stringWidth(furigana, furiganaFont) / 2 + width / 2d;
			double furiganaY = -f.getSize2D();
			Area furiganaPart = getTextArea(furigana, furiganaX, furiganaY, furiganaFont);
			area.add(furiganaPart);
			partX += width;
		}

		area.transform(AffineTransform.getTranslateInstance(x, y + furiganaFont.getSize2D() / 2d));
		return area;
	}

	public static Area getTextArea(FuriganaText text, double x, double y, Graphics2D g) {
		return getTextArea(text, x, y, g.getFont());
	}
}
