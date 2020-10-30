package supernovaw.kanjiapp2020.recognition;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

// stores strokes of KanjiVG loaded writings
public class BezierStroke extends Stroke {
	// used in defining size of the points list that is only used in comparison
	private static final int CURVE_PIECES = 4;
	private static final double DT = 1d / CURVE_PIECES;

	private double[] lengths;
	private double totalLength;
	private int pointsAmt;

	private List<Shape> segments;
	private Path2D path2d;

	// loads Stroke from writings file
	BezierStroke(ByteBuffer buffer) {
		// this part fills segments list with curve shapes
		int curvesAmt = buffer.getInt();
		segments = new ArrayList<>();
		for (int i = 0; i < curvesAmt; i++)
			readCurve(buffer, this);

		findPoints();
		path2d = new Path2D.Float();
		segments.forEach(s -> path2d.append(s, true));

		calculateAngles();
	}

	private void findPoints() {
		pointsAmt = 1 + CURVE_PIECES * segments.size();

		path = new ArrayList<>(pointsAmt);
		// all the segments except the first have their t=0 covered with the end of previous one
		path.add(Curves.getPointOnCurve(segments.get(0), 0));
		lengths = new double[pointsAmt]; // distance to the previous point (lengths[0] is always 0)
		for (int i = 0; i < segments.size(); i++) {
			for (int j = 1; j <= CURVE_PIECES; j++) {
				Point2D pCurrent = Curves.getPointOnCurve(segments.get(i), j * DT);
				Point2D pPrev = path.get(path.size() - 1);
				path.add(pCurrent);

				double dist = pCurrent.distance(pPrev);
				lengths[i * CURVE_PIECES + j] = dist;
				totalLength += dist;
			}
		}
	}

	Path2D getPath2D() {
		return path2d;
	}

	public Point2D getPoint(double t) {
		return Curves.findPointOnPath(path, lengths, totalLength, t);
	}

	private static void readCurve(ByteBuffer buffer, BezierStroke stroke) {
		byte curveType = buffer.get();
		float x1, y1, xCtrl1, yCtrl1, xCtrl2, yCtrl2, x2, y2;
		switch (curveType) {
			case 'c' -> {
				x1 = buffer.getFloat();
				y1 = buffer.getFloat();
				xCtrl1 = buffer.getFloat();
				yCtrl1 = buffer.getFloat();
				xCtrl2 = buffer.getFloat();
				yCtrl2 = buffer.getFloat();
				x2 = buffer.getFloat();
				y2 = buffer.getFloat();
				stroke.segments.add(new CubicCurve2D.Float(x1, y1, xCtrl1, yCtrl1, xCtrl2, yCtrl2, x2, y2));
			}
			case 'q' -> {
				x1 = buffer.getFloat();
				y1 = buffer.getFloat();
				xCtrl1 = buffer.getFloat();
				yCtrl1 = buffer.getFloat();
				x2 = buffer.getFloat();
				y2 = buffer.getFloat();
				stroke.segments.add(new QuadCurve2D.Float(x1, y1, xCtrl1, yCtrl1, x2, y2));
			}
			default -> throw new IllegalArgumentException("unknown curve type " +
					curveType + " (0x" + Integer.toHexString(curveType) + ")");
		}
	}
}
