package dev.dominator.points_visualiser;

import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@GameTestHolder(Shared.MODID)
@PrefixGameTestTemplate(false)
public class CommandGameTest {

	@GameTest(template = "empty")
	public static void buildPlySuggestsModels(GameTestHelper helper) {
		var server = helper.getLevel().getServer();
		var dispatcher = server.getCommands().getDispatcher();
		var parse = dispatcher.parse("build_ply ", server.createCommandSourceStack());

		List<String> suggestions = dispatcher.getCompletionSuggestions(parse).join()
			                           .getList().stream().map(Suggestion::getText).toList();

		if (!suggestions.contains(List.of("outdoor1", "room1", "room2"))) {
			throw new GameTestAssertException("Unexpected build_ply suggestions: " + suggestions);
		}
		helper.succeed();
	}
}
