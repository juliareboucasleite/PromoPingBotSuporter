package com.promoping.bot.comandos.general;

import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.utils.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class CupomCommand extends BaseCommand {

    private static final String COUPON_CODE = "PROMOPING";
    private static final String COUPON_DISCOUNT = "55%";
    private static final String COUPON_EXPIRES_AT = "3 de ago. de 2026, 23:59 GMT";
    private static final String COUPON_REDEMPTION_LIMIT = "1 vez";

    @Override
    public String getName() {
        return "cupom";
    }

    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("coupon");
    }

    @Override
    public String getDescription() {
        return "Mostra o cupom atual do PromoPing e a data de validade.";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Cupom PromoPing")
                .setDescription("Tem cupom novo no PromoPing! Use o codigo abaixo no site.")
                .addField("Codigo", "`" + COUPON_CODE + "`", true)
                .addField("Desconto", COUPON_DISCOUNT + " na compra", true)
                .addField("Valido ate", COUPON_EXPIRES_AT, true)
                .addField("Limite de resgate", COUPON_REDEMPTION_LIMIT, true)
                .setColor(0x22c55e)
                .setTimestamp()
                .setFooter("PromoPing");

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
