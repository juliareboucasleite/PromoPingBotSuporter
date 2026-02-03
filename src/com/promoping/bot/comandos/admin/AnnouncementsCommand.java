package com.promoping.bot.comandos.admin;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.dao.WebhookConfigDAO;
import com.promoping.bot.security.AccessControl;
import com.promoping.bot.services.AnnouncementsService;
import com.promoping.bot.utils.BotConfig;
import com.promoping.bot.utils.EmbedBuilder;

import java.util.HashSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Gerencia notificações de releases do GitHub no canal announcements.
 */
public class AnnouncementsCommand extends BaseCommand {
    
    private static final Set<String> ALLOWED_ROLE_IDS = new HashSet<>(Arrays.asList("1442655601682419722", "1442937735253065758"));
    private static final String ANNOUNCEMENTS_CHANNEL_ID = "1442931993888428143";
    
    private final AnnouncementsService announcementsService;
    private final AccessControl accessControl;
    
    public AnnouncementsCommand(AnnouncementsService announcementsService, AccessControl accessControl) {
        this.announcementsService = announcementsService;
        this.accessControl = accessControl;
    }
    
    @Override
    public String getName() {
        return "announcements";
    }
    
    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("anuncios", "github", "release");
    }
    
    @Override
    public String getDescription() {
        return "Gerencia notificações de releases do GitHub no canal announcements.";
    }
    
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        String action = args.length > 0 ? args[0].toLowerCase() : "status";
        MessageChannel channel = event.getChannel();
        String prefix = BotConfig.getPrefix();
        
        // Verificar permissões
        if (action.equals("sincronizar") || action.equals("sync")) {
            boolean hasAllowedRole = event.getMember().getRoles().stream()
                    .anyMatch(role -> ALLOWED_ROLE_IDS.contains(role.getId()));
            
            if (!hasAllowedRole) {
                channel.sendMessage("Você não tem permissão para sincronizar releases. Apenas membros com o cargo específico podem usar esta função.")
                        .queue();
                return;
            }
        } else {
            if (!accessControl.isAdmin(event.getMember())) {
                channel.sendMessage("Você precisa de permissões de administrador para usar este comando.")
                        .queue();
                return;
            }
        }
        
        try {
            if (action.equals("status") || action.equals("info") || action.isEmpty()) {
                WebhookConfigDAO.WebhookConfig config = announcementsService.getConfig();
                
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Configuração de Announcements")
                        .setDescription("Notificações de releases do GitHub")
                        .setColor(0xf4af55)
                        .setTimestamp();
                
                if (config == null) {
                    embed.addField("Status", "Webhook não configurado", false);
                } else {
                    embed.addField("Status", "Webhook configurado", true)
                            .addField("Ativo", config.isActive() ? "Sim" : "Não", true)
                            .addField("Canal", "<#" + ANNOUNCEMENTS_CHANNEL_ID + ">", true);
                    
                    String webhookUrl = config.getWebhookUrl();
                    if (webhookUrl != null && webhookUrl.length() > 50) {
                        embed.addField("Webhook URL", "`" + webhookUrl.substring(0, 50) + "...`", false);
                    } else if (webhookUrl != null) {
                        embed.addField("Webhook URL", "`" + webhookUrl + "`", false);
                    } else {
                        embed.addField("Webhook URL", "Não configurado", false);
                    }
                }
                
                embed.addField("Canal de Notificações", "<#" + ANNOUNCEMENTS_CHANNEL_ID + ">", false);
                
                channel.sendMessageEmbeds(embed.build()).queue();
                
            } else if (action.equals("configurar") || action.equals("config")) {
                if (args.length < 2) {
                    channel.sendMessage("Por favor, forneça a URL do webhook.\n**Uso:** `" + prefix + "announcements configurar <webhook-url>`")
                            .queue();
                    return;
                }
                
                String webhookUrl = args[1];
                announcementsService.saveConfig(webhookUrl);
                
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Webhook Configurado")
                        .setDescription("A configuração do webhook foi salva.")
                        .addField("Informações", 
                                "• Tipo: GitHub Releases\n" +
                                "• Canal: <#" + ANNOUNCEMENTS_CHANNEL_ID + ">\n" +
                                "• Status: Ativo", false)
                        .setColor(0x00ff00)
                        .setTimestamp();
                
                channel.sendMessageEmbeds(embed.build()).queue();
                
            } else if (action.equals("testar") || action.equals("test")) {
                announcementsService.sendTestNotification(event.getJDA());
                channel.sendMessage("Notificação de teste enviada no canal announcements!").queue();
                
            } else if (action.equals("sincronizar") || action.equals("sync")) {
                channel.sendMessage("Sincronizando releases do GitHub... Isso pode levar alguns segundos.").queue(msg -> {
                    AnnouncementsService.SyncResult result = announcementsService.syncReleases();
                    
                    if (result.isSuccess()) {
                        EmbedBuilder embed = new EmbedBuilder()
                                .setTitle("Sincronização Concluída")
                                .setDescription("Todas as releases do GitHub foram sincronizadas.")
                                .addField("Total de Releases", String.valueOf(result.getTotal()), true)
                                .addField("Enviadas", String.valueOf(result.getSent()), true)
                                .addField("Já Processadas", String.valueOf(result.getSkipped()), true)
                                .setColor(0x00ff00)
                                .setTimestamp();
                        
                        msg.editMessageEmbeds(embed.build()).queue();
                    } else {
                        msg.editMessage("Erro ao sincronizar: " + (result.getError() != null ? result.getError() : "Erro desconhecido")).queue();
                    }
                });
                
            } else {
                channel.sendMessage(
                        "Ação inválida!\n\n" +
                        "**Ações disponíveis:**\n" +
                        "• `status` - Mostra status da configuração\n" +
                        "• `configurar <url>` - Configura webhook URL (opcional)\n" +
                        "• `testar` - Envia uma notificação de teste\n" +
                        "• `sincronizar` - Sincroniza todas as releases do GitHub (requer cargo específico)\n\n" +
                        "**Exemplo:** `" + prefix + "announcements status`"
                ).queue();
            }
            
        } catch (Exception e) {
            channel.sendMessage("Ocorreu um erro ao processar o comando. Tente novamente.").queue();
        }
    }
}
