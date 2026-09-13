package dev.dominator.points_visualiser;

import dev.dominator.points_visualiser.builder.PlyBuilder;
import dev.dominator.points_visualiser.math.PlyMath;
import dev.dominator.points_visualiser.temp.MeshGenerator;
import net.minecraft.resources.ResourceLocation;
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
			MeshGenerator.generatePoints(new ResourceLocation(Shared.MODID, "worldgen/fragment.ply"));
			PlyMath.preparePoints(MeshGenerator.pointsCloud);
			PlyBuilder.INSTANCE.insertPly(MeshGenerator.pointsCloud);
		}
	}
}
