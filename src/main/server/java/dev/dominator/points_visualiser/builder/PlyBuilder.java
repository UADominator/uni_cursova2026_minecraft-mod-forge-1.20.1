package dev.dominator.points_visualiser.builder;

import dev.dominator.domilib.utils.LevelUtils;
import dev.dominator.points_visualiser.model.Point;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;

public class PlyBuilder {
	public final static PlyBuilder INSTANCE = new PlyBuilder();

	private static final int UPDATE_FLAG = 2;

	private static final Block PLACEHOLDER_BLOCK = Blocks.LIGHT_GRAY_CONCRETE;
	private static final BlockState BLOCK_STATE = PLACEHOLDER_BLOCK.defaultBlockState();

	private static final Level DEFAULT_LEVEL = ServerLifecycleHooks.getCurrentServer().overworld();

	private PlyBuilder() {
	}

	public void insertPly(List<Point> points) {
		for (Point point : points) {
			LevelUtils.setBlock(DEFAULT_LEVEL, point.toBlockPos(), BLOCK_STATE, UPDATE_FLAG);
		}
	}
}
