package com.promoping.bot.comandos.admin;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
// import net.dv8tion.jda.api.interactions.components.ActionRow; // Not available in this JDA version
// import net.dv8tion.jda.api.interactions.components.buttons.Button; // Not available in this JDA version
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
        return "Configura o sistema de tickets. Escolha o canal onde o botão de abrir ticket aparecerá. (Apenas administradores)";
    }
    
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (event.getGuild() == null) {
            event.getChannel().sendMessage("Este comando só pode ser usado em um servidor!")
                    .queue();
            return;
        }
        
        if (!accessControl.isAdmin(event.getMember())) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Sem Permissão")
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
                event.getChannel().sendMessage("Canal inválido! Mencione um canal de texto válido ou use o comando no canal desejado.\n**Exemplo:** `!ticket #suporte`")
                        .queue();
                return;
            }
        }
        
        EmbedBuilder supportEmbed = new EmbedBuilder()
                .setTitle("Suporte - PromoPing")
                .setDescription("**Precisa de ajuda ou suporte?** Clique no botão abaixo para abrir um ticket. Nossa equipe irá auxiliá-lo o mais breve possível!")
                .setColor(0x5865F2)
                .setTimestamp()
                .setFooter("©PromoPing • Todos os direitos reservados");        
        targetChannel.sendMessageEmbeds(supportEmbed.build())        // // .setComponents(ActionRow.of(...)) // Not available
                .queue();
        
        EmbedBuilder confirmEmbed = new EmbedBuilder()
                .setTitle("Sistema de Tickets Configurado!")
                .setDescription("O botão de abrir ticket foi enviado para " + targetChannel.getAsMention())
                .setColor(0x00ff00)
                .setTimestamp();
        
        event.getChannel().sendMessageEmbeds(confirmEmbed.build()).queue();
    }
}
