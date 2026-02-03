package com.promoping.bot.comandos.admin;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.security.AccessControl;
import com.promoping.bot.utils.BotConfig;
import com.promoping.bot.utils.EmbedBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Configura o painel de avaliações no canal.
 */
public class ReviewPanelCommand extends BaseCommand {
    
    private final AccessControl accessControl;
    
    public ReviewPanelCommand(AccessControl accessControl) {
        this.accessControl = accessControl;
    }
    
    @Override
    public String getName() {
        return "review-panel";
    }
    
    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("painel-review", "setup-review");
    }
    
    @Override
    public String getDescription() {
        return "Configura o painel de avaliações no canal. Escolha o canal onde o painel aparecerá. (Apenas administradores)";
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
                    .setDescription("Apenas administradores podem configurar o painel de avaliações!")
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
                event.getChannel().sendMessage("Canal inválido! Mencione um canal de texto válido ou use o comando no canal desejado.\n**Exemplo:** `" + prefix + "review-panel #reviews`")
                        .queue();
                return;
            }
        }
        
        EmbedBuilder reviewPanelEmbed = new EmbedBuilder()
                .setTitle("Sistema de Avaliações - PromoPing")
                .setDescription(
                        "**Deixe sua avaliação sobre nossos serviços!**\n\n" +
                        "Avalie o **Site**, **Bot** ou **Suporte** e ajude-nos a melhorar.\n\n" +
                        "**Como funciona:**\n" +
                        "1. Clique no botão abaixo\n" +
                        "2. Escolha o que deseja avaliar\n" +
                        "3. Decida se quer ser anónimo\n" +
                        "4. Envie sua avaliação\n\n" +
                        "**Você pode incluir uma nota de 1 a 5 estrelas na sua avaliação!**"
                )
                .setColor(0xffa500)
                .addField("Comandos Disponíveis", "`" + prefix + "review` - Iniciar avaliação\n`/review` - Iniciar avaliação (slash command)", false)
                .setTimestamp()
                .setFooter("©PromoPing • Todos os direitos reservados");        
        targetChannel.sendMessageEmbeds(reviewPanelEmbed.build())
                .setComponents(ActionRow.of(Button.primary("review_start", "Deixar Avaliacao")))
                .queue();
        
        EmbedBuilder confirmEmbed = new EmbedBuilder()
                .setTitle("Painel de Avaliações Configurado!")
                .setDescription("O painel de avaliações foi enviado para " + targetChannel.getAsMention())
                .setColor(0x00ff00)
                .setTimestamp();
        
        event.getChannel().sendMessageEmbeds(confirmEmbed.build()).queue();
    }
}
