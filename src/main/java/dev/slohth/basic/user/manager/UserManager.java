package dev.slohth.basic.user.manager;

import dev.slohth.basic.Basic;
import dev.slohth.basic.user.User;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserManager {

    private final Basic core;
    private final Set<User> registeredUsers = new HashSet<>();

    /**
     * Class constructor
     * @param core the main instance of the plugin
     */
    public UserManager(Basic core) { this.core = core; }

    /**
     * Registers a user and attempts to load its data
     * @param uuid the uuid of the player
     * @return whether a new user was successfully registered
     */
    public boolean register(UUID uuid) {
        if (this.getUser(uuid) != null) return false;
        try {
            User user = new User(this.core, uuid);
            user.load(); this.registeredUsers.add(user);
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    /**
     * Attempts to save a users data and then remove the user from memory
     * @param user the user to unregister
     * @return whether the user was successfully unregistered
     */
    public boolean unregister(User user) {
        try {
            user.save(); this.registeredUsers.remove(user);
            return true;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Gets a user object from a given uuid if it exists
     * @param uuid the uuid of the player
     * @return the User object if found, else null
     */
    public User getUser(UUID uuid) {
        for (User u : registeredUsers) if (u.getUuid() == uuid) return u;
        return null;
    }

}
