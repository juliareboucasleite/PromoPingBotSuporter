package com.promoping.bot.comandos;

import com.promoping.bot.services.Database;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.sql.Connection;

public class DbTest implements Command {

    @Override
    public String getName() { return "db"; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public String getDescription() {
        return "Testa a ligação à base de dados";
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
        try (Connection c = Database.getConnection()) {
            event.getChannel().sendMessage("BD conectada").queue();
        } catch (Exception e) {
            event.getChannel().sendMessage("Erro BD: " + e.getMessage()).queue();
        }
    }
}
