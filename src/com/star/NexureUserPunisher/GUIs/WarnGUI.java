package com.star.NexureUserPunisher.GUIs;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.star.NexureUserPunisher.Configs.Settings;
import com.star.NexureUserPunisher.Main;
import com.star.NexureUserPunisher.Events.PunishReasonEvent;
import com.star.NexureUserPunisher.Punishments.Punishment;
import com.star.NexureUserPunisher.Punishments.PunishmentList;
import com.star.NexureUserPunisher.Punishments.Punishment.PunishmentTypes;
import com.star.NexureUserPunisher.Users.User;
import com.star.NexureUserPunisher.Users.UserList;

public class WarnGUI extends CustomGUI {

	private User punishedUser;
	
	public WarnGUI(User punisherUser, User punishedUser, UserList userList, PunishmentList punishmentList) {
		super(punisherUser, 5, "Warn " + punishedUser.getUsername(), userList, punishmentList);
		
		this.punishedUser = punishedUser;
		
		openInventory();
		
		createIcons();
	}
	
	@Override
	protected void createIcons() {
		
		//Icon for going back to previous menu
		String backName = Settings.Plugin.PrimaryColor + "Go Back To Punish Menu";
		Icon backIcon = createIcon(Material.BARRIER, backName).addClickAction(new ClickAction() {
		    @Override
		    public void execute(User senderUser) {
		        new PunishGUI(senderUser, punishedUser, userList, punishmentList);
		    }
		});
		setIcon(0, backIcon);
		
		//Inner border
		String yellowGlassName = ChatColor.YELLOW + "Warn " + ChatColor.BOLD + "" + Settings.Plugin.PrimaryColor + punishedUser.getUsername() + ChatColor.YELLOW + "?";
		Icon yellowGlassIcon = createIcon(Material.YELLOW_STAINED_GLASS_PANE, yellowGlassName);
		int[] yellowGlassLocations = new int[] {1, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 43, 44};
		for (int i : yellowGlassLocations) {
			setIcon(i, yellowGlassIcon);
		}
		
		//Middle border
		String orangeGlassName = ChatColor.RED + "Warn " + ChatColor.BOLD + "" + Settings.Plugin.PrimaryColor + punishedUser.getUsername() + ChatColor.RED + "?";
		Icon orangeGlassIcon = createIcon(Material.ORANGE_STAINED_GLASS_PANE, orangeGlassName);
		int[] orangeGlassLocations = new int[] {2, 6, 10, 16, 19, 25, 28, 34, 38, 42};
		for (int i : orangeGlassLocations) {
			setIcon(i, orangeGlassIcon);
		}
		
		//Outside border
		String redGlassName = ChatColor.DARK_RED + "Warn " + ChatColor.DARK_RED + "" + Settings.Plugin.PrimaryColor + punishedUser.getUsername() + ChatColor.DARK_RED + "?";
		Icon redGlassIcon = createIcon(Material.RED_STAINED_GLASS_PANE, redGlassName);
		int[] redGlassLocations = new int[] {3, 4, 5, 11, 15, 20, 24, 29, 33, 39, 40, 41};
		for (int i : redGlassLocations) {
			setIcon(i, redGlassIcon);
		}
		
		
		//Confirmation about warning
		String warningName = ChatColor.LIGHT_PURPLE + "Warn " + ChatColor.BOLD + "" + Settings.Plugin.PrimaryColor + punishedUser.getUsername() + ChatColor.LIGHT_PURPLE + "?";
		String[] warningLore = new String[] {
				"",
				Settings.Plugin.PrimaryColor + "Are you sure?"
		};
		Icon warningIcon = createIcon(Material.PAPER, warningName, warningLore).addClickAction(new ClickAction() {
		    @Override
		    public void execute(User senderUser) {
		    	
		    	PunishReasonEvent punishReasonEvent = new PunishReasonEvent(PunishmentTypes.Warn, punishedUser, senderUser);
		    	Bukkit.getPluginManager().callEvent(punishReasonEvent);

		    	senderUser.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Please say your reason for warning " + Settings.Plugin.PrimaryColor + punishedUser.getUsername() + ChatColor.RED + ", or type " + ChatColor.YELLOW + "cancel" + ChatColor.RED + "to cancel.");
		    	senderUser.getBukkitPlayerInstance().closeInventory();
		    }
		});
		int[] warningLocations = new int[] {12, 13, 14, 21, 22, 23, 30, 31, 32};
		for (int i : warningLocations) {
			setIcon(i, warningIcon);
		}
	}
}
