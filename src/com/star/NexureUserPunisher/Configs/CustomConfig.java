package com.star.NexureUserPunisher.Configs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.star.NexureUserPunisher.Main;

/**
 * The base of any config files. It takes the config's fields from their files and converts to and from yml
 * @author Star
 *
 * @param <T>
 */
public abstract class CustomConfig<T extends CustomConfig<T>> {
	
	//The class that is extending this one
	private T configClass;
	
	//The name of the config file
	private String fileName;
	private File configFile;
	private FileConfiguration config;
	
	//List of config settings that need unique conversions
	private Map<String, ConfigSettingConversion<?, ?>> settingConversions;
	
	/**
	 * Constructor, just sets the file name
	 * @param fileName
	 */
	public CustomConfig(String fileName) {
		this.fileName = fileName;
		
		settingConversions = new HashMap<>();
	}
	
	/**
	 * Initializes the config, gives access to the child class
	 * @param configClass
	 */
	public void initialize(T configClass) {
		this.configClass = configClass;
		
		//Verifies that the file exists
		verifyFileExists();
		
		//Tries to load the config data from file
        config = new YamlConfiguration();
        try {
        	config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace(); 
        }
        
        //Takes all the config values from file, and applies them to the variables
        configToValues();
	}

	/**
	 * If the config file doesn't exist, it makes it
	 */
	private void verifyFileExists() {
		//Grab the config file
		configFile = new File(Main.getPlugin().getDataFolder(), fileName);
		
		//If it doesn't exist, create it
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			Main.getPlugin().saveResource(fileName, false);
         }
		
		
	}
	
	/**
	 * Recursive function to find all variables in a given class (including those in variables' sub-classes).
	 * @param <K> Essentially any class, although it won't work on sub classes that don't extend ConfigCategory
	 * @param category The class to start looking at
	 * @param path The current settings path
	 */
	private <K> void configToValues(K category, String path) {
		try {
			
			//Get a list of all the variables in the given class
			for (Field field : category.getClass().getFields()) {
				
				//Make sure that we can access the field
				field.setAccessible(true);

				//Get the value
				Object defaultValue = field.get(category);
				
				//update the path
				String newPath = path + "." + field.getName();
				
				//Check if the variable extends ConfigCategory (all the sub-classes do)
				if (defaultValue instanceof ConfigCategory) {
					
					//If it does, call recursively
					configToValues((K)defaultValue, newPath);
				} else {
					
					//If not, it's a config value
					//Check to see if the config file we loaded has it
					if (config.contains(newPath)) {
						
						//It's in the config, so now we have to load it
						//Check if we have a special conversion for this value
						if (settingConversions.containsKey(newPath)) {
							
							//Get the correct conversions
							ConfigSettingConversion<?, ?> configSetting = settingConversions.get(newPath);
							
							//Convert the string into an object
							Object convertedValue = configSetting.setObject(config.get(newPath));
							
							//Set the config's variable to be the converted object
							field.set(category, convertedValue);
							
							continue;
						} else {
							//We have no special conversions, figure out the type
							
							//Handles lists of values
							if (config.get(newPath).getClass().isAssignableFrom(ArrayList.class)) {
								field.set(category, config.getList(newPath));
							}
							
							else {
								//Just a string
								field.set(category, config.getString(newPath));
							}
						}
					}
					
					//The config file does not have an entry, so we want to set the default value
					else {
						
						//Check if we have a special conversion
						if (settingConversions.containsKey(newPath)) {
							
							//Get the conversions
							ConfigSettingConversion<?, ?> configSetting = settingConversions.get(newPath);
							
							//Convert the object to a string for the config
							Object convertedValue = configSetting.getFileSafe(defaultValue);
							
							//Set the config's value to the converted value
							config.set(newPath, convertedValue);
							
							continue;
						} else {
							
							//No special conversions, set the config's variable
							
							config.set(newPath, defaultValue);
						}
					}
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Calls the overload of configToValues to start up the recursion
	 * @param <K>
	 */
	private <K> void configToValues() {
		configToValues(configClass, configClass.getClass().getSimpleName());
	}

	/**
	 * Saves the covfig to file
	 */
	protected void saveConfig() {
		try {
			config.save(Main.getPlugin().getDataFolder().getPath() + "\\" + fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds an entry for a custom conversion
	 * @param <K>
	 * @param configSetting
	 */
	protected <O, F> void updateObjConversion(ConfigSettingConversion<O, F> configSetting) {
		for (String settingsPath : configSetting.getSettingsPaths()) {
			settingConversions.put(settingsPath, configSetting);
		}
	}
}
