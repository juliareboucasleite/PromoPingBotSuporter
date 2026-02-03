package com.promoping.bot.comandos.admin;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
// import net.dv8tion.jda.api.interactions.components.ActionRow; // Not available in this JDA version
// import net.dv8tion.jda.api.interactions.components.buttons.Button; // Not available in this JDA version
import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.security.AccessControl;
import com.promoping.bot.utils.BotConfig;
import com.promoping.bot.utils.EmbedBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Configura o painel de sugestões.
 */
public class SetupSugestaoCommand extends BaseCommand {
    
    private final AccessControl accessControl;
    
    public SetupSugestaoCommand(AccessControl accessControl) {
        this.accessControl = accessControl;
    }
    
    @Override
    public String getName() {
        return "setup-sugestao";
    }
    
    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("config-sugestao", "setup-sugerir", "config-sugerir");
    }
    
    @Override
    public String getDescription() {
        return "Configura o painel de sugestões. Escolha o canal onde o botão de sugerir aparecerá. (Apenas administradores)";
    }
    
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (event.getGuild() == null) {
            event.getChannel().sendMessage("Este comando só pode ser usado em um servidor!").queue();
            return;
        }
        
        if (!accessControl.isAdmin(event.getMember())) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Sem Permissão")
                    .setDescription("Apenas administradores podem configurar o painel de sugestões!")
                    .setColor(0xff0000)
                    .setTimestamp();
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }
        
TextChannel targetChannel = event.getChannel().asTextChannel();
        String prefix = BotConfig.getPrefix();

        if (args.length > 0) {
            String channelMention = args[0];
            String channelId = channelMention.replaceAll("[<#>]", "");

            TextChannel mentionedChannel = event.getGuild().getTextChannelById(channelId);
            if (mentionedChannel != null) {
                targetChannel = mentionedChannel;
            } else {
                event.getChannel().sendMessage("Canal inválido! Mencione um canal de texto válido ou use o comando no canal desejado.\n**Exemplo:** `" + prefix + "setup-sugestao #sugestoes`")
                        .queue();
                return;
            }
        }
        
        EmbedBuilder sugestaoPanelEmbed = new EmbedBuilder()
                .setTitle("Sugerir Funcionalidade")
                .setDescription(
                        "**Tem uma ideia para melhorar o PromoPing?**\n\n" +
                        "Clique no botão abaixo para abrir o formulário de sugestão.\n\n" +
                        "**O que você pode sugerir:**\n" +
                        "• Novas funcionalidades para o site\n" +
                        "• Melhorias no bot Discord\n" +
                        "• Recursos adicionais\n" +
                        "• Melhorias de interface\n\n" +
                        "**Sua sugestão será enviada diretamente para o painel administrativo!**"
                )
                .setColor(0x3B82F6)
                .setTimestamp()
                .setFooter("©PromoPing • Todos os direitos reservados");        
        targetChannel.sendMessageEmbeds(sugestaoPanelEmbed.build())        // // .setComponents(ActionRow.of(...)) // Not available
                .queue();
        
        EmbedBuilder confirmEmbed = new EmbedBuilder()
                .setTitle("Painel de Sugestões Configurado!")
                .setDescription("O painel de sugestões foi enviado para " + targetChannel.getAsMention())
                .setColor(0x00ff00)
                .setTimestamp();
        
        event.getChannel().sendMessageEmbeds(confirmEmbed.build()).queue();
    }
}
