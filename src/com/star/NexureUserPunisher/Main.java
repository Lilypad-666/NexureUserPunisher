package com.star.NexureUserPunisher;

import java.nio.file.Paths;

import org.bukkit.plugin.java.JavaPlugin;

import com.star.NexureUserPunisher.Commands.PunishCommand;
import com.star.NexureUserPunisher.Commands.ReportCommand;
import com.star.NexureUserPunisher.Configs.Settings;
import com.star.NexureUserPunisher.Events.EventListener;
import com.star.NexureUserPunisher.GUIs.CustomGUIListener;
import com.star.NexureUserPunisher.Punishments.PunishmentList;
import com.star.NexureUserPunisher.Users.UserList;

/**
 * Main class
 * @author Star
 *
 */
public class Main extends JavaPlugin {
	
	/**
	 * Static access to the Bukkit plugin
	 */
	private static JavaPlugin plugin;
	public static JavaPlugin getPlugin() {
		return plugin;
	}
	
	/**
	 * Reference to the class that listens for (most) events
	 */
	private EventListener eventListeners;
    
	/**
	 * List of all users
	 */
    private UserList userList;
    
    /**
     * List of all punishments made
     */
    private PunishmentList punishmentList;
    
    /**
     * Runs on plugin load
     */
    @Override
    public void onEnable() {
    	plugin = this;
        
        initializeSettings();

        //Creates the user list
        userList = new UserList();
        
        //Creates the punishment list
        punishmentList = new PunishmentList();
    	
        registerEvents();
        
        //Registers the commands for the plugin
    	registerCommands();
    }
    
    private void initializeSettings() {
		new Settings();
	}

	/**
     * Registers the event listeners
     */
    private void registerEvents() {
    	eventListeners = new EventListener(userList, punishmentList);
    	
        getServer().getPluginManager().registerEvents(eventListeners, this);
        getServer().getPluginManager().registerEvents(new CustomGUIListener(userList), this);
    }
    
    /**
     * Registers commands to be able to be used by users
     */
    private void registerCommands() {
        this.getCommand("punish").setExecutor(new PunishCommand(userList, punishmentList));
        this.getCommand("report").setExecutor(new ReportCommand(userList, punishmentList));
    }
    
	
}