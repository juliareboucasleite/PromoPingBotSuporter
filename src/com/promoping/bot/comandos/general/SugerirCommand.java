package com.promoping.bot.comandos.general;

import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.utils.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Arrays;
import java.util.List;


public class SugerirCommand extends BaseCommand {

    @Override
    public String getName() {
        return "sugerir";
    }

    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("sugestao", "suggestion", "suggest");
    }

    @Override
    public String getDescription() {
        return "Sugere uma nova funcionalidade ou melhoria para o site ou bot. A sugestão será enviada para o painel administrativo.";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Sugerir Funcionalidade")
                .setDescription(
                        "Tem uma ideia para melhorar o PromoPing?\n\n" +
                                "Clique no botão abaixo para abrir o formulário de sugestão.\n\n" +
                                "• Novas funcionalidades\n" +
                                "• Melhorias no bot\n" +
                                "• Recursos adicionais\n" +
                                "• Melhorias de interface"
                )
                .setColor(0x3B82F6)
                .setTimestamp()
                .setFooter("©PromoPing • Todos os direitos reservados");

        Button sugerirBtn = Button.primary(
                "abrir_formulario_sugestao",
                "Sugerir Funcionalidade"
        );

        event.getChannel()
                .sendMessageEmbeds(embed.build())
                .setActionRow(sugerirBtn)
                .queue();
    }
}
