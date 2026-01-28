package com.promoping.bot.main;

import com.promoping.bot.comandos.core.CommandManager;
import com.promoping.bot.comandos.admin.*;
import com.promoping.bot.comandos.general.*;
import com.promoping.bot.comandos.system.*;
import com.promoping.bot.dao.CountingDAO;
import com.promoping.bot.dao.ReviewDAO;
import com.promoping.bot.security.AccessControl;
import com.promoping.bot.services.*;

public class CommandBootstrap {

    public static void registerAll() {

        AccessControl accessControl = new AccessControl();

        CommandManager.register(new Help());
        CommandManager.register(new StatusCommand(new StatusService()));
        CommandManager.register(new ReviewCommand(new ReviewDAO()));
        CommandManager.register(new ReviewTextoCommand(new ReviewDAO()));
        CommandManager.register(new SuporteCommand());
        CommandManager.register(new RulesCommand());
        CommandManager.register(new ReportarCommand());
        CommandManager.register(new SugerirCommand());
        CommandManager.register(new ReviewPanelCommand(accessControl));
        CommandManager.register(new InvitePanelCommand(accessControl));
        CommandManager.register(new CommunityPanelCommand(accessControl));
        CommandManager.register(new SponsorPanelCommand(accessControl));
        CommandManager.register(new DbSchemaCommand(accessControl));

        CommandManager.register(new CountingCommand(new CountingDAO(), accessControl));
        CommandManager.register(new LockCommand(new ChannelService(), accessControl));
        CommandManager.register(new UnlockCommand(new ChannelService(), accessControl));

        CommandManager.register(new FecharTicketCommand(accessControl));

        System.out.println("Comandos registados");
    }
}
