package com.promoping.bot.comandos;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface Command {
    String getName();
    String getDescription();
    String getCategory();

    void execute(MessageReceivedEvent event);
}
