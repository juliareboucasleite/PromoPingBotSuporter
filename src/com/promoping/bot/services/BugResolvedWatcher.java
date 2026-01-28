package com.promoping.bot.services;

import com.promoping.bot.dao.DatabaseConnection;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BugResolvedWatcher {

    private static final String RESOLVED_STATUS = "resolved";
    private static final String CHANNEL_ID = "1442932490233708645";

    private final JDA jda;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private volatile Timestamp lastChecked;

    public BugResolvedWatcher(JDA jda) {
        this.jda = jda;
        this.lastChecked = Timestamp.from(Instant.now());
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::checkResolved, 10, 60, TimeUnit.SECONDS);
    }

    private void checkResolved() {
        Timestamp maxUpdated = null;
        String sql = "SELECT Id, Titulo, Descricao, Tipo, Prioridade, Status, DataCriacao, DataAtualizacao " +
                "FROM bugsprojetos WHERE Status = ? AND DataAtualizacao > ? ORDER BY DataAtualizacao ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, RESOLVED_STATUS);
            ps.setTimestamp(2, lastChecked);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("Id");
                    String titulo = rs.getString("Titulo");
                    String descricao = rs.getString("Descricao");
                    String tipo = rs.getString("Tipo");
                    String prioridade = rs.getString("Prioridade");
                    Timestamp criadoEm = rs.getTimestamp("DataCriacao");
                    Timestamp resolvidoEm = rs.getTimestamp("DataAtualizacao");

                    sendResolvedMessage(id, titulo, descricao, tipo, prioridade, criadoEm, resolvidoEm);

                    if (maxUpdated == null || resolvidoEm.after(maxUpdated)) {
                        maxUpdated = resolvidoEm;
                    }
                }
            }
        } catch (Exception ignored) {
            return;
        }

        if (maxUpdated != null) {
            lastChecked = maxUpdated;
        } else {
            lastChecked = Timestamp.from(Instant.now());
        }
    }

    private void sendResolvedMessage(int id, String titulo, String descricao, String tipo, String prioridade,
                                     Timestamp criadoEm, Timestamp resolvidoEm) {
        TextChannel channel = jda.getTextChannelById(CHANNEL_ID);
        if (channel == null) return;

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Bug Resolvido: " + titulo)
                .setDescription(descricao != null ? descricao : "*Sem descricao*")
                .addField("ID do Bug", "#" + id, true)
                .addField("Tipo", tipo != null ? tipo : "-", true)
                .addField("Prioridade", prioridade != null ? prioridade : "-", true)
                .addField("Criado em", criadoEm != null ? criadoEm.toString() : "-", true)
                .addField("Resolvido em", resolvidoEm != null ? resolvidoEm.toString() : "-", true)
                .setColor(0x00ff00);

        channel.sendMessageEmbeds(embed.build()).queue();
    }
}
