package com.star.NexureUserPunisher.Configs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * The primary purpose of this class is for handling objects where they don't neatly convert to/from strings, and you have to do it yourself
 * @author Star
 *
 * @param <O> Object type
 * @param <F> File safe type
 */
public class ConfigSettingConversion<O, F> {
	//The path in the settings, including the name of the setting
	private List<String> settingsPaths;
	
	//The functions to convert the object to/from a string
	private Function<O, F> ObjectToFile;
	private Function<F, O> FileToObject;

	/**
	 * Constructor that takes the paths, and new functions for ObjectToString and StringToObject
	 * @param ObjectToString
	 * @param StringToObject
	 * @param settingsPaths
	 */
	public ConfigSettingConversion(Function<O, F> ObjectToFile, Function<F, O> FileToObject, String... settingsPaths) {
		this.settingsPaths = Arrays.asList(settingsPaths);
		this.ObjectToFile = ObjectToFile;
		this.FileToObject = FileToObject;
	}
	
	/**
	 * Constructor that takes a single path, and new functions for ObjectToString and StringToObject
	 * @param ObjectToString
	 * @param StringToObject
	 * @param settingsPath
	 */
	public ConfigSettingConversion(Function<O, F> ObjectToFile, Function<F, O> FileToObject, String settingsPath) {
		this.settingsPaths = new ArrayList<String>(Arrays.asList(settingsPath));
		this.ObjectToFile = ObjectToFile;
		this.FileToObject = FileToObject;
	}

	/**
	 * Converts the parameter object into generic type T, then converts that to a file safe format
	 * I am only using this function in situations where I am positive the cast will be successful
	 * @param obj
	 * @return
	 */
	public F getFileSafe(Object obj) {
		return ObjectToFile.apply((O)obj);
	}
	
	/**
	 * Converts the file safe to the object
	 * @param object
	 * @return
	 */
	public O setObject(Object object) {
		return FileToObject.apply((F)object);
	}

	/**
	 * Gets the settings paths
	 * @return
	 */
	public List<String> getSettingsPaths() {
		return settingsPaths;
	}
	
}
