package dev.slohth.basic.rank.manager;

import dev.slohth.basic.Basic;
import dev.slohth.basic.rank.Rank;
import dev.slohth.basic.utils.framework.Config;

import java.util.ArrayList;
import java.util.List;

public class RankManager {

    private final Basic core;
    private final List<Rank> registeredRanks = new ArrayList<>();

    /**
     * Class constructor - registers all ranks in the config
     * @param core the main instance of the plugin
     */
    public RankManager(Basic core) {
        this.core = core;
        for (String rank : Config.RANKS.getConfig().getConfigurationSection("").getKeys(false)) this.register(rank);
        for (Rank rank : this.registeredRanks) try { rank.load(); } catch (NullPointerException e) { e.printStackTrace(); }
    }

    /**
     * Registers a rank, you must attempt to load its data separately
     * @param name the name of the rank
     * @return whether a new rank was successfully registered
     */
    public boolean register(String name) {
        if (this.getRank(name) != null) return false;
        Rank rank = new Rank(core, name, this.registeredRanks.size());
        this.registeredRanks.add(rank);
        return true;
    }

    /**
     * Attempts to save a ranks data and then remove the rank from memory
     * @param rank the rank to unregister
     * @return whether the rank was successfully unregistered
     */
    public boolean unregister(Rank rank) {
        try {
            rank.save(); this.registeredRanks.remove(rank);
            return true;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Gets a rank object from a given name if it exists
     * @param name the name of the rank
     * @return the Rank object if found, else null
     */
    public Rank getRank(String name) {
        for (Rank rank : this.registeredRanks) if (rank.getName().equalsIgnoreCase(name)) return rank;
        return null;
    }

    /**
     * Gets the default rank, if none found, returns the rank with lowest priority
     * @return the default rank as defined in ranks.yml
     */
    public Rank getDefault() {
        for (Rank rank : this.registeredRanks) if (rank.isDefault()) return rank;
        return registeredRanks.get(registeredRanks.size() - 1);
    }

}
