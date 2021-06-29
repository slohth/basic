package dev.slohth.basic;

import dev.slohth.basic.rank.manager.RankManager;
import dev.slohth.basic.user.listener.UserListener;
import dev.slohth.basic.user.manager.UserManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Basic extends JavaPlugin {

    private RankManager rankManager;
    private UserManager userManager;

    /**
     * @author Slohth
     */
    @Override
    public void onEnable() {
        this.rankManager = new RankManager(this);
        this.userManager = new UserManager(this);
        new UserListener(this);
    }

    public RankManager getRankManager() { return this.rankManager; }
    public UserManager getUserManager() { return this.userManager; }

}
