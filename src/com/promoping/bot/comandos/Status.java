package com.promoping.bot.comandos;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Status implements Command {

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String getDescription() {
        return "Mostra o estado do sistema";
    }

    @Override
    public String getCategory() {
        return "monitorizacao";
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getChannel().sendMessage(
                "Status: ONLINE\nAPI: OK"
        ).queue();
    }
}