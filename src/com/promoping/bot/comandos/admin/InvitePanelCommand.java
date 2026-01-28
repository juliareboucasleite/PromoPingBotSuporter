package com.promoping.bot.comandos.admin;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.security.AccessControl;
import com.promoping.bot.utils.EmbedBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Configura o painel de convite do servidor.
 */
public class InvitePanelCommand extends BaseCommand {
    
    private final AccessControl accessControl;
    
    public InvitePanelCommand(AccessControl accessControl) {
        this.accessControl = accessControl;
    }
    
    @Override
    public String getName() {
        return "invite-panel";
    }
    
    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("painel-convite", "setup-invite", "convite-panel");
    }
    
    @Override
    public String getDescription() {
        return "Configura o painel de convite do servidor. Escolha o canal onde o painel aparecerá. (Apenas administradores)";
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
                    .setDescription("Apenas administradores podem configurar o painel de convite!")
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
                event.getChannel().sendMessage("Canal inválido! Mencione um canal de texto válido ou use o comando no canal desejado.\n**Exemplo:** `!invite-panel #bem-vindo`")
                        .queue();
                return;
            }
        }
        
        String inviteUrl = "https://discord.gg/VbukwrCqYU";
        String siteUrl = System.getenv("SITE_URL");
        if (siteUrl == null || siteUrl.isEmpty()) {
            siteUrl = "http://promoping.pt";
        }
        String botInviteUrl = String.format("https://discord.com/api/oauth2/authorize?client_id=%s&permissions=8&scope=bot%%20applications.commands",
                event.getJDA().getSelfUser().getId());
        
        EmbedBuilder invitePanelEmbed = new EmbedBuilder()
                .setTitle("PromoPing - Junte-se à Nossa Comunidade!")
                .setDescription(
                        "**Bem-vindo ao PromoPing!**\n\n" +
                        "Somos uma plataforma completa para monitorização de preços de produtos em tempo real.\n\n" +
                        "**O que oferecemos:**\n" +
                        "• **Site** - Interface web completa para gestão de produtos\n" +
                        "• **Bot Discord** - Notificações automáticas de mudanças de preço\n" +
                        "• **Suporte** - Equipa dedicada para ajudar\n" +
                        "• **Comunidade** - Partilha experiências e avaliações\n\n" +
                        "**Junte-se ao nosso servidor Discord e comece a monitorizar os melhores preços!**"
                )
                .setColor(0xffa500)
                .addField("Site", "[Acessar Site](" + siteUrl + ")", true)
                .addField("Bot", "[Adicionar Bot](" + botInviteUrl + ")", true)
                .addField("Suporte", "Use `!suporte` para criar um ticket", true)
                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                .setTimestamp()
                .setFooter("©PromoPing • Todos os direitos reservados");        
        targetChannel.sendMessageEmbeds(invitePanelEmbed.build())
                .setComponents(ActionRow.of(
                        Button.link(inviteUrl, "Entrar no Servidor"),
                        Button.link(siteUrl, "Acessar Site"),
                        Button.link(botInviteUrl, "Adicionar Bot")
                ))
                .queue();
        
        EmbedBuilder confirmEmbed = new EmbedBuilder()
                .setTitle("Painel de Convite Configurado!")
                .setDescription("O painel de convite foi enviado para " + targetChannel.getAsMention())
                .setColor(0x00ff00)
                .setTimestamp();
        
        event.getChannel().sendMessageEmbeds(confirmEmbed.build()).queue();
    }
}
