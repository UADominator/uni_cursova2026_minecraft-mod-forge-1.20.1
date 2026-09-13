package dev.dominator.points_visualiser.math;

import dev.dominator.points_visualiser.model.Point;

import java.util.List;

public class PlyMath {

	///  Suggest 1i equals 1m in ply models. Let`s scale all by *x* times for better minecraft details
	private final static float SCALE = 40f;

	public static List<Point> preparePoints(List<Point> points) {
		points.forEach(i -> i.scaleXYZ(SCALE));
		return points;
	}

	public static List<Point> filterRedundant(List<Point> points) {
		points.forEach(i -> { }); //todo
		return points;
	}
}
