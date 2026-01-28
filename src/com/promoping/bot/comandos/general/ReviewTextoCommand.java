package com.promoping.bot.comandos.general;

import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.dao.ReviewDAO;
import com.promoping.bot.services.ReviewSessionStore;
import com.promoping.bot.services.ReviewSessionStore.ReviewSession;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class ReviewTextoCommand extends BaseCommand {

    private final ReviewDAO reviewDAO;

    public ReviewTextoCommand(ReviewDAO reviewDAO) {
        this.reviewDAO = reviewDAO;
    }

    @Override
    public String getName() {
        return "review-texto";
    }

    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("reviewtexto");
    }

    @Override
    public String getDescription() {
        return "Envia o texto da avaliacao iniciada pelo painel.";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (event.getGuild() == null) {
            event.getChannel().sendMessage("Este comando so pode ser usado em um servidor!").queue();
            return;
        }

        ReviewSession session = ReviewSessionStore.getSession(event.getAuthor().getId(), event.getChannel().getId());
        if (session == null) {
            event.getMessage().delete().queue(
                    success -> {},
                    error -> {}
            );
            sendDmOrChannel(event, "Inicie sua avaliacao com !review ou pelo painel.");
            return;
        }

        if (!session.isReadyForText()) {
            event.getMessage().delete().queue(
                    success -> {},
                    error -> {}
            );
            sendDmOrChannel(event, "Escolha categoria e estrelas antes de enviar o texto.");
            return;
        }

        String texto = String.join(" ", args).trim();
        if (texto.isEmpty()) {
            event.getMessage().delete().queue(
                    success -> {},
                    error -> {}
            );
            sendDmOrChannel(event, "Envie o texto da avaliacao. Ex: !review-texto Muito bom.");
            return;
        }

        event.getMessage().delete().queue(
                success -> {},
                error -> {}
        );

        ReviewCommand reviewCommand = new ReviewCommand(reviewDAO);
        reviewCommand.processReview(
                event.getAuthor().getId(),
                session.getTipo(),
                texto,
                session.getRating(),
                session.getAnonimo(),
                event
        );

        ReviewSessionStore.clearSession(event.getAuthor().getId(), event.getChannel().getId());
    }

    private static void sendDmOrChannel(MessageReceivedEvent event, String text) {
        event.getAuthor().openPrivateChannel().queue(dm -> {
            dm.sendMessage(text).queue(
                    success -> {},
                    error -> event.getChannel().sendMessage(text)
                            .queue(msg -> msg.delete().queueAfter(10, java.util.concurrent.TimeUnit.SECONDS))
            );
        }, error -> {
            event.getChannel().sendMessage(text)
                    .queue(msg -> msg.delete().queueAfter(10, java.util.concurrent.TimeUnit.SECONDS));
        });
    }
}
