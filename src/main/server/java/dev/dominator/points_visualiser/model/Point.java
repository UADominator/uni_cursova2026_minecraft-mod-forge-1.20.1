package dev.dominator.points_visualiser.model;


import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

@Accessors(fluent = true)
@AllArgsConstructor
public class Point {
	float x, y, z, r, g, b;

	public Point scaleXYZ(float scale) {
		x *= scale;
		y *= scale;
		z *= scale;
		return this;
	}

	public BlockPos toBlockPos() {
		return toBlockPos(Vec3i.ZERO);
	}

	public BlockPos toBlockPos(Vec3i offset) {
		return new BlockPos((int) (x + offset.getX()), (int) (y + offset.getY()), (int) (z + offset.getZ()));
	}
}
