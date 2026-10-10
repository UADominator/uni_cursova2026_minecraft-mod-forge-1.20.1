package dev.dominator.points_visualiser.builder;

import dev.dominator.domilib.utils.LevelUtils;
import dev.dominator.points_visualiser.color.ColorBehaviour;
import dev.dominator.points_visualiser.model.Point;
import dev.dominator.points_visualiser.model.PointStorage;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PlyBuilder {
	public final static PlyBuilder INSTANCE = new PlyBuilder();

	/// send to client | no update neighbors | don`t recalculate light
	// TODO: check if some mods can replace 1024
	private static final int UPDATE_FLAG = 2 | 16 | 1024;
	private static final int REMOVE_UPDATE_FLAG = 2 | 16 | 32 | 1024;

	private static final Level LEVEL = ServerLifecycleHooks.getCurrentServer().overworld();

	private final ConcurrentLinkedQueue<PointStorage> batches = new ConcurrentLinkedQueue<>();

	private PlyBuilder() {
	}

	public void insertPly() {
		if (batches.isEmpty()) return;

		for (Point point : this.batches.poll().pointsCloud()) {
			placeBlock(point);
		}
	}

	private void placeBlock(Point point) {
		BlockState block = ColorBehaviour.getBlock(point.getRgb());
		LevelUtils.setBlock(LEVEL, point.toBlockPos(), block, UPDATE_FLAG);
	}

	public void addPoints(PointStorage points) {
		// TODO: add split to batches
		batches.add(points);
	}

}
