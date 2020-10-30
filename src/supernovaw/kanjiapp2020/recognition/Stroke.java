package supernovaw.kanjiapp2020.recognition;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class Stroke {
	private static final int ANGLES_ARRAY_LENGTH = 32;

	protected List<Point2D> path;
	protected double[] angles;

	public Stroke(List<Point2D> path) {
		this.path = path;
		calculateAngles();
	}

	protected Stroke() {
	}

	final double compare(Stroke to) {
		return getAngleDifference(angles, to.angles);
	}

	final Point2D getStart() {
		return path.get(0);
	}

	final Point2D getEnd() {
		return path.get(path.size() - 1);
	}

	final Rectangle2D getBounds() {
		Point2D first = path.get(0);
		Rectangle2D result = new Rectangle2D.Double(first.getX(), first.getY(), 0, 0);
		for (int i = 1; i < path.size(); i++) result.add(path.get(i));
		return result;
	}

	// 'at' has to have the same scale factor for X and Y and have no rotation
	final void rescale(AffineTransform at) {
		path.forEach(p -> at.transform(p, p));
	}

	final void calculateAngles() {
		double[] lengths = new double[path.size()];
		double totalLength = 0;
		for (int i = 1; i < path.size(); i++) {
			lengths[i] = path.get(i - 1).distance(path.get(i));
			totalLength += lengths[i];
		}

		calculateAngles(lengths, totalLength);
	}

	final void calculateAngles(double[] lengths, double totalLength) {
		angles = new double[ANGLES_ARRAY_LENGTH];
		Point2D prev = path.get(0);
		for (int i = 0; i < ANGLES_ARRAY_LENGTH; i++) {
			double t = (i + 1d) / ANGLES_ARRAY_LENGTH;
			Point2D next = getPoint(t, lengths, totalLength);
			angles[i] = Math.atan2(next.getY() - prev.getY(), next.getX() - prev.getX());
			prev = next;
		}
	}

	// finds a point on the stroke (0 < t < 1)
	private final Point2D getPoint(double t, double[] lengths, double totalLength) {
		return Curves.findPointOnPath(path, lengths, totalLength, t);
	}

	// returns a value from 0 (similar strokes) to 1 (most dissimilar)
	private static double getAngleDifference(double[] angles1, double[] angles2) {
		double[] resultArray = new double[ANGLES_ARRAY_LENGTH];
		for (int i = 0; i < resultArray.length; i++)
			resultArray[i] = getAngleDifference(angles1[i], angles2[i]);

		double resultAngle = 0; // find the average of resultArray values
		for (int i = 1; i < resultArray.length; i++)
			resultAngle += resultArray[i];
		resultAngle /= resultArray.length;
		resultAngle /= Math.PI; // as Pi is the highest possible result, make output range from 0.0 to 1.0

		return resultAngle;
	}

	// for angles between -Pi to Pi (finds the difference in the shortest direction)
	private static double getAngleDifference(double ang1, double ang2) {
		double diff = Math.abs(ang2 - ang1);
		if (diff > Math.PI)
			diff = 2 * Math.PI - diff;
		return diff;
	}
}
