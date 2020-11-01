package supernovaw.kanjiapp2020.gui.scenes;

import supernovaw.kanjiapp2020.gui.Scene;
import supernovaw.kanjiapp2020.gui.Theme;
import supernovaw.kanjiapp2020.gui.elements.Button;
import supernovaw.kanjiapp2020.gui.elements.Label;
import supernovaw.kanjiapp2020.gui.layouts.BordersRelativeLayout;

public class MainScene extends Scene {
	public MainScene(Scene parent) {
		super(parent);
		BordersRelativeLayout l = new BordersRelativeLayout();
		setLayout(l);

		Button b = new Button(this, "Mode selection", () -> stackAdd(ModeSelectionScene.class));
		addElement(b);
		l.place(b, new Integer[]{0, -20, 270, 30, 0, 0});

		String creditsString = "Developed by supernovaw (Dmitrij Sigida), github.com/supernovaw";
		Label creditsLabel = new Label(this, creditsString, 0, Theme.getUiFont(), 0.65);
		addElement(creditsLabel);
		l.place(creditsLabel, new Integer[]{0, -10, 400, 20, 0, 1});
	}
}
