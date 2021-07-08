package dev.slohth.basic.rank;

import dev.slohth.basic.Basic;
import dev.slohth.basic.utils.CC;
import dev.slohth.basic.utils.ColoredString;
import dev.slohth.basic.utils.framework.Config;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class Rank {

    private final Basic core;

    private final String name;
    private final Map<String, Boolean> permissions = new HashMap<>();
    private final Set<Rank> inherits = new HashSet<>();

    private final int priority;

    private String display;
    private String prefix;
    private String tabPrefix;
    private boolean isDefault;

    private Team team;

    /**
     * Class constructor - creates a new rank object
     * @param core the main instance of the plugin
     * @param name the name of the rank as specified in ranks.yml
     * @param priority the priority of the rank
     */
    public Rank(Basic core, String name, int priority) {
        this.core = core; this.name = name; this.priority = priority;
    }

    /**
     * Attempts to load the data associated with this rank from config
     * @throws NullPointerException if any data required is missing
     */
    public void load() throws NullPointerException {
        this.team = core.getRankManager().getScoreboard().registerNewTeam(core.getRankManager().getPriorityChars()[priority] + name);
        ConfigurationSection options = Config.RANKS.getConfig().getConfigurationSection(this.name + ".options");

        if (Config.RANKS.getStringList(this.name + ".permissions") != null) {
            for (String perm : Config.RANKS.getStringList(this.name + ".permissions")) {
                boolean value = !perm.startsWith("-"); String s = value ? perm : perm.substring(1);
                this.permissions.put(s, value);
            }
        }

        this.display = CC.trns(options.getString("display"));
        this.prefix = CC.trns(options.getString("prefix"));
        this.setTabPrefix(options.getString("tab-prefix"));
        this.isDefault = options.get("default") != null && options.getBoolean("default");

        List<ChatColor> colors = ColoredString.getColorsOf(this.tabPrefix);
        if (colors.isEmpty()) colors.add(ChatColor.WHITE);
        this.team.setColor(colors.get(colors.size() - 1));

        for (String inherit : options.getStringList("inherits")) {
            if (core.getRankManager().getRank(inherit) == null) continue;
            if (!this.inherits.contains(core.getRankManager().getRank(inherit))) this.inherits.add(core.getRankManager().getRank(inherit));
        }
    }

    public void save() {

    }

    public Map<String, Boolean> getPermissions() { return this.permissions; }
    public Set<Rank> getInherits() { return this.inherits; }

    public String getName() { return this.name; }
    public String getDisplay() { return this.display; }
    public void setDisplay(String display) { this.display = display; }
    public String getPrefix() { return this.prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    public String getTabPrefix() { return this.tabPrefix; }
    public void setTabPrefix(String tabPrefix) {
        this.tabPrefix = tabPrefix;
        this.team.setPrefix(CC.trns(tabPrefix));
    }
    public boolean isDefault() { return this.isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
    public int getPriority() { return this.priority; }
    public Team getTeam() { return this.team; }

    public static Rank getHighestOf(Rank... ranks) {
        Rank rank = ranks[0];
        for (Rank r : ranks) if (r.getPriority() < rank.getPriority()) rank = r;
        return rank;
    }

    public static Rank getHighestOf(String... ranks) {
        List<Rank> toCheck = new ArrayList<>();
        for (String rank : ranks) if (JavaPlugin.getPlugin(Basic.class).getRankManager().getRank(rank) != null) toCheck.add(JavaPlugin.getPlugin(Basic.class).getRankManager().getRank(rank));
        return getHighestOf(toCheck.toArray(new Rank[0]));
    }

}
