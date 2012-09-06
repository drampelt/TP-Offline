package ca.drmc.tpoffline;

import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.one4me.ImprovedOfflinePlayer.ImprovedOfflinePlayer;

public class TPOffline extends JavaPlugin implements Listener {
	private static final Logger log = Logger.getLogger("Minecraft");
	
	public static Permission perms = null;
	
    public void onDisable() {
        // TODO: Place any custom disable code here.
    }

    public void onEnable() {
    	if (!setupPermissions() ) {
			//No economy plugin is found, disable plugin
			log.info(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
    	
    	log.info(String.format("[%s] - Enabled!", getDescription().getName()));
    }
    
    private boolean setupPermissions() {
		//Check for a permissions plugin
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}
    
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if(!(sender instanceof Player)) {
			//Make sure the console doesn't use this :P
			log.info("Only players can teleport!");
			return true;
		}
		if(command.getLabel().equals("tpoffline")){
			if(args.length == 1){
				if(perms.has(sender, "tpoffline.tp")){
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

