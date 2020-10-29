package supernovaw.kanjiapp2020.gui;

public class FadeOutAnimation {
	private final long animationPeriod;
	private long keyTimestamp;
	private boolean animatingCurrently;
	private AnimatingElement animatingElement;
	// key objects for the disappearing element (i.e. text String and 2 ints for X and Y)
	private Object[] previousObject;

	private FadeOutAnimation(long animationPeriod) {
		this.animationPeriod = animationPeriod;
	}

	public FadeOutAnimation(long animationPeriod, Element animating) {
		this(animationPeriod);
		animatingElement = new AnimatingElement(animating);
	}

	public FadeOutAnimation(long animationPeriod, Runnable animate) {
		this(animationPeriod);
		animatingElement = new AnimatingElement(animate);
	}

	public void animate(Object... previousObject) {
		this.previousObject = previousObject;
		keyTimestamp = System.currentTimeMillis();
		animatingCurrently = true;

		if (animatingElement != null)
			animatingElement.setActive(true);
	}

	public void setParameter(int index, Object p) {
		previousObject[index] = p;
	}

	public Object getParameter(int index) {
		return previousObject[index];
	}

	public boolean isAnimating() {
		return animatingCurrently;
	}

	public void setDisplayed(boolean displayed) {
		animatingElement.setDisplayed(displayed);
	}

	public double getPhase() {
		if (!animatingCurrently) return 1;

		long passed = System.currentTimeMillis() - keyTimestamp;
		if (passed >= animationPeriod) {
			if (passed > animationPeriod + Ease.AFT_STABILIZED_SPARE_DELAY) {
				animatingCurrently = false;
				if (animatingElement != null) animatingElement.setActive(false);
			}
			return 1;
		}
		return (double) passed / animationPeriod;
	}

	public double getEaseInOutCubic() {
		double phase = getPhase();
		if (phase == 1) return phase;
		else return Ease.easeInOutCubic(phase);
	}

	public double getEaseInOutSine() {
		double phase = getPhase();
		if (phase == 1) return phase;
		else return Ease.easeInOutSine(phase);
	}

	public double getEaseOutCubic() {
		double phase = getPhase();
		if (phase == 1) return phase;
		else return Ease.easeOutCubic(phase);
	}
}
