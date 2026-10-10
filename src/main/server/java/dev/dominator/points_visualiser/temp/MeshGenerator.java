package dev.dominator.points_visualiser.temp;

import dev.dominator.points_visualiser.Shared;
import dev.dominator.points_visualiser.model.Point;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;
import org.smurn.jply.PlyReader;
import org.smurn.jply.PlyReaderFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static dev.dominator.points_visualiser.math.PlyMath.packRGB;

public class MeshGenerator {


	private static InputStream resourceLocationToStream(ResourceLocation location) throws IOException {
		return ServerLifecycleHooks.getCurrentServer()
			       .getResourceManager()
			       .getResource(location)
			       .orElseThrow()
			       .open();
	}

	public static Collection<Point> generatePoints(ResourceLocation location) {
		Shared.LOGGER.info("Generating points for " + location);

		List<Point> pointsCloud = new ArrayList<>();

		try (InputStream is = resourceLocationToStream(location);
			PlyReader plyReader = new PlyReaderFile(is)) {

			ElementReader reader;
			while ((reader = plyReader.nextElementReader()) != null) {
				ElementType type = reader.getElementType();

				if ("vertex".equals(type.getName())) {
					Element pointData;
					while ((pointData = reader.readElement()) != null) {
						float x = (float) pointData.getDouble("x");
						float y = (float) pointData.getDouble("y");
						float z = (float) pointData.getDouble("z");

						int r = pointData.getInt("red");
						int g = pointData.getInt("green");
						int b = pointData.getInt("blue");

						pointsCloud.add(new Point(x, y, z, packRGB(r, g, b)));
					}
				}
				reader.close();
			}
		} catch (Exception e) {
			Shared.LOGGER.error("Error while generating points", e);
		}
		return pointsCloud;
	}
}
