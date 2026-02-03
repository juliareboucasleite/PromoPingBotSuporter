package com.promoping.bot.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public final class BotConfig {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = new FileInputStream("config.properties")) {
            PROPS.load(in);
        } catch (Exception ignored) {
        }
    }

    private BotConfig() {
    }

    public static String getPrefix() {
        String envPrefix = System.getenv("DISCORD_PREFIX");
        if (envPrefix != null && !envPrefix.isEmpty()) {
            return envPrefix;
        }
        String filePrefix = PROPS.getProperty("DISCORD_PREFIX");
        if (filePrefix != null && !filePrefix.isEmpty()) {
            return filePrefix;
        }
        return "p!";
    }
}
