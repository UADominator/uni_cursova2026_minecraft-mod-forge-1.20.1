package dev.dominator.points_visualiser.color;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static net.minecraft.world.level.block.Blocks.*;

public class ColorBehaviour {
	private static final String COLORS_RESOURCE = "/block_colors.json";
	@Getter
	private static final Map<Integer, Set<BlockState>> colormap = new HashMap<>();

	public static void initColors() {
		colormap.clear();
		try (InputStream is = ColorBehaviour.class.getResourceAsStream(COLORS_RESOURCE)) {
			if (is == null) throw new IOException("Missing resource " + COLORS_RESOURCE);
			JsonObject colors = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();

			for (Map.Entry<String, JsonElement> entry : colors.entrySet()) {
				Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(entry.getKey()));
				int rgb = Integer.parseInt(entry.getValue().getAsString().substring(1), 16);
				colormap.computeIfAbsent(rgb, k -> new HashSet<>()).add(block.defaultBlockState());
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		buildLookup();
	}

	private static final List<Set<BlockState>> palette = new ArrayList<>();

	private static final int[] lookup = new int[1 << 15];

	/// CIELAB distance delta E76
	public static BlockState getBlock(int rgb) {
		int key = (rgb >> 19 & 0x1F) << 10 | (rgb >> 11 & 0x1F) << 5 | (rgb >> 3 & 0x1F);
		return getRandomBlock(palette.get(lookup[key]));
	}

	private static BlockState getRandomBlock(Collection<BlockState> list) {
		if (list.isEmpty()) return LAVA.defaultBlockState();
		return list.stream().skip(ThreadLocalRandom.current().nextInt(list.size())).findFirst().orElseThrow();
	}

	private static void buildLookup() {
		palette.clear();
		List<double[]> paletteLab = new ArrayList<>();
		colormap.forEach((rgb, states) -> {
			palette.add(states);
			paletteLab.add(toLab(rgb));
		});

		for (int key = 0; key<lookup.length; key++) {
			int r = (key >> 10 & 0x1F) << 3 | 4;
			int g = (key >> 5 & 0x1F) << 3 | 4;
			int b = (key & 0x1F) << 3 | 4;
			double[] lab = toLab(r << 16 | g << 8 | b);

			int best = 0;
			double bestDist = Double.MAX_VALUE;
			for (int i = 0; i<paletteLab.size(); i++) {
				double[] p = paletteLab.get(i);
				double dl = lab[0] - p[0], da = lab[1] - p[1], db = lab[2] - p[2];
				double dist = dl * dl + da * da + db * db;
				if (dist<bestDist) {
					bestDist = dist;
					best = i;
				}
			}
			lookup[key] = best;
		}
	}

	public static double[] toLab(int rgb) {
		double r = lin((rgb >> 16 & 0xFF) / 255.0);
		double g = lin((rgb >> 8 & 0xFF) / 255.0);
		double b = lin((rgb & 0xFF) / 255.0);

		double x = (0.4124 * r + 0.3576 * g + 0.1805 * b) / 0.95047;
		double y = (0.2126 * r + 0.7152 * g + 0.0722 * b);
		double z = (0.0193 * r + 0.1192 * g + 0.9505 * b) / 1.08883;

		double fx = f(x), fy = f(y), fz = f(z);
		return new double[]{ 116 * fy - 16, 500 * (fx - fy), 200 * (fy - fz) };
	}

	public static double lin(double c) {
		return c<=0.04045 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
	}

	public static double f(double t) {
		return t>216.0 / 24389 ? Math.cbrt(t) : (24389.0 / 27 * t + 16) / 116;
	}

	/// Just block color paete
	/// generated resources
	public static Collection<Block> getWhitelistedBlocks() {
		return Set.of(
			BEDROCK,
			STONE,
			GRANITE,
			POLISHED_GRANITE,
			DIORITE,
			POLISHED_DIORITE,
			ANDESITE,
			POLISHED_ANDESITE,
			SANDSTONE,
			CUT_SANDSTONE,
			BRICKS,
			MOSSY_COBBLESTONE,
			OBSIDIAN,
			// SPAWNER,
			SNOW_BLOCK,
			CLAY,
			SOUL_SAND,
			SOUL_SOIL,
			BASALT,
			POLISHED_BASALT,
			GLOWSTONE,
			BROWN_MUSHROOM_BLOCK,
			RED_MUSHROOM_BLOCK,
			MUSHROOM_STEM,
			MELON,
			REDSTONE_LAMP,
			// COMMAND_BLOCK,
			NOTE_BLOCK,
			SLIME_BLOCK,
			PRISMARINE,
			PRISMARINE_BRICKS,
			DARK_PRISMARINE,
			SEA_LANTERN,
			PURPUR_BLOCK,
			END_STONE_BRICKS,
			// REPEATING_COMMAND_BLOCK,
			// CHAIN_COMMAND_BLOCK,
			FROSTED_ICE,
			MAGMA_BLOCK,
			NETHER_WART_BLOCK,
			RED_NETHER_BRICKS,
			DRIED_KELP_BLOCK,
			BLUE_ICE,
			WARPED_WART_BLOCK,
			SHROOMLIGHT,
			TARGET,
			HONEY_BLOCK,
			HONEYCOMB_BLOCK,
			CRYING_OBSIDIAN,
			BLACKSTONE,
			POLISHED_BLACKSTONE,
			POLISHED_BLACKSTONE_BRICKS,
			CRACKED_POLISHED_BLACKSTONE_BRICKS,
			CHISELED_POLISHED_BLACKSTONE,
			GILDED_BLACKSTONE,
			CHISELED_NETHER_BRICKS,
			CRACKED_NETHER_BRICKS,
			QUARTZ_BRICKS,
			SPONGE,
			WET_SPONGE,
			SAND,
			RED_SAND,
			GRAVEL,
			DRIPSTONE_BLOCK,
			MUD,
			DEEPSLATE,
			COBBLED_DEEPSLATE,
			POLISHED_DEEPSLATE,
			DEEPSLATE_TILES,
			DEEPSLATE_BRICKS,
			SMOOTH_BASALT,
			OCHRE_FROGLIGHT,
			VERDANT_FROGLIGHT,
			PEARLESCENT_FROGLIGHT,


			DEAD_TUBE_CORAL_BLOCK,
			DEAD_BRAIN_CORAL_BLOCK,
			DEAD_BUBBLE_CORAL_BLOCK,
			DEAD_FIRE_CORAL_BLOCK,
			DEAD_HORN_CORAL_BLOCK,
			TUBE_CORAL_BLOCK,
			BRAIN_CORAL_BLOCK,
			BUBBLE_CORAL_BLOCK,
			FIRE_CORAL_BLOCK,
			HORN_CORAL_BLOCK,
			// DEAD_TUBE_CORAL,
			// DEAD_BRAIN_CORAL,
			// DEAD_BUBBLE_CORAL,
			// DEAD_FIRE_CORAL,
			// DEAD_HORN_CORAL,
			// TUBE_CORAL,
			// BRAIN_CORAL,
			// BUBBLE_CORAL,
			// FIRE_CORAL,
			// HORN_CORAL,


			DIRT,
			COARSE_DIRT,
			COBBLESTONE,
			OAK_PLANKS,
			SPRUCE_PLANKS,
			BIRCH_PLANKS,
			JUNGLE_PLANKS,
			ACACIA_PLANKS,
			CHERRY_PLANKS,
			DARK_OAK_PLANKS,
			MANGROVE_PLANKS,
			BAMBOO_PLANKS,
			BAMBOO_MOSAIC,
			CRIMSON_PLANKS,
			WARPED_PLANKS,
			MUDDY_MANGROVE_ROOTS,
			STRIPPED_SPRUCE_LOG,
			STRIPPED_BIRCH_LOG,
			STRIPPED_JUNGLE_LOG,
			STRIPPED_ACACIA_LOG,
			STRIPPED_CHERRY_LOG,
			STRIPPED_DARK_OAK_LOG,
			STRIPPED_OAK_LOG,
			STRIPPED_MANGROVE_LOG,
			STRIPPED_BAMBOO_BLOCK,
			STRIPPED_OAK_WOOD,
			STRIPPED_SPRUCE_WOOD,
			STRIPPED_BIRCH_WOOD,
			STRIPPED_JUNGLE_WOOD,
			STRIPPED_ACACIA_WOOD,
			STRIPPED_CHERRY_WOOD,
			STRIPPED_DARK_OAK_WOOD,
			STRIPPED_MANGROVE_WOOD,
			OAK_LEAVES,
			SPRUCE_LEAVES,
			BIRCH_LEAVES,
			JUNGLE_LEAVES,
			ACACIA_LEAVES,
			CHERRY_LEAVES,
			DARK_OAK_LEAVES,
			MANGROVE_LEAVES,
			AZALEA_LEAVES,
			FLOWERING_AZALEA_LEAVES,

			OXIDIZED_COPPER,
			WEATHERED_COPPER,
			EXPOSED_COPPER,
			COPPER_BLOCK,
			OXIDIZED_CUT_COPPER,
			WEATHERED_CUT_COPPER,
			EXPOSED_CUT_COPPER,
			CUT_COPPER,

			// GOLD_ORE,
			// DEEPSLATE_GOLD_ORE,
			// NETHER_GOLD_ORE,
			// IRON_ORE,
			// DEEPSLATE_IRON_ORE,
			// COAL_ORE,
			// DEEPSLATE_COAL_ORE,
			// LAPIS_ORE,
			// DEEPSLATE_LAPIS_ORE,
			// DIAMOND_ORE,
			// DEEPSLATE_DIAMOND_ORE,
			// EMERALD_ORE,
			// DEEPSLATE_EMERALD_ORE,
			// NETHER_QUARTZ_ORE,
			// COPPER_ORE,
			// DEEPSLATE_COPPER_ORE,
			ANCIENT_DEBRIS,

			RAW_IRON_BLOCK,
			RAW_COPPER_BLOCK,
			RAW_GOLD_BLOCK,

			LAPIS_BLOCK,
			GOLD_BLOCK,
			IRON_BLOCK,
			DIAMOND_BLOCK,
			EMERALD_BLOCK,
			REDSTONE_BLOCK,
			QUARTZ_BLOCK,
			CHISELED_QUARTZ_BLOCK,
			NETHERITE_BLOCK,


			WHITE_CONCRETE,
			ORANGE_CONCRETE,
			MAGENTA_CONCRETE,
			LIGHT_BLUE_CONCRETE,
			YELLOW_CONCRETE,
			LIME_CONCRETE,
			PINK_CONCRETE,
			GRAY_CONCRETE,
			LIGHT_GRAY_CONCRETE,
			CYAN_CONCRETE,
			PURPLE_CONCRETE,
			BLUE_CONCRETE,
			BROWN_CONCRETE,
			GREEN_CONCRETE,
			RED_CONCRETE,
			BLACK_CONCRETE,

			WHITE_CONCRETE_POWDER,
			ORANGE_CONCRETE_POWDER,
			MAGENTA_CONCRETE_POWDER,
			LIGHT_BLUE_CONCRETE_POWDER,
			YELLOW_CONCRETE_POWDER,
			LIME_CONCRETE_POWDER,
			PINK_CONCRETE_POWDER,
			GRAY_CONCRETE_POWDER,
			LIGHT_GRAY_CONCRETE_POWDER,
			CYAN_CONCRETE_POWDER,
			PURPLE_CONCRETE_POWDER,
			BLUE_CONCRETE_POWDER,
			BROWN_CONCRETE_POWDER,
			GREEN_CONCRETE_POWDER,
			RED_CONCRETE_POWDER,
			BLACK_CONCRETE_POWDER,


			WHITE_WOOL,
			ORANGE_WOOL,
			MAGENTA_WOOL,
			LIGHT_BLUE_WOOL,
			YELLOW_WOOL,
			LIME_WOOL,
			PINK_WOOL,
			GRAY_WOOL,
			LIGHT_GRAY_WOOL,
			CYAN_WOOL,
			PURPLE_WOOL,
			BLUE_WOOL,
			BROWN_WOOL,
			GREEN_WOOL,
			RED_WOOL,
			BLACK_WOOL,

			WHITE_TERRACOTTA,
			ORANGE_TERRACOTTA,
			MAGENTA_TERRACOTTA,
			LIGHT_BLUE_TERRACOTTA,
			YELLOW_TERRACOTTA,
			LIME_TERRACOTTA,
			PINK_TERRACOTTA,
			GRAY_TERRACOTTA,
			LIGHT_GRAY_TERRACOTTA,
			CYAN_TERRACOTTA,
			PURPLE_TERRACOTTA,
			BLUE_TERRACOTTA,
			BROWN_TERRACOTTA,
			GREEN_TERRACOTTA,
			RED_TERRACOTTA,
			BLACK_TERRACOTTA,

			// WHITE_GLAZED_TERRACOTTA,
			// ORANGE_GLAZED_TERRACOTTA,
			// MAGENTA_GLAZED_TERRACOTTA,
			LIGHT_BLUE_GLAZED_TERRACOTTA,
			YELLOW_GLAZED_TERRACOTTA,
			LIME_GLAZED_TERRACOTTA,
			PINK_GLAZED_TERRACOTTA,
			GRAY_GLAZED_TERRACOTTA,
			LIGHT_GRAY_GLAZED_TERRACOTTA,
			CYAN_GLAZED_TERRACOTTA,
			PURPLE_GLAZED_TERRACOTTA,
			BLUE_GLAZED_TERRACOTTA,
			BROWN_GLAZED_TERRACOTTA,
			GREEN_GLAZED_TERRACOTTA,
			RED_GLAZED_TERRACOTTA,
			BLACK_GLAZED_TERRACOTTA
		);
	}
}
