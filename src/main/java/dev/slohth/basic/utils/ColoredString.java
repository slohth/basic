package dev.slohth.basic.utils;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;

public class ColoredString {

    public static ChatColor getColorOf(String text) {
        if (text == null || text.isEmpty()) return ChatColor.WHITE;
        text = text.replace('§', '&');
        Map<ChatColor, Integer> colors = new HashMap<>();
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                ChatColor c = ChatColor.getByChar(text.charAt(i + 1));
                colors.put(c, colors.containsKey(c) ? colors.get(c) + 1 : 1);
            }
        }
        if (colors.isEmpty()) return ChatColor.WHITE;
        ChatColor c = colors.keySet().stream().findFirst().get();
        for (ChatColor color : colors.keySet()) if (colors.get(color) >= colors.get(c)) c = color;
        return c;
    }

    public static List<ChatColor> getColorsOf(String text) {
        List<ChatColor> toReturn = new ArrayList<>();
        if (text == null || text.isEmpty()) { toReturn.add(ChatColor.WHITE); return toReturn; }
        text = text.replace('§', '&');
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                ChatColor c = ChatColor.getByChar(text.charAt(i + 1));
                toReturn.add(c);
            }
        }
        return toReturn;
    }

    public static String[] getHeadRows(UUID uuid) {
        HttpURLConnection c = null;
        try {
            c = (HttpURLConnection) new URL("https://minotar.net/helm/" + uuid.toString() + "/8.png").openConnection();
            c.connect();
            BufferedImage image = ImageIO.read(c.getInputStream());
            c.disconnect();

            String[] s = new String[8];
            for (int y = 0; y < image.getHeight(); y++) {
                StringBuilder msg = new StringBuilder();
                for (int x = 0; x < image.getWidth(); x++) {
                    int  clr  = image.getRGB(x, y);
                    String hex = String.format("#%02X%02X%02X", (clr & 0x00ff0000) >> 16, (clr & 0x0000ff00) >> 8, clr & 0x000000ff);
                    msg.append(net.md_5.bungee.api.ChatColor.of(hex)).append("█");
                }
                s[y] = msg.toString();
            }
            return s;
        } catch (IOException e) { return null; }
    }

    public static TextComponent[][] getHeadRowsComponents(UUID uuid) {
        TextComponent[][] head = new TextComponent[8][8];
        HttpURLConnection c = null;
        try {
            c = (HttpURLConnection) new URL("https://minotar.net/helm/" + uuid.toString() + "/8.png").openConnection();
            c.connect();
            BufferedImage image = ImageIO.read(c.getInputStream());
            c.disconnect();

            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int  clr  = image.getRGB(x, y);
                    String hex = String.format("#%02X%02X%02X", (clr & 0x00ff0000) >> 16, (clr & 0x0000ff00) >> 8, clr & 0x000000ff);

                    TextComponent component = new TextComponent("█");
                    component.setColor(net.md_5.bungee.api.ChatColor.of(hex));
                    head[y][x] = component;
                }
            }
            return head;
        } catch (IOException ignored) {}
        return null;
    }

    public static List<TextComponent> headRowToComponent(UUID uuid, int row) {
        List<TextComponent> toReturn = new ArrayList<>();
        HttpURLConnection c = null;
        try {
            c = (HttpURLConnection) new URL("https://minotar.net/helm/" + uuid.toString() + "/8.png").openConnection();
            c.connect();
            BufferedImage image = ImageIO.read(c.getInputStream());
            c.disconnect();
            for (int x = 0; x < image.getWidth(); x++) {
                int  clr  = image.getRGB(x, row);
                String hex = String.format("#%02X%02X%02X", (clr & 0x00ff0000) >> 16, (clr & 0x0000ff00) >> 8, clr & 0x000000ff);

                TextComponent component = new TextComponent("█");
                component.setColor(net.md_5.bungee.api.ChatColor.of(hex));
                toReturn.add(component);
            }
            return toReturn;
        } catch (IOException e) { return null; }
    }

    public static Color getColor(ChatColor chatColor) {
        switch (chatColor) {
            case AQUA:
                return Color.AQUA;
            case BLACK:
                return Color.BLACK;
            case BLUE:
                return Color.BLUE;
            case DARK_AQUA:
                return Color.TEAL;
            case DARK_BLUE:
                return Color.NAVY;
            case DARK_GRAY:
                return Color.SILVER;
            case GRAY:
                return Color.GRAY;
            case DARK_GREEN:
                return Color.GREEN;
            case GREEN:
                return Color.LIME;
            case DARK_PURPLE:
                return Color.PURPLE;
            case LIGHT_PURPLE:
                return Color.FUCHSIA;
            case DARK_RED:
            case RED:
                return Color.RED;
            case GOLD:
                return Color.ORANGE;
            case YELLOW:
                return Color.YELLOW;
            default:
                return Color.WHITE;
        }

    }
}