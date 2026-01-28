package com.promoping.bot.comandos.system;

import com.promoping.bot.comandos.core.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Status implements Command {

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String[] getAliases() {
        return new String[] { "health", "estado" };
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
    public boolean adminOnly() {
        return false;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        event.getChannel()
                .sendMessage("Status: ONLINE\nAPI: OK")
                .queue();
    }
}
