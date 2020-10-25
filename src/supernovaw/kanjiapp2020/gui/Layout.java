package supernovaw.kanjiapp2020.gui;

import java.awt.*;

public abstract class Layout {
	public abstract void addElement(Element e);

	public abstract void removeElement(Element e);

	public abstract Rectangle getBounds(Element e);

	public abstract void setSize(int width, int height);

	public abstract Dimension getSize();
}
