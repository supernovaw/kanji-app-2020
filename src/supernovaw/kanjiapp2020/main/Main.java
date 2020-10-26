package supernovaw.kanjiapp2020.main;

import supernovaw.kanjiapp2020.gui.Scene;
import supernovaw.kanjiapp2020.gui.TransitionScene;
import supernovaw.kanjiapp2020.gui.Window;
import supernovaw.kanjiapp2020.gui.elements.Button;
import supernovaw.kanjiapp2020.gui.layouts.BordersRelativeLayout;

public class Main {
	public static Scene s1, s2;

	public static void main(String[] args) {
		Window w = new Window("\u5341\u4E2D\u516B\u4E5D", 1366, 768, Assets.loadIcons());
		TransitionScene ts = new TransitionScene(w.getRootScene());
		w.setContent(ts);

		s1 = new Scene(ts) {
			{
				BordersRelativeLayout l = new BordersRelativeLayout();
				setLayout(l);
				Button b = new Button(this, "Switch 1", () -> ts.setCurrent(s2));
				addElement(b);
				l.place(b, new Integer[]{0, -20, 270, 30, 0, 0});
			}
		};
		s2 = new Scene(ts) {
			{
				BordersRelativeLayout l = new BordersRelativeLayout();
				setLayout(l);
				Button b = new Button(this, "Switch 2", () -> ts.setCurrent(s1));
				addElement(b);
				l.place(b, new Integer[]{0, 20, 270, 30, 0, 0});
			}
		};
		ts.setCurrent(s1);
		w.show();
	}
}
