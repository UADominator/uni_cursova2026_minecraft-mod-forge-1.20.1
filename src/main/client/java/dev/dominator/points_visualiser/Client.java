package dev.dominator.points_visualiser;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;


@Mod.EventBusSubscriber(
	bus = Mod.EventBusSubscriber.Bus.MOD,
	value = Dist.CLIENT
)
public class Client {

	@SubscribeEvent
	public static void onConstructModEvent(FMLConstructModEvent event) {
	}
}
