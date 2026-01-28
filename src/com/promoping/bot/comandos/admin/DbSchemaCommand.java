package com.promoping.bot.comandos.admin;

import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.dao.DatabaseConnection;
import com.promoping.bot.security.AccessControl;
import com.promoping.bot.utils.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Lista tabelas e colunas do banco.
 */
public class DbSchemaCommand extends BaseCommand {

    private final AccessControl accessControl;

    public DbSchemaCommand(AccessControl accessControl) {
        this.accessControl = accessControl;
    }

    @Override
    public String getName() {
        return "db-schema";
    }

    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("dbschema", "schema");
    }

    @Override
    public String getDescription() {
        return "Mostra as tabelas e colunas do banco. (Apenas administradores)";
    }

    @Override
    public boolean adminOnly() {
        return true;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (event.getGuild() == null) {
            event.getChannel().sendMessage("Este comando so pode ser usado em um servidor!").queue();
            return;
        }

        if (!accessControl.isAdmin(event.getMember())) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Sem Permissao")
                    .setDescription("Apenas administradores podem ver o schema do banco!")
                    .setColor(0xff0000)
                    .setTimestamp();

            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        if (args.length == 0) {
            listTables(event);
        } else {
            String table = args[0].trim();
            listColumns(event, table);
        }
    }

    private void listTables(MessageReceivedEvent event) {
        List<String> tables = new ArrayList<>();
        String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ? ORDER BY TABLE_NAME";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, System.getenv().getOrDefault("DB_NAME", "pap"));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tables.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            event.getChannel().sendMessage("Erro ao ler tabelas: " + e.getMessage()).queue();
            return;
        }

        if (tables.isEmpty()) {
            event.getChannel().sendMessage("Nenhuma tabela encontrada.").queue();
            return;
        }

        StringBuilder sb = new StringBuilder("Tabelas do banco:\n");
        for (String t : tables) {
            sb.append("- ").append(t).append("\n");
        }
        sendChunks(event, sb.toString());
    }

    private void listColumns(MessageReceivedEvent event, String table) {
        List<String> columns = new ArrayList<>();
        String sql = "SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_KEY, EXTRA " +
                "FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? " +
                "ORDER BY ORDINAL_POSITION";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, System.getenv().getOrDefault("DB_NAME", "pap"));
            ps.setString(2, table);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String col = rs.getString("COLUMN_NAME");
                    String type = rs.getString("COLUMN_TYPE");
                    String nullable = rs.getString("IS_NULLABLE");
                    String key = rs.getString("COLUMN_KEY");
                    String extra = rs.getString("EXTRA");
                    String meta = type + " " + (nullable.equals("NO") ? "NOT NULL" : "NULL");
                    if (key != null && !key.isEmpty()) meta += " " + key;
                    if (extra != null && !extra.isEmpty()) meta += " " + extra;
                    columns.add(col + " - " + meta.trim());
                }
            }
        } catch (SQLException e) {
            event.getChannel().sendMessage("Erro ao ler colunas: " + e.getMessage()).queue();
            return;
        }

        if (columns.isEmpty()) {
            event.getChannel().sendMessage("Tabela nao encontrada ou sem colunas: " + table).queue();
            return;
        }

        StringBuilder sb = new StringBuilder("Colunas de ").append(table).append(":\n");
        for (String c : columns) {
            sb.append("- ").append(c).append("\n");
        }
        sendChunks(event, sb.toString());
    }

    private void sendChunks(MessageReceivedEvent event, String text) {
        int max = 1900;
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(text.length(), start + max);
            String chunk = text.substring(start, end);
            event.getChannel().sendMessage("```\n" + chunk + "\n```").queue();
            start = end;
        }
    }
}
