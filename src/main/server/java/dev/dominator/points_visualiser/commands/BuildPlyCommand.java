package dev.dominator.points_visualiser.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.dominator.domilib.common.commands.CommonCommand;
import dev.dominator.points_visualiser.Shared;
import dev.dominator.points_visualiser.builder.PlyBuilder;
import dev.dominator.points_visualiser.model.PointStorage;
import dev.dominator.points_visualiser.temp.MeshGenerator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class BuildPlyCommand extends CommonCommand {
	private static final String MODELS_PATH = "worldgen";
	private static final String MODEL_EXTENSION = ".ply";

	@Override
	public LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal(getName())
			       .then(Commands.argument("model", StringArgumentType.word())
				             .suggests((context, builder) -> SharedSuggestionProvider.suggest(
					             getModelNames(context.getSource().getServer()), builder))
				             .executes(context -> {
					             String model = StringArgumentType.getString(context, "model");
					             if (!getModelNames(context.getSource().getServer()).contains(model)) {
						             sendError(context, "Unknown model: " + model);
						             return 0;
					             }

					             var mesh = PointStorage.of(MeshGenerator.generatePoints(
						             new ResourceLocation(Shared.MODID, MODELS_PATH + "/" + model + MODEL_EXTENSION)));

					             PlyBuilder.INSTANCE.addPoints(mesh);

					             sendSuccess(context, "Done");
					             return 1;
				             }));
	}

	/// Model names are the .ply file names in data/<modid>/worldgen, without the extension
	private static List<String> getModelNames(MinecraftServer server) {
		return server.getResourceManager()
			       .listResources(MODELS_PATH, location -> location.getPath().endsWith(MODEL_EXTENSION))
			       .keySet().stream()
			       .filter(location -> location.getNamespace().equals(Shared.MODID))
			       .map(location -> location.getPath()
				                        .substring(MODELS_PATH.length() + 1)
				                        .replace(MODEL_EXTENSION, ""))
			       .sorted()
			       .toList();
	}

	@Override
	protected String getName() {
		return "build_ply";
	}
}
