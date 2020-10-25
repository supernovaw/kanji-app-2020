package supernovaw.kanjiapp2020.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

final class WindowControlsScene extends RootScene {
	private final Dimension minimumSize;
	private final Window window;
	private final WindowTopBar windowTopBar;
	private final WindowResizeArea windowResizeArea;
	private boolean fullscreen;
	private Scene content;

	public WindowControlsScene(Dimension minimumSize, Window window) {
		super();
		this.window = window;
		this.minimumSize = minimumSize;
		setLayout(new WindowControlsLayout());
		addElement(windowTopBar = new WindowTopBar());
		addElement(windowResizeArea = new WindowResizeArea());
	}

	public void setContent(Scene content) {
		if (content == null) throw new NullPointerException("Null content cannot be set");
		if (this.content != null) throw new Error("The content has already been set");
		this.content = content;
		content.onSizeChanged(getLayout().getBounds(content).getSize());
		addElement(content);
	}

	public void onFullscreenChanged(boolean fullscreen) {
		this.fullscreen = fullscreen;
	}

	@Override
	protected void paint(Graphics2D g) {
		Rectangle r = getBounds();

		BufferedImage bg = Theme.getBackgroundImage();
		AffineTransform at = new AffineTransform();
		Dimension size = getLayout().getSize();
		double scaleX = size.getWidth() / bg.getWidth();
		double scaleY = size.getHeight() / bg.getHeight();
		double scale = Math.max(scaleX, scaleY);
		at.translate(size.width / 2d, size.height / 2d);
		at.scale(scale, scale);
		at.translate(bg.getWidth() / -2d, bg.getHeight() / -2d);
		g.drawImage(bg, at, null);

		g.setColor(Theme.background(0.2));
		g.fillRect(0, 0, r.width, r.height);

		Rectangle clip = g.getClipBounds();
		if (content != null && clip.intersects(content.getRepaintBounds())) content.paint(g);
		if (clip.intersects(windowTopBar.getRepaintBounds())) windowTopBar.paint(g);
		if (clip.intersects(windowResizeArea.getRepaintBounds())) windowResizeArea.paint(g);
	}

	private class WindowControlsLayout extends Layout {
		private int width, height;

		@Override
		public void addElement(Element e) {
		}

		@Override
		public void removeElement(Element e) {
		}

		@Override
		public Rectangle getBounds(Element e) {
			if (e == windowTopBar) return new Rectangle(0, 0, windowTopBar.WIDTH, windowTopBar.HEIGHT);
			if (e == windowResizeArea) return new Rectangle(width - windowResizeArea.WIDTH,
					height - windowResizeArea.HEIGHT, windowResizeArea.WIDTH, windowResizeArea.HEIGHT);
			if (e == content) return new Rectangle(0, windowTopBar.HEIGHT, width, height - windowTopBar.HEIGHT);
			throw new IllegalArgumentException("The element has to be one of: "
					+ windowTopBar + ", " + windowResizeArea + ", " + content);
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

	private class WindowTopBar extends Element {
		protected final int WIDTH = 90, HEIGHT = 30;

		// close, hide and maximize/minimize button colors
		private final Color RED_BUTTON = new Color(0xfc5b57);
		private final Color YELLOW_BUTTON = new Color(0xe5bf3c);
		private final Color GREEN_BUTTON = new Color(0x57c038);

		private final Color BAR_COLOR = new Color(0x23000000, true);
		private final Ease hoverEase; // animation for hovering over bounds (3 buttons)
		private final Ease holdButton1, holdButton2, holdButton3; // hold animation for each of 3 buttons
		/* Point in which the mouse started to drag the bar,
		 * used to calculate new window location while dragging
		 * in order to keep mouse at the same position relative
		 * to window's position */
		private Point holdBarPoint;
		private int buttonHeld;

		protected WindowTopBar() {
			super(WindowControlsScene.this);

			Runnable repaint = this::repaint;
			hoverEase = new Ease(200, repaint);
			hoverEase.setDisplayed(true);
			int buttonsHoldAnimationPeriod = 100;
			holdButton1 = new Ease(buttonsHoldAnimationPeriod, repaint);
			holdButton2 = new Ease(buttonsHoldAnimationPeriod, repaint);
			holdButton3 = new Ease(buttonsHoldAnimationPeriod, repaint);
			holdButton1.setDisplayed(true);
			holdButton2.setDisplayed(true);
			holdButton3.setDisplayed(true);
		}

		@Override
		protected Rectangle getRepaintBounds() {
			Rectangle r = getBounds();
			return new Rectangle(0, r.y, getParentSize().width, r.height);
		}

		// decreases RGB values by portion of 'b' and applies alpha parameter
		private Color blackout(Color c, double b, int applyAlpha) {
			double m = 1 - b;
			int r = (int) (c.getRed() * m);
			int g = (int) (c.getGreen() * m);
			int bl = (int) (c.getBlue() * m);
			return new Color(r, g, bl, applyAlpha);
		}

		@Override
		protected void paint(Graphics2D g) {
			int buttonsR = 7; // buttons radius
			double noFocusAlpha = 0.3; // when no focus, use this alpha

			/* when buttons are held, they black
			 * out this much (1.0 - full blackout)
			 */
			double buttonsBlackout = 0.3;

			int buttonsY = HEIGHT / 2;
			int closeX = WIDTH / 6;
			int hideX = WIDTH / 2;
			int resizeX = WIDTH - WIDTH / 6;

			int alpha = (int) ((noFocusAlpha + (1 - noFocusAlpha) * hoverEase.getEaseInOutSine()) * 255);

			// amount of blackout for each button
			double redB = holdButton1.getEaseInOutSine() * buttonsBlackout;
			double yellowB = holdButton2.getEaseInOutSine() * buttonsBlackout;
			double greenB = holdButton3.getEaseInOutSine() * buttonsBlackout;

			// finds colors with needed blackout and alpha
			Color red = blackout(RED_BUTTON, redB, alpha);
			Color yellow = blackout(YELLOW_BUTTON, yellowB, alpha);
			Color green = blackout(GREEN_BUTTON, greenB, alpha);

			// paint black transparent bar indicating drag area and window top
			g.setColor(BAR_COLOR);
			g.fillRect(0, 0, getParentSize().width, HEIGHT);

			// fill 3 circles for buttons
			g.setColor(red);
			g.fillOval(closeX - buttonsR, buttonsY - buttonsR, buttonsR * 2, buttonsR * 2);
			g.setColor(yellow);
			g.fillOval(hideX - buttonsR, buttonsY - buttonsR, buttonsR * 2, buttonsR * 2);
			g.setColor(green);
			g.fillOval(resizeX - buttonsR, buttonsY - buttonsR, buttonsR * 2, buttonsR * 2);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (!SwingUtilities.isLeftMouseButton(e))
				return;
			if (e.getY() > HEIGHT || e.getY() < 0) // if above or below bar
				return;

			if (new Rectangle(0, 0, WIDTH, HEIGHT).contains(e.getPoint())) { // press on one of the buttons
				double part = 3d * e.getX() / WIDTH;
				if (part < 1) {
					buttonHeld = 1;
					holdButton1.setSwitched(true);
				} else if (part < 2) {
					buttonHeld = 2;
					holdButton2.setSwitched(true);
				} else {
					buttonHeld = 3;
					holdButton3.setSwitched(true);
				}
			} else { // prepare for dragging
				holdBarPoint = e.getPoint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (!SwingUtilities.isLeftMouseButton(e))
				return;

			switch (buttonHeld) {
				case 1 -> System.exit(0); // close
				case 2 -> { // hide
					window.hideWindow();
					hoverEase.setSwitched(false);
				}
				case 3 -> window.toggleFullscreen(); // maximize / minimize
			}

			holdBarPoint = null; // stop dragging

			// release button in case it was held
			buttonHeld = 0;
			holdButton1.setSwitched(false);
			holdButton2.setSwitched(false);
			holdButton3.setSwitched(false);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (!SwingUtilities.isLeftMouseButton(e)) return;
			if (e.getClickCount() == 2 && getBounds().contains(e.getPoint())) window.toggleFullscreen();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			mouseMoved(e);
			if (!SwingUtilities.isLeftMouseButton(e))
				return;

			// if mouse goes outside a button, remove hold animation
			if (getButton(e.getPoint()) != buttonHeld) {
				buttonHeld = 0;
				holdButton1.setSwitched(false);
				holdButton2.setSwitched(false);
				holdButton3.setSwitched(false);
			}

			if (holdBarPoint == null)
				return;

			if (fullscreen) window.toggleFullscreen();
			// relocate window
			int offsetX = e.getX() - holdBarPoint.x, offsetY = e.getY() - holdBarPoint.y;
			window.moveWindow(offsetX, offsetY);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			hoverEase.setSwitched(getBounds().contains(e.getPoint()));
		}

		// returns number of button (1, 2, 3) that corresponds to a point
		private int getButton(Point p) {
			if (!getBounds().contains(p)) return 0;
			double part = 3d * p.getX() / WIDTH;

			if (part < 1)
				return 1;
			else if (part < 2)
				return 2;
			else
				return 3;
		}
	}

	private class WindowResizeArea extends Element {
		protected final int WIDTH = 60, HEIGHT = 60;
		private final Ease hoverEase, holdEase;
		private boolean drag;
		/* addX and addY are always negative and represent the
		 * gap between dragging point and the bottom-right
		 * corner of the window to adjust window's size smoothly
		 */
		private int addX, addY;

		protected WindowResizeArea() {
			super(WindowControlsScene.this);
			Runnable repaint = this::repaint;
			hoverEase = new Ease(100, repaint);
			hoverEase.setDisplayed(true);
			holdEase = new Ease(200, repaint);
			holdEase.setDisplayed(true);
		}

		@Override
		protected void paint(Graphics2D g) {
			Rectangle r = getBounds();

			double colorHover = hoverEase.getEaseInOutSine();
			double colorHold = holdEase.getEaseInOutSine();

			double positionPhase = hoverEase.getEaseInOutCubic();

			int maxAlphaHover = 70, maxAlphaHold = 110;
			double alpha = maxAlphaHover * colorHover + (maxAlphaHold - maxAlphaHover) * colorHold;

			g.setColor(new Color(255, 255, 255, (int) alpha));
			double offset = positionPhase * 2 + 10; // offset towards left-top
			paintResizeIcon(g, r.x + r.width - offset, r.y + r.height - offset);
		}

		private void paintResizeIcon(Graphics2D g, double x, double y) {
			// settings of arrow figure
			int arrLen = 10, arrThc = 3, arrDist = 27;

			int n = 14; // arrow polygon based on 3 setting values
			double[] polyXs = {0, -arrLen, -arrLen, -2 * arrThc, -arrDist + arrThc, -arrDist + arrThc, -arrDist,
					-arrDist, -arrDist + arrLen, -arrDist + arrLen, -arrDist + 2 * arrThc, -arrThc, -arrThc, 0};
			double[] polyYs = {0, 0, -arrThc, -arrThc, -arrDist + 2 * arrThc, -arrDist + arrLen, -arrDist + arrLen,
					-arrDist, -arrDist, -arrDist + arrThc, -arrDist + arrThc, -2 * arrThc, -arrLen, -arrLen};

			for (int i = 0; i < n; i++) { // shift points accordingly to arguments
				polyXs[i] += x;
				polyYs[i] += y;
			}

			// create a 2D figure
			Path2D path = new Path2D.Double(Path2D.WIND_NON_ZERO, n);
			for (int i = 1; i < n; i++)
				path.append(new Line2D.Double(polyXs[i - 1], polyYs[i - 1], polyXs[i], polyYs[i]), true);

			g.fill(path);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (!SwingUtilities.isLeftMouseButton(e))
				return; // only left mouse button
			if (!getBounds().contains(e.getPoint()) || fullscreen)
				return; // if outside bounds or fullscreen, ignore

			drag = true;
			Dimension size = getLayout().getSize();
			addX = size.width - e.getPoint().x;
			addY = size.height - e.getPoint().y;
			holdEase.setSwitched(true);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			boolean showResizeIcon;

			if (fullscreen) {
				showResizeIcon = false; // if fullscreen, no resize available
			} else {
				/* mouse might be outside of bounds but still dragging
				 * because window size can't be under a value set in Window
				 * if drag is true, still display icon in any case */
				showResizeIcon = getBounds().contains(e.getPoint()) || drag;
			}

			hoverEase.setSwitched(showResizeIcon);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			mouseMoved(e);

			// if mouse started dragging from within the bounds, ignore
			if (!drag) return;

			int w = e.getX() + addX, h = e.getY() + addY;
			window.setSize(Math.max(w, minimumSize.width), Math.max(h, minimumSize.height));
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				// if mouse is outside bounds after resizing, disable resize icon
				hoverEase.setSwitched(getBounds().contains(e.getPoint()));

				drag = false;
				holdEase.setSwitched(false);
			}
		}
	}
}
