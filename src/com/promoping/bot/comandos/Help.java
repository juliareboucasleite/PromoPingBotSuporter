package com.promoping.bot.comandos;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class Help implements Command {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Lista os comandos disponíveis";
    }

    @Override
    public String getCategory() {
        return "geral";
    }

    @Override
    public void execute(MessageReceivedEvent event) {

        Map<String, StringBuilder> byCategory = new LinkedHashMap<>();

        for (Command cmd : CommandManager.all()) {
            byCategory
                    .computeIfAbsent(cmd.getCategory(), k -> new StringBuilder())
                    .append("!").append(cmd.getName())
                    .append(" - ").append(cmd.getDescription())
                    .append("\n");
        }

        StringBuilder out = new StringBuilder("Comandos:\n\n");
        byCategory.forEach((cat, text) -> {
            out.append("[").append(cat).append("]\n");
            out.append(text).append("\n");
        });

        event.getChannel().sendMessage(out.toString()).queue();
    }
}
