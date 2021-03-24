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

public class ReportsGUI extends CustomGUI {
	
	private User punishedUser;
	
	private int currentPage;
	
	public ReportsGUI(User punisherUser, User punishedUser, int currentPage, UserList userList, PunishmentList punishmentList) {
		super(punisherUser, 5, "Page " + Settings.Plugin.PrimaryColor + (currentPage + 1)  + ChatColor.DARK_GRAY + " of Reports For " + Settings.Plugin.PrimaryColor + punisherUser.getUsername(), userList, punishmentList);
		
		this.punishedUser = punishedUser;
		
		this.currentPage = currentPage;
		
		openInventory();
		
		createIcons();
	}
	
	public ReportsGUI(User punisherUser, User punishedUser, UserList userList, PunishmentList punishmentList) {
		this(punisherUser, punishedUser, 0, userList, punishmentList);
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
		
		//border
		String glassName = ChatColor.YELLOW + "Reports for " + ChatColor.BOLD + "" + Settings.Plugin.PrimaryColor + punishedUser.getUsername();
		Icon glassIcon = createIcon(Material.YELLOW_STAINED_GLASS_PANE, glassName);
		int[] glassLocations = new int[] {1, 7, 8, 9, 17, 27, 35, 36, 37, 43, 44};
		for (int i : glassLocations) {
			setIcon(i, glassIcon);
		}
		
		int[] validPositions = new int[] {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
		int currentPosition = 0;
		
		List<List<Punishment>> userPunishmentsSplit = splitList(punishmentList.getUsersPunishments(punishedUser.getUUID()), 21);
		
		//Back button
		if (currentPage >= 1) {
			String prevPageName = Settings.Plugin.PrimaryColor + "Previous Page";
			Icon prevPageIcon = createIcon(Material.REDSTONE, prevPageName).addClickAction(new ClickAction() {
			    @Override
			    public void execute(User senderUser) {
			        new ReportsGUI(senderUser, punishedUser, --currentPage, userList, punishmentList);
			    }
			});
			
			setIcon(18, prevPageIcon);
		}
		
		//Forward button
		if (currentPage < userPunishmentsSplit.size() - 1) {
			String nextPageName = Settings.Plugin.PrimaryColor + "Next Page";
			Icon nextPageIcon = createIcon(Material.REDSTONE, nextPageName).addClickAction(new ClickAction() {
			    @Override
			    public void execute(User senderUser) {
			        new ReportsGUI(senderUser, punishedUser, ++currentPage, userList, punishmentList);
			    }
			});
			
			setIcon(26, nextPageIcon);
		}
		
		for (Punishment punishment : userPunishmentsSplit.get(currentPage)) {
			if (punishment.getPunishmentType() == PunishmentTypes.Report) {
				
				User punisherUser = userList.getUser(punishment.getPunisherUUID());
				
				//List of reports
				String reportName;
				String[] reportLore;
				
				if (punisherUser == null) {
					reportName = ChatColor.LIGHT_PURPLE + "Report by " + Settings.Plugin.PrimaryColor + punishment.getPunisherUUID();
					reportLore = new String[] {
							"",
							ChatColor.BOLD + "Date: " + ChatColor.GRAY + punishment.getStartDateTimeFormatted(),
							ChatColor.BOLD + "Reason: " + ChatColor.GRAY + punishment.getReason()
					};
				} else {
					reportName = ChatColor.LIGHT_PURPLE + "Report by " + Settings.Plugin.PrimaryColor + punisherUser.getUsername();
					reportLore = new String[] {
							"",
							ChatColor.BOLD + "Date: " + ChatColor.GRAY + punishment.getStartDateTimeFormatted(),
							ChatColor.BOLD + "Reason: " + ChatColor.GRAY + punishment.getReason()
					};
				}
				
				Icon reportIcon = createIcon(Material.PAPER, reportName, reportLore).addClickAction(new ClickAction() {
				    @Override
				    public void execute(User senderUser) {
				    	
				    	PunishReasonEvent punishReasonEvent = new PunishReasonEvent(PunishmentTypes.Warn, punishedUser, senderUser);
				    	Bukkit.getPluginManager().callEvent(punishReasonEvent);


				    	senderUser.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Please say your reason for warning " + Settings.Plugin.PrimaryColor + punishedUser.getUsername() + ChatColor.RED + ".");
				    	senderUser.getBukkitPlayerInstance().closeInventory();
				    }
				});
				
				setIcon(validPositions[currentPosition++], reportIcon);
			}
		}
	}
}
