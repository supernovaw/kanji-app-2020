package supernovaw.kanjiapp2020.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public final class Window {
	private static final int ROUND_CORNERS = 11;
	private static final int MINIMUM_WIDTH = 500, MINIMUM_HEIGHT = 500;
	private final WindowControlsScene rootScene;
	private JFrame frame;
	private JPanel frameContent;
	private boolean fullscreen;
	private FadeOutAnimation fullscreenSwitchRepaintCaller;

	public Window(String title, int width, int height, List<Image> icons) {
		rootScene = new WindowControlsScene(new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT), this);

		rootScene.onSizeChanged(new Dimension(width, height));
		initFrame(title, width, height, icons);
		fullscreenSwitchRepaintCaller = new FadeOutAnimation(1, frame::repaint);
		fullscreenSwitchRepaintCaller.setDisplayed(true);
		rootScene.setCallers(frameContent::repaint, frameContent::repaint, this::refreshMousePosition);
		rootScene.setDisplayed(true);
		InputEvents.redirectEvents(frame, rootScene);
	}

	public RootScene getRootScene() {
		return rootScene;
	}

	public void setContent(Scene content) {
		rootScene.setContent(content);
		rootScene.setDisplayed(true);
		rootScene.repaint();
	}

	private void initFrame(String title, int width, int height, List<Image> icons) {
		frame = new JFrame(title);
		frame.setIconImages(icons);

		if (fullscreen) frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setMinimumSize(new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT));
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);

		frame.setFocusTraversalKeysEnabled(false); // allows to use the Tab key
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		frame.setUndecorated(true);
		frame.setBackground(new Color(0, true));

		initFrameContent();
		initListeners();
	}

	private void initFrameContent() {
		frameContent = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
				g2d.setColor(Color.black);
				g2d.fillRect(0, 0, getWidth(), getHeight());

				rootScene.paint(g2d);

				// invoking getPhase is necessary for the repaint calls to stop when they should
				fullscreenSwitchRepaintCaller.getPhase();
			}
		};
		frame.setContentPane(frameContent);
	}

	private void initListeners() {
		frameContent.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (frame.getExtendedState() == Frame.MAXIMIZED_BOTH) {
					fullscreen = true;
					frame.setShape(null);
				} else if (frame.getExtendedState() == Frame.NORMAL) {
					fullscreen = false;
					frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(),
							frame.getHeight(), ROUND_CORNERS, ROUND_CORNERS));
				}
				rootScene.onSizeChanged(frameContent.getSize());
				rootScene.onFullscreenChanged(fullscreen);

				refreshMousePosition();
			}
		});
		frameContent.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				rootScene.onMousePositionChanged(e.getPoint());
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				rootScene.onMousePositionChanged(e.getPoint());
			}
		});
	}

	/* Makes a new mouseMoved event manually, for situations
	 * when the mouse did not move itself, but the elements below
	 * it did, and they need to receive a new mouse position. */
	protected void refreshMousePosition() {
		Point newMousePos = frameContent.getMousePosition();
		if (newMousePos != null) {
			MouseEvent event = new MouseEvent(frameContent, 0, System.currentTimeMillis(),
					0, newMousePos.x, newMousePos.y, 0, false);
			rootScene.mouseMoved(event);
			rootScene.onMousePositionChanged(newMousePos);
		}
	}

	protected void toggleFullscreen() {
		if (fullscreen) { // exit fullscreen
			frame.setExtendedState(Frame.NORMAL);
		} else { // enter fullscreen
			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		}
		fullscreen = !fullscreen;
		rootScene.onFullscreenChanged(fullscreen);
		fullscreenSwitchRepaintCaller.animate();
	}

	protected void hideWindow() {
		frame.setState(Frame.ICONIFIED);
	}

	protected void moveWindow(int x, int y) {
		frame.setLocation(frame.getX() + x, frame.getY() + y);
	}

	protected void setSize(int w, int h) {
		int addW = w - frameContent.getWidth();
		int addH = h - frameContent.getHeight();
		frame.setSize(frame.getWidth() + addW, frame.getHeight() + addH);
	}

	public void show() {
		frame.setVisible(true);
	}
}
