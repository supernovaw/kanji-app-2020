package supernovaw.kanjiapp2020.recognition;

import java.awt.geom.Point2D;
import java.util.List;

// Helps determining the difference between characters. This is essential for kanji as 土 and 士
public class StrokesConnection {
	private Point2D offsetBetweenStartPoints;
	private Point2D offsetBetweenEndpoints;

	public StrokesConnection(Stroke s1, Stroke s2) {
		Point2D start1 = s1.getStart();
		Point2D start2 = s2.getStart();
		Point2D end1 = s1.getEnd();
		Point2D end2 = s2.getEnd();

		offsetBetweenStartPoints = new Point2D.Double(start2.getX() - start1.getX(), start2.getY() - start1.getY());
		offsetBetweenEndpoints = new Point2D.Double(end2.getX() - end1.getX(), end2.getY() - end1.getY());
	}

	// Rreturns a value from 0 (similar) to 1 (most dissimilar)
	public double compare(StrokesConnection to) {
		double diffStarts = offsetBetweenStartPoints.distance(to.offsetBetweenStartPoints);
		double diffEnds = offsetBetweenEndpoints.distance(to.offsetBetweenEndpoints);
		return (diffStarts + diffEnds) / CharacterWriting.CHARACTER_BOX_SIZE / 2d;
	}

	// Finds difference for 2 lists with the same size
	public static double compare(List<StrokesConnection> cs1, List<StrokesConnection> cs2) {
		if (cs1.size() != cs2.size()) throw new IllegalArgumentException(
				"Different input list sizes: " + cs1.size() + ", " + cs2.size());
		if (cs1.isEmpty()) return 0;

		double sum = 0;
		for (int i = 0; i < cs1.size(); i++)
			sum += cs1.get(i).compare(cs2.get(i));
		return sum / cs1.size();
	}
}
