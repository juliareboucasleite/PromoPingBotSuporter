package com.promoping.bot.comandos.core;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface Command {

    String getName();
    String[] getAliases();
    String getDescription();
    String getCategory();

    default boolean adminOnly() {
        return true;
    }

    void execute(MessageReceivedEvent event, String[] args);
}
