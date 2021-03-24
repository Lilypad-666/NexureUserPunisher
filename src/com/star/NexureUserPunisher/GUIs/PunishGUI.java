package com.star.NexureUserPunisher.GUIs;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.star.NexureUserPunisher.Configs.Settings;
import com.star.NexureUserPunisher.Punishments.Punishment;
import com.star.NexureUserPunisher.Punishments.PunishmentList;
import com.star.NexureUserPunisher.Users.User;
import com.star.NexureUserPunisher.Users.UserList;
import com.star.NexureUserPunisher.Main;

public class PunishGUI extends CustomGUI {

	private User punishedUser;
	
	public PunishGUI(User punisherUser, User punishedUser, UserList userList, PunishmentList punishmentList) {
		super(punisherUser, 5, "Punish " + punishedUser.getUsername(), userList, punishmentList);
		
		this.punishedUser = punishedUser;
		
		openInventory();
		
		createIcons();
	}
	
	@Override
	protected void createIcons() {
		String glassPaneName = ChatColor.LIGHT_PURPLE + "Recent Punishments";
		Icon glassPaneIcon = createIcon(Material.GLASS_PANE, glassPaneName);
		
		for (int i = 0; i < 9; i++) {
			setIcon(i + 18, glassPaneIcon);
		}
		
		//Create a temporary head
		String tempUserHeadName = Settings.Plugin.PrimaryColor + "" + ChatColor.BOLD + punishedUser.getUsername();
		String[] tempUserHeadLore = new String[] {
				"",
				"ChatColor.WHITE + \"Member Since: \" + ChatColor.GRAY + reportedUser.getJoinDateFormatted()"
		};
		Icon tempUserHead = createIcon(Material.PLAYER_HEAD, tempUserHeadName, tempUserHeadLore);
		setIcon(4, tempUserHead);
		
		//Try to set the user's head skin
		setUserHead();
		
		//Icon for warning
		String reportsName = ChatColor.LIGHT_PURPLE + "View User Reports";
		Icon reportsIcon = createIcon(Material.BLAZE_ROD, reportsName).addClickAction(new ClickAction() {
		    @Override
		    public void execute(User senderUser) {
		        new ReportsGUI(senderUser, punishedUser, userList, punishmentList);
		    }
		});
		setIcon(9, reportsIcon);
		
		//Icon for warning
		String warningName = ChatColor.LIGHT_PURPLE + "Warn User";
		Icon warningIcon = createIcon(Material.PAPER, warningName).addClickAction(new ClickAction() {
		    @Override
		    public void execute(User senderUser) {
		        new WarnGUI(senderUser, punishedUser, userList, punishmentList);
		    }
		});
		setIcon(11, warningIcon);

		//Icon for muting
		String muteName = ChatColor.LIGHT_PURPLE + "Mute User";
		Icon muteIcon = createIcon(Material.WRITABLE_BOOK, muteName);
		setIcon(13, muteIcon);
		
		//Icon for kicking
		String kickName = ChatColor.LIGHT_PURPLE + "Kick User";
		Icon kickIcon = createIcon(Material.WATER_BUCKET, kickName);
		setIcon(15, kickIcon);
		
		//Icon for banning
		String banName = ChatColor.LIGHT_PURPLE + "Ban User";
		Icon banIcon = createIcon(Material.LAVA_BUCKET, banName);
		setIcon(17, banIcon);
	}
	
	//setUserHead and getHeadValue are modified from
	//https://www.spigotmc.org/threads/tutorial-get-user-heads-without-lag.396186/
	private void setUserHead(){
		new BukkitRunnable() {
			@Override
			public void run() {
				
				String textureValue;
				textureValue = getHeadValue();
	            
	            if (textureValue == null){
	            	textureValue = "";
	            }
	            
	            ItemStack userHead = new ItemStack(Material.PLAYER_HEAD);
	            SkullMeta userHeadMeta = (SkullMeta)userHead.getItemMeta();
	            
	            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");
	            gameProfile.getProperties().put("textures", new Property("textures", textureValue));
	            
	            Field profileMethod = null;
	            	try {
						profileMethod = userHeadMeta.getClass().getDeclaredField("profile");
		            	profileMethod.setAccessible(true);
		            	profileMethod.set(userHeadMeta, gameProfile);
					} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
	            
	            String headDisplayName = Settings.Plugin.PrimaryColor + "" + ChatColor.BOLD + punishedUser.getUsername();
	            userHeadMeta.setDisplayName(headDisplayName);
	            
	            ArrayList<String> lore = new ArrayList<>();
	            lore.add("");
	            lore.add(ChatColor.WHITE + "Member Since: " + ChatColor.GRAY + punishedUser.getJoinDateFormatted());
	            userHeadMeta.setLore(lore);
	            
	            userHead.setItemMeta(userHeadMeta);
	            
	            Icon userHeadIcon = new Icon(userHead);
	    		
	    		setIcon(4, userHeadIcon);
			}
			
		}.runTaskAsynchronously(Main.getPlugin());
    }
	
	private String getHeadValue() {
    	Gson g = new Gson();
    	JsonObject obj;
    	
        String signature = getURLData("https://sessionserver.mojang.com/session/minecraft/profile/" + punishedUser.getUUID());
        
        obj = g.fromJson(signature, JsonObject.class);
        
        String value = obj.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
        
        String decoded = new String(Base64.getDecoder().decode(value));
        
        obj = g.fromJson(decoded,JsonObject.class);
        
        String skinURL = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
        
        byte[] skinByte = ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinURL + "\"}}}").getBytes();
        
        return new String(Base64.getEncoder().encode(skinByte));
	}
	
	private String getURLData(String requestURL) {
	    try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
	            StandardCharsets.UTF_8.toString()))
	    {
	        scanner.useDelimiter("\\A");
	        return scanner.hasNext() ? scanner.next() : "";
	    } catch (IOException e) {
	    	e.printStackTrace();
	    	return "";
	    }
	}

}
