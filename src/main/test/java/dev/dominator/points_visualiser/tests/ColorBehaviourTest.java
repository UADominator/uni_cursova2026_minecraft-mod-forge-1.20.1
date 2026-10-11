package dev.dominator.points_visualiser.tests;

import dev.dominator.points_visualiser.color.ColorBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MinecraftBootstrap.class)
class ColorBehaviourTest {

	private static final Map<BlockState, Integer> blockColors = new HashMap<>();

	@BeforeAll
	static void loadColors() {
		ColorBehaviour.initColors();
		ColorBehaviour.getColormap().forEach((rgb, states) -> states.forEach(state -> blockColors.put(state, rgb)));
	}

	@Test
	void toLabMatchesReferenceValues() {
		// Reference values for D65 white point.
		assertArrayEquals(new double[]{ 100, 0, 0 }, ColorBehaviour.toLab(0xFFFFFF), 0.1);
		assertArrayEquals(new double[]{ 0, 0, 0 }, ColorBehaviour.toLab(0x000000), 0.1);
		assertArrayEquals(new double[]{ 53.24, 80.09, 67.20 }, ColorBehaviour.toLab(0xFF0000), 0.1);
		assertArrayEquals(new double[]{ 87.73, -86.18, 83.18 }, ColorBehaviour.toLab(0x00FF00), 0.1);
		assertArrayEquals(new double[]{ 32.30, 79.19, -107.86 }, ColorBehaviour.toLab(0x0000FF), 0.1);
	}

	/// The lookup picks the palette color nearest to the center of the 15-bit cell of the input.
	/// So for a palette color c the chosen color is at most 2 * distance(c, cell center) away.
	@Test
	void getBlockPicksNearPaletteColor() {
		assertFalse(blockColors.isEmpty(), "block_colors.json loaded no colors");

		for (int rgb : ColorBehaviour.getColormap().keySet()) {
			Integer chosen = blockColors.get(ColorBehaviour.getBlock(rgb));
			assertNotNull(chosen, "getBlock returned a block outside the palette for #%06X".formatted(rgb));

			double limit = 2 * distance(rgb, cellCenter(rgb)) + 1e-9;
			double actual = distance(rgb, chosen);
			assertTrue(
				actual<=limit,
				"#%06X mapped to #%06X, delta E %.2f > %.2f".formatted(rgb, chosen, actual, limit)
			);
		}
	}

	private static int cellCenter(int rgb) {
		return (rgb & 0xF8F8F8) | 0x040404;
	}

	private static double distance(int first, int second) {
		double[] a = ColorBehaviour.toLab(first), b = ColorBehaviour.toLab(second);
		double dl = a[0] - b[0], da = a[1] - b[1], db = a[2] - b[2];
		return Math.sqrt(dl * dl + da * da + db * db);
	}
}
