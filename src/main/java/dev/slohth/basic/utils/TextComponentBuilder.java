package dev.slohth.basic.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TextComponentBuilder {

    private final TextComponent textComponent;

    public TextComponentBuilder(String text) {
        this.textComponent = new TextComponent(text);
    }

    public TextComponentBuilder hover(HoverEvent.Action action, String... value) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            sb.append(CC.trns(value[i]));
            if (i != value.length - 1) {
                sb.append("\n");
            }
        }
        textComponent.setHoverEvent(new HoverEvent(action, new ComponentBuilder(sb.toString()).create()));
        return this;
    }

    public TextComponentBuilder click(ClickEvent.Action action, String value) {
        textComponent.setClickEvent(new ClickEvent(action, value));
        return this;
    }

    public TextComponent build() {
        return textComponent;
    }

}