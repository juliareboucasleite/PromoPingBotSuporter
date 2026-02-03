package com.promoping.bot.comandos.admin;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.dao.NewsConfigDAO;
import com.promoping.bot.security.AccessControl;
import com.promoping.bot.utils.BotConfig;
import com.promoping.bot.utils.EmbedBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Configura o sistema de notícias automáticas sobre categorias monitoradas.
 */
public class NewsCommand extends BaseCommand {
    
    private final NewsConfigDAO newsConfigDAO;
    private final AccessControl accessControl;
    
    public NewsCommand(NewsConfigDAO newsConfigDAO, AccessControl accessControl) {
        this.newsConfigDAO = newsConfigDAO;
        this.accessControl = accessControl;
    }
    
    @Override
    public String getName() {
        return "news";
    }
    
    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("noticias", "news-config");
    }
    
    @Override
    public String getDescription() {
        return "Configura o sistema de notícias automáticas sobre categorias monitoradas.";
    }
    
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (!accessControl.isAdmin(event.getMember())) {
            event.getChannel().sendMessage("Você precisa de permissões de administrador para usar este comando.")
                    .queue();
            return;
        }
        
        String prefix = BotConfig.getPrefix();
        String action = args.length > 0 ? args[0].toLowerCase() : "status";
        MessageChannel channel = event.getChannel();
        
        try {
            if (action.equals("status") || action.equals("info") || action.isEmpty()) {
                NewsConfigDAO.NewsConfig config = newsConfigDAO.getActiveConfig();
                
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Configuração de Notícias")
                        .setDescription("Sistema de notícias automáticas sobre categorias monitoradas")
                        .setColor(0x5865F2)
                        .setTimestamp();
                
                if (config == null) {
                    embed.addField("Status", "Sistema não configurado", false)
                            .addField("Como configurar", "Use `" + prefix + "news configurar <canal-id>` para ativar o sistema de notícias.", false);
                } else {
                    TextChannel newsChannel = event.getJDA().getTextChannelById(config.getChannelId());
                    
                    embed.addField("Status", "Sistema ativo", true)
                            .addField("Canal", newsChannel != null ? "<#" + config.getChannelId() + ">" : "Canal não encontrado", true)
                            .addField("Frequência", (config.getCheckInterval() > 0 ? config.getCheckInterval() : 60) + " minutos", true)
                            .addField("Categorias Monitoradas", 
                                    config.getMonitoredCategories() != null && !config.getMonitoredCategories().isEmpty() 
                                    ? config.getMonitoredCategories() : "Todas as categorias", false);
                }
                
                channel.sendMessageEmbeds(embed.build()).queue();
                
            } else if (action.equals("configurar") || action.equals("config")) {
                if (args.length < 2) {
                    channel.sendMessage("Por favor, forneça o ID do canal.\n**Uso:** `" + prefix + "news configurar <canal-id>`\n**Exemplo:** `" + prefix + "news configurar 123456789012345678`")
                            .queue();
                    return;
                }
                
                String channelId = args[1];
                TextChannel targetChannel = event.getJDA().getTextChannelById(channelId);
                
                if (targetChannel == null) {
                    channel.sendMessage("Canal não encontrado! Verifique o ID do canal.").queue();
                    return;
                }
                
                if (!event.getGuild().getSelfMember().hasPermission(targetChannel, Permission.MESSAGE_SEND)) {
                    channel.sendMessage("O bot não tem permissão para enviar mensagens nesse canal!").queue();
                    return;
                }
                
                newsConfigDAO.createOrUpdate(channelId);
                
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Canal de Notícias Configurado")
                        .setDescription("O sistema de notícias foi configurado com sucesso!")
                        .addField("Canal", "<#" + channelId + ">", true)
                        .addField("Status", "Ativo", true)
                        .setColor(0x00ff00)
                        .setTimestamp();
                
                channel.sendMessageEmbeds(embed.build()).queue();
                
            } else if (action.equals("testar") || action.equals("test")) {
                NewsConfigDAO.NewsConfig config = newsConfigDAO.getActiveConfig();
                
                if (config == null) {
                    channel.sendMessage("Sistema de notícias não configurado! Use `" + prefix + "news configurar <canal-id>` primeiro.").queue();
                    return;
                }
                
                TextChannel newsChannel = event.getJDA().getTextChannelById(config.getChannelId());
                if (newsChannel == null) {
                    channel.sendMessage("Canal de notícias não encontrado!").queue();
                    return;
                }
                
                EmbedBuilder testEmbed = new EmbedBuilder()
                        .setTitle("Notícia de Teste - Tecnologia")
                        .setDescription("Esta é uma notícia de teste para verificar o sistema de notícias automáticas.")
                        .addField("Categoria", "Tecnologia", true)
                        .addField("Impacto", "Alto (8/10)", true)
                        .addField("Fonte", "Teste", true)
                        .setColor(0x5865F2)
                        .setTimestamp()
                        .setFooter("PromoPing - Notícias Automáticas");
                
                newsChannel.sendMessageEmbeds(testEmbed.build()).queue();
                channel.sendMessage("Notícia de teste enviada no canal de notícias!").queue();
                
            } else if (action.equals("desativar") || action.equals("disable")) {
                newsConfigDAO.deactivate();
                
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Sistema Desativado")
                        .setDescription("O sistema de notícias foi desativado.")
                        .setColor(0xff9900)
                        .setTimestamp();
                
                channel.sendMessageEmbeds(embed.build()).queue();
                
            } else {
                channel.sendMessage(
                        "Ação inválida!\n\n" +
                        "**Ações disponíveis:**\n" +
                        "• `status` - Mostra status da configuração\n" +
                        "• `configurar <canal-id>` - Configura canal de notícias\n" +
                        "• `testar` - Envia uma notícia de teste\n" +
                        "• `desativar` - Desativa o sistema\n\n" +
                        "**Exemplo:** `" + prefix + "news configurar 123456789012345678`"
                ).queue();
            }
            
        } catch (Exception e) {
            channel.sendMessage("Ocorreu um erro ao processar o comando. Tente novamente.").queue();
        }
    }
}
