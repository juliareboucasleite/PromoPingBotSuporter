package com.promoping.bot.listeners;

import com.promoping.bot.comandos.core.Command;
import com.promoping.bot.comandos.core.CommandManager;
import com.promoping.bot.security.AccessControl;
import com.promoping.bot.utils.BotConfig;
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
        String prefix = BotConfig.getPrefix();
        if (!raw.startsWith(prefix)) return;

        String[] parts = raw.substring(prefix.length()).split("\\s+");
        String name = parts[0].toLowerCase();
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        Command cmd = CommandManager.get(name);
        if (cmd == null) return;

        cmd.execute(event, args);
    }
}
