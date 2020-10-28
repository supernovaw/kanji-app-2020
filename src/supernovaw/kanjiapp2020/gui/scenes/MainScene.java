package supernovaw.kanjiapp2020.gui.scenes;

import supernovaw.kanjiapp2020.gui.Scene;
import supernovaw.kanjiapp2020.gui.elements.Button;
import supernovaw.kanjiapp2020.gui.layouts.BordersRelativeLayout;

public class MainScene extends Scene {
	public MainScene(Scene parent) {
		super(parent);
		BordersRelativeLayout l = new BordersRelativeLayout();
		setLayout(l);

		Button b = new Button(this, "Mode selection", () -> stackAdd(ModeSelectionScene.class));
		addElement(b);
		l.place(b, new Integer[]{0, -20, 270, 30, 0, 0});
	}
}
