package com.promoping.bot.comandos.admin;

import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.security.AccessControl;
import com.promoping.bot.services.ChannelService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class UnlockCommand extends BaseCommand {

    private final ChannelService channelService;
    private final AccessControl accessControl;

    public UnlockCommand(ChannelService channelService, AccessControl accessControl) {
        this.channelService = channelService;
        this.accessControl = accessControl;
    }

    @Override
    public String getName() {
        return "unlock";
    }

    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("destrancar", "desbloquear");
    }

    @Override
    public String getDescription() {
        return "Destranca o canal atual.";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {

        if (event.getMember() == null) return;

        if (!accessControl.hasPermission(event.getMember(), Permission.MANAGE_CHANNEL)
                && !accessControl.isAdmin(event.getMember())) {
            event.getChannel().sendMessage("Sem permissão.").queue();
            return;
        }

        if (!(event.getChannel() instanceof TextChannel)) {
            event.getChannel().sendMessage("Este comando só funciona em canais de texto.").queue();
            return;
        }

        TextChannel channel = (TextChannel) event.getChannel();

        try {
            channelService.unlockChannel(channel);
            channel.sendMessage("🔓 Canal destrancado.").queue();
        } catch (Exception e) {
            channel.sendMessage("Erro ao destrancar o canal.").queue();
        }
    }
}
