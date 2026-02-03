package com.promoping.bot.comandos.admin;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.security.AccessControl;
import com.promoping.bot.services.MessageService;
import com.promoping.bot.utils.BotConfig;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Comando para limpar mensagens do chat.
 */
public class ClearCommand extends BaseCommand {
    
    private final MessageService messageService;
    private final AccessControl accessControl;
    
    public ClearCommand(MessageService messageService, AccessControl accessControl) {
        this.messageService = messageService;
        this.accessControl = accessControl;
    }
    
    @Override
    public String getName() {
        return "clear";
    }
    
    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("purge", "limpar", "delete");
    }
    
    @Override
    public String getDescription() {
        return "Limpa mensagens do chat. Pode limpar de 1 a 100 mensagens.";
    }
    
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        MessageChannel channel = event.getChannel();
        String prefix = BotConfig.getPrefix();
        
        if (!accessControl.hasPermission(event.getMember(), Permission.MESSAGE_MANAGE) &&
            !accessControl.isAdmin(event.getMember())) {
            channel.sendMessage("Você precisa de permissão para gerenciar mensagens para usar este comando.")
                    .queue();
            return;
        }
        
        if (!(channel instanceof net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel)) {
            channel.sendMessage("Este comando só funciona em canais de servidor.").queue();
            return;
        }
        
        net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel guildChannel = 
            (net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel) channel;
        
        if (!event.getGuild().getSelfMember().hasPermission(guildChannel, Permission.MESSAGE_MANAGE)) {
            channel.sendMessage("Eu não tenho permissão para gerenciar mensagens neste canal.")
                    .queue();
            return;
        }
        
        if (args.length == 0) {
            channel.sendMessage("Por favor, forneça um número válido de mensagens para deletar (1-100).\n**Uso:** `" + prefix + "clear <número>`")
                    .queue();
            return;
        }
        
        try {
            int amount = Integer.parseInt(args[0]);
            
            if (amount < 1 || amount > 100) {
                channel.sendMessage("Você pode deletar entre 1 e 100 mensagens por vez.")
                        .queue();
                return;
            }
            
            int deleted = messageService.deleteMessages(guildChannel, amount);
            
            if (deleted == 0) {
                channel.sendMessage("Não há mensagens recentes o suficiente para deletar (máximo 14 dias).")
                        .queue();
                return;
            }
            
            Message confirmMsg = channel.sendMessage("**" + deleted + "** mensagem(ns) deletada(s)!")
                    .complete();
            
            confirmMsg.delete().queueAfter(3, TimeUnit.SECONDS);
            
        } catch (NumberFormatException e) {
            channel.sendMessage("Por favor, forneça um número válido de mensagens para deletar (1-100).\n**Uso:** `" + prefix + "clear <número>`")
                    .queue();
        } catch (Exception e) {
            channel.sendMessage("Ocorreu um erro ao deletar as mensagens. Tente novamente.")
                    .queue();
        }
    }
}
