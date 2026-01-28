package com.promoping.bot.listeners;

import com.promoping.bot.comandos.Command;
import com.promoping.bot.comandos.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String msg = event.getMessage().getContentRaw();
        if (!msg.startsWith("!")) return;

        String name = msg.substring(1).toLowerCase();
        Command cmd = CommandManager.get(name);
        if (cmd != null) {
            cmd.execute(event);
        }
    }
}
