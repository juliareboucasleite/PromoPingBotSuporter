package com.promoping.bot.utils;

import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class EmbedBuilder {
    
    private String title;
    private String description;
    private Color color;
    private String thumbnail;
    private String footerText;
    private String footerIcon;
    private Instant timestamp;
    private String authorName;
    private String authorIcon;
    private String authorUrl;
    private final List<Field> fields = new ArrayList<>();
    
    public EmbedBuilder setTitle(String title) {
        this.title = title;
        return this;
    }
    
    public EmbedBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public EmbedBuilder setColor(int rgb) {
        this.color = new Color(rgb);
        return this;
    }
    
    public EmbedBuilder setThumbnail(String url) {
        this.thumbnail = url;
        return this;
    }
    
    public EmbedBuilder setFooter(String text) {
        this.footerText = text;
        return this;
    }
    
    public EmbedBuilder setFooter(String text, String iconUrl) {
        this.footerText = text;
        this.footerIcon = iconUrl;
        return this;
    }
    
    public EmbedBuilder setTimestamp() {
        this.timestamp = Instant.now();
        return this;
    }
    
    public EmbedBuilder setAuthor(String name) {
        this.authorName = name;
        return this;
    }
    
    public EmbedBuilder setAuthor(String name, String iconUrl) {
        this.authorName = name;
        this.authorIcon = iconUrl;
        return this;
    }
    
    public EmbedBuilder setAuthor(String name, String iconUrl, String url) {
        this.authorName = name;
        this.authorIcon = iconUrl;
        this.authorUrl = url;
        return this;
    }
    
    public EmbedBuilder addField(String name, String value, boolean inline) {
        fields.add(new Field(name, value, inline));
        return this;
    }
    
    public MessageEmbed build() {
        net.dv8tion.jda.api.EmbedBuilder builder = new net.dv8tion.jda.api.EmbedBuilder();
        
        if (title != null) builder.setTitle(title);
        if (description != null) builder.setDescription(description);
        if (color != null) builder.setColor(color);
        if (thumbnail != null) builder.setThumbnail(thumbnail);
        if (footerText != null) {
            if (footerIcon != null) {
                builder.setFooter(footerText, footerIcon);
            } else {
                builder.setFooter(footerText);
            }
        }
        if (timestamp != null) builder.setTimestamp(timestamp);
        if (authorName != null) {
            if (authorIcon != null && authorUrl != null) {
                builder.setAuthor(authorName, authorUrl, authorIcon);
            } else if (authorIcon != null) {
                builder.setAuthor(authorName, null, authorIcon);
            } else {
                builder.setAuthor(authorName);
            }
        }
        
        for (Field field : fields) {
            builder.addField(field.name, field.value, field.inline);
        }
        
        return builder.build();
    }
    
    private static class Field {
        final String name;
        final String value;
        final boolean inline;
        
        Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }
    }
}
