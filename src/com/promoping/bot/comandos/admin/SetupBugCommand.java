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
 * Configura o painel de reportar bugs.
 */
public class SetupBugCommand extends BaseCommand {
    
    private final AccessControl accessControl;
    
    public SetupBugCommand(AccessControl accessControl) {
        this.accessControl = accessControl;
    }
    
    @Override
    public String getName() {
        return "setup-bug";
    }
    
    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("config-bug", "setup-reportar", "config-reportar");
    }
    
    @Override
    public String getDescription() {
        return "Configura o painel de reportar bugs. Escolha o canal onde o botão de reportar bug aparecerá. (Apenas administradores)";
    }
    
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (event.getGuild() == null) {
            event.getChannel().sendMessage("Este comando só pode ser usado em um servidor!").queue();
            return;
        }
        String prefix = BotConfig.getPrefix();
        
        if (!accessControl.isAdmin(event.getMember())) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Sem Permissão")
                    .setDescription("Apenas administradores podem configurar o painel de reportar bugs!")
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
                event.getChannel().sendMessage("Canal inválido! Mencione um canal de texto válido ou use o comando no canal desejado.\n**Exemplo:** `" + prefix + "setup-bug #reportar-bugs`")
                        .queue();
                return;
            }
        }
        
        EmbedBuilder bugPanelEmbed = new EmbedBuilder()
                .setTitle("Reportar Bug")
                .setDescription(
                        "**Encontrou um bug ou problema?**\n\n" +
                        "Clique no botão abaixo para abrir o formulário de reporte.\n\n" +
                        "**O que você pode reportar:**\n" +
                        "• Bugs e erros no sistema\n" +
                        "• Problemas de funcionalidade\n" +
                        "• Sugestões de melhorias\n\n" +
                        "**Seu reporte será enviado diretamente para o painel administrativo!**"
                )
                .setColor(0xFF6B6B)
                .setTimestamp()
                .setFooter("©PromoPing • Todos os direitos reservados");        
        targetChannel.sendMessageEmbeds(bugPanelEmbed.build())        // // .setComponents(ActionRow.of(...)) // Not available
                .queue();
        
        EmbedBuilder confirmEmbed = new EmbedBuilder()
                .setTitle("Painel de Reportar Bugs Configurado!")
                .setDescription("O painel de reportar bugs foi enviado para " + targetChannel.getAsMention())
                .setColor(0x00ff00)
                .setTimestamp();
        
        event.getChannel().sendMessageEmbeds(confirmEmbed.build()).queue();
    }
}
