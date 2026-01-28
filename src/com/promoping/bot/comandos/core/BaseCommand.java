package com.promoping.bot.comandos.core;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Collections;
import java.util.List;

/**
 * Base class for all commands providing default implementations.
 */
public abstract class BaseCommand implements Command {

    @Override
    public String[] getAliases() {
        List<String> aliases = getAliasesList();
        return aliases != null && !aliases.isEmpty() 
            ? aliases.toArray(new String[0]) 
            : new String[0];
    }

    /**
     * Override this method to provide aliases as a List.
     * @return List of aliases for this command
     */
    protected List<String> getAliasesList() {
        return Collections.emptyList();
    }

    @Override
    public String getCategory() {
        return "general";
    }

    @Override
    public boolean adminOnly() {
        return false;
    }
}
