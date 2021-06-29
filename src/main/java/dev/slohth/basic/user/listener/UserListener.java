package dev.slohth.basic.user.listener;

import dev.slohth.basic.Basic;
import dev.slohth.basic.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserListener implements Listener {

    private final Basic core;

    /**
     * Class constructor - registers the listeners for registering users
     * @param core the main instance of the plugin
     */
    public UserListener(Basic core) {
        this.core = core; Bukkit.getPluginManager().registerEvents(this, core);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!core.getUserManager().register(e.getPlayer().getUniqueId())) {
            e.getPlayer().kickPlayer(CC.trns("&cFailed to authenticate user"));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (core.getUserManager().getUser(e.getPlayer().getUniqueId()) != null) {
            core.getUserManager().unregister(core.getUserManager().getUser(e.getPlayer().getUniqueId()));
        }
    }

}
