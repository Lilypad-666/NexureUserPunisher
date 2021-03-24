package com.star.NexureUserPunisher.Punishments;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Punishments for the users
 * @author Star
 *
 */
public class Punishment implements Comparable<Punishment> {
	
	private static final AtomicInteger currentId = new AtomicInteger();
	
	//The list of different punishment types
	public enum PunishmentTypes {
		Report,
		Warn,
		Mute,
		Kick,
		Ban
	}
	
	//Keeps track of which punishment type this is
	private PunishmentTypes punishmentType;
	
	//Keeps track of the punishment ID
	private int punishmentId;
	
	//The UUIDs of both the player who got punished, and who did the punishing
	private UUID punishedUserUUID;
	private UUID punisherUserUUID;
	
	//The reason for the punishment
	private String reason;
	
	//The start and (if applicable) end of the punishment
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;

	/**
	 * Creates an existing punishment
	 * @param punishmentId
	 * @param punishmentType
	 * @param punishedUserUUID
	 * @param punisherUserUUID
	 * @param startDateTime
	 * @param endDateTime
	 * @param reason
	 */
	public Punishment(int punishmentId, PunishmentTypes punishmentType, UUID punishedUserUUID, UUID punisherUserUUID, LocalDateTime startDateTime, long durationInSeconds, String reason) {
		this.punishmentId = punishmentId;
		this.punishmentType = punishmentType;

		this.punishedUserUUID = punishedUserUUID;
		this.punisherUserUUID = punisherUserUUID;
		
		this.startDateTime = startDateTime;
		this.endDateTime = startDateTime.plusSeconds(durationInSeconds);
		
		this.reason = reason;
	}
	
	/**
	 * Creates an existing punishment that has no duration
	 * @param punishmentId
	 * @param punishmentType
	 * @param punishedUserUUID
	 * @param punisherUserUUID
	 * @param startDateTime
	 * @param endDateTime
	 * @param reason
	 */
	public Punishment(int punishmentId, PunishmentTypes punishmentType, UUID punishedUserUUID, UUID punisherUserUUID, LocalDateTime startDateTime, String reason) {
		this(punishmentId, punishmentType, punishedUserUUID, punisherUserUUID, startDateTime, 0, reason);
	}
	
	/**
	 * Crates a new punishment
	 * Auto-increments the id
	 * @param punishmentType
	 * @param punishedUserUUID
	 * @param punisherUserUUID
	 * @param startDateTime
	 * @param endDateTime
	 * @param reason
	 */
	public Punishment(PunishmentTypes punishmentType, UUID punishedUserUUID, UUID punisherUserUUID, long durationInSeconds, String reason) {
		this(currentId.incrementAndGet(), punishmentType, punishedUserUUID, punisherUserUUID, LocalDateTime.now().withNano(0), durationInSeconds, reason);
	}
	
	/**
	 * Crates a new punishment that has no duration
	 * Auto-increments the id
	 * @param punishmentType
	 * @param punishedUserUUID
	 * @param punisherUserUUID
	 * @param startDateTime
	 * @param endDateTime
	 * @param reason
	 */
	public Punishment(PunishmentTypes punishmentType, UUID punishedUserUUID, UUID punisherUserUUID, String reason) {
		this(currentId.incrementAndGet(), punishmentType, punishedUserUUID, punisherUserUUID, LocalDateTime.now().withNano(0), 0, reason);
	}
	
	/**
	 * Gets the punishment type
	 * @return
	 */
	public PunishmentTypes getPunishmentType() {
		return punishmentType;
	}
	
	/**
	 * Gets the user who was punished's UUID
	 * @return
	 */
	public UUID getPunishedUUID() {
		return punishedUserUUID;
	}
	
	/**
	 * Gets the player who did the punishing's UUID
	 * @return
	 */
	public UUID getPunisherUUID() {
		return punisherUserUUID;
	}
	
	
	/**
	 * Gets the reason for the punishment
	 * @return
	 */
	public String getReason() {
		return reason;
	}
	
	/**
	 * Gets when the punishment was started
	 * @return
	 */
	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}
	
	/**
	 * Gets the end time, if there is one (if not, it'll be the same as the start time)
	 * @return
	 */
	public LocalDateTime getEndDateTime() {
		return endDateTime;
	}
	
	/**
	 * Gets the start time, formatted into a string
	 * @return
	 */
	public String getStartDateTimeFormatted() {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		return startDateTime.format(formatter);
	}

	/**
	 * Gets the end time, formatted into a string
	 * @return
	 */
	public String getEndDateTimeFormatted() {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		return endDateTime.format(formatter);
	}
	
	/**
	 * Helper class, formats the time into segments
	 * @return
	 */
	private String getRemainingTime() {
	    long dayInMS = 86400000L;
	    long hourInMS = 3600000L;
	    long minuteInMS = 60000L;
		
		long totalMS = ChronoUnit.MILLIS.between(startDateTime, endDateTime);
		
		long days = TimeUnit.MILLISECONDS.toDays(totalMS);
        totalMS -= dayInMS * days;
        
        long hours = TimeUnit.MILLISECONDS.toHours(totalMS);
        totalMS -= hourInMS * hours;
        
        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalMS);
        totalMS -= minuteInMS * minutes;
        
        long seconds = TimeUnit.MILLISECONDS.toSeconds(totalMS);
        
        String output = "";
        
        if (days > 0) {
        	return days + " days " + hours + " hours " + minutes + " minutes " + seconds + " seconds";
        }
        
        if (hours > 0) {
        	return hours + " hours " + minutes + " minutes " + seconds + " seconds";
        }
        
        if (minutes > 0) {
        	return minutes + " minutes " + seconds + " seconds";
        }
        
        if (seconds > 0) {
        	return seconds + " seconds";
        }
        
        return "Finished";
		
	}
	
	/**
	 * Gets the ID of the punishment
	 * @return
	 */
	public int getPunishmentId() {
		return punishmentId;
	}
	
	/**
	 * Gets the data in a flat file format
	 * @return
	 */
	public String dataToString() {
		
		// ReportID PunishmentType ReportedUserUUID MadeReportUserUUID Reason StartTime EndTime
		return String.format("%-6s %-8s %-38s %-38s %-26s %-26s", punishmentId, punishmentType, punishedUserUUID, punisherUserUUID, formatDateTime(startDateTime), formatDateTime(endDateTime)) + " " + reason;
	}
	
	/**
	 * Formats the given DateTime
	 * @param dateTime
	 * @return
	 */
	private String formatDateTime(LocalDateTime dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		return dateTime.format(formatter);
	}

	/**
	 * Allows sorting of the Punishments, in PunishmentsDatabase.java
	 */
	@Override
	public int compareTo(Punishment i) {
	  return getStartDateTime().compareTo(i.getStartDateTime());
	}

	public static void setIdSequence(int id) {
		currentId.set(id);
	}
	
}
