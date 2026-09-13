package dev.dominator.points_visualiser;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;


// Keep at least one @GameTest: GameTestServer aborts with "No test batches were given!" if none
// are registered. template = "empty" is data/<modid>/structures/empty.nbt, a 3x3x3 air volume.
@GameTestHolder(Shared.MODID)
@PrefixGameTestTemplate(false)
public class SmokeGameTest {

	@GameTest(template = "empty")
	public static void blocksCanBePlaced(GameTestHelper helper) {
		BlockPos pos = new BlockPos(1, 1, 1);

		helper.setBlock(pos, Blocks.DIAMOND_BLOCK);
		helper.assertBlockPresent(Blocks.DIAMOND_BLOCK, pos);
		helper.succeed();
	}

	@GameTest(template = "empty")
	public static void gravityMakesSandFall(GameTestHelper helper) {
		BlockPos floor = new BlockPos(1, 0, 1);
		BlockPos above = new BlockPos(1, 2, 1);

		// The structure is pure air, so the test provides its own floor.
		helper.setBlock(floor, Blocks.STONE);
		helper.setBlock(above, Blocks.SAND);

		// succeedWhen re-checks every tick until it passes or times out - never sleep.
		helper.succeedWhen(() -> {
			helper.assertBlockPresent(Blocks.SAND, floor.above());
			helper.assertBlockNotPresent(Blocks.SAND, above);
		});
	}
}
