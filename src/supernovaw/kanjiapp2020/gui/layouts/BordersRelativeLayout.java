package supernovaw.kanjiapp2020.gui.layouts;

import supernovaw.kanjiapp2020.gui.Element;
import supernovaw.kanjiapp2020.gui.Layout;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public final class BordersRelativeLayout extends Layout {
	/* The integers set is: X and Y offset, width and height, offset type
	 * for X and Y. The offset type is -1 for the offset between parent's
	 * left or top side and element's left or top side, 0 for the offset
	 * between the parent center and component's center, or 1 for the offset
	 * between parent's right or bottom side and element's right or bottom side.
	 */
	private final Map<Element, Integer[]> elements;
	private int width, height;

	public BordersRelativeLayout() {
		elements = new HashMap<>();
	}

	@Override
	public void addElement(Element e) {
		elements.put(e, new Integer[6]);
	}

	@Override
	public void removeElement(Element e) {
		elements.remove(e);
	}

	public void place(Element e, Integer[] values) {
		if (values.length != 6) {
			throw new IllegalArgumentException("Wrong array size " + values.length);
		}
		int x = values[4], y = values[5];
		if (x != -1 && x != 0 && x != 1) {
			throw new IllegalArgumentException("Incorrect X offset type " +
					x + ". -1, 0, 1 are the only allowed values.");
		}
		if (y != -1 && y != 0 && y != 1) {
			throw new IllegalArgumentException("Incorrect Y offset type " +
					y + ". -1, 0, 1 are the only allowed values.");
		}
		elements.put(e, values);
	}

	@Override
	public Rectangle getBounds(Element e) {
		Integer[] values = elements.get(e);
		int elementW = values[2];
		int elementH = values[3];
		Dimension min = e.getMinimumSize();
		Dimension max = e.getMaximumSize();
		if (min != null) {
			elementW = Math.max(elementW, min.width);
			elementH = Math.max(elementH, min.height);
		}
		if (max != null) {
			elementW = Math.min(elementW, max.width);
			elementH = Math.min(elementH, max.height);
		}
		int offsetX = values[0];
		int offsetY = values[1];
		int typeX = values[4];
		int typeY = values[5];
		int elementX = switch (typeX) {
			case -1 -> offsetX;
			case 0 -> (width - elementW) / 2 + offsetX;
			default -> width - elementW + offsetX;
		};
		int elementY = switch (typeY) {
			case -1 -> offsetY;
			case 0 -> (height - elementH) / 2 + offsetY;
			default -> height - elementH + offsetY;
		};
		return new Rectangle(elementX, elementY, elementW, elementH);
	}

	@Override
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public Dimension getSize() {
		return new Dimension(width, height);
	}
}
