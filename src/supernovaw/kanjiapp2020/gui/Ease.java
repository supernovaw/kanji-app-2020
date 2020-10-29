package supernovaw.kanjiapp2020.gui;

public class Ease {
	/* Only halt animation when element is repainted with phase 1 or 0 for sure.
	 * There is a bug when repainting is shut instantly after reaching constant
	 * phase which causes element to have around 5-10% alpha when it should be
	 * gone already, without any consequent auto repaints. This is probably
	 * caused by internal swing working: if paintComponent is run, it doesn't
	 * necessarily mean that painted details in it are displayed in window.
	 * Delay here is used to make sure that the element is repainted fully
	 * in its 1.0 or 0.0 final phase condition. */
	public static final int AFT_STABILIZED_SPARE_DELAY = 200;

	private final long transitionInterval;
	private boolean switched;
	private long keyTimestamp;
	private AnimatingElement animatingElement;

	public Ease(long transitionInterval) {
		this.transitionInterval = transitionInterval;
	}

	public Ease(long transitionInterval, Element animating) {
		this(transitionInterval);
		animatingElement = new AnimatingElement(animating);
	}

	public Ease(long transitionInterval, Runnable repaintCall) {
		this(transitionInterval);
		animatingElement = new AnimatingElement(repaintCall);
	}

	public static double easeInOutSine(double f) {
		checkForEasing(f);
		return 0.5 - 0.5 * Math.cos(Math.PI * f);
	}

	public static double easeInOutCubic(double f) {
		checkForEasing(f);
		if (f < 0.5) return 4 * Math.pow(f, 3);
		else return 1 - 4 * Math.pow(1 - f, 3);
	}

	public static double easeOutCubic(double f) {
		checkForEasing(f);
		return 1 - Math.pow(1 - f, 3);
	}

	private static void checkForEasing(double f) {
		if (f < 0 || f > 1) throw new IllegalArgumentException("f has to fall within [0..1]");
	}

	public boolean isSwitched() {
		return switched;
	}

	public void setSwitched(boolean s) {
		if (switched == s)
			return;
		this.switched = s;

		if (animatingElement != null)
			animatingElement.setActive(true);

		long t = System.currentTimeMillis();
		if (keyTimestamp + transitionInterval < t) {
			keyTimestamp = t;
		} else { // change animation direction on the run
			keyTimestamp = 2 * t - keyTimestamp - transitionInterval;
		}
	}

	// Switches the ease bypassing the animation, as it has initially been in that condition
	public void setInitially(boolean switched) {
		this.switched = switched;
	}

	/* This method is used by the majority of elements when their
	 * scene is no longer on the screen and the animations need
	 * to be disabled. However, this method implies that after the
	 * element is gone from the screen, it has to be switched off:
	 * for instance, if a button is gone, it is useful to also switch
	 * its hover and hold animations off. But some elements like
	 * radio buttons might still require their state to be the same
	 * even after they are gone. setDisplayedSaveCondition(displayed)
	 * does the same thing without switching the ease off. */
	public void setDisplayed(boolean displayed) {
		animatingElement.setDisplayed(displayed);
		if (!displayed) setSwitched(false);
	}

	public void setDisplayedSaveCondition(boolean displayed) {
		animatingElement.setDisplayed(displayed);
	}

	public double getPhase() {
		long passed = System.currentTimeMillis() - keyTimestamp;
		double f;

		if (passed > transitionInterval) {
			if (passed > transitionInterval + AFT_STABILIZED_SPARE_DELAY && animatingElement != null)
				animatingElement.setActive(false);

			f = 1;
		} else {
			f = (double) passed / transitionInterval;
		}

		if (!switched) f = 1 - f;
		return f;
	}

	public double getEaseInOutSine() {
		return easeInOutSine(getPhase());
	}

	public double getEaseInOutCubic() {
		return easeInOutCubic(getPhase());
	}

	public double getEaseOutCubic() {
		return easeOutCubic(getPhase());
	}
}
