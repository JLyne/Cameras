package water.of.cup.cameras.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import water.of.cup.cameras.Utils;

public class CameraPlace implements Listener {
	@EventHandler
	public void cameraPlaced(BlockPlaceEvent e) {
		//Prevent players from placing Cameras
		if (Utils.isCamera(e.getItemInHand())) {
			e.setCancelled(true);
		}
	}
}
