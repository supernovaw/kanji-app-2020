package supernovaw.kanjiapp2020.main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class Assets {
	private static final String DIRECTORY = "assets/";

	public static InputStream getStream(String name) throws FileNotFoundException {
		return new FileInputStream(DIRECTORY + name);
	}

	public static byte[] loadStream(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read;
		byte[] buffer = new byte[1048576];
		while ((read = in.read(buffer)) != -1)
			out.write(buffer, 0, read);
		in.close();
		return out.toByteArray();
	}

	public static byte[] loadBytes(String name) throws IOException {
		return loadStream(getStream(name));
	}

	public static BufferedImage loadImage(String name) {
		try {
			return ImageIO.read(getStream(name));
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	public static Font loadFont(String name) {
		try {
			return Font.createFont(Font.TRUETYPE_FONT, getStream("fonts/" + name));
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	public static List<Image> loadIcons() {
		try {
			int[] resolutions = {16, 20, 32, 40, 64, 128};
			ArrayList<Image> result = new ArrayList<>();
			for (int i : resolutions) result.add(loadImage("icon/icon" + i + ".png"));
			return result;
		} catch (Exception e) {
			throw new Error(e);
		}
	}
}
