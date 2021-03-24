package com.star.NexureUserPunisher.Events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.star.NexureUserPunisher.Configs.Settings;
import com.star.NexureUserPunisher.Punishments.PunishmentList;
import com.star.NexureUserPunisher.Punishments.Punishment;
import com.star.NexureUserPunisher.Punishments.Punishment.PunishmentTypes;
import com.star.NexureUserPunisher.Users.User;
import com.star.NexureUserPunisher.Users.UserList;
import com.star.NexureUserPunisher.Main;

/**
 * Listens for most events (except for GUI)
 * 
 * @author Star
 *
 */
public class EventListener implements Listener {
	
	//Reference to the list of users
	UserList userList;
	
	//Reference to the list of punishments
	PunishmentList punishmentList;

	// List of users who are typing reasons for punishments
	private Map<UUID, PunishReasonEvent> waitingForPunishReason;

	/**
	 * Sets the main reference, and initializes variables
	 * 
	 * @param main
	 */
	public EventListener(UserList userList, PunishmentList punishmentList) {
		this.userList = userList;
		this.punishmentList = punishmentList;

		waitingForPunishReason = new HashMap<UUID, PunishReasonEvent>();
	}

	/**
	 * Triggers when a player joins the server Grabs data if we have it, otherwise
	 * creates a new user for them
	 * 
	 * @param e
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		// Gets the bukkit player object
		org.bukkit.entity.Player bukkitPlayer = e.getPlayer();

		// Get the uuid
		UUID uuid = bukkitPlayer.getUniqueId();

		//Either finds an existingly loaded user or 
		User user = userList.getUser(uuid);
		
		//Set the bukkit instance
		user.setBukkitPlayerInstance(bukkitPlayer);
	}

	/**
	 * Triggers when a player leaves the server Removes their player instance
	 * 
	 * @param e
	 */
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		User user = userList.getUser(e.getPlayer().getUniqueId());

		if (user != null) {
			user.removeBukkitPlayerInstance();
		}
	}

	/**
	 * Custom event, triggers when a player is requested to give a reason for their
	 * punishment
	 * 
	 * @param e
	 */
	@EventHandler
	public void onPunishReasonEvent(PunishReasonEvent e) {
		User user = e.getReporterUser();
		UUID userUUID = user.getUUID();

		// Add them to the list of users to intercept the next chat
		waitingForPunishReason.put(userUUID, e);
	}

	/**
	 * Triggers when a player chats Handles punish reason requests
	 * 
	 * @param e
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();

		// If we're waiting on a reason from them
		if (waitingForPunishReason.containsKey(uuid)) {
			// Cancel the message
			e.setCancelled(true);

			// Get the punish reason event, which contains all the data about the punishment
			PunishReasonEvent punishReason = waitingForPunishReason.get(uuid);

			// Remove the user from the waiting for punish reason list
			waitingForPunishReason.remove(uuid);

			// Get the player who is being punished and the one punishing
			User punishedUser = punishReason.getReportedUser();
			User punisherUser = punishReason.getReporterUser();

			// Get the reason for the punishment
			String reason = e.getMessage();

			// Get the type of punishment
			PunishmentTypes punishmentType = punishReason.getPunishmentType();

			// Create the punishment, this will punish the player
			punishmentList.createPunishment(punishmentType, punishedUser.getUUID(), punisherUser.getUUID(), punishReason.getDuration(), reason);

			// Handles chat messages when a player is reported (only through the UI, from a
			// staff member, not implemented, TODO)
			if (punishmentType == PunishmentTypes.Report) {
				
			}

			// Handles chat messages when a player is warned
			if (punishmentType == PunishmentTypes.Warn) {
				// Send the player punishing, confirming the warn
				punisherUser.sendMessage(ChatColor.YELLOW + "Succesfully warned " + Settings.Plugin.PrimaryColor
						+ punishedUser.getUsername() + ChatColor.YELLOW + " for " + reason);

				// Inform the player being warned
				punishedUser.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "[WARNING] " + ChatColor.WHITE
						+ "You have been warned by " + Settings.Plugin.PrimaryColor + punisherUser.getUsername()
						+ ChatColor.WHITE + " for " + ChatColor.YELLOW + reason);
			}

			// Handles chat messages when a player is muted
			if (punishmentType == PunishmentTypes.Mute) {

			}

			// Handles chat messages when a player is kicked
			if (punishmentType == PunishmentTypes.Kick) {

			}

			// Handles chat messages when a player is banned
			if (punishmentType == PunishmentTypes.Ban) {

			}

		}
	}

}
