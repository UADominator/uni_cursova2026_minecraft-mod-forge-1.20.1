package dev.dominator.points_visualiser.tests;

import dev.dominator.points_visualiser.model.Point;
import dev.dominator.points_visualiser.model.PointStorage;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MinecraftBootstrap.class)
class PointStorageTest {

	// SCALE is 40, so x = 0.01 and x = 0.02 both land in block 0, x = 0.03 lands in block 1.

	@Test
	void pointsInOneBlockGetLinearAverageColor() {
		Map<BlockPos, Integer> blocks = build(new Point(0.01f, 0, 0, 0x000000), new Point(0.02f, 0, 0, 0xFFFFFF));

		// Linear 0.5 is sRGB 188. A naive sRGB average would give 127 or 128.
		assertEquals(Map.of(BlockPos.ZERO, 0xBCBCBC), blocks);
	}

	@Test
	void singlePointKeepsItsColor() {
		Map<BlockPos, Integer> blocks = build(new Point(0.01f, 0, 0, 0x123456));

		assertEquals(Map.of(BlockPos.ZERO, 0x123456), blocks);
	}

	@Test
	void pointsInDifferentBlocksStaySeparate() {
		Map<BlockPos, Integer> blocks = build(new Point(0.01f, 0, 0, 0xFF0000), new Point(0.03f, 0, 0, 0x0000FF));

		assertEquals(Map.of(BlockPos.ZERO, 0xFF0000, new BlockPos(1, 0, 0), 0x0000FF), blocks);
	}

	@Test
	void negativeAndPositiveZeroAreOneBlock() {
		Map<BlockPos, Integer> blocks = build(new Point(-0f, 0, 0, 0xFFFFFF), new Point(0f, 0, 0, 0xFFFFFF));

		assertEquals(Map.of(BlockPos.ZERO, 0xFFFFFF), blocks);
	}

	private static Map<BlockPos, Integer> build(Point... points) {
		PointStorage storage = PointStorage.of(List.of(points));
		assertEquals(
			storage.pointsCloud().size(),
			storage.pointsCloud().stream().map(Point::toBlockPos).distinct().count(),
			"two points in the cloud share one block"
		);
		return storage.pointsCloud().stream().collect(Collectors.toMap(Point::toBlockPos, Point::getRgb));
	}
}
