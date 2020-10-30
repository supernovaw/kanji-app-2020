package supernovaw.kanjiapp2020.recognition;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CharacterWriting {
	// KanjiVG writings box size
	public static final int CHARACTER_BOX_SIZE = 109;

	private List<Stroke> strokes;
	private List<StrokesConnection> strokesConnections;

	public CharacterWriting(List<List<Point2D>> strokes) {
		this.strokes = new ArrayList<>(strokes.size());
		strokes.forEach(s -> this.strokes.add(new Stroke(s)));
		calculateConnections();
	}

	public CharacterWriting() {
		this(Collections.emptyList());
		strokesConnections = Collections.emptyList();
	}

	CharacterWriting(ByteBuffer buffer) {
		int strokesAmt = buffer.getInt();
		strokes = new ArrayList<>(strokesAmt);
		for (int i = 0; i < strokesAmt; i++)
			strokes.add(new BezierStroke(buffer));
		calculateConnections();
	}

	/* Makes it so that the written character will have the same size
	 * as the sample writings that it is going to be compared to. */
	void rescale() {
		AffineTransform at = getTransformForRescaling();
		strokes.forEach(s -> s.rescale(at));
		calculateConnections();
	}

	private void calculateConnections() {
		if (strokes.size() < 2) {
			strokesConnections = Collections.emptyList();
			return;
		}
		strokesConnections = new ArrayList<>(strokes.size() - 1);
		for (int i = 1; i < strokes.size(); i++)
			strokesConnections.add(new StrokesConnection(strokes.get(i - 1), strokes.get(i)));
	}

	private Rectangle2D getBounds() {
		if (strokes.isEmpty()) return new Rectangle2D.Double(0, 0, 0, 0);
		Rectangle2D result = strokes.get(0).getBounds();
		for (int i = 1; i < strokes.size(); i++) result.add(strokes.get(i).getBounds());
		return result;
	}

	private AffineTransform getTransformForRescaling() {
		Rectangle2D bounds = getBounds();

		double neededSize = CharacterWriting.CHARACTER_BOX_SIZE;
		double scaleX = neededSize / bounds.getWidth(); // if bounds are stretched to be neededSize wide
		double scaleY = neededSize / bounds.getHeight(); // if bounds are stretched to be neededSize high

		// resulting scale is the minimum of scaleX and Y to make the resulting writing fit in needed size
		double scale = Math.min(scaleX, scaleY);

		AffineTransform at = new AffineTransform();
		at.scale(scale, scale);
		at.translate(-bounds.getX(), -bounds.getY());

		return at;
	}

	/* Returns 0 for the same, 1 for the most dissimilar writings.
	 * The written one (by the user) has to be rescaled first. */
	double compare(CharacterWriting to) {
		if (to.strokes.size() != this.strokes.size()) return 1d;

		double avgAngDifference = 0;
		for (int i = 0; i < strokes.size(); i++) {
			Stroke s1 = this.strokes.get(i);
			Stroke s2 = to.strokes.get(i);
			avgAngDifference += s1.compare(s2);
		}

		double angDifference = avgAngDifference / strokes.size();
		double posDifference = StrokesConnection.compare(this.strokesConnections, to.strokesConnections);

		return 0.5 * angDifference + 0.5 * posDifference;
	}
}
