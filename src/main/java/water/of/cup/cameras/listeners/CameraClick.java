package water.of.cup.cameras.listeners;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import water.of.cup.cameras.Camera;
import water.of.cup.cameras.Picture;
import water.of.cup.cameras.Utils;

public class CameraClick implements Listener {

	private final Camera instance = Camera.getInstance();

	@EventHandler
	public void cameraClicked(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if(e.getItem() == null)
			return;

		if(e.getItem().getItemMeta() == null)
			return;

		if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
				&& Utils.isCamera(e.getItem())) {

			boolean usePerms = instance.getConfig().getBoolean("settings.camera.permissions");
			if(usePerms && !p.hasPermission("cameras.useitem")) return;

			boolean messages = instance.getConfig().getBoolean("settings.messages.enabled");
			if (p.getInventory().firstEmpty() == -1) { //check to make sure there is room in the inventory for the map
				if(messages) {
					p.sendMessage(Utils.legacySerializer.deserialize(instance.getConfig().getString("settings.messages.invfull")));
				}
				return;
			}
			if (p.getInventory().contains(Material.PAPER)) { //check to make sure the player has paper
				boolean tookPicture = Picture.takePicture(p);

				if(tookPicture) {
					//remove 1 paper from the player's inventory
					Map<Integer, ? extends ItemStack> paperHash = p.getInventory().all(Material.PAPER);
					for (ItemStack item : paperHash.values()) {
						item.setAmount(item.getAmount() - 1);
						break;
					}
				}
			} else {
				if(messages) {
					p.sendMessage(Utils.legacySerializer.deserialize(instance.getConfig().getString("settings.messages.nopaper")));
				}
			}
			
		}
	}
}
