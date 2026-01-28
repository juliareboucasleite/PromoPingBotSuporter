package com.promoping.bot.main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.promoping.bot.dao.CountingDAO;
import com.promoping.bot.dao.NewsConfigDAO;
import com.promoping.bot.dao.ReviewDAO;
import com.promoping.bot.dao.TwitchChannelDAO;
import com.promoping.bot.dao.WebhookConfigDAO;
import com.promoping.bot.security.AccessControl;
import com.promoping.bot.services.AnnouncementsService;
import com.promoping.bot.services.ChannelService;
import com.promoping.bot.services.MessageService;
import com.promoping.bot.services.StatusService;

import java.util.Set;

/**
 * Classe principal do bot Discord PromoPing.
 */
public class Bot extends ListenerAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(Bot.class);
    
    private final JDA jda;
    private final AccessControl accessControl;
    private final ChannelService channelService;
    private final MessageService messageService;
    private final StatusService statusService;
    private final CountingDAO countingDAO;
    private final WebhookConfigDAO webhookConfigDAO;
    private final NewsConfigDAO newsConfigDAO;
    private final TwitchChannelDAO twitchChannelDAO;
    private final AnnouncementsService announcementsService;
    private final ReviewDAO reviewDAO;
    private com.promoping.bot.comandos.general.ReviewCommand reviewCommand;
    
    public Bot(String token, String prefix, Set<Long> adminIds) {
        this.accessControl = new AccessControl();
        this.channelService = new ChannelService();
        this.messageService = new MessageService();
        this.statusService = new StatusService();
        this.countingDAO = new CountingDAO();
        this.webhookConfigDAO = new WebhookConfigDAO();
        this.newsConfigDAO = new NewsConfigDAO();
        this.twitchChannelDAO = new TwitchChannelDAO();
        this.announcementsService = new AnnouncementsService(webhookConfigDAO);
        this.reviewDAO = new ReviewDAO();
        this.reviewCommand = new com.promoping.bot.comandos.general.ReviewCommand(reviewDAO);
        
        registerCommands();
        
        this.jda = JDABuilder.createDefault(token)
                .setActivity(Activity.watching("PromoPing"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(this)
                .build();
    }
    
    private void registerCommands() {
        logger.info("Bot initialized");
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
    }
    
    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().startsWith("review_tipo_")) {
            String userId = event.getUser().getId();
            String selectedValue = event.getValues().get(0);
            String tipo = selectedValue.split("_")[0]; // site, bot, ou suporte
            
            event.deferReply().queue();
            
            com.promoping.bot.utils.EmbedBuilder embed = new com.promoping.bot.utils.EmbedBuilder()
                    .setTitle("Anonimato")
                    .setDescription("Você está avaliando: **" + 
                            (tipo.equals("site") ? "Site" : tipo.equals("bot") ? "Bot" : "Suporte") + "**\n\n" +
                            "Deseja que sua avaliação seja anónima?")
                    .setColor(0x5865F2)
                    .setTimestamp();
            
            event.getHook().editOriginalEmbeds(embed.build()).queue();
        }
    }
    
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().startsWith("review_")) {
            String componentId = event.getComponentId();
            
            if (componentId.startsWith("review_anonimo_")) {
                String[] parts = componentId.split("_");
                boolean isAnonimo = parts[2].equals("sim");
                String tipo = parts[3];
                String userId = parts[4];
                
                if (!userId.equals(event.getUser().getId())) {
                    event.reply("Este botão não é para você!").setEphemeral(true).queue();
                    return;
                }
                
                event.deferReply().queue();
                
                com.promoping.bot.utils.EmbedBuilder embed = new com.promoping.bot.utils.EmbedBuilder()
                        .setTitle("Avaliação")
                        .setDescription(
                                "**Avaliando:** " + (tipo.equals("site") ? "Site" : tipo.equals("bot") ? "Bot" : "Suporte") + "\n" +
                                "**Anónimo:** " + (isAnonimo ? "Sim" : "Não") + "\n\n" +
                                "**Por favor, envie sua avaliação:**\n" +
                                "Use o comando `!review-texto <sua avaliação>`\n\n" +
                                "**Exemplo:** `!review-texto Excelente serviço! Muito útil.`"
                        )
                        .addField("Dica", "Você também pode incluir uma nota de 1 a 5 estrelas usando: `!review-texto 5 Estrelas Excelente!`", false)
                        .setColor(0x00ff00)
                        .setTimestamp();
                
                event.getHook().editOriginalEmbeds(embed.build())
                        .setComponents()
                        .queue();
                
            } else if (componentId.startsWith("review_rating_")) {
                String[] parts = componentId.split("_");
                int rating = Integer.parseInt(parts[2]);
                String tipo = parts[3];
                String userId = parts[4];
                
                if (!userId.equals(event.getUser().getId())) {
                    event.reply("Este botão não é para você!").setEphemeral(true).queue();
                    return;
                }
                
                event.deferReply().queue();
                
                StringBuilder stars = new StringBuilder();
                for (int i = 0; i < rating; i++) {
                    stars.append("★");
                }
                for (int i = 0; i < (5 - rating); i++) {
                    stars.append("☆");
                }
                
                com.promoping.bot.utils.EmbedBuilder embed = new com.promoping.bot.utils.EmbedBuilder()
                        .setTitle("Rating Selecionado")
                        .setDescription("**Rating:** " + stars.toString() + " (" + rating + "/5)")
                        .setColor(0x00ff00)
                        .setTimestamp();
                
                event.getHook().editOriginalEmbeds(embed.build())
                        .setComponents()
                        .queue();
            }
        }
        
        if (event.getComponentId().equals("aceitar_regras_promoping")) {
            if (event.getGuild() == null) {
                event.reply("Este botão só funciona dentro de um servidor.").setEphemeral(true).queue();
                return;
            }
            
            Member member = event.getMember();
            if (member == null) {
                event.reply("Não consegui identificar seu usuário no servidor.").setEphemeral(true).queue();
                return;
            }
            
            Role role = event.getGuild().getRoleById("1443627596565712978");
            if (role == null) {
                event.reply("Cargo não encontrado. Avise a equipe.").setEphemeral(true).queue();
                return;
            }
            
            if (member.getRoles().contains(role)) {
                event.reply("Você já tem esse cargo.").setEphemeral(true).queue();
                return;
            }
            
            event.deferReply(true).queue();
            event.getGuild().addRoleToMember(member, role).queue(
                    success -> event.getHook().editOriginal("Cargo atribuído! Obrigado por aceitar as regras.").queue(),
                    error -> event.getHook().editOriginal("Não consegui atribuir o cargo. Verifique as permissões do bot.").queue()
            );
        }
    }
    
    public void start() {
        try {
            jda.awaitReady();
            logger.info("Bot iniciado com sucesso!");
        } catch (InterruptedException e) {
            logger.error("Erro ao iniciar bot", e);
            Thread.currentThread().interrupt();
        }
    }
    
    public void shutdown() {
        jda.shutdown();
        logger.info("Bot encerrado");
    }
    
    public JDA getJDA() {
        return jda;
    }
}
