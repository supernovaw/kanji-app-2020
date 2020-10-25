package supernovaw.kanjiapp2020.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AnimatingElement {
	private static final List<AnimatingElement> ALL_INSTANCES = new ArrayList<>();

	static {
		start();
	}

	private final Runnable repaintCall;
	private boolean active, displayed;

	public AnimatingElement(Runnable repaintCall) {
		this.repaintCall = repaintCall;
		ALL_INSTANCES.add(this);
	}

	public AnimatingElement(Element element) {
		this(element::repaint);
	}

	private static void start() {
		int frameRate = getAvailableFrameRate();
		int delay = 1000 / frameRate;
		Thread loopThread = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				for (int i = 0; i < ALL_INSTANCES.size(); i++) {
					AnimatingElement element = ALL_INSTANCES.get(i);
					if (element.active && element.displayed)
						element.repaintCall.run();
				}
			}
		});
		loopThread.start();
	}

	private static int getAvailableFrameRate() { // gets the maximum available refresh rate of the monitor(s)
		GraphicsDevice[] gds = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		int max = -1;
		for (GraphicsDevice gd : gds) {
			int rate = gd.getDisplayMode().getRefreshRate();
			if (rate == DisplayMode.REFRESH_RATE_UNKNOWN) continue;
			if (max < rate) max = rate;
		}
		if (max == -1) max = 60; // in case unknown, set to 60
		return max;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}
}
