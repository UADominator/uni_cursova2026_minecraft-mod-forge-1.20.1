package dev.dominator.points_visualiser.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

import java.util.Objects;

@AllArgsConstructor
public class Point {
	float x, y, z;
	@Getter
	int rgb;

	public Point scaleXYZ(float scale) {
		x *= scale;
		y *= scale;
		z *= scale;
		return this;
	}

	public Point floor() {
		x = (float) Math.floor(x);
		y = (float) Math.floor(y);
		z = (float) Math.floor(z);
		return this;
	}

	public BlockPos toBlockPos() {
		return toBlockPos(Vec3i.ZERO);
	}

	public BlockPos toBlockPos(Vec3i offset) {
		return new BlockPos((int) (x + offset.getX()), (int) (y + offset.getY()), (int) (z + offset.getZ()));
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Point point)) return false;
		return compare(x, point.x) == 0 && compare(y, point.y) == 0 && compare(z, point.z) == 0;
	}

	/// make -0 and 0 same number
	private static int compare(float first, float second) {
		if (first<second) return -1;
		if (first>second) return 1;
		return 0;
	}

	@Override
	public int hashCode() {
		// + 0f turns -0f into 0f, to match equals
		return Objects.hash(x + 0f, y + 0f, z + 0f);
	}
}
