package com.promoping.bot.comandos;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Counting implements Command {

    @Override
    public String getName() {
        return "counting";
    }

    @Override
    public String[] getAliases() {
        return new String[] { "count", "contagem" };
    }

    @Override
    public String getDescription() {
        return "Gerencia o sistema de contagem";
    }

    @Override
    public String getCategory() {
        return "admin";
    }

    @Override
    public boolean adminOnly() {
        return true;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {

        if (args.length == 0) {
            event.getChannel()
                    .sendMessage("Use: status | configurar | reset | desativar")
                    .queue();
            return;
        }

        switch (args[0].toLowerCase()) {
            case "status":
                event.getChannel().sendMessage("Contagem ativa").queue();
                break;

            case "reset":
                event.getChannel().sendMessage("Contagem resetada").queue();
                break;

            case "desativar":
                event.getChannel().sendMessage("Contagem desativada").queue();
                break;

            default:
                event.getChannel().sendMessage("Ação inválida").queue();
        }
    }
}
