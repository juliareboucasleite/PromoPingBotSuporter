package com.promoping.bot.main;

import com.promoping.bot.comandos.CommandManager;
import com.promoping.bot.comandos.Help;
import com.promoping.bot.comandos.Ping;
import com.promoping.bot.comandos.Status;
import com.promoping.bot.listeners.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler config.properties", e);
        }

        String token = props.getProperty("DISCORD_TOKEN");
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("DISCORD_TOKEN nao definido");
        }

        CommandManager.register(new Help());
        CommandManager.register(new Ping());
        CommandManager.register(new Status());

        JDA jda = JDABuilder.create(token, EnumSet.of(
                GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_PRESENCES))
                .addEventListeners(new MessageListener())
                .build();

        jda.awaitReady();
        System.out.println("PromoPing Support CONNECTED");
    }
}

