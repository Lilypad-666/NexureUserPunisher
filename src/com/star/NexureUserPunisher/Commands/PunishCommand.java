package com.star.NexureUserPunisher.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import com.star.NexureUserPunisher.Main;
import com.star.NexureUserPunisher.GUIs.PunishGUI;
import com.star.NexureUserPunisher.Punishments.PunishmentList;
import com.star.NexureUserPunisher.Users.User;
import com.star.NexureUserPunisher.Users.UserList;

/**
 * Command for /punish <user>
 * Opens up a GUI to punish users, and shows you their past infractions
 * @author Star
 *
 */
public class PunishCommand extends com.star.NexureUserPunisher.Commands.Command  {
	
	//Reference to the list of users
	UserList userList;
	
	//Reference to the list of punishments
	PunishmentList punishmentList;
	
	/**
	 * Setting the main reference
	 * @param main
	 */
	public PunishCommand(UserList userList, PunishmentList punishmentList) {
		this.userList = userList;
		this.punishmentList = punishmentList;
	}

	/**
	 * Opens up the punish GUI for the requested player
	 */
	@Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		
		//If console is typing this, send an apology, can't open on console
		if (sender instanceof ConsoleCommandSender) {
			((ConsoleCommandSender)sender).sendMessage("This command doesn't work from the console at this time, I apologize for the inconvenience!");
			return true;
		}

    	//Ensure that a user has been requested
    	if (args.length == 0) {
    		sender.sendMessage("Usage: /punish <user>");
            return true;
    	}
    	
    	//Ensure that it's a player who sent the message
		if (sender instanceof org.bukkit.entity.Player) {
			
			//Get the user who sent the command and the user they're looking up's User objects.
	    	User punisherUser = userList.getUser(((org.bukkit.entity.Player)sender).getUniqueId());
	    	User punishedUser = userList.getUserByUsername(args[0]);
	    	
	    	//If the user can't be found, send a message.
	    	if (punishedUser == null) {
	    		sender.sendMessage("Unable to locate " + ChatColor.BOLD + args[0] + ChatColor.WHITE + ", please check the spelling and try again!");
	    		return true;
	    	}
	    	
	    	//Open a punish window for the player
	    	new PunishGUI(punisherUser, punishedUser, userList, punishmentList);
		}
    	return true;
    }
}
