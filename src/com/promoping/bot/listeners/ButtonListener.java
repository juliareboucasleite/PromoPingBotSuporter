package com.promoping.bot.listeners;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.EmbedBuilder;

public class ButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        switch (event.getComponentId()) {

            case "abrir_formulario_sugestao":
                event.replyEmbeds(
                        new EmbedBuilder()
                                .setTitle("Sugestão")
                                .setDescription("📋 Formulário de sugestão será aberto aqui.")
                                .setColor(0x3B82F6)
                                .build()
                ).setEphemeral(true).queue();
                break;

            case "abrir_formulario_bug":
                event.reply("🐛 Formulário de bug em breve.")
                        .setEphemeral(true)
                        .queue();
                break;

            case "abrir_ticket_promoping":
                event.reply("🎫 Criando ticket...")
                        .setEphemeral(true)
                        .queue();
                break;
        }
    }
}
