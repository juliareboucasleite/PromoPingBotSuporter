package com.promoping.bot.comandos;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Ping implements Command {

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Responde com pong";
    }

    @Override
    public String getCategory() {
        return "geral";
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getChannel().sendMessage("Pong!").queue();
    }
}