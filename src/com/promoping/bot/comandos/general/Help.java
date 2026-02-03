package com.promoping.bot.comandos.general;

import com.promoping.bot.comandos.core.Command;
import com.promoping.bot.comandos.core.CommandManager;
import com.promoping.bot.utils.BotConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Help implements Command {

    private static final int EMBED_DESCRIPTION_MAX = 4096;
    private static final int FIELD_VALUE_MAX = 1024;

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String[] getAliases() {
        return new String[] { "ajuda", "comandos", "h" };
    }

    @Override
    public String getDescription() {
        return "Lista os comandos disponíveis (menu paginado)";
    }

    @Override
    public String getCategory() {
        return "geral";
    }

    @Override
    public boolean adminOnly() {
        return false;
    }

    /**
     * Constrói a lista de páginas do help (intro + uma página por categoria).
     * Usado pelo comando e pelo ButtonListener para navegação.
     */
    public static List<MessageEmbed> buildPages(String prefix) {
        List<MessageEmbed> pages = new ArrayList<>();
        Map<String, List<Command>> byCategory = new LinkedHashMap<>();
        Map<String, Command> unique = new LinkedHashMap<>();

        for (Command cmd : CommandManager.all()) {
            unique.putIfAbsent(cmd.getName(), cmd);
        }
        for (Command cmd : unique.values()) {
            byCategory
                    .computeIfAbsent(cmd.getCategory(), k -> new ArrayList<>())
                    .add(cmd);
        }

        // Página 0: introdução
        String introDesc = "Sistema de monitoramento e suporte via Discord.\n\n" +
                "**Todos os comandos começam com o prefixo `" + prefix + "` (configurável em config.properties).**\n\n" +
                "Use os botões abaixo para navegar entre as páginas e ver os comandos por categoria.";
        pages.add(new EmbedBuilder()
                .setTitle("PromoPing Bot — Ajuda")
                .setDescription(introDesc)
                .setColor(0xffa500)
                .setFooter("Use os botões para navegar • PromoPing")
                .build());

        // Uma página por categoria
        for (Map.Entry<String, List<Command>> e : byCategory.entrySet()) {
            String category = e.getKey();
            List<Command> commands = e.getValue();
            StringBuilder body = new StringBuilder();
            for (Command cmd : commands) {
                body.append("• **").append(prefix).append(cmd.getName()).append("** — ")
                        .append(cmd.getDescription()).append("\n");
            }
            String value = body.toString();
            if (value.length() > FIELD_VALUE_MAX) {
                value = value.substring(0, FIELD_VALUE_MAX - 3) + "...";
            }
            pages.add(new EmbedBuilder()
                    .setTitle("Comandos — " + category)
                    .setDescription(value)
                    .setColor(0x5865F2)
                    .setFooter("PromoPing • " + prefix + "help")
                    .build());
        }

        return pages;
    }

    /**
     * Retorna os botões de navegação para a página atual (0-based).
     */
    public static List<Button> buildNavButtons(int currentPage, int totalPages) {
        String prevId = "help_prev_" + currentPage + "_" + totalPages;
        String nextId = "help_next_" + currentPage + "_" + totalPages;
        Button anterior = Button.secondary(prevId, "◀ Anterior").withDisabled(currentPage <= 0);
        Button proximo = Button.secondary(nextId, "Próximo ▶").withDisabled(currentPage >= totalPages - 1);
        Button fechar = Button.danger("help_close", "Fechar");
        return List.of(anterior, proximo, fechar);
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        String prefix = BotConfig.getPrefix();
        List<MessageEmbed> pages = buildPages(prefix);
        if (pages.isEmpty()) {
            event.getChannel().sendMessage("Nenhum comando registrado.").queue();
            return;
        }
        int total = pages.size();
        MessageEmbed first = pages.get(0);
        EmbedBuilder withFooter = new EmbedBuilder()
                .setTitle(first.getTitle())
                .setDescription(first.getDescription())
                .setColor(first.getColor() != null ? first.getColor().getRGB() : 0xffa500)
                .setFooter("Página 1 de " + total + " • PromoPing - Ajuda");
        event.getChannel()
                .sendMessageEmbeds(withFooter.build())
                .setComponents(ActionRow.of(buildNavButtons(0, total)))
                .queue();
    }
}
