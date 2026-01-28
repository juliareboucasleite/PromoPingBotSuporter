package com.promoping.bot.comandos.general;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.utils.EmbedBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Reporta um bug ou problema encontrado no sistema.
 */
public class ReportarCommand extends BaseCommand {

    @Override
    public String getName() {
        return "reportar";
    }

    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("bug", "report", "reportbug");
    }

    @Override
    public String getDescription() {
        return "Reporta um bug ou problema encontrado no sistema. O bug sera enviado para o painel administrativo.";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Reportar Bug")
                .setDescription(
                        "**Encontrou um bug ou problema?**\n\n" +
                        "Clique no botao abaixo para abrir o formulario de reporte.\n\n" +
                        "**O que voce pode reportar:**\n" +
                        "- Bugs e erros no sistema\n" +
                        "- Problemas de funcionalidade\n" +
                        "- Sugestoes de melhorias\n\n" +
                        "**Seu reporte sera enviado diretamente para o painel administrativo!**"
                )
                .setColor(0xFF6B6B)
                .setTimestamp()
                .setFooter("PromoPing - Todos os direitos reservados");

        Button reportarBtn = Button.danger("abrir_formulario_bug", "Reportar Bug");

        event.getChannel()
                .sendMessageEmbeds(embed.build())
                .setActionRow(reportarBtn)
                .queue();
    }
}
