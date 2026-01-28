package com.promoping.bot.comandos.admin;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.security.AccessControl;
import com.promoping.bot.utils.EmbedBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Configura o sistema de tickets.
 */
public class TicketCommand extends BaseCommand {

    private final AccessControl accessControl;

    public TicketCommand(AccessControl accessControl) {
        this.accessControl = accessControl;
    }

    @Override
    public String getName() {
        return "ticket";
    }

    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("setup-ticket", "config-ticket");
    }

    @Override
    public String getDescription() {
        return "Configura o sistema de tickets. Escolha o canal onde o botao de abrir ticket aparecera. (Apenas administradores)";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (event.getGuild() == null) {
            event.getChannel().sendMessage("Este comando so pode ser usado em um servidor!")
                    .queue();
            return;
        }

        if (!accessControl.isAdmin(event.getMember())) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Sem Permissao")
                    .setDescription("Apenas administradores podem configurar o sistema de tickets!")
                    .setColor(0xff0000)
                    .setTimestamp();

            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        TextChannel targetChannel = event.getChannel().asTextChannel();

        if (args.length > 0) {
            String channelMention = args[0];
            String channelId = channelMention.replaceAll("[<#>]", "");

            TextChannel mentionedChannel = event.getGuild().getTextChannelById(channelId);
            if (mentionedChannel != null) {
                targetChannel = mentionedChannel;
            } else {
                event.getChannel().sendMessage("Canal invalido! Mencione um canal de texto valido ou use o comando no canal desejado.
**Exemplo:** `!ticket #suporte`")
                        .queue();
                return;
            }
        }

        EmbedBuilder supportEmbed = new EmbedBuilder()
                .setTitle("Suporte - PromoPing")
                .setDescription("**Precisa de ajuda ou suporte?** Clique no botao abaixo para abrir um ticket. Nossa equipe ira auxilia-lo o mais breve possivel!")
                .setColor(0x5865F2)
                .setTimestamp()
                .setFooter("PromoPing - Todos os direitos reservados");

        Button abrirTicketBtn = Button.primary("abrir_ticket_promoping", "Abrir Ticket");

        targetChannel.sendMessageEmbeds(supportEmbed.build())
                .setActionRow(abrirTicketBtn)
                .queue();

        EmbedBuilder confirmEmbed = new EmbedBuilder()
                .setTitle("Sistema de Tickets Configurado!")
                .setDescription("O botao de abrir ticket foi enviado para " + targetChannel.getAsMention())
                .setColor(0x00ff00)
                .setTimestamp();

        event.getChannel().sendMessageEmbeds(confirmEmbed.build()).queue();
    }
}
