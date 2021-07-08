package dev.slohth.basic.test;

import dev.slohth.basic.Basic;
import dev.slohth.basic.rank.Rank;
import dev.slohth.basic.user.User;
import dev.slohth.basic.utils.CC;
import dev.slohth.basic.utils.ColoredString;
import dev.slohth.basic.utils.TextComponentBuilder;
import dev.slohth.basic.utils.framework.Config;
import dev.slohth.basic.utils.framework.command.Args;
import dev.slohth.basic.utils.framework.command.Command;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TestCommand {

    private final Basic core;

    public TestCommand(Basic core) {
        this.core = core; core.getFramework().registerCommands(this);
    }

    @Command(name = "fly", permission = "basic.fly", inGameOnly = true)
    public void flyCommand(Args args) {
        args.getPlayer().setAllowFlight(!args.getPlayer().getAllowFlight());
        args.getPlayer().sendMessage(CC.trns("&bFlight has been &3" + (args.getPlayer().getAllowFlight() ? "enabled" : "disabled")));
    }

    @Command(name = "profile", permission = "basic.profile", inGameOnly = true)
    public void infoCommand(Args args) {
        UUID uuid = Bukkit.getOfflinePlayer(args.getArgs(0)).getUniqueId();
        User user = core.getUserManager().getUser(args.getPlayer().getUniqueId());
        Player player = user.getPlayer();
        OfflinePlayer o = Bukkit.getOfflinePlayer(uuid);
        if (args.getArgs().length == 1 && Config.USERS.getConfig().getConfigurationSection("").getKeys(false).contains(uuid.toString())) {
            TextComponent[][] head = ColoredString.getHeadRowsComponents(uuid);
            if (head == null) return;

            Rank r = o.isOnline() ? core.getUserManager().getUser(uuid).getPrimaryRank() : Rank.getHighestOf(Config.USERS.getStringList(uuid.toString() + ".ranks").toArray(new String[0]));
            String n =  o.getName(); boolean s = n.endsWith("s");

            String discord = Config.USERS.getString(uuid.toString() + ".profile.discord") == null ? "Unassigned" : Config.USERS.getString(uuid.toString() + ".profile.discord");
            String status = o.isOnline() ? "&aOnline" : o.isBanned() ? "&cBanned" : "&7Offline";

            player.sendMessage(" ");
            player.spigot().sendMessage(head[0]);
            player.spigot().sendMessage(addString(head[1], CC.trns(" &b→ ") + r.getPrefix() + r.getTeam().getColor() + n + CC.trns(s ? "&b' Profile" : "&b's Profile")));
            player.spigot().sendMessage(head[2]);
            player.spigot().sendMessage(addString(head[3], CC.trns(" &b→ &7Discord: &f" + discord)));
            player.spigot().sendMessage(addString(head[4], CC.trns(" &b→ &7Status: " + status)));
            player.spigot().sendMessage(addString(head[5], CC.trns(" &b→ &7Playtime: &f" + getPlaytime(uuid))));

            List<TextComponent> socials = new ArrayList<>();
            if (Config.USERS.getConfig().getConfigurationSection(uuid.toString() + ".profile.socials") != null) {
                for (String social : Config.USERS.getConfig().getConfigurationSection(uuid.toString() + ".profile.socials").getKeys(false)) {
                    String text = "";
                    switch (social) {
                        case "youtube":
                            text = CC.trns("&f&nYouTube"); break;
                        case "twitch":
                            text = CC.trns("&f&nTwitch"); break;
                        case "twitter":
                            text = CC.trns("&f&nTwitter"); break;
                        case "github":
                            text = CC.trns("&f&nGitHub"); break;
                    }
                    socials.add(new TextComponentBuilder(CC.trns(text)).click(ClickEvent.Action.OPEN_URL, Config.USERS.getString(uuid.toString() + ".profile.socials." + social)).build());
                }
            }

            List<TextComponent> toSend = new ArrayList<>(Arrays.asList(head[6]));
            toSend.add(new TextComponentBuilder(CC.trns(" &b→ &7Socials: ")).build());
            if (socials.isEmpty()) {
                toSend.add(new TextComponentBuilder(CC.trns("&fUnassigned")).build());
            } else {
                for (int i = 0; i < socials.size(); i++) {
                    toSend.add(socials.get(i));
                    if (i != socials.size() - 1) toSend.add(new TextComponentBuilder(CC.trns("&f, ")).build());
                }
            }

            player.spigot().sendMessage(toSend.toArray(new TextComponent[0]));

            player.spigot().sendMessage(head[7]);
            player.sendMessage(" ");

        }
    }

    @Command(name = "smite")
    public void smiteCommand(Args args) {
        if (args.getArgs().length == 1 && Bukkit.getPlayer(args.getArgs(0)) != null) {
            Player p = Bukkit.getPlayer(args.getArgs(0));
            p.getWorld().strikeLightningEffect(p.getLocation());
        }
    }

    @Command(name = "glow", permission = "basic.glow", inGameOnly = true)
    public void glowCommand(Args args) {
//        ChatColor color = core.getUserManager().getUser(args.getPlayer().getUniqueId()).getPrimaryRank().getTeam().getColor();
//        Color bColor = color.asBungee().getColor();
        Player p = args.getPlayer();
        p.setGlowing(!p.isGlowing());
        p.sendMessage(CC.trns("&bGlow has been &3" + (p.isGlowing() ? "enabled" : "disabled")));
    }

    private String getPlaytime(UUID uuid) {
        long millis = (Bukkit.getOfflinePlayer(uuid).getStatistic(Statistic.PLAY_ONE_MINUTE) / 60) * 1000L;

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder();
        if (days != 0) sb.append(days).append(" day").append(days == 1 ? ", " : "s, ");
        if (hours != 0) sb.append(hours).append(" hour").append(hours == 1 ? ", " : "s, ");
        if (minutes != 0) sb.append(minutes).append(" minute").append(minutes == 1 ? ", " : "s, ");
        sb.append(seconds).append(" second").append(seconds == 1 ? "" : "s");

        return sb.toString();
    }

    private TextComponent c(String s) {
        return new TextComponentBuilder(s).build();
    }

    private TextComponent[] addString(TextComponent[] from, String toAdd) {
        List<TextComponent> l = new ArrayList<>(Arrays.asList(from)); l.add(c(toAdd));
        return l.toArray(new TextComponent[0]);
    }

}
