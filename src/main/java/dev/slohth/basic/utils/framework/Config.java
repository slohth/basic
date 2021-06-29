package dev.slohth.basic.utils.framework;

import dev.slohth.basic.Basic;
import dev.slohth.basic.utils.CC;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public enum Config {

    RANKS("ranks"),
    USERS("player-data");

    private final File file;
    private final YamlConfiguration config;
    private final String name;

    Config(String name) {
        Basic core = JavaPlugin.getPlugin(Basic.class);
        this.file = new File(core.getDataFolder(), name + ".yml");
        core.saveResource(name + ".yml", false);
        this.config = YamlConfiguration.loadConfiguration(this.file);
        this.name = name;
    }

    public String getString(String path) {
        if (config.contains(path)) return CC.trns(Objects.requireNonNull(config.getString(path)));
        return null;
    }

    public String getStringOrDefault(String path, String def) {
        String s = getString(path);
        return s == null ? def : s;
    }

    public int getInteger(String path) {
        if (config.contains(path)) return config.getInt(path);
        return 0;
    }

    public boolean getBoolean(String path) {
        return config.contains(path) && config.getBoolean(path);
    }

    public double getDouble(String path) {
        if (config.contains(path)) return config.getDouble(path);
        return 0.0;
    }

    public Object get(String path) {
        if (config.contains(path)) return config.get(path);
        return null;
    }

    public void set(String path, Object value) { config.set(path, value); }

    public List<String> getStringList(String path) {
        if (config.contains(path)) return CC.trns(config.getStringList(path));
        return null;
    }

    public Set<String> getKeys(boolean deep) { return config.getKeys(deep); }

    public File getFile() { return file; }

    public YamlConfiguration getConfig() { return config; }

    public void reloadConfig() {
        try { config.load(file); } catch (IOException | InvalidConfigurationException ignored) {}
    }

    public void saveConfig() { try { config.save(file); } catch (IOException ignored) {} }

    public String getName() { return name; }

    // =========================================

    public static void saveAll() {
        for (Config c : Config.values()) c.saveConfig();
    }

    public static void reloadAll() {
        for (Config c : Config.values()) c.reloadConfig();
    }

}
