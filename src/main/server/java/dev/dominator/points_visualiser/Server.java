package dev.dominator.points_visualiser;

import dev.dominator.points_visualiser.builder.PlyBuilder;
import dev.dominator.points_visualiser.color.ColorBehaviour;
import dev.dominator.points_visualiser.commands.BuildPlyCommand;
import dev.dominator.points_visualiser.commands.CleanupWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Server {


	@SubscribeEvent
	public static void onConstructModEvent(FMLConstructModEvent event) {
	}

	@Mod.EventBusSubscriber(
		modid = Shared.MODID,
		bus = Mod.EventBusSubscriber.Bus.FORGE
	)
	public static class ServerEvents {
		@SubscribeEvent
		public static void onServerStartedEvent(ServerStartedEvent event) {
			ColorBehaviour.initColors();
		}

		@SubscribeEvent
		public static void onRegisterCommands(RegisterCommandsEvent event) {
			event.getDispatcher().register(new BuildPlyCommand().register());
			event.getDispatcher().register(new CleanupWorld().register());
		}

		@SubscribeEvent
		public static void tick(TickEvent.ServerTickEvent event) {
			PlyBuilder.INSTANCE.insertPly();
		}
	}
}
