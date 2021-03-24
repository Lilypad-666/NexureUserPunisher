package com.star.NexureUserPunisher.Commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import com.star.NexureUserPunisher.Main;

/**
 * Abstract Command class, stores a reference to the Main class
 * @author Star
 *
 */
public abstract class Command implements CommandExecutor  {

	/**
	 * Called when the command is ran
	 */
	public abstract boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args);
}
