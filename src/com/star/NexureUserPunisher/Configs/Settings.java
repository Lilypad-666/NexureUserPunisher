package com.star.NexureUserPunisher.Configs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import net.md_5.bungee.api.ChatColor;

/**
 * The primary settings for this plugin
 * You can add more fields here, and the CustomConfig handles getting them to the document
 * @author Star
 *
 */
public class Settings extends CustomConfig<Settings> {

	/**
	 * The types of storage
	 * @author Star
	 *
	 */
	public enum StorageTypes {
		Database,
		FlatFile
	}
	
	public static PluginConfig Plugin = new PluginConfig();
	public static class PluginConfig extends ConfigCategory {
		public String Prefix = "Punisher";
		public ChatColor PrimaryColor = ChatColor.AQUA;
	}
	
	public static StaffConfig Staff = new StaffConfig();
	public static class StaffConfig extends ConfigCategory {
		public List<UUID> Moderators = new ArrayList<>();
		public List<UUID> Admin = new ArrayList<>();
	}
	
	public static StorageConfig Storage = new StorageConfig();
	public static class StorageConfig extends ConfigCategory {
		public StorageTypes StorageType = StorageTypes.FlatFile;
		public String UsersStorageName = "users";
		public String PunishmentsStorageName = "punishments";
		
		public DatabaseConfig Database = new DatabaseConfig();
		public class DatabaseConfig extends ConfigCategory {
			public String DatabaseHost = "localhost";
			public String DatabasePort = "3306";
			public String DatabaseName = "nexure_user_punisher";
			public String DatabaseUsername = "";
			public String DatabasePassword = "";
		}
	}
	
	public Settings() {
		super("settings.yml");
		
		//These handle converting colors to/from String/ChatColor
		Function<ChatColor, String> ChatColorToString = (color) -> {
			return color.getName();
		};
		Function<String, ChatColor> StringToChatColor = (string) -> {
			return ChatColor.of(string);
		};
		
		//This applies the functions
		updateObjConversion(new ConfigSettingConversion<ChatColor, String>(ChatColorToString, StringToChatColor, "Settings.Plugin.PrimaryColor"));
		
		//These handle making uuid list file safe
		Function<ArrayList<UUID>, ArrayList<String>> UUIDListToStringList = (uuidList) -> {
			ArrayList<String> stringList = new ArrayList<>();
			
			for (UUID uuid : uuidList) {
				stringList.add(uuid.toString());
			}
			
			return stringList;
		};
		Function<ArrayList<String>, ArrayList<UUID>> StringListToUUIDList = (stringList) -> {
			ArrayList<UUID> uuidList = new ArrayList<>();
			
			for (String uuid : stringList) {
				uuidList.add(UUID.fromString(uuid));
			}
			
			return uuidList;
		};
		
		//This applies the functions
		updateObjConversion(new ConfigSettingConversion<ArrayList<UUID>, ArrayList<String>>(UUIDListToStringList, StringListToUUIDList, "Settings.Staff.Moderators", "Settings.Staff.Admin"));
		
		//Handles converting to/from StorageType
		Function<StorageTypes, String> StorageTypeToString = (storageType) -> {
			return storageType.toString();
		};
		Function<String, StorageTypes> StringToStorageType = (string) -> {
			StorageTypes type = StorageTypes.FlatFile;
			
			try {
				type = StorageTypes.valueOf(string);
			} catch(IllegalArgumentException e) {
				System.out.println("ERROR! StorageType in settings.yml must be 'Database' or 'FlatFile'! Defaulting to FlatFile!");
			}
			
			return type;
		};
		
		//This applies the functions
		updateObjConversion(new ConfigSettingConversion<StorageTypes, String>(StorageTypeToString, StringToStorageType, "Settings.Storage.StorageType"));
		
		//Initilaizes the config
		initialize(this);
		
		//Saves the config to file, in case there isn't one
		saveConfig();
	}
	
}
