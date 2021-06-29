package dev.slohth.basic.rank;

import dev.slohth.basic.Basic;
import dev.slohth.basic.utils.CC;
import dev.slohth.basic.utils.framework.Config;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Rank {

    private final Basic core;

    private final String name;
    private final Map<String, Boolean> permissions = new HashMap<>();
    private final Set<Rank> inherits = new HashSet<>();

    private String display;
    private String prefix;
    private String tabPrefix;
    private boolean isDefault;
    private int priority;

    /**
     * Class constructor - creates a new rank object
     * @param core the main instance of the plugin
     * @param name the name of the rank as specified in ranks.yml
     * @param priority the priority of the rank
     */
    public Rank(Basic core, String name, int priority) { this.core = core; this.name = name; this.priority = priority; }

    /**
     * Attempts to load the data associated with this rank from config
     * @throws NullPointerException if any data required is missing
     */
    public void load() throws NullPointerException {
        ConfigurationSection options = Config.RANKS.getConfig().getConfigurationSection(this.name + ".options");

        for (String perm : Config.RANKS.getStringList(this.name + ".permissions")) {
            boolean value = !perm.startsWith("-"); String s = value ? perm : perm.substring(1);
            this.permissions.put(s, value);
        }

        this.display = CC.trns(options.getString("display"));
        this.prefix = CC.trns(options.getString("prefix"));
        this.tabPrefix = CC.trns(options.getString("tab-prefix"));
        this.isDefault = options.get("default") != null && options.getBoolean("default");

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

    public boolean isDefault() { return this.isDefault; }

}
