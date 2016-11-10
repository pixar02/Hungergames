package me.minebuilders.hg.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;
import net.md_5.bungee.api.ChatColor;

public class Leave implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender,Command command,String label,String[] args) {
		if(HG.plugin.getConfig().getBoolean("settings.Leave") == true){
			
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.RED + "You must be a player to perform this command!");
				return false;
			} else{
				Player player = (Player) sender;
				if(!HG.plugin.players.containsKey(player.getUniqueId())){
					sender.sendMessage(ChatColor.RED + "Your not in a valid game!");
				}else{
					if(player.hasPermission("hg.leave")){
						HG.plugin.players.get(player.getUniqueId()).getGame().leave(player);
						Util.msg(player, "&cYou left Hungergames!");						
						return true;
					}else{
						sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command!");
					}	
				}
			}
		}else{
			sender.sendMessage(ChatColor.RED + "This command is not enabled");
			return false;
		}
		return false;
	}
}
