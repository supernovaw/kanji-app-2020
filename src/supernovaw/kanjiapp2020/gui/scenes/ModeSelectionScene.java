package supernovaw.kanjiapp2020.gui.scenes;

import supernovaw.kanjiapp2020.gui.Scene;
import supernovaw.kanjiapp2020.gui.elements.Button;
import supernovaw.kanjiapp2020.gui.layouts.BordersRelativeLayout;

public class ModeSelectionScene extends Scene {
	public ModeSelectionScene(Scene parent) {
		super(parent);
		BordersRelativeLayout l = new BordersRelativeLayout();
		setLayout(l);

		Button b = new Button(this, "Cancel", this::stackRemove);
		addElement(b);
		l.place(b, new Integer[]{-65, -10, 100, 30, 1, 1});
	}
}
