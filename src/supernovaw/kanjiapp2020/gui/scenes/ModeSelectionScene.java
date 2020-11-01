package supernovaw.kanjiapp2020.gui.scenes;

import supernovaw.kanjiapp2020.cards.Card;
import supernovaw.kanjiapp2020.cards.Cards;
import supernovaw.kanjiapp2020.cards.CardsGroup;
import supernovaw.kanjiapp2020.gui.Scene;
import supernovaw.kanjiapp2020.gui.elements.Button;
import supernovaw.kanjiapp2020.gui.elements.Checkbox;
import supernovaw.kanjiapp2020.gui.layouts.BordersRelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModeSelectionScene extends Scene {
	private final BordersRelativeLayout layout;
	private Checkbox[] groupCheckboxes;
	private List<CardsGroup> checkboxCorrespondingGroups;

	public ModeSelectionScene(Scene parent) {
		super(parent);
		layout = new BordersRelativeLayout();
		setLayout(layout);

		Button apply = new Button(this, "Apply", this::apply);
		addElement(apply);
		layout.place(apply, new Integer[]{-175, -10, 100, 30, 1, 1});

		Button cancel = new Button(this, "Cancel", this::stackRemove);
		addElement(cancel);
		layout.place(cancel, new Integer[]{-65, -10, 100, 30, 1, 1});
	}

	private void apply() {
		List<Card> list = new ArrayList<>();
		for (int i = 0; i < groupCheckboxes.length; i++) {
			if (groupCheckboxes[i].isChecked()) {
				CardsGroup g = checkboxCorrespondingGroups.get(i);
				list.addAll(Arrays.asList(g.getCards()));
			}
		}
		if (list.isEmpty()) return;
		Cards.setCurrentGroup(list);
		stackRemove();
	}

	private void updateCheckboxes() {
		List<CardsGroup> gs = Cards.getGroups();
		if (groupCheckboxes == null) {
			groupCheckboxes = new Checkbox[gs.size()];
			for (int i = 0; i < groupCheckboxes.length; i++) {
				Checkbox c = new Checkbox(this, null);
				groupCheckboxes[i] = c;
				addElement(c);
				layout.place(c, new Integer[]{100, 100 + i * 30, 300, 30, -1, -1});
			}
		} else if (groupCheckboxes.length != gs.size()) {
			Checkbox[] newGroup = new Checkbox[gs.size()];
			if (newGroup.length < groupCheckboxes.length) {
				System.arraycopy(groupCheckboxes, 0, newGroup, 0, newGroup.length);
				for (int i = newGroup.length; i < groupCheckboxes.length; i++) {
					removeElement(groupCheckboxes[i]);
				}
			} else {
				System.arraycopy(groupCheckboxes, 0, newGroup, 0, groupCheckboxes.length);
				for (int i = groupCheckboxes.length; i < newGroup.length; i++) {
					Checkbox c = new Checkbox(this, null);
					addElement(c);
					layout.place(c, new Integer[]{100, 100 + i * 30, 300, 30, -1, -1});
					newGroup[i] = c;
				}
			}
			groupCheckboxes = newGroup;
		}
		for (int i = 0; i < gs.size(); i++) {
			Checkbox c = groupCheckboxes[i];
			c.setText(gs.get(i).getName());
			c.setChecked(false);
		}
		checkboxCorrespondingGroups = gs;
	}

	@Override
	protected void setDisplayed(boolean displayed) {
		if (displayed) {
			Cards.load();
			updateCheckboxes();
		}
		super.setDisplayed(displayed);
	}
}
