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
    public String[] getAliases() {
        return new String[] { "ajuda", "comandos", "h" };
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
    public boolean adminOnly() {
        return false;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {

        Map<String, StringBuilder> byCategory = new LinkedHashMap<>();
        Map<String, Command> unique = new LinkedHashMap<>();

        // remove duplicados (por nome principal)
        for (Command cmd : CommandManager.all()) {
            unique.putIfAbsent(cmd.getName(), cmd);
        }

        for (Command cmd : unique.values()) {
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
