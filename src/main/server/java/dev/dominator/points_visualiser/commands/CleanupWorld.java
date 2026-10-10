package dev.dominator.points_visualiser.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.dominator.domilib.common.commands.CommonCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

public class CleanupWorld extends CommonCommand {
	// notify client, skip neighbour updates and drops.
	private static final int UPDATE_FLAGS = 2 | 16 | 32 | 1024;

	@Override
	public LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal(getName())
			       .requires(source -> source.hasPermission(2))
			       .then(Commands.argument("radius", IntegerArgumentType.integer(0))
				             .executes(context -> {
					             int radius = IntegerArgumentType.getInteger(context, "radius");
					             ServerLevel level = context.getSource().getServer().overworld();
					             cleanup(level, radius);
					             context.getSource().sendSuccess(() -> Component.literal("Cleaned radius " + radius), true);
					             return 1;
				             }));
	}

	private static void cleanup(ServerLevel level, int radius) {
		var air = Blocks.AIR.defaultBlockState();
		var pos = new BlockPos.MutableBlockPos();
		for (int x = -radius; x<=radius; x++) {
			for (int z = -radius; z<=radius; z++) {
				for (int y = level.getMinBuildHeight(); y<level.getMaxBuildHeight(); y++) {
					level.setBlock(pos.set(x, y, z), air, UPDATE_FLAGS);
				}
			}
		}

		var box = new AABB(
			-radius, level.getMinBuildHeight(), -radius,
			radius + 1, level.getMaxBuildHeight(), radius + 1
		);
		level.getEntities((Entity) null, box, e -> !(e instanceof Player))
			.forEach(Entity::discard);
	}

	@Override
	protected String getName() {
		return "cleanup-world";
	}
}
