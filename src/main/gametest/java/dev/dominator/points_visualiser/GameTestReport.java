package dev.dominator.points_visualiser;

import net.minecraft.gametest.framework.GlobalTestReporter;
import net.minecraft.gametest.framework.JUnitLikeTestReporter;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.io.File;


/** Writes a JUnit XML report for runGameTestServer so CI can display individual test results. */
@Mod.EventBusSubscriber(
	modid = Shared.MODID,
	bus = Mod.EventBusSubscriber.Bus.MOD
)
public class GameTestReport {

	private static final String REPORT_PATH = "test-results/gametest.xml";


	@SubscribeEvent
	public static void installXmlReporter(FMLCommonSetupEvent event) {
		if (!Boolean.getBoolean("forge.gameTestServer")) {
			return;
		}

		try {
			File report = new File(REPORT_PATH);
			//noinspection ResultOfMethodCallIgnored
			report.getAbsoluteFile().getParentFile().mkdirs();
			GlobalTestReporter.replaceWith(new JUnitLikeTestReporter(report));
		} catch (Exception failure) {
			// The exit code is authoritative, so a missing report must not fail the run.
			Shared.LOGGER.error("Could not install the JUnit XML game test reporter", failure);
		}
	}
}
