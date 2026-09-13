package dev.dominator.points_visualiser;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


// Every run gets -mixin.config=${mod_id}.<side>.mixins.json, so a config whose filename or
// refmap still carries the old mod id loads nothing and fails silently in game.
class MixinConfigTest {

	@ParameterizedTest
	@ValueSource(strings = { "shared", "client", "server" })
	void mixinConfigIsNamedAfterTheModId(String side) throws Exception {
		String fileName = Shared.MODID + "." + side + ".mixins.json";

		try (InputStream in = MixinConfigTest.class.getResourceAsStream("/" + fileName)) {
			assertNotNull(in, "missing mixin config on the classpath: " + fileName);

			JsonObject config = JsonParser.parseReader(
				new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();

			assertEquals(
				Shared.MODID + "." + side + ".refmap.json", config.get("refmap").getAsString(),
				fileName + " points at a refmap build.gradle does not generate"
			);
			assertEquals(
				Shared.class.getPackageName() + ".mixins", config.get("package").getAsString(),
				fileName + " declares a mixin package that does not match the sources"
			);
			assertFalse(config.getAsJsonArray("mixins").isEmpty(), fileName + " lists no mixin classes");
		}
	}
}
