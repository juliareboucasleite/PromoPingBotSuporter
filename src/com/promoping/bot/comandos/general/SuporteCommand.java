package com.promoping.bot.comandos.general;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.utils.EmbedBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Cria um ticket de suporte. Use no privado do bot ou no servidor.
 */
public class SuporteCommand extends BaseCommand {
    
    @Override
    public String getName() {
        return "suporte";
    }
    
    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("support");
    }
    
    @Override
    public String getDescription() {
        return "Cria um ticket de suporte. Use no privado do bot ou no servidor.";
    }
    
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        // Se for mensagem privada (DM), criar ticket
        if (event.getGuild() == null) {
            if (args.length == 0) {
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Criar Ticket de Suporte")
                        .setDescription(
                                "Para criar um ticket, use o comando seguido da sua dúvida ou problema.\n\n" +
                                "**Exemplos com slash command:**\n" +
                                "`/suporte mensagem: Preciso de ajuda com notificações`\n" +
                                "`/suporte mensagem: Tenho um problema ao fazer login`\n\n" +
                                "**Exemplos com comando de texto:**\n" +
                                "`!suporte Preciso de ajuda com notificações`\n" +
                                "`!suporte Tenho um problema ao fazer login`"
                        )
                        .setColor(0x5865F2)
                        .setTimestamp()
                        .setFooter("©PromoPing • Todos os direitos reservados");
                
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }
            
            // Criar ticket usando a função do bot (precisa ser implementado no Bot.java)
            String ticketMessage = String.join(" ", args);            event.getChannel().sendMessage("Funcionalidade de criar ticket via DM será implementada em breve.").queue();
            return;
        }
        
        // Se for no servidor, mostrar informações sobre como criar ticket via DM
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Sistema de Suporte via Mensagem Privada")
                .setDescription(
                        "**Como criar um ticket via mensagem privada:**\n\n" +
                        "1. Envie uma mensagem privada para este bot\n" +
                        "2. Use o comando `/suporte` ou `!suporte` seguido da sua dúvida ou problema\n" +
                        "3. Um ticket será criado automaticamente no servidor\n" +
                        "4. Nossa equipe de suporte responderá o mais breve possível\n\n" +
                        "**Exemplos com slash command:**\n" +
                        "`/suporte mensagem: Preciso de ajuda com notificações`\n\n" +
                        "**Exemplos com comando de texto:**\n" +
                        "`!suporte Preciso de ajuda com notificações`\n" +
                        "`!suporte Tenho um problema ao fazer login`"
                )
                .addField("Dica", "Você também pode usar `/ticket` ou `!ticket` no privado do bot para criar um ticket.", false)
                .setColor(0x5865F2)
                .setTimestamp()
                .setFooter("©PromoPing • Todos os direitos reservados");
        
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
