package com.star.NexureUserPunisher.Punishments;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.star.NexureUserPunisher.Main;
import com.star.NexureUserPunisher.Configs.Settings;
import com.star.NexureUserPunisher.DataSources.DataSource;
import com.star.NexureUserPunisher.DataSources.Database;
import com.star.NexureUserPunisher.DataSources.FlatFile;
import com.star.NexureUserPunisher.Punishments.Punishment.PunishmentTypes;
import com.star.NexureUserPunisher.Users.User;

/**
 * Handles the calls/reads for the punishment data
 * 
 * @author Star
 *
 */
public abstract class PunishmentListGateway {

	// The data source to get data from flatfile/database
	private DataSource dataSource;

	private final Map<String, String> dataHeaders = Map.of("Id", "INTEGER NOT NULL UNIQUE PRIMARY KEY", "Type",
			"VARCHAR(255)", "Reported_User", "VARCHAR(255)", "Report_By_User", "VARCHAR(255)", "Start_Time",
			"VARCHAR(255)", "End_Time", "VARCHAR(255)", "Reason", "VARCHAR(255)");

	/**
	 * Sets the datasource
	 * 
	 * @param main
	 */
	public PunishmentListGateway() {

		if (Settings.Storage.StorageType == Settings.StorageTypes.FlatFile) {
			Path punishmentsFilePath = Paths
					.get(Main.getPlugin().getDataFolder().getPath(), Settings.Storage.PunishmentsStorageName + ".txt")
					.toAbsolutePath();
			dataSource = new FlatFile(punishmentsFilePath, "%-6s %-8s %-38s %-38s %-26s %-26s %-6s", dataHeaders);
		}

		if (Settings.Storage.StorageType == Settings.StorageTypes.Database) {
			dataSource = new Database(Settings.Storage.PunishmentsStorageName, dataHeaders);
		}
	}

	protected void savePunishment(Punishment punishment) {
		dataSource.addRow(punishmentToMap(punishment));
	}

	private Map<String, String> punishmentToMap(Punishment punishment) {
		Map<String, String> dataMap = new HashMap<>();

		dataMap.put("Id", String.valueOf(punishment.getPunishmentId()));
		dataMap.put("Type", String.valueOf(punishment.getPunishmentType().toString()));
		dataMap.put("Reported_User", String.valueOf(punishment.getPunishedUUID().toString()));
		dataMap.put("Report_By_User", String.valueOf(punishment.getPunisherUUID().toString()));
		dataMap.put("Start_Time", String.valueOf(punishment.getStartDateTimeFormatted()));
		dataMap.put("End_Time", String.valueOf(punishment.getEndDateTimeFormatted()));
		dataMap.put("Reason", String.valueOf(punishment.getReason()));

		return dataMap;
	}

	public List<Punishment> getUsersPunishments(UUID uuid) {
		List<Punishment> punishmentList = new ArrayList<>();

		List<Map<String, String>> punishmentMapList = dataSource.getRows("Reported User", uuid.toString());

		for (Map<String, String> punishmentMap : punishmentMapList) {
			int punishmentId = Integer.parseInt(punishmentMap.get("Id"));
			PunishmentTypes punishmentType = PunishmentTypes.valueOf(punishmentMap.get("Type"));
			UUID reportedUser = UUID.fromString(punishmentMap.get("Reported_User"));
			UUID reporteeUser = UUID.fromString(punishmentMap.get("Report_By_User"));

			// Formatter to turn the string date format into a localdatetime object
			DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

			LocalDateTime startDateTime = LocalDateTime.parse(punishmentMap.get("Start_Time"), formatter);
			LocalDateTime endDateTime = LocalDateTime.parse(punishmentMap.get("End_Time"), formatter);

			long duration = Duration.between(startDateTime, endDateTime).toSeconds();

			String reason = "";

			if (punishmentMap.containsKey("Reason")) {
				reason = punishmentMap.get("Reason");
			}

			Punishment punishment = new Punishment(punishmentId, punishmentType, reportedUser, reporteeUser,
					startDateTime, duration, reason);
		}

		return punishmentList;
	}

	/**
	 * Gets the highest punishment Id
	 */
	protected int getHighestId() {
		String highestId = dataSource.getMax("Id");

		try {
			return Integer.parseInt(highestId);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

}
