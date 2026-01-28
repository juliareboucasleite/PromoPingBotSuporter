package com.promoping.bot.listeners;

import com.promoping.bot.services.ReviewSessionStore;
import com.promoping.bot.services.ReviewSessionStore.ReviewSession;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;

public class ButtonListener extends ListenerAdapter {

    private static final String SUPPORT_ROLE_ID = System.getenv("DISCORD_SUPPORT_ROLE_ID");
    private static final String TICKETS_CATEGORY_NAME = "Tickets";

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String componentId = event.getComponentId();
        if (componentId.equals("review_tipo_select")) {
            handleReviewTipoSelect(event);
            return;
        }
        if (componentId.equals("ticket_category_select")) {
            handleTicketCategorySelect(event);
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String componentId = event.getComponentId();
        if (componentId.startsWith("review_rating_")) {
            handleRating(event, componentId);
            return;
        }

        switch (componentId) {
            case "review_start":
                ReviewSessionStore.startSession(event.getUser().getId(), event.getChannel().getId());

                EmbedBuilder startEmbed = new EmbedBuilder()
                        .setTitle("Sistema de Avaliacoes")
                        .setDescription("Escolha o que deseja avaliar:")
                        .setColor(0xffa500);

                StringSelectMenu menu = StringSelectMenu.create("review_tipo_select")
                        .setPlaceholder("Selecione o que deseja avaliar...")
                        .addOption("Site", "site", "Avaliar o site PromoPing")
                        .addOption("Bot", "bot", "Avaliar o bot Discord")
                        .addOption("Suporte", "suporte", "Avaliar o atendimento de suporte")
                        .build();

                event.replyEmbeds(startEmbed.build())
                        .setEphemeral(true)
                        .setComponents(ActionRow.of(menu))
                        .queue();
                break;

            case "review_anonimo_sim":
            case "review_anonimo_nao":
                handleAnonimo(event);
                break;

            case "aceitar_regras_promoping":
                handleAceitarRegras(event);
                break;

            case "abrir_formulario_sugestao":
                openSugestaoModal(event);
                break;

            case "abrir_formulario_bug":
                openBugModal(event);
                break;

            case "abrir_ticket_promoping":
                openTicketMenu(event);
                break;

            case "ticket_close":
                closeTicket(event);
                break;

            case "ticket_call_support":
                callSupport(event);
                break;
        }
    }

    private void handleReviewTipoSelect(StringSelectInteractionEvent event) {
        ReviewSession session = ReviewSessionStore.getSession(event.getUser().getId(), event.getChannel().getId());
        if (session == null) {
            event.reply("Inicie sua avaliacao com !review ou pelo painel.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String tipo = event.getValues().get(0);
        session.setTipo(tipo);

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Anonimato")
                .setDescription("Voce esta avaliando: **" + tipoNome(tipo) + "**

Deseja que sua avaliacao seja anonima?")
                .setColor(0x5865F2);

        event.replyEmbeds(embed.build())
                .setEphemeral(true)
                .setComponents(ActionRow.of(
                        Button.success("review_anonimo_sim", "Sim, Anonimo"),
                        Button.secondary("review_anonimo_nao", "Nao, Mostrar Nome")
                ))
                .queue();
    }

    private void handleAnonimo(ButtonInteractionEvent event) {
        ReviewSession session = ReviewSessionStore.getSession(event.getUser().getId(), event.getChannel().getId());
        if (session == null || session.getTipo() == null) {
            event.reply("Inicie sua avaliacao com !review ou pelo painel.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        boolean anonimo = event.getComponentId().equals("review_anonimo_sim");
        session.setAnonimo(anonimo);

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Avaliacao")
                .setDescription("Escolha sua nota de 1 a 5 estrelas.")
                .setColor(0x5865F2);

        event.replyEmbeds(embed.build())
                .setEphemeral(true)
                .setComponents(ActionRow.of(
                        Button.primary("review_rating_1", "1"),
                        Button.primary("review_rating_2", "2"),
                        Button.primary("review_rating_3", "3"),
                        Button.primary("review_rating_4", "4"),
                        Button.primary("review_rating_5", "5")
                ))
                .queue();
    }

    private void handleRating(ButtonInteractionEvent event, String componentId) {
        ReviewSession session = ReviewSessionStore.getSession(event.getUser().getId(), event.getChannel().getId());
        if (session == null || session.getTipo() == null || session.getAnonimo() == null) {
            event.reply("Inicie sua avaliacao com !review ou pelo painel.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        int rating = Integer.parseInt(componentId.substring("review_rating_".length()));
        session.setRating(rating);

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Avaliacao")
                .setDescription("Nota selecionada: " + rating + "/5

Agora envie sua avaliacao:
`!review-texto <sua avaliacao>`")
                .setColor(0x00ff00);

        event.replyEmbeds(embed.build())
                .setEphemeral(true)
                .queue();
    }

    private void handleAceitarRegras(ButtonInteractionEvent event) {
        if (event.getGuild() == null) {
            event.reply("Este botao so funciona dentro de um servidor.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        Member member = event.getMember();
        if (member == null) {
            event.reply("Nao consegui identificar seu usuario no servidor.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        Role role = event.getGuild().getRoleById("1443627596565712978");
        if (role == null) {
            event.reply("Cargo nao encontrado. Avise a equipe.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (member.getRoles().contains(role)) {
            event.reply("Voce ja tem esse cargo.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        event.deferReply(true).queue();
        event.getGuild().addRoleToMember(member, role).queue(
                success -> event.getHook().editOriginal("Cargo atribuido! Obrigado por aceitar as regras.").queue(),
                error -> event.getHook().editOriginal("Nao consegui atribuir o cargo. Verifique as permissoes do bot.").queue()
        );
    }

    private void openSugestaoModal(ButtonInteractionEvent event) {
        TextInput titulo = TextInput.create(
                        "titulo_sugestao",
                        "Titulo da sugestao",
                        TextInputStyle.SHORT
                )
                .setPlaceholder("Ex: Notificacoes por WhatsApp")
                .setRequired(true)
                .build();

        TextInput descricao = TextInput.create(
                        "descricao_sugestao",
                        "Descreva a sua ideia",
                        TextInputStyle.PARAGRAPH
                )
                .setPlaceholder("Explique a funcionalidade ou melhoria...")
                .setRequired(true)
                .build();

        Modal modal = Modal.create(
                        "modal_sugestao",
                        "Nova Sugestao"
                )
                .addActionRow(titulo)
                .addActionRow(descricao)
                .build();

        event.replyModal(modal).queue();
    }

    private void openBugModal(ButtonInteractionEvent event) {
        TextInput tituloBug = TextInput.create(
                        "titulo_bug",
                        "Titulo do bug",
                        TextInputStyle.SHORT
                )
                .setPlaceholder("Ex: Erro ao enviar review")
                .setRequired(true)
                .build();

        TextInput descricaoBug = TextInput.create(
                        "descricao_bug",
                        "Descreva o problema",
                        TextInputStyle.PARAGRAPH
                )
                .setPlaceholder("Explique o que aconteceu e como reproduzir...")
                .setRequired(true)
                .build();

        Modal bugModal = Modal.create(
                        "modal_bug",
                        "Reportar Bug"
                )
                .addActionRow(tituloBug)
                .addActionRow(descricaoBug)
                .build();

        event.replyModal(bugModal).queue();
    }

    private void openTicketMenu(ButtonInteractionEvent event) {
        StringSelectMenu menu = StringSelectMenu.create("ticket_category_select")
                .setPlaceholder("Selecione a categoria do ticket...")
                .addOption("Duvida sobre o Bot", "bot")
                .addOption("Problema no Site", "site")
                .addOption("Pagamentos/Planos", "pagamentos")
                .addOption("Outros", "outros")
                .build();

        event.reply("Escolha a categoria do seu ticket:")
                .setEphemeral(true)
                .setComponents(ActionRow.of(menu))
                .queue();
    }

    private void handleTicketCategorySelect(StringSelectInteractionEvent event) {
        if (event.getGuild() == null) {
            event.reply("Este menu so funciona dentro de um servidor.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String categoria = event.getValues().get(0);
        String categoriaLabel = ticketCategoryLabel(categoria);

        TextChannel existing = findExistingTicket(event.getGuild().getTextChannels(), event.getUser().getId());
        if (existing != null) {
            event.reply("Voce ja tem um ticket aberto: " + existing.getAsMention())
                    .setEphemeral(true)
                    .queue();
            return;
        }

        Category category = getOrCreateTicketsCategory(event);
        String channelName = buildTicketChannelName(event.getUser().getName(), event.getUser().getId());

        event.getGuild().createTextChannel(channelName, category)
                .setTopic("ticket_owner:" + event.getUser().getId() + "|categoria:" + categoriaLabel)
                .addPermissionOverride(event.getGuild().getPublicRole(),
                        Collections.emptyList(),
                        Arrays.asList(Permission.VIEW_CHANNEL))
                .addPermissionOverride(event.getMember(),
                        Arrays.asList(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY),
                        Collections.emptyList())
                .addPermissionOverride(event.getGuild().getSelfMember(),
                        Arrays.asList(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY),
                        Collections.emptyList())
                .queue(channel -> {
                    if (SUPPORT_ROLE_ID != null && !SUPPORT_ROLE_ID.isEmpty()) {
                        Role supportRole = event.getGuild().getRoleById(SUPPORT_ROLE_ID);
                        if (supportRole != null) {
                            channel.upsertPermissionOverride(supportRole)
                                    .setAllowed(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY)
                                    .queue();
                        }
                    }

                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("Ticket de Suporte Criado")
                            .setDescription("**Ticket criado por:** " + event.getUser().getAsMention() + "
" +
                                    "**Categoria:** " + categoriaLabel + "

" +
                                    "**Informacoes**
" +
                                    "- Um membro da equipe de suporte respondera em breve
" +
                                    "- Descreva seu problema com detalhes
" +
                                    "- Use os botoes abaixo para gerenciar o ticket")
                            .setColor(0x00ff00)
                            .setTimestamp()
                            .setFooter("PromoPing - Suporte");

                    Button fecharBtn = Button.danger("ticket_close", "Fechar Ticket");
                    Button chamarBtn = Button.primary("ticket_call_support", "Chamar Suporte");

                    channel.sendMessageEmbeds(embed.build())
                            .setActionRow(fecharBtn, chamarBtn)
                            .queue();

                    event.reply("Ticket criado: " + channel.getAsMention())
                            .setEphemeral(true)
                            .queue();
                }, error -> event.reply("Nao consegui criar o ticket. Verifique as permissoes do bot.")
                        .setEphemeral(true)
                        .queue());
    }

    private TextChannel findExistingTicket(List<TextChannel> channels, String userId) {
        for (TextChannel ch : channels) {
            String topic = ch.getTopic();
            if (topic != null && topic.contains("ticket_owner:" + userId)) {
                return ch;
            }
        }
        return null;
    }

    private Category getOrCreateTicketsCategory(StringSelectInteractionEvent event) {
        List<Category> cats = event.getGuild().getCategoriesByName(TICKETS_CATEGORY_NAME, true);
        if (!cats.isEmpty()) return cats.get(0);
        return event.getGuild().createCategory(TICKETS_CATEGORY_NAME).complete();
    }

    private String buildTicketChannelName(String username, String userId) {
        String clean = username.toLowerCase().replaceAll("[^a-z0-9]", "");
        if (clean.isEmpty()) clean = userId.substring(0, 4);
        String suffix = userId.length() > 4 ? userId.substring(userId.length() - 4) : userId;
        return "ticket-" + clean + "-" + suffix;
    }

    private void closeTicket(ButtonInteractionEvent event) {
        TextChannel channel = event.getChannel().asTextChannel();
        if (!channel.getName().startsWith("ticket-")) {
            event.reply("Este botao so funciona em canais de ticket.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        boolean isAdmin = event.getMember() != null && event.getMember().hasPermission(Permission.ADMINISTRATOR);
        boolean hasSupport = SUPPORT_ROLE_ID != null && event.getMember() != null &&
                event.getMember().getRoles().stream().anyMatch(r -> r.getId().equals(SUPPORT_ROLE_ID));
        boolean isOwner = isTicketOwner(channel, event.getUser().getId());

        if (!isOwner && !isAdmin && !hasSupport) {
            event.reply("Sem permissao para fechar este ticket.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        EmbedBuilder closeEmbed = new EmbedBuilder()
                .setTitle("Ticket Fechado")
                .setDescription("Este ticket foi fechado por " + event.getUser().getAsMention())
                .addField("Informacao", "O canal sera deletado em 10 segundos.", false)
                .setColor(0xff0000)
                .setTimestamp()
                .setFooter("PromoPing - Suporte");

        channel.sendMessageEmbeds(closeEmbed.build()).queue();
        channel.delete().queueAfter(10, java.util.concurrent.TimeUnit.SECONDS);
        event.reply("Fechando ticket...").setEphemeral(true).queue();
    }

    private void callSupport(ButtonInteractionEvent event) {
        if (SUPPORT_ROLE_ID == null || SUPPORT_ROLE_ID.isEmpty()) {
            event.reply("Cargo de suporte nao configurado.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        Role supportRole = event.getGuild() != null ? event.getGuild().getRoleById(SUPPORT_ROLE_ID) : null;
        if (supportRole == null) {
            event.reply("Cargo de suporte nao encontrado.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        event.getChannel().sendMessage(supportRole.getAsMention() + " - Novo ticket aguardando atendimento.").queue();
        event.reply("Suporte chamado.").setEphemeral(true).queue();
    }

    private boolean isTicketOwner(TextChannel channel, String userId) {
        String topic = channel.getTopic();
        return topic != null && topic.contains("ticket_owner:" + userId);
    }

    private static String tipoNome(String tipo) {
        if ("site".equals(tipo)) return "Site";
        if ("bot".equals(tipo)) return "Bot";
        return "Suporte";
    }

    private static String ticketCategoryLabel(String value) {
        if ("bot".equals(value)) return "Duvida sobre o Bot";
        if ("site".equals(value)) return "Problema no Site";
        if ("pagamentos".equals(value)) return "Pagamentos/Planos";
        return "Outros";
    }
}
