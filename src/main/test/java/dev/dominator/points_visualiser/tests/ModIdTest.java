package dev.dominator.points_visualiser.tests;

import dev.dominator.points_visualiser.Shared;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


class ModIdTest {

	@Test
	void modIdConstantMatchesGradleProperty() {
		Assertions.assertEquals(
			System.getProperty("mod_id"), Shared.MODID,
			"Shared.MODID drifted from mod_id in gradle.properties: @Mod would register the wrong id"
		);
	}

	@Test
	void modIdIsValidForForge() {
		assertTrue(
			Shared.MODID.matches("[a-z][a-z0-9_]{1,63}"),
			"mod id must match [a-z][a-z0-9_]{1,63}, was: " + Shared.MODID
		);
	}
}
