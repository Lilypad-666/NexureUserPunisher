package com.star.NexureUserPunisher.Punishments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.star.NexureUserPunisher.Punishments.Punishment.PunishmentTypes;

/**
 * Keeps a list of all the punishments, handles creating new ones
 * 
 * @author Star
 *
 */
public class PunishmentList extends PunishmentListGateway {

	// The list of all punishments for all players
	private List<Punishment> punishments;

	public PunishmentList() {
		super();

		punishments = new ArrayList<>();

		// Gets the most recent punishment's ID
		Punishment.setIdSequence(getHighestId());
	}

	public void createPunishment(PunishmentTypes punishmentType, UUID reportedUserUUID, UUID madeReportUUID,
			long durationInSeconds, String reason) {
		Punishment punishment = new Punishment(punishmentType, reportedUserUUID, madeReportUUID, durationInSeconds,
				reason);

		addPunishment(punishment);

		savePunishment(punishment);
	}

	/**
	 * Creates a new punishment with a duration of 0
	 * 
	 * @param punishmentType
	 * @param reportedUserUUID
	 * @param madeReportUUID
	 * @param reason
	 */
	public void createPunishment(PunishmentTypes punishmentType, UUID reportedUserUUID, UUID madeReportUUID,
			String reason) {
		createPunishment(punishmentType, reportedUserUUID, madeReportUUID, 0, reason);
	}

	/**
	 * Adds a punishment to the list without punishing the player again, used when
	 * loading from file
	 */
	private void addPunishment(Punishment punishment) {
		int pos = Collections.binarySearch(punishments, punishment);
		if (pos < 0) {
			punishments.add(-pos - 1, punishment);
		}
	}

	public List<Punishment> getUsersPunishments(UUID uuid) {
		List<Punishment> usersPunishments = new ArrayList<>();

		for (Punishment punishment : punishments) {
			if (punishment.getPunishedUUID().equals(uuid)) {
				usersPunishments.add(punishment);
			}
		}

		return usersPunishments;
	}
}
