package supernovaw.kanjiapp2020.gui.scenes;

import supernovaw.kanjiapp2020.gui.Scene;
import supernovaw.kanjiapp2020.gui.Theme;
import supernovaw.kanjiapp2020.gui.elements.Button;
import supernovaw.kanjiapp2020.gui.elements.CharacterWritingArea;
import supernovaw.kanjiapp2020.gui.elements.Label;
import supernovaw.kanjiapp2020.gui.layouts.BordersRelativeLayout;
import supernovaw.kanjiapp2020.recognition.Recognition;

public class ModeSelectionScene extends Scene {
	private CharacterWritingArea cw;
	private Label label;

	public ModeSelectionScene(Scene parent) {
		super(parent);
		BordersRelativeLayout l = new BordersRelativeLayout();
		setLayout(l);

		Button b = new Button(this, "Cancel", this::stackRemove);
		addElement(b);
		l.place(b, new Integer[]{-65, -10, 100, 30, 1, 1});

		cw = new CharacterWritingArea(this, () -> label.changeText(Recognition.getMostSimilarCharacter(cw.flushWriting())));
		addElement(cw);
		l.place(cw, new Integer[]{0, 0, 300, 300, 0, 0});

		label = new Label(this, "", 0, Theme.getJapaneseFont(), 5);
		addElement(label);
		l.place(label, new Integer[]{0, 300, 300, 300, 0, 0});
	}
}
