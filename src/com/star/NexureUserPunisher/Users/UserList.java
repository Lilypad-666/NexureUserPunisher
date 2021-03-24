package com.star.NexureUserPunisher.Users;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserList extends UserListGateway {

	// The list of users, saves datasource calls
	private List<User> users;

	public UserList() {
		super();

		users = new ArrayList<>();
	}

	public User getUser(UUID uuid) {
		for (User user : users) {
			if (user.getUUID().equals(uuid)) {
				return user;
			}
		}

		// Try to locate the user in the datasource
		User user = findUser(uuid);

		// The user can't be found, so create a new user
		if (user == null) {
			user = createUser(uuid);

			// Something went wrong here, the player is no longer online
			if (user == null) {
				return null;
			}
		}

		users.add(user);

		return user;
	}

	public User getUser(org.bukkit.entity.Player bukkitPlayer) {
		return getUser(bukkitPlayer.getUniqueId());
	}

	public User getUserByUsername(String username) {
		for (User user : users) {
			if (user.getUsername().toUpperCase().equals(username.toUpperCase())) {
				return user;
			}
		}

		// Try to locate the user in the datasource
		User user = findUser(username);

		// The user can't be found, return null
		if (user == null) {
			return null;
		}

		users.add(user);

		return user;
	}
}