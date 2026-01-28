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
 * Configura o painel de sponsors.
 */
public class SponsorPanelCommand extends BaseCommand {

    private final AccessControl accessControl;

    public SponsorPanelCommand(AccessControl accessControl) {
        this.accessControl = accessControl;
    }

    @Override
    public String getName() {
        return "sponsor-panel";
    }

    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("painel-sponsor", "setup-sponsor", "sponsors-panel");
    }

    @Override
    public String getDescription() {
        return "Configura o painel de sponsors. Escolha o canal onde o painel aparecera. (Apenas administradores)";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (event.getGuild() == null) {
            event.getChannel().sendMessage("Este comando so pode ser usado em um servidor!").queue();
            return;
        }

        if (!accessControl.isAdmin(event.getMember())) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Sem Permissao")
                    .setDescription("Apenas administradores podem configurar o painel de sponsors!")
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
                event.getChannel().sendMessage("Canal invalido! Mencione um canal de texto valido ou use o comando no canal desejado.\n**Exemplo:** `!sponsor-panel #sponsors`")
                        .queue();
                return;
            }
        }

        String patreonUrl = "https://patreon.com/PromoPing";
        String paypalUrl = "https://www.paypal.com/donate/?hosted_button_id=SCCD4N72ZGXTW";
        String instagramUrl = "https://www.instagram.com/rwboucas/";
        String githubUrl = "https://github.com/juliareboucasleite/PromoPing";

        EmbedBuilder sponsorEmbed = new EmbedBuilder()
                .setTitle("Sponsor PromoPing")
                .setDescription(
                        "**Sponsor juliareboucasleite/PromoPing**\n\n" +
                        "Apoie o projeto e ajude a manter o PromoPing ativo.\n\n" +
                        "**Links oficiais:**\n" +
                        "• Patreon\n" +
                        "• PayPal\n" +
                        "• Instagram\n" +
                        "• GitHub"
                )
                .setColor(0xffa500)
                .setTimestamp()
                .setFooter("PromoPing • Obrigado pelo apoio");

        targetChannel.sendMessageEmbeds(sponsorEmbed.build())
                .setComponents(ActionRow.of(
                        Button.link(patreonUrl, "Patreon"),
                        Button.link(paypalUrl, "PayPal"),
                        Button.link(instagramUrl, "Instagram"),
                        Button.link(githubUrl, "GitHub")
                ))
                .queue();

        EmbedBuilder confirmEmbed = new EmbedBuilder()
                .setTitle("Painel de Sponsors Configurado!")
                .setDescription("O painel de sponsors foi enviado para " + targetChannel.getAsMention())
                .setColor(0x00ff00)
                .setTimestamp();

        event.getChannel().sendMessageEmbeds(confirmEmbed.build()).queue();
    }
}
