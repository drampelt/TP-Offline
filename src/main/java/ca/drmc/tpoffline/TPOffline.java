package ca.drmc.tpoffline;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.one4me.ImprovedOfflinePlayer.ImprovedOfflinePlayer;

public class TPOffline extends JavaPlugin implements Listener {
	private static final Logger log = Logger.getLogger("Minecraft");
	
    public void onDisable() {
    	log.info(String.format("[%s] - Disabled!", getDescription().getName()));
    }

    public void onEnable() {
    	log.info(String.format("[%s] - Enabled!", getDescription().getName()));
    }
    
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if(!(sender instanceof Player)) {
			//Make sure the console doesn't use this :P
			log.info("Only players can teleport!");
			return true;
		}
		if(command.getLabel().equals("tpoffline")){
			if(args.length == 1){
				if(sender.hasPermission("tpoffline.tp")){
					ImprovedOfflinePlayer iop = new ImprovedOfflinePlayer(args[0]);
					if(!iop.exists()) {
						sender.sendMessage("[TPOffline] Player not found!");
						return true;
					}
					Location l = iop.getMiscLocation();
					Player p = (Player) sender;
					p.teleport(l);
					sender.sendMessage("[TPOffline] Teleported to " + args[0]);
					return true;
				}else{
					sender.sendMessage("[TPOffline] You don't have permission to do that.");
					return true;
				}
			}else{
				return false;
			}
		}else{
			return false;
		}
    }
}

