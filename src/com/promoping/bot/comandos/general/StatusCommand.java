package com.promoping.bot.comandos.general;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.services.StatusService;
import com.promoping.bot.utils.EmbedBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Mostra informações sobre o sistema PromoPing e estatísticas do bot.
 */
public class StatusCommand extends BaseCommand {
    
    private final StatusService statusService;
    
    public StatusCommand(StatusService statusService) {
        this.statusService = statusService;
    }
    
    @Override
    public String getName() {
        return "status";
    }
    
    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("info", "stats");
    }
    
    @Override
    public String getDescription() {
        return "Mostra informações sobre o sistema PromoPing e estatísticas do bot.";
    }
    
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        try {
            StatusService.StatusInfo info = statusService.getStatusInfo(event.getJDA());
            
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Status do PromoPing Bot")
                    .addField("Status", info.isMonitoring() ? "Ativo" : "Parado", true)
                    .addField("Uptime", info.getUptime(), true)
                    .addField("Última Verificação", info.getLastCheck(), true)
                    .addField("Produtos Monitorados", String.valueOf(info.getProductCount()), true)
                    .addField("Usuários Discord", String.valueOf(info.getDiscordUsers()), true)
                    .addField("Mudanças Hoje", String.valueOf(info.getChangesToday()), true)
                    .addField("Intervalo", info.getCheckInterval() + " minutos", true)
                    .addField("Servidores", String.valueOf(info.getGuildCount()), true)
                    .addField("Prefixo", info.getPrefix(), true)
                    .setColor(0xffa500)
                    .setTimestamp()
                    .setFooter("PromoPing - Monitor de Preços");
            
            String logoUrl = System.getenv("PROMOPING_LOGO_URL");
            if (logoUrl != null && !logoUrl.isEmpty()) {
                embed.setThumbnail(logoUrl);
            }
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            
        } catch (Exception e) {
            event.getChannel().sendMessage("Erro ao obter status do bot.").queue();
        }
    }
}
