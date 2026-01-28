package com.promoping.bot.comandos.general;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.dao.ReviewDAO;
import com.promoping.bot.services.ReviewSessionStore;
import com.promoping.bot.utils.EmbedBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReviewCommand extends BaseCommand {
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewCommand.class);
    private static final String REVIEWS_CHANNEL_ID = System.getenv("DISCORD_REVIEWS_CHANNEL_ID");
    
    private final ReviewDAO reviewDAO;
    
    public ReviewCommand(ReviewDAO reviewDAO) {
        this.reviewDAO = reviewDAO;
    }
    
    @Override
    public String getName() {
        return "review";
    }
    
    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("avaliar", "avaliacao", "feedback");
    }
    
    @Override
    public String getDescription() {
        return "Deixa uma avaliacao sobre o site, bot ou suporte. Pode escolher ser anonimo.";
    }
    
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (event.getGuild() == null) {
            event.getChannel().sendMessage("Este comando so pode ser usado em um servidor!").queue();
            return;
        }

        try {
            EmbedBuilder initialEmbed = new EmbedBuilder()
                    .setTitle("Sistema de Avaliacoes")
                    .setDescription("Escolha o que deseja avaliar:")
                    .setColor(0xffa500)
                    .setTimestamp()
                    .setFooter("PromoPing - Avaliacoes");

            StringSelectMenu menu = StringSelectMenu.create("review_tipo_select")
                    .setPlaceholder("Selecione o que deseja avaliar...")
                    .addOption("Site", "site", "Avaliar o site PromoPing")
                    .addOption("Bot", "bot", "Avaliar o bot Discord")
                    .addOption("Suporte", "suporte", "Avaliar o atendimento de suporte")
                    .build();

            ReviewSessionStore.startSession(event.getAuthor().getId(), event.getChannel().getId());

            event.getMessage().delete().queue(
                    success -> {},
                    error -> {}
            );

            event.getAuthor().openPrivateChannel().queue(dm -> {
                dm.sendMessageEmbeds(initialEmbed.build())
                        .setComponents(ActionRow.of(menu))
                        .queue(
                                success -> {},
                                error -> event.getChannel()
                                        .sendMessageEmbeds(initialEmbed.build())
                                        .setComponents(ActionRow.of(menu))
                                        .queue(msg -> msg.delete().queueAfter(20, TimeUnit.SECONDS))
                        );
            }, error -> {
                event.getChannel()
                        .sendMessageEmbeds(initialEmbed.build())
                        .setComponents(ActionRow.of(menu))
                        .queue(msg -> msg.delete().queueAfter(20, TimeUnit.SECONDS));
            });

        } catch (Exception e) {
            logger.error("Erro no comando review", e);
            event.getChannel().sendMessage("Erro interno! Tente novamente em alguns minutos.").queue();
        }
    }

    public void processReview(String userId, String tipo, String texto, Integer rating, boolean isAnonimo, 
                             MessageReceivedEvent event) {
        try {

            String referenciaId = reviewDAO.getReferenciaIdByDiscordId(userId);
            if (referenciaId == null) {
                event.getChannel().sendMessage("Voce precisa estar registado no sistema. Use `/registar` primeiro.").queue();
                return;
            }

            if (reviewDAO.hasRecentReview(referenciaId, tipo)) {
                event.getChannel().sendMessage("Voce ja enviou uma avaliacao recentemente. Aguarde alguns minutos.").queue();
                return;
            }

            int reviewId = reviewDAO.saveReview(referenciaId, tipo, texto, rating, isAnonimo);
            logger.info("Review salva: ID={}, Tipo={}, Rating={}", reviewId, tipo, rating);

            String tipoNome = tipo.equals("site") ? "Site" : tipo.equals("bot") ? "Bot" : "Suporte";
            int color = rating != null ? (rating >= 4 ? 0x00ff00 : rating >= 3 ? 0xffa500 : 0xff0000) : 0x5865F2;
            
            EmbedBuilder reviewEmbed = new EmbedBuilder()
                    .setTitle("Avaliacao - " + tipoNome)
                    .setDescription(texto != null && !texto.isEmpty() ? texto : "*Sem texto*")
                    .setColor(color)
                    .setTimestamp()
                    .setFooter("PromoPing - Avaliacoes");
            
            if (rating != null) {
                String stars = repeatString(":star:", rating);
                reviewEmbed.addField("Avaliacao", stars + " (" + rating + "/5)", false);
            }
            
            if (isAnonimo) {
                reviewEmbed.setAuthor("Avaliacao Anonima");
            } else {
                reviewEmbed.setAuthor("Avaliacao Anonima");
            }
            
            TextChannel reviewsChannel = null;
            if (REVIEWS_CHANNEL_ID != null && !REVIEWS_CHANNEL_ID.isEmpty()) {
                reviewsChannel = event.getJDA().getTextChannelById(REVIEWS_CHANNEL_ID);
            }
            
            if (reviewsChannel == null) {
                reviewsChannel = event.getGuild().getTextChannels().stream()
                        .filter(ch -> ch.getName().equals("reviews"))
                        .findFirst()
                        .map(TextChannel.class::cast)
                        .orElse(null);
            }
            
            if (reviewsChannel != null) {
                reviewsChannel.sendMessageEmbeds(reviewEmbed.build()).queue();
            } else {
                event.getChannel().sendMessageEmbeds(reviewEmbed.build()).queue();
            }

            EmbedBuilder confirmEmbed = new EmbedBuilder()
                    .setTitle("Avaliacao - " + tipoNome)
                    .setDescription(reviewsChannel != null ? "Sua avaliacao foi enviada para " + reviewsChannel.getAsMention() : "Sua avaliacao foi enviada!")
                    .setColor(0x00ff00)
                    .setTimestamp();
            
            event.getAuthor().openPrivateChannel().queue(dm -> {
                dm.sendMessageEmbeds(confirmEmbed.build()).queue(
                        success -> {},
                        error -> event.getChannel()
                                .sendMessage(event.getAuthor().getAsMention() + " - Sua avaliacao foi enviada!")
                                .queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS))
                );
            }, error -> {
                event.getChannel()
                        .sendMessage(event.getAuthor().getAsMention() + " - Sua avaliacao foi enviada!")
                        .queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
            });
            
        } catch (Exception e) {
            logger.error("Erro ao processar review", e);
            event.getAuthor().openPrivateChannel().queue(dm -> {
                dm.sendMessage("Erro ao processar sua avaliacao. Tente novamente.").queue();
            }, error -> {
                event.getChannel().sendMessage("Erro ao processar sua avaliacao. Tente novamente.")
                        .queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            });
        }
    }
    
    private static String repeatString(String str, int count) {
        if (count <= 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}
