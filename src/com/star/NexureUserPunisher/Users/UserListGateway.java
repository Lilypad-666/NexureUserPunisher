package com.star.NexureUserPunisher.Users;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.star.NexureUserPunisher.Main;
import com.star.NexureUserPunisher.Configs.Settings;
import com.star.NexureUserPunisher.DataSources.DataSource;
import com.star.NexureUserPunisher.DataSources.Database;
import com.star.NexureUserPunisher.DataSources.FlatFile;

/**
 * Handles the calls/reads for the users data
 * 
 * @author Star
 *
 */
public abstract class UserListGateway {

	// The data source to get data from flatfile/database
	private DataSource dataSource;

	private final Map<String, String> dataHeaders = Map.of("UUID", "VARCHAR(255) NOT NULL UNIQUE PRIMARY KEY",
			"Username", "VARCHAR(255)", "Join_Date", "VARCHAR(255)");

	/**
	 * Sets the datasource
	 * 
	 * @param main
	 */
	public UserListGateway() {
		if (Settings.Storage.StorageType == Settings.StorageTypes.FlatFile) {
			Path userFilePath = Paths
					.get(Main.getPlugin().getDataFolder().getPath(), Settings.Storage.UsersStorageName + ".txt")
					.toAbsolutePath();
			dataSource = new FlatFile(userFilePath, "%-40s %-20s %-15s", dataHeaders);
		}

		if (Settings.Storage.StorageType == Settings.StorageTypes.Database) {
			dataSource = new Database(Settings.Storage.UsersStorageName, dataHeaders);
		}
	}

	protected User findUser(UUID uuid) {
		Map<String, String> data = dataSource.getRow("UUID", uuid.toString());

		if (data.isEmpty()) {
			return null;
		}

		String username = data.get("Username");

		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
		LocalDate joinDate = LocalDate.parse(data.get("Join_Date"), formatter);

		User user = new User(uuid, username, joinDate);

		return user;
	}

	protected User findUser(String username) {
		Map<String, String> data = dataSource.getRow("Username", username);

		if (data.isEmpty()) {
			return null;
		}

		UUID uuid = UUID.fromString(data.get("UUID"));

		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
		LocalDate joinDate = LocalDate.parse(data.get("Join_Date"), formatter);

		User user = new User(uuid, username, joinDate);

		return user;
	}

	protected User createUser(UUID uuid) {
		org.bukkit.entity.Player player = Bukkit.getPlayer(uuid);

		if (player == null) {
			// No player found online
			return null;
		}

		String username = player.getName();

		User user = new User(uuid, username);

		saveUser(user);

		// Return a newly created user
		return user;
	}

	private void saveUser(User user) {
		dataSource.addRow(userToMap(user));
	}

	private Map<String, String> userToMap(User user) {
		Map<String, String> dataMap = new HashMap<>();

		dataMap.put("UUID", user.getUUID().toString());
		dataMap.put("Username", user.getUsername());
		dataMap.put("Join_Date", user.getJoinDateFormatted());

		return dataMap;
	}
}