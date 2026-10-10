package dev.dominator.points_visualiser;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

// Each test fails without the server mixins. The power source is placed after its target,
// because a redstone lamp only reacts to a neighbour update, not to its own placement.
@GameTestHolder(Shared.MODID)
@PrefixGameTestTemplate(false)
public class MixinGameTest {
	private static final BlockPos FLOOR = new BlockPos(1, 0, 1);
	private static final BlockPos FLOATING = new BlockPos(1, 2, 1);

	// MixinFallingBlock

	@GameTest(template = "empty")
	public static void sandDoesNotFall(GameTestHelper helper) {
		assertDoesNotFall(helper, Blocks.SAND);
	}

	@GameTest(template = "empty")
	public static void gravelDoesNotFall(GameTestHelper helper) {
		assertDoesNotFall(helper, Blocks.GRAVEL);
	}

	@GameTest(template = "empty")
	public static void concretePowderDoesNotFall(GameTestHelper helper) {
		assertDoesNotFall(helper, Blocks.WHITE_CONCRETE_POWDER);
	}

	@GameTest(template = "empty")
	public static void anvilDoesNotFall(GameTestHelper helper) {
		assertDoesNotFall(helper, Blocks.ANVIL);
	}

	@GameTest(template = "empty")
	public static void sandStaysWhenSupportIsRemoved(GameTestHelper helper) {
		BlockPos support = FLOATING.below();

		helper.setBlock(support, Blocks.STONE);
		helper.setBlock(FLOATING, Blocks.SAND);
		helper.destroyBlock(support);

		helper.runAfterDelay(20, () -> {
			helper.assertBlockPresent(Blocks.SAND, FLOATING);
			helper.assertEntityNotPresent(EntityType.FALLING_BLOCK);
			helper.succeed();
		});
	}

	private static void assertDoesNotFall(GameTestHelper helper, Block block) {
		helper.setBlock(FLOOR, Blocks.STONE);
		helper.setBlock(FLOATING, block);

		helper.runAfterDelay(20, () -> {
			helper.assertBlockPresent(block, FLOATING);
			helper.assertBlockNotPresent(block, FLOOR.above());
			helper.assertEntityNotPresent(EntityType.FALLING_BLOCK);
			helper.succeed();
		});
	}

	// MixinTntBlock

	@GameTest(template = "empty")
	public static void poweredTntDoesNotPrime(GameTestHelper helper) {
		BlockPos tnt = new BlockPos(1, 2, 1);

		helper.setBlock(tnt, Blocks.TNT);
		helper.setBlock(tnt.below(), Blocks.REDSTONE_BLOCK);

		helper.runAfterDelay(20, () -> {
			assertNoPrimedTnt(helper, tnt);
			helper.succeed();
		});
	}

	@GameTest(template = "empty")
	public static void flintAndSteelDoesNotPrimeTnt(GameTestHelper helper) {
		BlockPos tnt = new BlockPos(1, 1, 1);
		Player player = helper.makeMockPlayer();

		helper.setBlock(tnt, Blocks.TNT);
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.FLINT_AND_STEEL));
		helper.useBlock(tnt, player);

		helper.runAfterDelay(20, () -> {
			assertNoPrimedTnt(helper, tnt);
			helper.succeed();
		});
	}

	@GameTest(template = "empty")
	public static void explosionDoesNotChainTnt(GameTestHelper helper) {
		BlockPos tnt = new BlockPos(1, 1, 1);

		helper.setBlock(tnt, Blocks.TNT);
		Vec3 center = Vec3.atCenterOf(helper.absolutePos(tnt));
		helper.getLevel().explode(null, center.x, center.y, center.z, 1f, Level.ExplosionInteraction.TNT);

		// wasExploded gives the chained TNT a fuse of 10 to 29 ticks, so check before it can go off.
		helper.runAfterDelay(5, () -> {
			assertNoPrimedTnt(helper, tnt);
			helper.succeed();
		});
	}

	// Primed TNT can drift out of the 3x3x3 structure, where assertEntityNotPresent does not look.
	private static void assertNoPrimedTnt(GameTestHelper helper, BlockPos tnt) {
		AABB area = new AABB(helper.absolutePos(tnt)).inflate(3);
		if (!helper.getLevel().getEntities(EntityType.TNT, area, Entity::isAlive).isEmpty()) {
			throw new GameTestAssertException("Did not expect primed TNT near " + tnt);
		}
	}

	// MixinBlockStateBase

	@GameTest(template = "empty")
	public static void redstoneBlockDoesNotLightLamp(GameTestHelper helper) {
		BlockPos lamp = new BlockPos(1, 2, 1);

		helper.setBlock(lamp, Blocks.REDSTONE_LAMP);
		helper.setBlock(lamp.below(), Blocks.REDSTONE_BLOCK);

		helper.runAfterDelay(20, () -> {
			helper.assertBlockProperty(lamp, RedstoneLampBlock.LIT, false);
			helper.succeed();
		});
	}

	@GameTest(template = "empty")
	public static void leverDoesNotLightLamp(GameTestHelper helper) {
		BlockPos lamp = new BlockPos(1, 1, 1);
		BlockPos lever = lamp.east();

		helper.setBlock(FLOOR, Blocks.STONE);
		helper.setBlock(lever.below(), Blocks.STONE);
		helper.setBlock(lamp, Blocks.REDSTONE_LAMP);
		helper.setBlock(lever, Blocks.LEVER.defaultBlockState()
			                       .setValue(LeverBlock.FACE, AttachFace.FLOOR)
			                       .setValue(LeverBlock.POWERED, true));

		helper.runAfterDelay(20, () -> {
			helper.assertBlockProperty(lamp, RedstoneLampBlock.LIT, false);
			helper.succeed();
		});
	}

	@GameTest(template = "empty")
	public static void redstoneWireStaysUnpowered(GameTestHelper helper) {
		BlockPos wire = new BlockPos(1, 1, 1);

		helper.setBlock(FLOOR, Blocks.STONE);
		helper.setBlock(wire, Blocks.REDSTONE_WIRE);
		helper.setBlock(wire.east(), Blocks.REDSTONE_BLOCK);

		helper.runAfterDelay(20, () -> {
			helper.assertBlockProperty(wire, RedStoneWireBlock.POWER, 0);
			helper.succeed();
		});
	}

	@GameTest(template = "empty")
	public static void pistonDoesNotExtend(GameTestHelper helper) {
		BlockPos piston = new BlockPos(1, 1, 1);

		helper.setBlock(piston, Blocks.PISTON);
		helper.setBlock(piston.east(), Blocks.REDSTONE_BLOCK);

		helper.runAfterDelay(20, () -> {
			helper.assertBlockProperty(piston, PistonBaseBlock.EXTENDED, false);
			helper.succeed();
		});
	}
}
