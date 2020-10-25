package supernovaw.kanjiapp2020.main;

import supernovaw.kanjiapp2020.gui.Scene;
import supernovaw.kanjiapp2020.gui.Window;
import supernovaw.kanjiapp2020.gui.elements.Button;
import supernovaw.kanjiapp2020.gui.layouts.BordersRelativeLayout;

import javax.swing.*;

public class Main {
	public static void main(String[] args) {
		Window w = new Window("\u5341\u4E2D\u516B\u4E5D", 1366, 768, Assets.loadIcons());
		Scene s = new Scene(w.getRootScene()) {
			{
				BordersRelativeLayout l = new BordersRelativeLayout();
				setLayout(l);
				Button b = new Button(this, "Hello user", () ->
						JOptionPane.showMessageDialog(null, ":)"));
				addElement(b);
				l.place(b, new Integer[]{0, 0, 270, 30, 0, 0});
			}
		};
		w.setContent(s);
		w.show();
	}
}
