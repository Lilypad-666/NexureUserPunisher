package com.star.NexureUserPunisher.Commands;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;


import com.star.NexureUserPunisher.Main;
import com.star.NexureUserPunisher.Configs.Settings;
import com.star.NexureUserPunisher.Punishments.Punishment;
import com.star.NexureUserPunisher.Punishments.Punishment.PunishmentTypes;
import com.star.NexureUserPunisher.Punishments.PunishmentList;
import com.star.NexureUserPunisher.Users.User;
import com.star.NexureUserPunisher.Users.UserList;

/**
 * Command for /report <user> <reason>
 * Lets anyone report any other player, for the reason given
 * @author Star
 *
 */
public class ReportCommand extends com.star.NexureUserPunisher.Commands.Command  {
	
	//Reference to the list of users
	UserList userList;
	
	//Reference to the list of punishments
	PunishmentList punishmentList;
	
	/**
	 * Setting the main reference
	 * @param main
	 */
	public ReportCommand(UserList userList, PunishmentList punishmentList) {
		this.userList = userList;
		this.punishmentList = punishmentList;
	}

	/**
	 * When the report command is called, submit the report
	 */
	@Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		
    	//Ensure that there is a user selected
    	if (args.length < 2) {
    		sender.sendMessage("Usage: /report <user> <reason>");
            return true;
    	}
    	
    	//Get the user that is being reported
    	User punishedUser = userList.getUserByUsername(args[0]);
    	
    	if (punishedUser == null) {
    		sender.sendMessage("Unable to locate " + ChatColor.BOLD + args[0] + ChatColor.WHITE + ", please check the spelling and try again!");
    		return true;
    	}
    	
    	//Gets the reason from the sent message
    	String reason = args[1];
		
    	//Defaults the punished user UUID to 0, so that the console can report/punish
    	UUID punisherUUID = new UUID(0, 0);
    	
    	if (sender instanceof org.bukkit.entity.Player) {
    		//Sets the UUID to the player, if applicable
    		punisherUUID = ((org.bukkit.entity.Player)sender).getUniqueId();
    	}
    	
    	//Creates the punishment and adds it to the list
		punishmentList.createPunishment(PunishmentTypes.Report, punishedUser.getUUID(), punisherUUID, reason);
    	
		//Send the user a message that their report went through
		sender.sendMessage(ChatColor.WHITE + "Succesfully reported " + Settings.Plugin.PrimaryColor + punishedUser.getUsername() + ChatColor.WHITE + " for " + ChatColor.YELLOW + args[1]);
	    	
    	return true;
    }
}
