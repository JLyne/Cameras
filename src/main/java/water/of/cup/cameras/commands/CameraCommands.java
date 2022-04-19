package water.of.cup.cameras.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import water.of.cup.cameras.Camera;
import water.of.cup.cameras.Picture;

public class CameraCommands implements CommandExecutor {

    private final Camera instance = Camera.getInstance();

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player p = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("takepicture")) {
            if(instance.getConfig().getBoolean("settings.camera.permissions")) {
                if(!p.hasPermission("cameras.command")) return false;
            }

            Picture.takePicture(p);
            return true;
        }

        return false;
    }

}
