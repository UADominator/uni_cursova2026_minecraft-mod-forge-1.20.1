package dev.dominator.points_visualiser.model;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Accessors(fluent = true)
public class PointStorage {
	// TODO: якщо одна точка має кілька кольорів, береться тільки перший колір з усіх.
	//  Можливо в майбутньому треба буде поміняти на середній колір etc
	@Getter
	private Set<Point> pointsCloud = new HashSet<>();

	public static PointStorage of(Collection<Point> points) {
		var pointStorage = new PointStorage();

		pointStorage.preparePoints(points);

		pointStorage.pointsCloud.addAll(points);
		return pointStorage;
	}

	///  Suggest 1i equals 1 meter in ply models. Let`s scale all by *x* times for better minecraft details
	/// тобто 1 метр в реальності == 40 блокам
	private final static float SCALE = 40f;

	private void preparePoints(Collection<Point> points) {
		points.forEach(i -> i.scaleXYZ(SCALE).floor());
	}
}
