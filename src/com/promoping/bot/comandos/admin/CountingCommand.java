package com.promoping.bot.comandos.admin;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.dao.CountingDAO;
import com.promoping.bot.security.AccessControl;
import com.promoping.bot.utils.EmbedBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Gerencia o sistema de contagem no servidor.
 */
public class CountingCommand extends BaseCommand {
    
    private final CountingDAO countingDAO;
    private final AccessControl accessControl;
    
    public CountingCommand(CountingDAO countingDAO, AccessControl accessControl) {
        this.countingDAO = countingDAO;
        this.accessControl = accessControl;
    }
    
    @Override
    public String getName() {
        return "counting";
    }
    
    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("contagem", "count");
    }
    
    @Override
    public String getDescription() {
        return "Gerencia o sistema de contagem no servidor.";
    }
    
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (!accessControl.isAdmin(event.getMember())) {
            event.getChannel().sendMessage("Você precisa de permissões de administrador para usar este comando.")
                    .queue();
            return;
        }
        
        String action = args.length > 0 ? args[0].toLowerCase() : "status";
        
        try {
            if (action.equals("status") || action.equals("info") || action.isEmpty()) {
                CountingDAO.CountingConfig config = countingDAO.getConfig(event.getGuild().getId());
                
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Sistema de Contagem")
                        .setColor(0x5865F2)
                        .setTimestamp();
                
                if (config == null) {
                    embed.setDescription("Sistema de contagem não configurado.")
                            .addField("Como configurar", "Use `!counting configurar <canal>` para configurar um canal de contagem.", false);
                } else {
                    TextChannel channel = event.getGuild().getTextChannelById(config.getChannelId());
                    
                    embed.setDescription("Sistema de contagem ativo")
                            .addField("Canal", channel != null ? "<#" + config.getChannelId() + ">" : "Canal não encontrado", true)
                            .addField("Número Atual", String.valueOf(config.getCurrentNumber()), true)
                            .addField("Recorde", String.valueOf(config.getHighScore()), true)
                            .addField("Último Usuário", config.getLastUserId() != null ? "<@" + config.getLastUserId() + ">" : "Ninguém", true);
                }
                
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                
            } else if (action.equals("configurar") || action.equals("config") || action.equals("set")) {
                if (args.length < 2) {
                    event.getChannel().sendMessage("Por favor, mencione o canal ou forneça o ID.\n**Uso:** `!counting configurar #canal`")
                            .queue();
                    return;
                }
                
                String channelMention = args[1];
                String channelId = channelMention.replaceAll("[<#>]", "");
                TextChannel channel = event.getGuild().getTextChannelById(channelId);
                
                if (channel == null) {
                    event.getChannel().sendMessage("Canal não encontrado!").queue();
                    return;
                }
                
                CountingDAO.CountingConfig existing = countingDAO.getConfig(event.getGuild().getId());
                if (existing != null) {
                    countingDAO.updateConfig(event.getGuild().getId(), channel.getId());
                } else {
                    countingDAO.createConfig(event.getGuild().getId(), channel.getId());
                }
                
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Canal de Contagem Configurado")
                        .setDescription("O canal " + channel.getAsMention() + " foi configurado para contagem.")
                        .addField("Como funciona", 
                                "• Os membros devem enviar números em sequência (1, 2, 3...)\n" +
                                "• Cada número deve ser enviado por uma pessoa diferente\n" +
                                "• Se alguém errar, a contagem volta para 0", false)
                        .setColor(0x00ff00)
                        .setTimestamp();
                
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                
            } else if (action.equals("reset") || action.equals("zerar")) {
                countingDAO.resetCounting(event.getGuild().getId());
                event.getChannel().sendMessage("Contagem resetada para 0!").queue();
                
            } else if (action.equals("desativar") || action.equals("remove")) {
                countingDAO.deleteConfig(event.getGuild().getId());
                event.getChannel().sendMessage("Sistema de contagem desativado!").queue();
                
            } else {
                event.getChannel().sendMessage(
                        "Ação inválida!\n\n" +
                        "**Ações disponíveis:**\n" +
                        "• `status` - Mostra status do sistema\n" +
                        "• `configurar <canal>` - Configura canal de contagem\n" +
                        "• `reset` - Reseta a contagem para 0\n" +
                        "• `desativar` - Desativa o sistema\n\n" +
                        "**Exemplo:** `!counting configurar #contagem`"
                ).queue();
            }
            
        } catch (Exception e) {
            event.getChannel().sendMessage("Ocorreu um erro ao processar o comando. Tente novamente.").queue();
        }
    }
}
