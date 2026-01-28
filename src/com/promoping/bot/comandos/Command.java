package com.promoping.bot.comandos;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface Command {

    String getName();
    String[] getAliases();
    String getDescription();
    String getCategory();
    boolean adminOnly();

    void execute(MessageReceivedEvent event, String[] args);
}
