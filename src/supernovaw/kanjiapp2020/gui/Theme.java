package supernovaw.kanjiapp2020.gui;

import supernovaw.kanjiapp2020.main.Assets;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Theme {
	private static final Color foreground = Color.white;
	private static final Color background = Color.black;
	private static final Font uiFont;
	private static final Font japaneseFont;
	private static final float fontSize = 20f;
	private static final BufferedImage backgroundImage;
	private static double backgroundDim = 0.2;

	static {
		uiFont = Assets.loadFont("SFProText-Light.ttf").deriveFont(fontSize);
		japaneseFont = Assets.loadFont("YuGothL.ttc").deriveFont(fontSize);
		backgroundImage = Assets.loadImage("bg.png");
	}

	public static Color foreground() {
		return foreground;
	}

	public static Color background() {
		return background;
	}

	public static Color foreground(double alpha) {
		int a = (int) Math.round(alpha * 255);
		return new Color((foreground.getRGB() & 0xffffff) | (a << 24), true);
	}

	public static Color background(double alpha) {
		int a = (int) Math.round(alpha * 255);
		return new Color((background.getRGB() & 0xffffff) | (a << 24), true);
	}

	public static Font getUiFont() {
		return uiFont;
	}

	public static Font getJapaneseFont() {
		return japaneseFont;
	}

	public static BufferedImage getBackgroundImage() {
		return backgroundImage;
	}

	public static double getBackgroundDim() {
		return backgroundDim;
	}

	public static void setBackgroundDim(double backgroundDim) {
		Theme.backgroundDim = backgroundDim;
	}
}
