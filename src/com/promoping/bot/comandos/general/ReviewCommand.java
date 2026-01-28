package com.promoping.bot.comandos.general;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.dao.ReviewDAO;
import com.promoping.bot.utils.EmbedBuilder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReviewCommand extends BaseCommand {
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewCommand.class);
    private static final String REVIEWS_CHANNEL_ID = System.getenv("DISCORD_REVIEWS_CHANNEL_ID");
    
    private static final Map<String, ReviewSession> activeSessions = new ConcurrentHashMap<>();
    
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
        return Arrays.asList("avaliar", "avaliação", "feedback");
    }
    
    @Override
    public String getDescription() {
        return "Deixa uma avaliação sobre o site, bot ou suporte. Pode escolher ser anónimo.";
    }
    
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (event.getGuild() == null) {
            event.getChannel().sendMessage("Este comando só pode ser usado em um servidor!").queue();
            return;
        }
        
        String userId = event.getAuthor().getId();
        String sessionKey = userId + "_" + event.getChannel().getId();
        
        if (activeSessions.containsKey(sessionKey)) {
            event.getChannel().sendMessage("Você já tem uma avaliação em progresso. Complete ou aguarde o timeout.").queue();
            return;
        }
        
        try {
            EmbedBuilder initialEmbed = new EmbedBuilder()
                    .setTitle("Sistema de Avaliações")
                    .setDescription("Escolha o que deseja avaliar:")
                    .setColor(0xffa500)
                    .setTimestamp()
                    .setFooter("PromoPing - Avaliações");
            
            MessageCreateBuilder messageBuilder = new MessageCreateBuilder()
                    .setEmbeds(initialEmbed.build());
            
            event.getChannel().sendMessage(messageBuilder.build()).queue(msg -> {
                ReviewSession session = new ReviewSession(userId, event.getAuthor().getName(), 
                        event.getAuthor().getAvatarUrl(), msg.getId());
                activeSessions.put(sessionKey, session);

                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                scheduler.schedule(() -> {
                    activeSessions.remove(sessionKey);
                    scheduler.shutdown();
                }, 5, TimeUnit.MINUTES);
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
                event.getChannel().sendMessage("Você precisa estar registado no sistema. Use `/registar` primeiro.").queue();
                return;
            }

            if (reviewDAO.hasRecentReview(referenciaId, tipo)) {
                event.getChannel().sendMessage("Você já enviou uma avaliação recentemente. Aguarde alguns minutos.").queue();
                return;
            }

            int reviewId = reviewDAO.saveReview(referenciaId, tipo, texto, rating, isAnonimo);
            logger.info("Review salva: ID={}, Tipo={}, Rating={}", reviewId, tipo, rating);

            String tipoNome = tipo.equals("site") ? "Site" : tipo.equals("bot") ? "Bot" : "Suporte";
            int color = rating != null ? (rating >= 4 ? 0x00ff00 : rating >= 3 ? 0xffa500 : 0xff0000) : 0x5865F2;
            
            EmbedBuilder reviewEmbed = new EmbedBuilder()
                    .setTitle("Avaliação - " + tipoNome)
                    .setDescription(texto != null && !texto.isEmpty() ? texto : "*Sem texto*")
                    .setColor(color)
                    .setTimestamp()
                    .setFooter("PromoPing - Avaliações");
            
            if (rating != null) {
                String stars = repeatString("★", rating) + repeatString("☆", 5 - rating);
                reviewEmbed.addField("Avaliação", stars + " (" + rating + "/5)", false);
            }
            
            if (isAnonimo) {
                reviewEmbed.setAuthor("Avaliação Anónima");
            } else {
                reviewEmbed.setAuthor(event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
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
                    .setTitle("Avaliação Enviada!")
                    .setDescription(reviewsChannel != null ? "Sua avaliação foi enviada para " + reviewsChannel.getAsMention() : "Sua avaliação foi enviada!")
                    .setColor(0x00ff00)
                    .setTimestamp();
            
            event.getAuthor().openPrivateChannel().queue(dm -> {
                dm.sendMessageEmbeds(confirmEmbed.build()).queue(
                        success -> {},
                        error -> {
                           
                            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " - Sua avaliação foi enviada!")
                                    .queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
                        }
                );
            });
            
        } catch (Exception e) {
            logger.error("Erro ao processar review", e);
            event.getChannel().sendMessage("Erro ao processar sua avaliação. Tente novamente.").queue();
        }
    }
    
    private static class ReviewSession {
        private final String userId;
        private final String userName;
        private final String userAvatar;
        private final String messageId;
        
        public ReviewSession(String userId, String userName, String userAvatar, String messageId) {
            this.userId = userId;
            this.userName = userName;
            this.userAvatar = userAvatar;
            this.messageId = messageId;
        }
        
        public String getUserId() { return userId; }
        public String getUserName() { return userName; }
        public String getUserAvatar() { return userAvatar; }
        public String getMessageId() { return messageId; }
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
