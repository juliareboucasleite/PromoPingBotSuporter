package com.promoping.bot.comandos;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private static final Map<String, Command> COMMANDS = new HashMap<>();

    public static void register(Command command) {
        COMMANDS.put(command.getName(), command);

        for (String alias : command.getAliases()) {
            COMMANDS.put(alias, command);
        }
    }


    public static Command get(String name) {
        return COMMANDS.get(name);
    }

    public static Collection<Command> all() {
        return COMMANDS.values();
    }

}
