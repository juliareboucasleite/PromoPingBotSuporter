package com.promoping.bot.comandos.general;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.utils.EmbedBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Mostra as regras do bot PromoPing.
 */
public class RulesCommand extends BaseCommand {
    
    @Override
    public String getName() {
        return "regras";
    }
    
    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("rules", "r");
    }
    
    @Override
    public String getDescription() {
        return "Mostra as regras do bot PromoPing.";
    }
    
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        String rulesContent = 
                "1. **Respeite todos os usuários.** Não serão toleradas ofensas, racismo, ou preconceitos de qualquer natureza.\n" +
                "2. **Não faça spam.** Evite enviar mensagens repetidas ou anúncios não autorizados.\n" +
                "3. **Use os comandos corretamente.** Abuse dos comandos pode levar a ban.\n" +
                "4. **Não tente explorar falhas do bot.** Vulnerabilidades devem ser reportadas à equipe.\n" +
                "5. **Não compartilhe informações pessoais ou sensíveis no Discord.**\n" +
                "6. **Siga os Termos de Uso** do PromoPing e do Discord.\n" +
                "7. **Dúvidas ou problemas:** Abra um ticket pelo comando `!suporte` no privado do bot ou nesse canal <#1442960813563449516>.";
        
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Regras do PromoPing")
                .setDescription(rulesContent)
                .setColor(0xffa500)
                .setTimestamp()
                .setFooter("Página 1 de 1 • PromoPing");
        
        String logoUrl = System.getenv("PROMOPING_LOGO_URL");
        if (logoUrl != null && !logoUrl.isEmpty()) {
            embed.setThumbnail(logoUrl);
        }        
        event.getChannel().sendMessageEmbeds(embed.build())
                .setComponents(ActionRow.of(Button.success("aceitar_regras_promoping", "Eu Li e Concordo")))
                .queue();
    }
}
