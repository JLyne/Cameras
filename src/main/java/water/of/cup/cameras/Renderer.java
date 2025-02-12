package water.of.cup.cameras;

import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Renderer extends MapRenderer {
	private final Camera instance = Camera.getInstance();

	@Override
	public void render(MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
		if (map.isLocked()) {
			return;
		}

		boolean transparentWater = instance.getConfig().getBoolean("settings.camera.transparentWater");
		boolean shadows = instance.getConfig().getBoolean("settings.camera.shadows");

		// get pitch and yaw of players head to calculate ray trace directions
		Location eyes = player.getEyeLocation();
		double pitch = -Math.toRadians(player.getEyeLocation().getPitch());
		double yaw = Math.toRadians(player.getEyeLocation().getYaw() + 90);

		byte[][] canvasBytes = new byte[128][128];

		// loop through every pixel on map
		for (int x = 0; x < 128; x++) {
			for (int y = 0; y < 128; y++) {

				// calculate ray rotations
				double yrotate = -((y) * .9 / 128 - .45);
				double xrotate = ((x) * .9 / 128 - .45);

				Vector rayTraceVector = new Vector(Math.cos(yaw + xrotate) * Math.cos(pitch + yrotate),
						Math.sin(pitch + yrotate), Math.sin(yaw + xrotate) * Math.cos(pitch + yrotate));

				RayTraceResult result = player.getWorld().rayTraceBlocks(eyes, rayTraceVector, 256);

				// Color change for liquids
				RayTraceResult liquidResult = player.getWorld().rayTraceBlocks(eyes, rayTraceVector, 256,
						FluidCollisionMode.ALWAYS, false);
				double[] dye = new double[] { 1, 1, 1 }; // values color is multiplied by
				if (transparentWater) {
					if (liquidResult != null) {
						if (liquidResult.getHitBlock().getType().equals(Material.WATER))
							dye = new double[] { .5, .5, 1 };
						if (liquidResult.getHitBlock().getType().equals(Material.LAVA))
							dye = new double[] { 1, .3, .3 };
					}
				}

				if (result != null) {
					byte lightLevel = result.getHitBlock().getRelative(result.getHitBlockFace()).getLightLevel();

					if(lightLevel > 0 && shadows) {
						double shadowLevel = 15.0;

						for(int i = 0; i < dye.length; i++) {
							dye[i] = dye[i] * (lightLevel / shadowLevel);
						}
					}

					byte color;
					if (transparentWater) {
						color = Utils.colorFromType(result.getHitBlock(), dye);
					} else {
						color = Utils.colorFromType(liquidResult.getHitBlock(), dye);
					}
					canvas.setPixel(x, y, color);
					canvasBytes[x][y] = color;
				} else if (liquidResult != null) {
					// set map pixel to color of liquid block found
					byte color = Utils.colorFromType(liquidResult.getHitBlock(), new double[] { 1, 1, 1 });
					canvas.setPixel(x, y, color);
					canvasBytes[x][y] = color;
				} else {
					// no block was hit, so we will assume we are looking at the sky
					canvas.setPixel(x, y, MapPalette.PALE_BLUE);
					canvasBytes[x][y] = MapPalette.PALE_BLUE;
				}
			}
		}

		Bukkit.getScheduler().runTaskAsynchronously(Camera.getInstance(),
				() -> MapStorage.store(map.getId(), canvasBytes));

		map.setLocked(true);
	}
}
