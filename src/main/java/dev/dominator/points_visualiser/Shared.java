package dev.dominator.points_visualiser;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(Shared.MODID)
public class Shared {
	public static final String MODID = "points_visualiser";
	public static final Logger LOGGER = LogManager.getLogger(MODID);


	public Shared() {
		LOGGER.info("{} - loaded", MODID);
	}
}
