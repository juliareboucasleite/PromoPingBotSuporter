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
        Database database = new Database();
        ReviewDAO reviewDAO = new ReviewDAO(database);
        CountingDAO countingDAO = new CountingDAO(database);
        TicketService ticketService = new TicketService(database);
        ChannelService channelService = new ChannelService();
        StatusService statusService = new StatusService();
        CommandManager.register(new Help());
        CommandManager.register(new StatusCommand(statusService));
        CommandManager.register(new ReviewCommand(reviewDAO));
        CommandManager.register(new SuporteCommand(ticketService));
        CommandManager.register(new RulesCommand());
        CommandManager.register(new ReportarCommand());
        CommandManager.register(new SugerirCommand());
        CommandManager.register(new CountingCommand(countingDAO, accessControl));
        CommandManager.register(new LockCommand(channelService, accessControl));
        CommandManager.register(new UnlockCommand(channelService, accessControl));
        CommandManager.register(new SetupTicketCommand(ticketService, accessControl));
        CommandManager.register(new FecharTicketCommand(ticketService, accessControl));
        CommandManager.register(new SetupBugCommand(accessControl));
        CommandManager.register(new SetupSugestaoCommand(accessControl));
        CommandManager.register(new InvitePanelCommand(accessControl));
        CommandManager.register(new ReviewPanelCommand(accessControl));

        System.out.println("Comandos registados");
    }
}

