package supernovaw.kanjiapp2020.main;

import supernovaw.kanjiapp2020.gui.StackScene;
import supernovaw.kanjiapp2020.gui.Window;
import supernovaw.kanjiapp2020.gui.scenes.MainScene;

public class Main {
	private static final String TITLE = "\u5341\u4E2D\u516B\u4E5D"; // 十中八九, just a random phrase I liked

	public static void main(String[] args) {
		createWindow();
	}

	private static void createWindow() {
		Window w = new Window(TITLE, 1366, 768, Assets.loadIcons());
		StackScene ss = new StackScene(w.getRootScene(), MainScene.class);
		w.setContent(ss);
		w.show();
	}
}
