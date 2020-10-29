package supernovaw.kanjiapp2020.gui.scenes;

import supernovaw.kanjiapp2020.gui.Scene;
import supernovaw.kanjiapp2020.gui.Theme;
import supernovaw.kanjiapp2020.gui.elements.Button;
import supernovaw.kanjiapp2020.gui.elements.Label;
import supernovaw.kanjiapp2020.gui.elements.SeekBar;
import supernovaw.kanjiapp2020.gui.layouts.BordersRelativeLayout;

public class ModeSelectionScene extends Scene {
	private Label dimLabel;

	public ModeSelectionScene(Scene parent) {
		super(parent);
		BordersRelativeLayout l = new BordersRelativeLayout();
		setLayout(l);

		Button b = new Button(this, "Cancel", this::stackRemove);
		addElement(b);
		l.place(b, new Integer[]{-65, -10, 100, 30, 1, 1});

		dimLabel = new Label(this, getBackgroundDimText(), -1);
		addElement(dimLabel);
		l.place(dimLabel, new Integer[]{0, -10, 450, 30, 0, 0});

		SeekBar sb = new SeekBar(this, Theme.getBackgroundDim(), backgroundDim -> {
			Theme.setBackgroundDim(backgroundDim);
			dimLabel.setText(getBackgroundDimText());
			repaintEntirely();
		});
		addElement(sb);
		l.place(sb, new Integer[]{0, 20, 500, 50, 0, 0});
	}

	private String getBackgroundDimText() {
		double dim = Theme.getBackgroundDim();
		return "Background Dim: " + (int) Math.round(dim * 100) + "%";
	}
}
