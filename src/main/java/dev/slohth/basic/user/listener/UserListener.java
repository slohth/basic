package dev.slohth.basic.user.listener;

import dev.slohth.basic.Basic;
import dev.slohth.basic.rank.Rank;
import dev.slohth.basic.user.User;
import dev.slohth.basic.utils.CC;
import dev.slohth.basic.utils.ColoredString;
import dev.slohth.basic.utils.TextComponentBuilder;
import dev.slohth.basic.utils.framework.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
        e.getPlayer().setScoreboard(core.getRankManager().getScoreboard());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        User user = core.getUserManager().getUser(e.getPlayer().getUniqueId());
        Rank r = user.getDisplay();

        String format = Config.MESSAGES.getString("chat.groups." + r.getName()) != null ?
                Config.MESSAGES.getString("chat.groups." + r.getName()) : Config.MESSAGES.getString("chat.default");

        TextComponentBuilder name = new TextComponentBuilder(r.getTeam().getColor() + e.getPlayer().getName());
        if (Config.MESSAGES.getStringList("chat.hover") != null && !Config.MESSAGES.getStringList("chat.hover").isEmpty()) {
            List<String> toHover = new ArrayList<>();
            for (String s : Config.MESSAGES.getStringList("chat.hover")) {
                toHover.add(CC.trns(s.replace("%name%", e.getPlayer().getName()).replace("%rank_name%", r.getDisplay())));
            }
            name.hover(HoverEvent.Action.SHOW_TEXT, toHover.toArray(new String[0]));
        }
        TextComponent hover = name.build();

        format = format.replace("%rank_prefix%", r.getPrefix());
        format = format.replace("%message%", ChatColor.stripColor(e.getMessage()));
        format = CC.trns(format);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.spigot().sendMessage(
                    new TextComponentBuilder(format.split("%name%")[0]).build(),
                    hover, new TextComponentBuilder(format.split("%name%")[1]).build()
            );
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (core.getUserManager().getUser(e.getPlayer().getUniqueId()) != null) {
            core.getUserManager().unregister(core.getUserManager().getUser(e.getPlayer().getUniqueId()));
        }
    }

}
