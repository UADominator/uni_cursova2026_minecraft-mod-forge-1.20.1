package dev.dominator.points_visualiser.tests;

import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.NativeImage;
import dev.dominator.points_visualiser.Shared;
import dev.dominator.points_visualiser.color.ColorBehaviour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@Mod.EventBusSubscriber(
	modid = Shared.MODID,
	bus = Mod.EventBusSubscriber.Bus.MOD,
	value = Dist.CLIENT
)
public class BlockColorsGenerator {
	private static final Path OUTPUT = Path.of("..", "src", "main", "server", "resources", "block_colors.json");

	private static final double PLAINS_TEMPERATURE = 0.8;
	private static final double PLAINS_DOWNFALL = 0.4;

	@SubscribeEvent
	public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener((ResourceManagerReloadListener) resourceManager -> generate());
	}

	private static void generate() {
		Map<String, String> colors = new TreeMap<>();
		for (Block block : ColorBehaviour.getWhitelistedBlocks()) {
			colors.put(
				BuiltInRegistries.BLOCK.getKey(block).toString(),
				String.format("#%06X", blockColor(block.defaultBlockState()))
			);
		}

		Path path = FMLPaths.GAMEDIR.get().resolve(OUTPUT).normalize();
		try {
			Files.writeString(path, new GsonBuilder().setPrettyPrinting().create().toJson(colors));
			Shared.LOGGER.info("Wrote {} block colors to {}", colors.size(), path);
		} catch (IOException e) {
			Shared.LOGGER.error("Failed to write block colors to {}", path, e);
		}
	}

	private static int blockColor(BlockState state) {
		BakedQuad quad = topQuad(state);
		int rgb = averageColor(quad.getSprite().contents());
		return quad.isTinted() ? multiply(rgb, tint(state, quad.getTintIndex())) : rgb;
	}

	private static BakedQuad topQuad(BlockState state) {
		BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
		RandomSource random = RandomSource.create(42);
		List<BakedQuad> quads = model.getQuads(state, Direction.UP, random, ModelData.EMPTY, null);
		if (quads.isEmpty()) quads = model.getQuads(state, null, random, ModelData.EMPTY, null);
		return quads.get(0);
	}

	private static int averageColor(SpriteContents contents) {
		NativeImage image = contents.getOriginalImage();
		long red = 0, green = 0, blue = 0, count = 0;
		for (int y = 0; y<contents.height(); y++) {
			for (int x = 0; x<contents.width(); x++) {
				int abgr = image.getPixelRGBA(x, y);
				if ((abgr >>> 24) == 0) continue;
				red += abgr & 0xFF;
				green += (abgr >> 8) & 0xFF;
				blue += (abgr >> 16) & 0xFF;
				count++;
			}
		}
		if (count == 0) return 0;
		return (int) (red / count) << 16 | (int) (green / count) << 8 | (int) (blue / count);
	}

	private static int tint(BlockState state, int tintIndex) {
		int tint = Minecraft.getInstance().getBlockColors().getColor(state, null, null, tintIndex);
		return tint == FoliageColor.getDefaultColor() ? FoliageColor.get(PLAINS_TEMPERATURE, PLAINS_DOWNFALL) : tint;
	}

	private static int multiply(int rgb, int tint) {
		if (tint == -1) return rgb;
		int red = ((rgb >> 16) & 0xFF) * ((tint >> 16) & 0xFF) / 255;
		int green = ((rgb >> 8) & 0xFF) * ((tint >> 8) & 0xFF) / 255;
		int blue = (rgb & 0xFF) * (tint & 0xFF) / 255;
		return red << 16 | green << 8 | blue;
	}
}
