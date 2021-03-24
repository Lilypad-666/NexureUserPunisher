package com.star.NexureUserPunisher.Users;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


public class User {
	
	org.bukkit.entity.Player bukkitPlayerInstance;
	
	private String username;
	private UUID uuid;
	private LocalDate joinDate;
	
	public User(UUID uuid, String username) {
		this.uuid = uuid;
		this.username = username;
		this.joinDate = LocalDate.now();
	}
	
	public User(UUID uuid, String username, LocalDate joinDate) {
		this(uuid, username);
		this.joinDate = joinDate;
	}
	
	/**
	 * When the user is console
	 */
	public User() {
		this(new UUID(0,0), "<CONSOLE>", LocalDate.of(1986, 2, 23));
	}
	
	public String getUsername() {
		return username;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public String dataToString() {
		return String.format("%-40s %-20s %-15s", uuid, username, getJoinDateFormatted());
	}
	 
	public String getJoinDateFormatted() {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
		return joinDate.format(formatter);
	}
	
	public void sendMessage(String message) {
		if (bukkitPlayerInstance != null) {
			bukkitPlayerInstance.sendMessage(message);
		}
	}
	
	public org.bukkit.entity.Player getBukkitPlayerInstance() {
		return bukkitPlayerInstance;
	}
	
	public void setBukkitPlayerInstance(org.bukkit.entity.Player bukkitPlayerInstance) {
		this.bukkitPlayerInstance = bukkitPlayerInstance;
	}
	
	public void removeBukkitPlayerInstance() {
		bukkitPlayerInstance = null;
	}
}
