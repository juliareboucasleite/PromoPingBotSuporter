package com.promoping.bot.main;

import com.promoping.bot.listeners.ButtonListener;
import com.promoping.bot.listeners.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import java.io.FileInputStream;
import java.util.EnumSet;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws Exception {

        CommandBootstrap.registerAll();
        String token = loadToken();
        JDA jda = JDABuilder.create(token, EnumSet.of(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS
                ))
                .addEventListeners(new MessageListener(), new ButtonListener())
                .build()
                .awaitReady();

        System.out.println("PromoPing Support Bot CONNECTED");
    }

    private static String loadToken() {
        try (FileInputStream in = new FileInputStream("config.properties")) {
            Properties props = new Properties();
            props.load(in);

            String token = props.getProperty("DISCORD_TOKEN");
            if (token == null || token.isEmpty()) {
                throw new IllegalStateException("DISCORD_TOKEN not defined");
            }
            return token;

        } catch (Exception e) {
            throw new RuntimeException("Error loading config.properties", e);
        }
    }
}
