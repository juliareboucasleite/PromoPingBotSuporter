package com.promoping.bot.listeners;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import com.promoping.bot.dao.BugReportDAO;
import com.promoping.bot.dao.SuggestionDAO;

import java.sql.SQLException;

public class ModalListener extends ListenerAdapter {

    private final SuggestionDAO suggestionDAO = new SuggestionDAO();
    private final BugReportDAO bugReportDAO = new BugReportDAO();

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {

        String modalId = event.getModalId();
        if (!modalId.equals("modal_sugestao") && !modalId.equals("modal_bug")) return;

        if (modalId.equals("modal_sugestao")) {
            String titulo = event.getValue("titulo_sugestao").getAsString();
            String descricao = event.getValue("descricao_sugestao").getAsString();

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Nova Sugestao")
                    .addField("Titulo", titulo, false)
                    .addField("Descricao", descricao, false)
                    .addField("Autor", event.getUser().getAsTag(), false)
                    .setColor(0x3B82F6);

            try {
                String descricaoDb = "Discord: " + event.getUser().getAsTag() + " (" + event.getUser().getId() + ")\n" + descricao;
                suggestionDAO.saveSuggestion(titulo, descricaoDb, "ambos", "medium", "pendente", 0);
            } catch (SQLException e) {
                event.reply("Erro ao salvar sua sugestao no banco.").setEphemeral(true).queue();
                return;
            }

            event.reply("Obrigado! A sua sugestao foi enviada.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String tituloBug = event.getValue("titulo_bug").getAsString();
        String descricaoBug = event.getValue("descricao_bug").getAsString();

        EmbedBuilder embedBug = new EmbedBuilder()
                .setTitle("Novo Bug Reportado")
                .addField("Titulo", tituloBug, false)
                .addField("Descricao", descricaoBug, false)
                .addField("Autor", event.getUser().getAsTag(), false)
                .setColor(0xFF6B6B);

        try {
            String descricaoDb = "Discord: " + event.getUser().getAsTag() + " (" + event.getUser().getId() + ")\n" + descricaoBug;
            bugReportDAO.saveBug(tituloBug, descricaoDb, "bug", "medium", "open");
        } catch (SQLException e) {
            event.reply("Erro ao salvar seu bug no banco.").setEphemeral(true).queue();
            return;
        }

        event.reply("Obrigado! Seu reporte de bug foi enviado.")
                .setEphemeral(true)
                .queue();
    }
}
