package dev.dominator.points_visualiser.model;

import dev.dominator.points_visualiser.color.ColorBehaviour;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Accessors(fluent = true)
public class PointStorage {
	// TODO: якщо одна точка має кілька кольорів, береться тільки перший колір з усіх.
	//  Можливо в майбутньому треба буде поміняти на середній колір etc
	@Getter
	private Set<Point> pointsCloud = new HashSet<>();

	public static PointStorage of(Collection<Point> points) {
		var pointStorage = new PointStorage();

		pointStorage.putPoints(points);

		return pointStorage;
	}

	///  Suggest 1i equals 1 meter in ply models. Let`s scale all by *x* times for better minecraft details
	/// тобто 1 метр в реальності == 40 блокам
	private final static float SCALE = 40f;

	private void putPoints(Collection<Point> points) {
		Map<Point, ColorSum> blocks = new HashMap<>();
		points.forEach(i -> {
			i.scaleXYZ(SCALE).floor();
			blocks.computeIfAbsent(i, k -> new ColorSum()).add(i.getRgb());
		});
		blocks.forEach((k, v) -> {
			this.pointsCloud.add(new Point(k.x, k.y, k.z, v.average()));
		});
	}

	private static final class ColorSum {
		double r, g, b;
		int count;

		void add(int rgb) {
			r += ColorBehaviour.lin((rgb >> 16 & 0xFF) / 255.0);
			g += ColorBehaviour.lin((rgb >> 8 & 0xFF) / 255.0);
			b += ColorBehaviour.lin((rgb & 0xFF) / 255.0);
			count++;
		}

		int average() {
			return toSrgb(r / count) << 16 | toSrgb(g / count) << 8 | toSrgb(b / count);
		}

		private static int toSrgb(double c) {
			double s = c<=0.0031308 ? 12.92 * c : 1.055 * Math.pow(c, 1 / 2.4) - 0.055;
			return (int) Math.round(Math.max(0, Math.min(1, s)) * 255);
		}
	}
}
