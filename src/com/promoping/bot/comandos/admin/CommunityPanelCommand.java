package com.promoping.bot.comandos.admin;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.security.AccessControl;
import com.promoping.bot.utils.EmbedBuilder;
import com.promoping.bot.utils.BotConfig;

import java.util.Arrays;
import java.util.List;

/**
 * Configura o painel de recursos da comunidade (GitHub Discussions).
 */
public class CommunityPanelCommand extends BaseCommand {
    
    private final AccessControl accessControl;
    
    public CommunityPanelCommand(AccessControl accessControl) {
        this.accessControl = accessControl;
    }
    
    @Override
    public String getName() {
        return "community-panel";
    }
    
    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("painel-community", "setup-community", "community-resources");
    }
    
    @Override
    public String getDescription() {
        return "Configura o painel de recursos da comunidade (GitHub Discussions). Escolha o canal onde o painel aparecerá. (Apenas administradores)";
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
                    .setDescription("Apenas administradores podem configurar o painel de recursos da comunidade!")
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
                event.getChannel().sendMessage("Canal inválido! Mencione um canal de texto válido ou use o comando no canal desejado.\n**Exemplo:** `" + prefix + "community-panel #community-resources`")
                        .queue();
                return;
            }
        }
        
        String githubDiscussionsUrl = "https://github.com/juliareboucasleite/PromoPing/discussions";
        String githubRepoUrl = "https://github.com/juliareboucasleite/PromoPing";
        String githubBotSuporterUrl = "https://github.com/juliareboucasleite/PromoPingBotSuporter";
        String siteUrl = System.getenv("SITE_URL");
        if (siteUrl == null || siteUrl.isEmpty()) {
            siteUrl = "http://promoping.pt";
        }
        
        EmbedBuilder communityPanelEmbed = new EmbedBuilder()
                .setTitle("Recursos da Comunidade - PromoPing")
                .setDescription(
                        "**Bem-vindo ao canal de recursos da comunidade!**\n\n" +
                        "Aqui encontrará links úteis para participar ativamente na comunidade PromoPing.\n\n" +
                        "**O que pode fazer:**\n" +
                        "• **Discutir** - Partilhe ideias, faça perguntas e participe em discussões\n" +
                        "• **Reportar Bugs** - Ajude-nos a melhorar reportando problemas\n" +
                        "• **Sugerir Funcionalidades** - Partilhe suas ideias para novas funcionalidades\n" +
                        "• **Colaborar** - Contribua para o projeto no GitHub\n\n" +
                        "**Junte-se às discussões e faça parte da nossa comunidade!**"
                )
                .setColor(0x24292e)
                .addField("GitHub Discussions", "[Participar nas Discussões](" + githubDiscussionsUrl + ")", false)
                .addField("Repositório GitHub", "[Ver Código Fonte](" + githubRepoUrl + ")", true)
                .addField("Bot de Suporte", "[GitHub Bot Suporter](" + githubBotSuporterUrl + ")", true)
                .addField("Site", "[Acessar Site](" + siteUrl + ")", true)
                .addField("Categorias Disponíveis", "Anúncios • Reportar Bugs • Sugestões • Geral • Q&A", false)
                .setThumbnail("https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png")
                .setTimestamp()
                .setFooter("©PromoPing • Todos os direitos reservados");        
        targetChannel.sendMessageEmbeds(communityPanelEmbed.build())
                .setComponents(ActionRow.of(
                        Button.link(githubDiscussionsUrl, "GitHub Discussions"),
                        Button.link(githubRepoUrl, "Repositorio GitHub"),
                        Button.link(githubBotSuporterUrl, "Bot Suporter"),
                        Button.link(siteUrl, "Acessar Site")
                ))
                .queue();
        
        EmbedBuilder confirmEmbed = new EmbedBuilder()
                .setTitle("Painel de Recursos da Comunidade Configurado!")
                .setDescription("O painel de recursos da comunidade foi enviado para " + targetChannel.getAsMention())
                .setColor(0x00ff00)
                .setTimestamp();
        
        event.getChannel().sendMessageEmbeds(confirmEmbed.build()).queue();
    }
}
