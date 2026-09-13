package dev.dominator.points_visualiser;

import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;


/**
 * Boots the registries so Block/BlockState/Item are usable from JUnit. Apply with
 * {@code @ExtendWith(MinecraftBootstrap.class)}.
 *
 * <p>Forge patches {@code Bootstrap.bootStrap()} to call {@code NetworkHooks.init()}, which
 * always throws outside ModLauncher. That call is second-to-last and every registry step runs
 * before it, so it is swallowed and the outcome verified. See REFERENCE.md for the details.
 */
public class MinecraftBootstrap implements BeforeAllCallback {

	private static boolean done;


	@Override
	public void beforeAll(ExtensionContext context) {
		ensureBootstrapped();
	}

	static synchronized void ensureBootstrapped() {
		if (done) {
			return;
		}

		SharedConstants.tryDetectVersion();
		try {
			Bootstrap.bootStrap();
		} catch (Throwable failure) {
			if (!isForgeNetworkInit(failure)) {
				throw failure;
			}
		}

		// bootStrap() sets its own guard flag before doing any work, so a partial run cannot be
		// retried - fail here instead of letting every test report a confusing registry error.
		if (BuiltInRegistries.BLOCK.getKey(Blocks.STONE) == null) {
			throw new IllegalStateException("Minecraft bootstrap did not populate the block registry");
		}

		done = true;
	}

	private static boolean isForgeNetworkInit(Throwable failure) {
		for (Throwable t = failure; t != null; t = t.getCause()) {
			for (StackTraceElement frame : t.getStackTrace()) {
				String className = frame.getClassName();
				if (className.equals("net.minecraftforge.network.NetworkHooks")
					|| className.equals("net.minecraftforge.network.NetworkConstants")) {
					return true;
				}
			}
		}
		return false;
	}
}
