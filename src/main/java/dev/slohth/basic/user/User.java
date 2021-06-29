package dev.slohth.basic.user;

import com.mojang.authlib.GameProfile;
import dev.slohth.basic.Basic;
import dev.slohth.basic.rank.Rank;
import dev.slohth.basic.utils.CC;
import dev.slohth.basic.utils.framework.Config;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

public class User {

    private final Basic core;

    private final UUID uuid;
    private final GameProfile profile;

    private final Set<Rank> ranks = new HashSet<>();
    private final Map<String, Boolean> permissions = new HashMap<>();

    private PermissionAttachment attachment;

    /**
     * Class constructor - creates a new user object
     * @param core the main instance of the plugin
     * @param uuid the uuid of the player to register
     */
    public User(Basic core, UUID uuid) {
        this.core = core; this.uuid = uuid; this.profile = ((CraftPlayer) Bukkit.getPlayer(uuid)).getProfile();
    }

    /**
     * Attempts to load the data associated with this user from config
     * @throws NullPointerException
     */
    public void load() throws NullPointerException {
        if (Config.USERS.getConfig().getConfigurationSection("").getKeys(false).contains(this.uuid.toString())) {
            for (String rank : Config.USERS.getStringList(this.uuid.toString() + ".ranks")) {
                if (core.getRankManager().getRank(rank) != null) this.ranks.add(core.getRankManager().getRank(rank));
            }
            for (String perm : Config.USERS.getStringList(this.uuid.toString() + ".permissions")) {
                boolean value = !perm.startsWith("-"); String s = value ? perm : perm.substring(1);
                this.permissions.put(s, value);
            }
        } else {
            this.ranks.add(core.getRankManager().getDefault());
        }
        this.recalculatePermissions();
    }

    public void save() {

    }

    /**
     * Calculates and applies the permissions from ranks and user
     */
    public void recalculatePermissions() {
        if (this.attachment != null) this.attachment.remove();
        this.attachment = this.getPlayer().addAttachment(this.core);

        Map<String, Boolean> rankPermissions = new HashMap<>();
        for (Rank rank : this.ranks) this.calculateRankPermissions(rankPermissions, rank);

        for (Map.Entry<String, Boolean> perm : rankPermissions.entrySet()) {
            if (perm.getKey().equals("*")) {
                for (Permission permissions : Bukkit.getPluginManager().getPermissions()) {
                    attachment.setPermission(permissions.getName(), true);
                }
            } else { attachment.setPermission(perm.getKey(), perm.getValue()); }
        }

        for (Map.Entry<String, Boolean> perm : this.permissions.entrySet()) {
            if (perm.getKey().equals("*")) {
                for (Permission permissions : Bukkit.getPluginManager().getPermissions()) {
                    attachment.setPermission(permissions.getName(), true);
                }
            } else { attachment.setPermission(perm.getKey(), perm.getValue()); }
        }
    }

    /**
     * Recursive method to calculate permissions including inherits
     * @param from the permissions from the previous recursion
     * @param rank the rank to check
     * @return the updated permissions from the current recursion
     */
    private Map<String, Boolean> calculateRankPermissions(Map<String, Boolean> from, Rank rank) {
        for (Map.Entry<String, Boolean> permission : rank.getPermissions().entrySet()) {
            from.put(permission.getKey(), permission.getValue());
        }
        for (Rank inherit : rank.getInherits()) calculateRankPermissions(from, inherit);
        return from;
    }

    /**
     * Sends the message(s) to the player, coloured automatically
     * Each separate message will be sent on a different line
     * @param messages the message(s) to send
     */
    public void sendMessage(String... messages) {
        for (String m : messages) this.getPlayer().sendMessage(CC.trns(m));
    }

    /**
     * Returns the bukkit player object associated with this user
     * @return the player object
     */
    public Player getPlayer() { return Bukkit.getPlayer(this.uuid); }

    public UUID getUuid() { return this.uuid; }

}
