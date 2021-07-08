package dev.slohth.basic;

import dev.slohth.basic.rank.manager.RankManager;
import dev.slohth.basic.test.TestCommand;
import dev.slohth.basic.user.listener.UserListener;
import dev.slohth.basic.user.manager.UserManager;
import dev.slohth.basic.utils.CC;
import dev.slohth.basic.utils.framework.command.Framework;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Slohth
 */
public final class Basic extends JavaPlugin implements Listener {

    private Framework framework;

    private RankManager rankManager;
    private UserManager userManager;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        this.framework = new Framework(this);
        this.rankManager = new RankManager(this);
        this.userManager = new UserManager(this);
        new UserListener(this);

        new TestCommand(this);
    }

    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().equalsIgnoreCase("/about basic")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(CC.trns("&bThis server is running &3BASIC &bversion &3DEV-1.0"));
            e.getPlayer().sendMessage(CC.trns("&bDeveloped and maintained by &3Slohth"));
        }
    }

    public Framework getFramework() { return this.framework; }
    public RankManager getRankManager() { return this.rankManager; }
    public UserManager getUserManager() { return this.userManager; }

}
