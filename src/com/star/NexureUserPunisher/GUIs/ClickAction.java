package com.star.NexureUserPunisher.GUIs;

import com.star.NexureUserPunisher.Users.User;

/**
 * Very simple interface to handle GUI clicks
 * @author Star
 *
 */
public interface ClickAction {
	/**
	 * Executes the action associated with the Item
	 * @param user
	 */
	 void execute(User user);
}