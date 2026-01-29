package com.promoping.bot.main;

import com.promoping.bot.listeners.ButtonListener;
import com.promoping.bot.listeners.MessageListener;
import com.promoping.bot.services.BugResolvedWatcher;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import java.io.FileInputStream;
import java.util.EnumSet;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import com.promoping.bot.listeners.ModalListener;
import net.dv8tion.jda.api.entities.Activity;


public class Main {

    public static void main(String[] args) throws Exception {

        CommandBootstrap.registerAll();
        String token = loadToken();
        JDA jda = JDABuilder.create(token, EnumSet.of(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS
                ))
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("Bot feito com Java"))
                .addEventListeners(new MessageListener(), new ButtonListener(),  new ModalListener())
                .build()
                .awaitReady();

        String[] statusTexts = {
                "Bot feito com Java",
                "Bot administrativo feito pra auxiliar PromoPing",
                "Ajuda 24/7"
        };
        AtomicInteger statusIndex = new AtomicInteger(0);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            int i = statusIndex.getAndUpdate(v -> (v + 1) % statusTexts.length);
            jda.getPresence().setActivity(Activity.playing(statusTexts[i]));
        }, 0, 30, TimeUnit.SECONDS);

        new BugResolvedWatcher(jda).start();

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
