package com.promoping.bot.services;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class MessageService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    public int deleteMessages(GuildMessageChannel channel, int amount) {
        try {
            int messagesToDelete = amount + 1;
            List<Message> messages = channel.getHistory().retrievePast(Math.min(messagesToDelete, 100)).complete();

            OffsetDateTime twoWeeksAgo = OffsetDateTime.now().minus(14, ChronoUnit.DAYS);
            List<Message> deletable = messages.stream()
                    .filter(msg -> msg.getTimeCreated().isAfter(twoWeeksAgo))
                    .collect(Collectors.toList());
            
            if (deletable.isEmpty()) {
                return 0;
            }
            if (deletable.size() == 1) {
                deletable.get(0).delete().complete();
                return 1;
            }
            channel.deleteMessages(deletable).complete();
            return deletable.size();
        } catch (Exception e) {
            logger.error("Error deleting messages", e);
            throw new RuntimeException("Error deleting messages", e);
        }
    }
}
