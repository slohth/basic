package dev.slohth.basic.user;

import com.mojang.authlib.GameProfile;
import dev.slohth.basic.Basic;
import dev.slohth.basic.rank.Rank;
import dev.slohth.basic.utils.CC;
import dev.slohth.basic.utils.framework.Config;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_16_R3.Particles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class User {

    private final Basic core;

    private final UUID uuid;
    private final GameProfile profile;

    private final Set<Rank> ranks = new HashSet<>();
    private final Map<String, Boolean> permissions = new HashMap<>();

    private PermissionAttachment attachment;
    private Rank display;

    private BukkitTask task = null;

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
     * @throws NullPointerException if any data required is missing
     */
    public void load() throws NullPointerException {
        if (Config.USERS.getConfig().getConfigurationSection("").getKeys(false).contains(this.uuid.toString())) {
            for (String rank : Config.USERS.getStringList(this.uuid.toString() + ".ranks")) {
                if (core.getRankManager().getRank(rank) != null) this.ranks.add(core.getRankManager().getRank(rank));
            }
            if (Config.USERS.getStringList(this.uuid.toString() + ".permissions") != null) {
                for (String perm : Config.USERS.getStringList(this.uuid.toString() + ".permissions")) {
                    boolean value = !perm.startsWith("-"); String s = value ? perm : perm.substring(1);
                    this.permissions.put(s, value);
                }
            }
            if (Config.USERS.getString(this.uuid.toString() + ".display") != null) {
                if (core.getRankManager().getRank(Config.USERS.getString(this.uuid.toString() + ".display")) != null) {
                    this.display = core.getRankManager().getRank(Config.USERS.getString(this.uuid.toString() + ".display"));
                }
            }
        } else {
            this.ranks.add(core.getRankManager().getDefault());
        }
        this.recalculatePermissions();
        if (this.display == null) this.display = this.getPrimaryRank();
        this.display.getTeam().addPlayer(this.getPlayer());

        this.getPlayer().setScoreboard(core.getRankManager().getScoreboard());

        this.task = Bukkit.getScheduler().runTaskTimer(core, () -> {
            if (!getPlayer().isFlying()) return;
            Location l = getPlayer().getLocation();

            for (double r = 0.25; r < 1.75; r += 0.5) {
                for (int i = 0; i < 360; i += 8) {
                    double radians = i * (Math.PI / 180);
                    double x = Math.sin(radians) * r, z = Math.cos(radians) * r;

                    for (Entity e : getPlayer().getNearbyEntities(60, 60 ,60)) {
                        if (e instanceof Player) {
                            ((CraftPlayer) e).getHandle().playerConnection.sendPacket(
                                    new PacketPlayOutWorldParticles(Particles.CLOUD, true,
                                            l.getX() + x, l.getY(), l.getZ() + z, 0, 0, 0, 0.05F, 1)
                            );
                        }
                    }
                }
            }

//            for (int i = 0; i < 35; i++) {
//                ((CraftPlayer) getPlayer()).getHandle().playerConnection.sendPacket(
//                        new PacketPlayOutWorldParticles(Particles.CLOUD, true,
//                                l.getX(), l.getY() - 0.25, l.getZ(), 0, 0, 0, 0.05F, 1)
//                );
//            }

        }, 2, 2);

    }

    public void save() {
        task.cancel();
        List<String> ranks = new ArrayList<>();
        List<String> permissions = new ArrayList<>();
        for (Rank rank : this.ranks) ranks.add(rank.getName());
        for (Map.Entry<String, Boolean> p : this.permissions.entrySet()) permissions.add(p.getValue() ? p.getKey() : "-" + p.getKey());
        Config.USERS.set(this.uuid.toString() + ".ranks", ranks.isEmpty() ? null : ranks);
        Config.USERS.set(this.uuid.toString() + ".permissions", permissions.isEmpty() ? null : permissions);
        Config.USERS.set(this.uuid.toString() + ".display", this.getDisplay().equals(this.getPrimaryRank()) ? null : this.getDisplay().getName());
        Config.USERS.saveConfig();
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
     * Gets the primary rank (top priority) of the user
     * @return the rank found
     */
    public Rank getPrimaryRank() {
        Rank rank = null;
        for (Rank r : this.ranks) {
            if (rank == null) rank = r;
            if (r.getPriority() < rank.getPriority()) rank = r;
        }
        return rank;
    }

    /**
     * Sets the user display in rank and chat to a specific rank, updates immediately
     * This does not effect any permissions of the user
     * @param rank the rank to display the playes as
     */
    public void setDisplay(Rank rank) {
        if (this.display.getTeam().getPlayers().contains(this.getPlayer())) this.display.getTeam().removePlayer(this.getPlayer());
        this.display = rank;
        this.display.getTeam().addPlayer(this.getPlayer());
    }

    /**
     * Returns the bukkit player object associated with this user
     * @return the player object
     */
    public Player getPlayer() { return Bukkit.getPlayer(this.uuid); }

    public UUID getUuid() { return this.uuid; }
    public Set<Rank> getRanks() { return this.ranks; }
    public Rank getDisplay() { return this.display; }

}
