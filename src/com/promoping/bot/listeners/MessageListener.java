package com.promoping.bot.listeners;

import com.promoping.bot.comandos.Command;
import com.promoping.bot.comandos.CommandManager;
import com.promoping.bot.security.AccessControl;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;
        if (event.getMember() == null) return;
        if (!AccessControl.canUseBot(event.getMember())) return;

        String raw = event.getMessage().getContentRaw();
        if (!raw.startsWith("!")) return;

        String[] parts = raw.substring(1).split("\\s+");
        String name = parts[0].toLowerCase();
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        Command cmd = CommandManager.get(name);
        if (cmd == null) return;

        cmd.execute(event, args);
    }
}
