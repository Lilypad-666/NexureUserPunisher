package com.star.NexureUserPunisher.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.star.NexureUserPunisher.Punishments.Punishment.PunishmentTypes;
import com.star.NexureUserPunisher.Users.User;

/**
 * Event that stores the data when a player is punished
 * @author Star
 *
 */
public class PunishReasonEvent extends Event {

	//The type of punishment
    private final PunishmentTypes punishmentType;
    
    //The user who was reported
    private final User reportedUser;
    
    //The user who did the report
    private final User reporterUser;
    
    //The length of the punishment
    private final long duration;

    /**
     * Constructor to set variables
     * @param punishmentType
     * @param reportedUser
     * @param reporterUser
     * @param duration
     */
    public PunishReasonEvent(PunishmentTypes punishmentType, User reportedUser, User reporterUser, long duration) {
    	this.punishmentType = punishmentType;
    	this.reportedUser = reportedUser;
    	this.reporterUser = reporterUser;
    	this.duration = duration;
    }
    
    /**
     * Constructor for punishments with no duration (reports, warns, kicks)
     * @param punishmentType
     * @param reportedUser
     * @param commandSender
     */
    public PunishReasonEvent(PunishmentTypes punishmentType, User reportedUser, User commandSender) {
    	this(punishmentType, reportedUser, commandSender, 0);
    }

    /**
     * Required for extending Event
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * Required for extending Event
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Required for extending Event
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
    /**
     * Returns the type of punishment
     * @return
     */
    public PunishmentTypes getPunishmentType() {
    	return punishmentType;
    }
    
    /**
     * Returns the user who was reported
     * @return
     */
    public User getReportedUser() {
    	return reportedUser;
    }
    
    /**
     * Returns the user who did the reporting
     * @return
     */
    public User getReporterUser() {
    	return reporterUser;
    }
    
    /**
     * Gets the duration of the punishment
     * @return
     */
    public long getDuration() {
    	return duration;
    }
}
