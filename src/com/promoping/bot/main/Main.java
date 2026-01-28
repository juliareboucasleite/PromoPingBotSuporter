package com.promoping.bot.main;

import com.promoping.bot.comandos.*;
import com.promoping.bot.listeners.MessageListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import java.io.FileInputStream;
import java.util.EnumSet;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        String token = loadToken();
        CommandManager.register(new Help());
        CommandManager.register(new Ping());
        CommandManager.register(new Status());
        CommandManager.register(new Counting());

        JDABuilder.create(token, EnumSet.of(
                                GatewayIntent.GUILD_MESSAGES,
                                GatewayIntent.MESSAGE_CONTENT,
                                GatewayIntent.GUILD_MEMBERS,
                                GatewayIntent.GUILD_PRESENCES))
                                .addEventListeners(new MessageListener())
                                .build()
                                .awaitReady();

        System.out.println("PromoPing Support CONNECTED");
    }

    private static String loadToken() {
        try (FileInputStream in = new FileInputStream("config.properties")) {
            Properties props = new Properties();
            props.load(in);

            String token = props.getProperty("DISCORD_TOKEN");
            if (token == null || token.isEmpty()) {
                throw new IllegalStateException("DISCORD_TOKEN nao definido");
            }
            return token;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar config.properties", e);
        }
    }
}
