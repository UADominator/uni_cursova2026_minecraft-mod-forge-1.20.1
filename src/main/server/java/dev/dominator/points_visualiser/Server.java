package dev.dominator.points_visualiser;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Server {

	@SubscribeEvent
	public static void onConstructModEvent(FMLConstructModEvent event) {
	}
}
