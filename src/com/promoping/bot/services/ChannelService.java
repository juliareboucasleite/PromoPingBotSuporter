package com.promoping.bot.services;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ChannelService {

    private static final Logger logger = LoggerFactory.getLogger(ChannelService.class);

    public void lockChannel(TextChannel channel) {
        try {
            channel.upsertPermissionOverride(
                    channel.getGuild().getPublicRole()
            ).deny(Permission.MESSAGE_SEND).queue();

        } catch (Exception e) {
            logger.error("Erro ao trancar canal", e);
            throw new RuntimeException("Erro ao trancar canal", e);
        }
    }
    public void unlockChannel(TextChannel channel) {
        try {
            channel.upsertPermissionOverride(
                    channel.getGuild().getPublicRole()
            ).clear(Permission.MESSAGE_SEND).queue();
        } catch (Exception e) {
            logger.error("Erro ao destrancar canal", e);
            throw new RuntimeException("Erro ao destrancar canal", e);
        }
    }
}
