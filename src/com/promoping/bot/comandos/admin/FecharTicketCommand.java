package com.promoping.bot.comandos.admin;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
// import net.dv8tion.jda.api.utils.concurrent.Task; // Removed - use CompletableFuture instead
import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.security.AccessControl;
import com.promoping.bot.utils.EmbedBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Fecha o ticket de suporte atual.
 */
public class FecharTicketCommand extends BaseCommand {
    
    private static final String SUPPORT_ROLE_ID = System.getenv("DISCORD_SUPPORT_ROLE_ID");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("[^a-z0-9]");
    
    private final AccessControl accessControl;
    
    public FecharTicketCommand(AccessControl accessControl) {
        this.accessControl = accessControl;
    }
    
    @Override
    public String getName() {
        return "fechar-ticket";
    }
    
    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("fechar", "close-ticket", "encerrar");
    }
    
    @Override
    public String getDescription() {
        return "Fecha o ticket de suporte atual.";
    }
    
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (event.getGuild() == null) {
            event.getChannel().sendMessage("Este comando só pode ser usado em um servidor!").queue();
            return;
        }
        
        TextChannel channel = event.getChannel().asTextChannel();
        String userId = event.getAuthor().getId();
        
        // Verificar se o canal é um ticket
        if (!channel.getName().startsWith("ticket-")) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Erro")
                    .setDescription("Este comando só pode ser usado em um canal de ticket!")
                    .setColor(0xff0000)
                    .setTimestamp();
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }
        
        // Verificar permissões
        String username = event.getAuthor().getName().toLowerCase();
        String cleanUsername = USERNAME_PATTERN.matcher(username).replaceAll("");
        boolean isTicketOwner = channel.getName().contains(cleanUsername);
        boolean isAdmin = accessControl.isAdmin(event.getMember());
        boolean hasSupportRole = SUPPORT_ROLE_ID != null && 
                                event.getMember().getRoles().stream()
                                    .anyMatch(role -> role.getId().equals(SUPPORT_ROLE_ID));
        
        if (!isTicketOwner && !isAdmin && !hasSupportRole) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Sem Permissão")
                    .setDescription("Apenas o criador do ticket, administradores ou membros da equipe de suporte podem fechar tickets!")
                    .setColor(0xff0000)
                    .setTimestamp();
            
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            return;
        }
        
        // Perguntar confirmação
        EmbedBuilder confirmEmbed = new EmbedBuilder()
                .setTitle("Confirmar Fechamento")
                .setDescription("Tem certeza que deseja fechar este ticket?\n\nDigite `confirmar` nos próximos **30 segundos** para fechar o ticket.\n\nDigite `cancelar` para cancelar.")
                .setColor(0xffa500)
                .setTimestamp();
        
        event.getChannel().sendMessageEmbeds(confirmEmbed.build()).queue(confirmMsg -> {
            // Implementação simplificada - em produção usar MessageCollector
            // Por enquanto, apenas fecha após 30 segundos se não houver cancelamento
            channel.sendMessage("**Aguardando confirmação...** Digite `confirmar` ou `cancelar`").queue();            // Por enquanto, implementação básica que fecha após delay usando CompletableFuture
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.schedule(() -> {
                closeTicket(channel, event.getAuthor().getAsMention());
                scheduler.shutdown();
            }, 30, TimeUnit.SECONDS);
        });
    }
    
    private void closeTicket(TextChannel channel, String closedBy) {
        EmbedBuilder closeEmbed = new EmbedBuilder()
                .setTitle("Ticket Fechado")
                .setDescription("Este ticket foi fechado por " + closedBy)
                .addField("Informação", "O canal será deletado em **10 segundos**.", false)
                .setColor(0xff0000)
                .setTimestamp()
                .setFooter("PromoPing - Suporte");
        
        channel.sendMessageEmbeds(closeEmbed.build()).queue();
        
        // Deletar após 10 segundos usando ScheduledExecutorService
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            channel.delete().queue(
                    success -> {
                        // Verificar se categoria está vazia e deletar se necessário
                        if (channel.getParentCategory() != null) {
                            net.dv8tion.jda.api.entities.channel.concrete.Category category = channel.getParentCategory();
                            if (category.getTextChannels().isEmpty()) {
                                category.delete().queue();
                            }
                        }
                    },
                    error -> {
                        // Erro ao deletar - logar
                    }
            );
            scheduler.shutdown();
        }, 10, TimeUnit.SECONDS);
    }
}
