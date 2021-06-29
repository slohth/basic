package dev.slohth.basic.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class CC {

    public static String trns(final String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    public static List<String> trns(final List<String> lines) {
        final List<String> toReturn = new ArrayList<>();
        for (final String line : lines) toReturn.add(ChatColor.translateAlternateColorCodes('&', line));
        return toReturn;
    }

    public static List<String> trns(final String[] lines) {
        final List<String> toReturn = new ArrayList<>();
        for (final String line : lines) if (line != null) toReturn.add(ChatColor.translateAlternateColorCodes('&', line));
        return toReturn;
    }

}